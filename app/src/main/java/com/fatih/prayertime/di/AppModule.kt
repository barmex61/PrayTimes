package com.fatih.prayertime.di

import android.app.Application
import android.content.Context
import android.location.Geocoder
import androidx.room.Room
import com.fatih.prayertime.data.local.dao.GlobalAlarmDao
import com.fatih.prayertime.data.local.dao.PrayDao
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
import com.fatih.prayertime.data.local.dao.FavoritesDao
import com.fatih.prayertime.data.local.dao.PrayerStatisticsDao
import com.fatih.prayertime.data.local.database.AppDatabase
import com.fatih.prayertime.data.remote.HadithApi
import com.fatih.prayertime.data.remote.IslamicCalendarApi
import com.fatih.prayertime.data.repository.FavoritesRepositoryImpl
import com.fatih.prayertime.data.repository.HadithRepositoryImp
import com.fatih.prayertime.data.repository.IslamicCalendarRepositoryImp
import com.fatih.prayertime.data.repository.LocalDataRepositoryImpl
import com.fatih.prayertime.data.repository.PrayerStatisticsRepositoryImpl
import com.fatih.prayertime.data.repository.SettingsRepositoryImp
import com.fatih.prayertime.data.settings.SettingsDataStore
import com.fatih.prayertime.domain.repository.FavoritesRepository
import com.fatih.prayertime.domain.repository.HadithRepository
import com.fatih.prayertime.domain.repository.IslamicCalendarRepository
import com.fatih.prayertime.domain.repository.LocalDataRepository
import com.fatih.prayertime.domain.repository.PrayerStatisticsRepository
import com.fatih.prayertime.domain.repository.SettingsRepository
import com.fatih.prayertime.domain.use_case.formatted_use_cases.FormattedUseCase
import com.fatih.prayertime.domain.use_case.location_use_cases.GetLocationAndAddressUseCase
import com.fatih.prayertime.domain.use_case.permission_use_case.PermissionsUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.GetAllFavoritesUseCase
import com.fatih.prayertime.domain.use_case.favorites_use_cases.RemoveFavoriteUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.GetPrayerCountsUseCase
import com.fatih.prayertime.domain.use_case.statistics_use_cases.GetStatisticsUseCase
import com.fatih.prayertime.util.config.ApiConfig.ALADHAN_API_BASE_URL
import com.fatih.prayertime.util.config.ApiConfig.HADITH_API_BASE_URL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Module {

    @Provides
    @Singleton
    fun providePrayRepository(prayApi : PrayApi) : PrayApiRepository = PrayApiRepositoryImp(prayApi)

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit = Retrofit.Builder().baseUrl(ALADHAN_API_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

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
        setMinUpdateDistanceMeters(1000f)
    }.build()

    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(@ApplicationContext context : Context) = NetworkConnectivityManager(context)

    @Provides
    @Singleton
    fun provideConnectivityRepository(networkConnectivityManager: NetworkConnectivityManager) : ConnectivityRepository = ConnectivityRepositoryImp(networkConnectivityManager )
    
    @Provides
    @Singleton
    fun providePrayDao(appDatabase: AppDatabase) = appDatabase.prayDao()

    @Provides
    @Singleton
    fun providePrayDatabaseRepository(prayDao: PrayDao) : PrayDatabaseRepository = PrayDatabaseRepositoryImp(prayDao)

    @Provides
    @Singleton
    fun provideAlarmDao(appDatabase: AppDatabase) = appDatabase.globalAlarmDao()

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context,settingsDataStore: SettingsDataStore,formattedUseCase: FormattedUseCase) = AlarmScheduler(context,settingsDataStore,formattedUseCase)

    @Provides
    @Singleton
    fun provideAlarmDatabaseRepo(globalAlarmDao: GlobalAlarmDao,alarmScheduler: AlarmScheduler) : AlarmDatabaseRepository = AlarmDatabaseRepositoryImp(globalAlarmDao,alarmScheduler)

    @Provides
    @Singleton
    fun provideRequestNotificationPermissionUseCase(@ApplicationContext context: Context) = PermissionsUseCase(context)


    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context,
    ): SettingsDataStore = SettingsDataStore(context)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsDataStore: SettingsDataStore
    ): SettingsRepository = SettingsRepositoryImp(settingsDataStore)

    @MainScreenLocation
    @Provides
    @Singleton
    fun provideActivityLocationAndAddressRepository(
        fusedLocationProviderClient: FusedLocationProviderClient,
        locationRequest: LocationRequest,
        geocoder: Geocoder
    ): LocationAndAddressRepository = LocationAndAddressRepoImp(fusedLocationProviderClient, locationRequest, geocoder)

    @WorkerLocation
    @Provides
    @Singleton
    fun provideWorkerLocationAndAddressRepository(
        fusedLocationProviderClient: FusedLocationProviderClient,
        locationRequest: LocationRequest,
        geocoder: Geocoder
    ): LocationAndAddressRepository = LocationAndAddressRepoImp(fusedLocationProviderClient, locationRequest, geocoder)

    @MainScreenLocation
    @Provides
    @Singleton
    fun provideActivityGetLocationAndAddressUseCase(
        @MainScreenLocation locationAndAddressRepository: LocationAndAddressRepository
    ): GetLocationAndAddressUseCase = GetLocationAndAddressUseCase(locationAndAddressRepository)

    @WorkerLocation
    @Provides
    @Singleton
    fun provideWorkerGetLocationAndAddressUseCase(
        @WorkerLocation locationAndAddressRepository: LocationAndAddressRepository
    ): GetLocationAndAddressUseCase = GetLocationAndAddressUseCase(locationAndAddressRepository)

    @Provides
    @Singleton
    fun provideLocationAndAddressRepository(
        fusedLocationProviderClient: FusedLocationProviderClient,
        locationRequest: LocationRequest,
        geocoder: Geocoder
    ): LocationAndAddressRepository = LocationAndAddressRepoImp(fusedLocationProviderClient, locationRequest, geocoder)

    @Provides
    @Singleton
    fun provideIslamicCalendarApi() : IslamicCalendarApi = Retrofit.Builder().baseUrl(ALADHAN_API_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(IslamicCalendarApi::class.java)

    @Provides
    @Singleton
    fun provideIslamicCalendarRepository(islamicCalendarApi: IslamicCalendarApi) : IslamicCalendarRepository = IslamicCalendarRepositoryImp(islamicCalendarApi)

    @Provides
    @Singleton
    fun provideHadithApi() : HadithApi = Retrofit.Builder().baseUrl(HADITH_API_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(HadithApi::class.java)

    @Provides
    @Singleton
    fun provideHadithRepositoryImp(hadithApi: HadithApi) : HadithRepository = HadithRepositoryImp(hadithApi)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFavoritesDao(database: AppDatabase): FavoritesDao {
        return database.favoritesDao()
    }

    @Provides
    @Singleton
    fun providePrayerStatisticsDao(database: AppDatabase): PrayerStatisticsDao {
        return database.prayerStatisticsDao()
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(favoritesDao: FavoritesDao): FavoritesRepository {
        return FavoritesRepositoryImpl(favoritesDao)
    }

    @Provides
    @Singleton
    fun providePrayerStatisticsRepository(statisticsDao: PrayerStatisticsDao): PrayerStatisticsRepository {
        return PrayerStatisticsRepositoryImpl(statisticsDao)
    }

    @Provides
    @Singleton
    fun provideGetAllFavoritesUseCase(repository: FavoritesRepository): GetAllFavoritesUseCase {
        return GetAllFavoritesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRemoveFavoriteUseCase(repository: FavoritesRepository): RemoveFavoriteUseCase {
        return RemoveFavoriteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetStatisticsUseCase(repository: PrayerStatisticsRepository): GetStatisticsUseCase {
        return GetStatisticsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPrayerCountsUseCase(repository: PrayerStatisticsRepository): GetPrayerCountsUseCase {
        return GetPrayerCountsUseCase(repository)
    }

    @Provides
    @Singleton
    fun bindLocalDataRepository(
        @ApplicationContext context: Context,
    ): LocalDataRepository = LocalDataRepositoryImpl(context as Application)

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainScreenLocation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WorkerLocation