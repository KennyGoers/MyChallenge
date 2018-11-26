package com.trixiesoft.mychallenge

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.trixiesoft.mychallenge.api.FilmLocation
import com.trixiesoft.mychallenge.api.FilmLocationAPI
import com.trixiesoft.mychallenge.vm.MovieLocationListViewModel
import org.junit.After
import org.junit.Assert
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
class InstrumentedTest {

    private val viewModel by lazy { MovieLocationListViewModel() }

    // Just a simple unit test, under normal coding conditions I would write more appropriate tests

    @Test
    @Throws(Exception::class)
    fun testViewModel() {
        var movieMap: LinkedHashMap<String, MutableList<FilmLocation>>? = null
        var error: Throwable? = null
        viewModel.getMovieLocationsByMovie()
            .subscribe({
                movieMap = it
            }, {
                Log.e("MovieListActivity", "", it)
            })

        Assert.assertNotNull(movieMap)
        Assert.assertNotEquals(movieMap!!.size,  0)
        Assert.assertNull(error)
    }
}
