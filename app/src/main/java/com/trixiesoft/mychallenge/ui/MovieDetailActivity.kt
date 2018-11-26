package com.trixiesoft.mychallenge.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import androidx.core.app.NavUtils
import com.trixiesoft.mychallenge.R
import com.trixiesoft.mychallenge.api.FilmLocation
import kotlinx.android.synthetic.main.activity_movie_detail.*

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [MovieListActivity].
 */
class MovieDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // used when not using element transitions
        // overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        setSupportActionBar(detail_toolbar)

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val filmLocation: FilmLocation = intent.getParcelableExtra("film")
        supportActionBar?.title = "${filmLocation.title} (${filmLocation.releaseYear})"

        if (savedInstanceState == null) {
            val fragment = MovieDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("film", filmLocation)
                }
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.movie_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this);
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun finish() {
        super.finish()
        // used when not using element transitions
        // overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }
}
