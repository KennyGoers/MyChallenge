package com.trixiesoft.mychallenge.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.trixiesoft.mychallenge.R
import com.trixiesoft.mychallenge.api.FilmLocation
import com.trixiesoft.mychallenge.api.actors
import com.trixiesoft.mychallenge.util.bindOptionalView
import com.trixiesoft.mychallenge.util.bindView
import com.trixiesoft.mychallenge.vm.MovieLocationListViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MovieListActivity : AppCompatActivity() {

    private var twoPane: Boolean = false
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val detailContainer: FrameLayout? by bindOptionalView(R.id.movie_detail_container)
    private val recyclerView: RecyclerView by bindView(R.id.movie_list)

    private val viewModel: MovieLocationListViewModel by lazy {
        ViewModelProviders.of(this).get(MovieLocationListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (detailContainer != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        getDisposable = viewModel.getMovieLocationsByMovie()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                recyclerView.adapter = Adapter(it)
            }, {
                Log.e("MovieListActivity", "", it)
            })
    }

    private var getDisposable: Disposable? = null

    fun showFilmLocation(filmLocation: FilmLocation, options: ActivityOptions) {
        if (twoPane) {
            val fragment = MovieDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("film", filmLocation as Parcelable)
                }
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.movie_detail_container, fragment)
                .commit()
        } else {
            startActivity(Intent(this, MovieDetailActivity::class.java).apply {
                putExtra("film", filmLocation as Parcelable)
            }, options.toBundle())
        }
    }

    private inner class Adapter(
        private val values: LinkedHashMap<String, MutableList<FilmLocation>>
    ) :
        RecyclerView.Adapter<VH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            // TODO: look into if this is not allocating a list just to get an indexible value
            holder.bind(values.values.toList()[position])
        }

        override fun getItemCount() = values.size
    }

    private inner class VH(parent: ViewGroup):
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)) {

        val titleView: TextView by bindView(R.id.txtTitle)
        val actorsView: TextView by bindView(R.id.txtActors)
        val container: LinearLayout by bindView(R.id.container)
        val expandImage: ImageView by bindView(R.id.expandImage)
        var films: List<FilmLocation>? = null
        val expanded: MutableSet<String> = mutableSetOf()

        init {
            itemView.setOnClickListener {
                val id = films!!.first().id
                if (expanded.contains(id))
                    expanded.remove(id)
                else
                    expanded.add(id)
                expandOrCollapse(true)
            }
        }

        fun bind(films: List<FilmLocation>) {
            this.films = films
            films.first().apply {
                titleView.text = "${title} (${releaseYear})"
                actorsView.text = if (actor1.isNullOrBlank()) "Actors not specified" else "Starring: ${actors()}"
            }
            expandOrCollapse(false)
        }

        fun expandOrCollapse(animate: Boolean) {
            if (expanded.contains(films!!.first().id)) {
                for (filmLocation in films!!) {
                    val child = LayoutInflater.from(itemView.context)
                        .inflate(R.layout.list_item_sub, itemView as ViewGroup, false)
                    if (!filmLocation.locations.isNullOrBlank()) {
                        // skip those film locations that are empty (bad data)
                        (child as TextView).text = filmLocation.locations
                        container.addView(child)
                        child.tag = filmLocation
                        child.setOnClickListener {
                            showFilmLocation(it.tag as FilmLocation,
                                ActivityOptions.makeSceneTransitionAnimation(
                                    this@MovieListActivity,
                                    Pair.create<View, String>(it, "code-location"),
                                    Pair.create<View, String>(titleView, "code-title"))
                                )
                        }
                        expandImage.setImageResource(R.drawable.ic_chevron_up)
                    }
                }
            } else {
                expandImage.setImageResource(R.drawable.ic_chevron_down)
                container.removeAllViews()
            }
        }
    }
}
