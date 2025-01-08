package com.fatih.namazvakitleri.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fatih.namazvakitleri.data.local.AddressEntity
import com.fatih.namazvakitleri.data.local.dao.AddressDao

@Database(entities = [AddressEntity::class], version = 1, exportSchema = false)
abstract class AddressDatabase : RoomDatabase() {

    abstract fun addressDao(): AddressDao
}