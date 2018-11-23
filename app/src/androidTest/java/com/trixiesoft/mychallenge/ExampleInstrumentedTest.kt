package com.trixiesoft.mychallenge

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.trixiesoft.mychallenge.api.FilmLocation
import com.trixiesoft.mychallenge.api.FilmLocationAPI
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

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        //db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        //db!!.close()
    }

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
