package com.fatih.namazvakitleri.data.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import com.fatih.namazvakitleri.domain.model.Address
import com.fatih.namazvakitleri.domain.repository.LocationAndAddressRepository
import com.fatih.namazvakitleri.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class LocationAndAddressRepoImp @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest,
    private val geocoder: Geocoder
) : LocationAndAddressRepository {

    private var locationCallback : LocationCallback? = null
    private var isAlreadyCallbackAvailable : Boolean = false

    private suspend fun getAddressWithRetry(location: Location, maxRetries: Int = 3, retryDelay: Long = 10000): android.location.Address? {
        repeat(maxRetries) { attempt ->
            try {
                println("yess")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 3)
                return addresses?.getOrNull(0)
            } catch (e: IOException) {
                println(e)
                if (attempt < maxRetries - 1) {
                    delay(retryDelay)
                } else {
                    println("Geocoder failed after $maxRetries retries $e")
                }
            }
        }
        return null
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLocationAndAddressInformation(): Flow<Resource<Address>> = callbackFlow<Resource<Address>> {
        if (locationCallback == null){
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let { location ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val address = getAddressWithRetry(location)
                                val addressModel = Address(
                                    location.latitude,
                                    location.longitude,
                                    address?.countryName,
                                    address?.adminArea,
                                    address?.subAdminArea,
                                    address?.subLocality,
                                    address?.getAddressLine(0)
                                )
                                println(addressModel)
                                trySend(Resource.success(addressModel))
                            } catch (e: IOException) {
                                println(e.message)
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
                println(exception)
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

}
