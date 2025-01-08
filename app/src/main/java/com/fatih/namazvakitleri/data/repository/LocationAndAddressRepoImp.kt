package com.fatih.namazvakitleri.data.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.fatih.namazvakitleri.data.local.dao.AddressDao
import com.fatih.namazvakitleri.domain.model.Address
import com.fatih.namazvakitleri.domain.repository.LocationAndAddressRepository
import com.fatih.namazvakitleri.util.Resource
import com.fatih.namazvakitleri.util.toAddress
import com.fatih.namazvakitleri.util.toAddressEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class LocationAndAddressRepoImp @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest,
    private val geocoder: Geocoder,
    private val addressDao : AddressDao,
) : LocationAndAddressRepository {

    private var locationCallback : LocationCallback? = null
    private var isAlreadyCallbackAvailable : Boolean = false

    private suspend fun getAddressWithRetry(location: Location, maxRetries: Int = 10, retryDelay: Long = 10000): Resource<Address> {
        repeat(maxRetries) { attempt ->
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 3)
                val address = addresses?.getOrNull(0)
                val addressModel = Address(
                    location.latitude,
                    location.longitude,
                    address?.countryName,
                    address?.adminArea,
                    address?.subAdminArea,
                    address?.subLocality,
                    address?.getAddressLine(0)
                )
                return Resource.success(addressModel)
            } catch (e: IOException) {
                if (attempt < maxRetries - 1) {
                    delay(retryDelay)
                } else {
                    return Resource.error("Geocoder failed after $maxRetries retries $e")
                }
            }
        }
        return Resource.error("Nothing happened")
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLocationAndAddressInformation(): Flow<Resource<Address>> = callbackFlow<Resource<Address>> {
        if (locationCallback == null){
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let { location ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                trySend(Resource.loading())
                                val address = getAddressWithRetry(location)
                                trySend(address)
                            } catch (e: IOException) {
                                trySend(Resource.error(e.message))
                            }
                        }
                    }
                }
            }
        }

        if (!isAlreadyCallbackAvailable){
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            ).addOnFailureListener { exception ->
                close(exception) // Hata durumunda Flow'u kapat
                isAlreadyCallbackAvailable = false
                locationCallback = null
            }
            isAlreadyCallbackAvailable = true
        }
        awaitClose {
            isAlreadyCallbackAvailable = false
            fusedLocationProviderClient.removeLocationUpdates(locationCallback!!)
            locationCallback = null
        }
    }.flowOn(Dispatchers.IO) // IO thread'inde çalıştır

    override suspend fun getCurrentAddress(): Address? {
        return try {
            addressDao.getCurrentAddress().toAddress()
        }catch (e:Exception){
            null
        }
    }

    override suspend fun saveAddressToDatabase(address: Address) {
        addressDao.insertAddress(address.toAddressEntity())
    }
}
