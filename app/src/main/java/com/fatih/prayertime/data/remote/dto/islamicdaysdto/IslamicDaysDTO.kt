package com.fatih.prayertime.data.remote.dto.islamicdaysdto

import com.google.gson.annotations.SerializedName

data class IslamicDaysDTO(
    @SerializedName("data")
    val islamicDaysDatumDTOS: List<IslamicDaysDataDTO>
)