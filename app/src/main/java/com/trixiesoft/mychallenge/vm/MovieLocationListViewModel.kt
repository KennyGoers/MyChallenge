package com.trixiesoft.mychallenge.vm

import androidx.lifecycle.ViewModel
import com.trixiesoft.mychallenge.api.FilmLocation
import com.trixiesoft.mychallenge.api.FilmLocationAPI
import io.reactivex.Single

class MovieLocationListViewModel: ViewModel() {

    // Simple cached/single load
    private fun getMovieLocations(): Single<List<FilmLocation>> {
        return Single.defer {
            synchronized (filmLocationLock) {
                if (filmLocations != null)
                    Single.just(filmLocations)
                else
                    FilmLocationAPI.getFilmLocations()
                        .map<List<FilmLocation>> { filmLocations = it; it }
            }
        }
    }

    private var filmLocations: List<FilmLocation>? = null
    private val filmLocationLock: Any = Any()

    // return an ordered map from the list grouped by title, title is enough for this data set to be unique
    fun getMovieLocationsByMovie(): Single<LinkedHashMap<String, MutableList<FilmLocation>>> {
        return getMovieLocations()
            .map {
                it.groupByTo(LinkedHashMap()) { fl -> fl.title }
            }
    }
}