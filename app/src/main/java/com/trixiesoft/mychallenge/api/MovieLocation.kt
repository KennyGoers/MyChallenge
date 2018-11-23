package com.trixiesoft.mychallenge.api

import com.google.gson.annotations.SerializedName


// https://data.sfgov.org/resource/wwmu-gmzc.json

data class MovieLocation(
    @SerializedName("actor_1") val actor1: String,
    @SerializedName("actor_2") val actor2: String,
    @SerializedName("actor_3") val actor3: String,
    @SerializedName("director") val director: String,
    @SerializedName("distributor") val distributor: String,
    @SerializedName("fun_facts") val funFacts: String,
    @SerializedName("locations") val locations: String,
    @SerializedName("production_company") val productionCompany: String,
    @SerializedName("release_year") val releaseYear: String,
    @SerializedName("title") val title: String,
    @SerializedName("writer") val writer: String
)