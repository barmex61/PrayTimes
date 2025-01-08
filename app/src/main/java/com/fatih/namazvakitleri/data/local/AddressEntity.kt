package com.fatih.namazvakitleri.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Address")
data class AddressEntity(
    @PrimaryKey(autoGenerate = false)
    val id : Int = 0 ,
    val latitude: Double?,
    val longitude: Double?,
    val country: String?,
    val city: String?,
    val district: String?,
    val street: String?,
    val fullAddress: String?
)
