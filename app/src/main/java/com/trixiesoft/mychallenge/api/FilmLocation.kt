package com.trixiesoft.mychallenge.api

import android.os.Parcelable
import android.view.accessibility.AccessibilityRecord
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilmLocation (
    val sid: Int,
    val id: String,
    val position: Int,
    val createdAt: Long,
    val createdMeta: String?,
    val updatedAt: Long,
    val updatedMeta: String?,
    // val metaData: Any?, ignored
    val title: String,
    val releaseYear: String,
    val locations: String?,
    val funFacts: String?,
    val productionCompany: String?,
    val distributor: String?,
    val director: String?,
    val writer: String?,
    val actor1: String?,
    val actor2: String?,
    val actor3: String?
): Parcelable

fun FilmLocation.actors(): String? {
    if (actor1.isNullOrBlank())
        return "Not Specified"
    var actors = actor1
    if (actor2.isNullOrEmpty())
        return actors
    actors = "$actors, $actor2"
    if (actor3.isNullOrEmpty())
        return actors
    return "$actors, $actor3"
}