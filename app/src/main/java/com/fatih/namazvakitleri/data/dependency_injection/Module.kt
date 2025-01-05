package com.fatih.namazvakitleri.data.dependency_injection

import com.fatih.namazvakitleri.data.remote.PrayApi
import com.fatih.namazvakitleri.data.repository.PrayRepositoryInterface
import com.fatih.namazvakitleri.domain.repository.PrayRepository
import com.fatih.namazvakitleri.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Module {

    @Provides
    @Singleton
    fun providePrayRepository(prayApi : PrayApi) : PrayRepository = PrayRepositoryInterface(prayApi)

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

    @Provides
    @Singleton
    fun providePrayApi(retrofit: Retrofit) : PrayApi = retrofit.create(PrayApi::class.java)
}