package com.fatih.namazvakitleri.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fatih.namazvakitleri.data.local.AddressEntity

@Dao
interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Query("SELECT * FROM Address WHERE id = :id")
    suspend fun getAllAddresses(id : Int = 0): AddressEntity

}