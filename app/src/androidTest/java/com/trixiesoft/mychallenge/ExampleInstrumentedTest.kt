package com.trixiesoft.mychallenge

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.trixiesoft.mychallenge.api.FilmLocation
import com.trixiesoft.mychallenge.api.FilmLocationAPI
import com.trixiesoft.mychallenge.vm.MovieLocationListViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    //val viewmodel by lazy { MovieLocationListViewModel(InstrumentationRegistry.getInstrumentation().targetContext) }

    @Test
    @Throws(Exception::class)
    fun testFilmLocation() {
        var filmLocations: List<FilmLocation>
        val disposable = FilmLocationAPI.getFilmLocations()
            .subscribe {
                locationData -> filmLocations = locationData
            }
    }
}
