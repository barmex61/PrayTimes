package com.fatih.namazvakitleri.data.dependency_injection

import android.content.Context
import android.location.Geocoder
import com.fatih.namazvakitleri.data.remote.PrayApi
import com.fatih.namazvakitleri.data.repository.LocationAndAddressRepoImp
import com.fatih.namazvakitleri.data.repository.PrayRepositoryImp
import com.fatih.namazvakitleri.domain.repository.LocationAndAddressRepository
import com.fatih.namazvakitleri.domain.repository.PrayRepository
import com.fatih.namazvakitleri.util.Constants.BASE_URL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Module {

    @Provides
    @Singleton
    fun providePrayRepository(prayApi : PrayApi) : PrayRepository = PrayRepositoryImp(prayApi)

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

    @Provides
    @Singleton
    fun providePrayApi(retrofit: Retrofit) : PrayApi = retrofit.create(PrayApi::class.java)

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context : Context) = Geocoder(context, Locale.getDefault())

    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context : Context) = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideLocationRequest() : LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
        setWaitForAccurateLocation(true)
        setMinUpdateDistanceMeters(100f)
    }.build()

    @Provides
    @Singleton
    fun provideLocationAndAddressRepository(
        fusedLocationProviderClient: FusedLocationProviderClient,
        locationRequest: LocationRequest,
        geocoder: Geocoder) : LocationAndAddressRepository = LocationAndAddressRepoImp(fusedLocationProviderClient,locationRequest,geocoder)
}