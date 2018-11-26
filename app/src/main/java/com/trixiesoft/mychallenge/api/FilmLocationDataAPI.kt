package com.trixiesoft.mychallenge.api

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

class FilmLocationAPI {
    companion object {
        fun getFilmLocations(): Single<List<FilmLocation>> {
            return FilmLocationAPI.instance.getMovieLocationData()
                .map{
                    val listResult = mutableListOf<FilmLocation>()
                    for (filmData in it.data) {
                        listResult.add(FilmLocation(
                            (filmData[0] as Double).toInt(),
                            filmData[1] as String,
                            (filmData[2] as Double).toInt(),
                            (filmData[3] as Double).toLong(),
                            filmData[4] as String?,
                            (filmData[5] as Double).toLong(),
                            filmData[6] as String?,
                            // filmData[7] ignore meta data structure
                            filmData[8] as String,
                            filmData[9] as String,
                            filmData[10] as String?,
                            filmData[11] as String?,
                            filmData[12] as String?,
                            filmData[13] as String?,
                            filmData[14] as String?,
                            filmData[15] as String?,
                            filmData[16] as String?,
                            filmData[17] as String?,
                            filmData[18] as String?
                        ))
                    }
                    return@map listResult
                }
        }

        private fun create(): FilmLocationInterface {
            val logging = HttpLoggingInterceptor()
            // set your desired log level
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder().addInterceptor(logging)

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://openlibrary.org/")
                .client(httpClient.build())
                .build()
            return retrofit.create(FilmLocationInterface::class.java)
        }
        private val instance by lazy { create() }
    }

    interface FilmLocationInterface {
        // The Esoteric ugly end-pint
        @GET("https://data.sfgov.org/api/views/yitu-d5am/rows.json?accessType=DOWNLOAD")
        @Headers("X-App-Token: myUD0w8qkL29CCmzmq6DwyWLP")
        fun getMovieLocationData(): Single<FilmLocationData>

        // The nice clean endpoint :)
        @GET("https://data.sfgov.org/resource/wwmu-gmzc.json")
        @Headers("X-App-Token: myUD0w8qkL29CCmzmq6DwyWLP")
        fun getMovieLocations(): Single<MovieLocation>
    }

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

    data class FilmLocationData (
        @SerializedName("meta") val meta: Meta,
        @SerializedName("data") val data: List<List<Any?>>
    )

    data class Meta (
        @SerializedName("view") val view: View
    )

    data class View (
        @SerializedName("columns") val columns: List<Column>
    )

    data class Column(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("dataTypeName") val dataTypeName: String,
        @SerializedName("fieldName") val fieldName: String,
        @SerializedName("flags") val flags: List<String>,
        @SerializedName("position") val position: Int,
        @SerializedName("renderTypeName") val renderTypeName: String
    )
}

