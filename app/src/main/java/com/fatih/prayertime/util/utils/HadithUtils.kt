package com.fatih.prayertime.util.utils

import com.fatih.prayertime.data.remote.dto.hadithdto.HadithCollection
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSectionInfo
import com.fatih.prayertime.data.remote.dto.hadithdto.HadithSections
import com.fatih.prayertime.data.remote.dto.hadithdto.Sections
import com.fatih.prayertime.domain.model.HadithSectionCardData
import kotlin.reflect.KProperty1

object HadithUtils {
    fun combineSectionsAndDetails(hadithCollection: HadithCollection): List<HadithSectionCardData> {
        val sectionList = hadithCollection.metadata.sections.toList()
        val detailsList = hadithCollection.metadata.section_details.toList()

        return sectionList.zip(detailsList) { section, details ->
            val subHadithList = hadithCollection.hadiths.subList(
                details?.hadithnumber_first.anyToInt()!! - 1,
                details?.hadithnumber_last.anyToInt()!!
            )
            HadithSectionCardData(section, details, subHadithList, subHadithList.size)
        }
    }

    fun Sections.toList(): List<String?> {
        return this::class.members
            .filterIsInstance<KProperty1<Sections, *>>()
            .sortedBy { it.name.toInt() }
            .map { it.get(this) as String? }
            .filter { !it.isNullOrEmpty() }
    }

    fun HadithSections.toList(): List<HadithSectionInfo?> {
        return this::class.members
            .filterIsInstance<KProperty1<HadithSections, *>>()
            .sortedBy { it.name.toInt() }
            .map { it.get(this) as HadithSectionInfo? }
            .filterNot { it != null && it.hadithnumber_first == 0f && it.hadithnumber_last == 0f }
    }

    fun KProperty1<HadithSectionInfo, *>.getPropertyName(): String {
        return when (this.name) {
            "hadithnumber_first" -> "Starts at hadith no"
            "hadithnumber_last" -> "Ends at hadith no"
            else -> ""
        }
    }

    fun Any?.anyToInt(): Int? {
        return try {
            (this as Float).toInt()
        } catch (e: Exception) {
            null
        }
    }
} 