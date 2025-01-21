package com.fatih.prayertime.data.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.DeadObjectException
import android.os.Looper
import com.fatih.prayertime.domain.model.Address
import com.fatih.prayertime.domain.repository.LocationAndAddressRepository
import com.fatih.prayertime.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class LocationAndAddressRepoImp @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest,
    private val geocoder: Geocoder,
) : LocationAndAddressRepository {

    private var locationCallback : LocationCallback? = null

    private suspend fun getAddressWithRetry(location: Location, maxRetries: Int = 10, retryDelay: Long = 10000): Resource<Address> {
        repeat(maxRetries) { attempt ->
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude,1)
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
                println("IOExceptionGeocode $e")
                if (attempt < maxRetries - 1) {
                    delay(retryDelay)
                } else {
                    println("Geocoder failed after $maxRetries retries $e")
                    return Resource.error("Geocoder failed after $maxRetries retries $e")
                }
            }
        }
        return Resource.error("Nothing happened")
    }

    override suspend fun getLocationAndAddressInformation(): Flow<Resource<Address>> = callbackFlow<Resource<Address>> {
        if (locationCallback == null){
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let { location ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val address = getAddressWithRetry(location)
                                trySend(address)
                            } catch (e: IOException) {
                                println("IOException $e")
                                trySend(Resource.error(e.message))
                            }catch (e:DeadObjectException){
                                println("DeadObjectException $e")
                                trySend(Resource.error(e.message))
                            }catch (e:Exception){
                                println(e.message)
                                trySend(Resource.error(e.message))
                            }
                        }
                    }
                }
            }
        }
        try {

            fusedLocationProviderClient.removeLocationUpdates(locationCallback!!)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            ).addOnFailureListener { exception ->
                println("addOnFailureListener $exception")
                close(exception) // Hata durumunda Flow'u kapat
            }

        }catch (e:SecurityException){
            println("SecurityException $e")
            close(e)
        }
        catch (e:Exception){
            println("Exception $e")
            close(e)
        }

        awaitClose {
            println("close")
            //fusedLocationProviderClient.removeLocationUpdates(locationCallback!!)
            // locationCallback = null
        }
    }.flowOn(Dispatchers.IO) // IO thread'inde çalıştır

    override fun removeCallback(){
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
        }
        locationCallback = null
    }
}
