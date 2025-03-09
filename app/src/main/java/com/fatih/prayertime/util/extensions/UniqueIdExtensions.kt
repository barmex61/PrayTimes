package com.fatih.prayertime.util.extensions

import com.fatih.prayertime.util.model.enums.FavoritesType

fun generateItemId(type: String, title: String, collectionPath: String?, sectionIndex: Int?, hadithIndex: Int?): Long {
    val uniqueString = if (type == FavoritesType.HADIS.name && collectionPath != null && sectionIndex != null && hadithIndex != null) {
        "$type-$collectionPath-$sectionIndex-$hadithIndex-$title"
    } else {
        "$type-$title"
    }
    return uniqueString.hashCode().toLong()
}