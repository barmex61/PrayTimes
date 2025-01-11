package com.fatih.prayertime.data.dependency_injection

import android.content.Context
import android.location.Geocoder
import androidx.room.Room
import com.fatih.prayertime.data.local.dao.GlobalAlarmDao
import com.fatih.prayertime.data.local.dao.PrayDao
import com.fatih.prayertime.data.local.database.GlobalAlarmDatabase
import com.fatih.prayertime.data.local.database.PrayDatabase
import com.fatih.prayertime.data.network.NetworkConnectivityManager
import com.fatih.prayertime.data.remote.PrayApi
import com.fatih.prayertime.data.repository.AlarmDatabaseRepositoryImp
import com.fatih.prayertime.data.repository.ConnectivityRepositoryImp
import com.fatih.prayertime.data.repository.LocationAndAddressRepoImp
import com.fatih.prayertime.data.repository.PrayApiRepositoryImp
import com.fatih.prayertime.data.repository.PrayDatabaseRepositoryImp
import com.fatih.prayertime.domain.repository.AlarmDatabaseRepository
import com.fatih.prayertime.domain.repository.ConnectivityRepository
import com.fatih.prayertime.domain.repository.LocationAndAddressRepository
import com.fatih.prayertime.domain.repository.PrayApiRepository
import com.fatih.prayertime.domain.repository.PrayDatabaseRepository
import com.fatih.prayertime.data.alarm.AlarmScheduler
import com.fatih.prayertime.domain.use_case.permission_use_case.PermissionUseCase
import com.fatih.prayertime.util.Constants.BASE_URL
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
    fun providePrayRepository(prayApi : PrayApi) : PrayApiRepository = PrayApiRepositoryImp(prayApi)

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
    fun provideLocationRequest() : LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
        setMinUpdateDistanceMeters(2000f)
    }.build()

    @Provides
    @Singleton
    fun provideLocationAndAddressRepository(
        fusedLocationProviderClient: FusedLocationProviderClient,
        locationRequest: LocationRequest,
        geocoder: Geocoder
    ) : LocationAndAddressRepository = LocationAndAddressRepoImp(fusedLocationProviderClient,locationRequest,geocoder)

    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(@ApplicationContext context : Context) = NetworkConnectivityManager(context)

    @Provides
    @Singleton
    fun provideConnectivityRepository(networkConnectivityManager: NetworkConnectivityManager) : ConnectivityRepository = ConnectivityRepositoryImp(networkConnectivityManager )

    @Provides
    @Singleton
    fun providePrayDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(context, PrayDatabase::class.java, "pray_database").build()

    @Provides
    @Singleton
    fun providePrayDao(prayDatabase: PrayDatabase) = prayDatabase.prayDao()

    @Provides
    @Singleton
    fun providePrayDatabaseRepository(prayDao: PrayDao) : PrayDatabaseRepository = PrayDatabaseRepositoryImp(prayDao)

    @Provides
    @Singleton
    fun provideAlarmDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(context,GlobalAlarmDatabase::class.java, "alarm_database").build()

    @Provides
    @Singleton
    fun provideAlarmDao(globalAlarmDatabase: GlobalAlarmDatabase) = globalAlarmDatabase.globalAlarmDao()

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context) = AlarmScheduler(context)

    @Provides
    @Singleton
    fun provideAlarmDatabaseRepo(globalAlarmDao: GlobalAlarmDao,alarmScheduler: AlarmScheduler) : AlarmDatabaseRepository = AlarmDatabaseRepositoryImp(globalAlarmDao,alarmScheduler)

    @Provides
    @Singleton
    fun provideRequestNotificationPermissionUseCase(@ApplicationContext context: Context) = PermissionUseCase(context)

}