package com.trixiesoft.mychallenge.vm

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import com.trixiesoft.mychallenge.api.FilmLocation
import com.trixiesoft.mychallenge.api.FilmLocationAPI
import io.reactivex.Single

class MovieLocationListViewModel(@NonNull application: Application ): AndroidViewModel(application) {

    fun getMovieLocations(): Single<List<FilmLocation>> {
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

    fun getMoveLocationsByMovie(): Single<Map<String, List<FilmLocation>>> {
        return getMovieLocations()
            .map {
                it.groupBy { it -> it.title }
            }
    }
}