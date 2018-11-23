package com.trixiesoft.mychallenge.ui

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.trixiesoft.mychallenge.R
import com.trixiesoft.mychallenge.api.FilmLocation
import com.trixiesoft.mychallenge.api.FilmLocationAPI
import com.trixiesoft.mychallenge.util.bindOptionalView
import com.trixiesoft.mychallenge.util.bindView
import com.trixiesoft.mychallenge.vm.MovieLocationListViewModel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [MovieDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class MovieListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
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

        getDisposable = viewModel.getMovieLocations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, it, twoPane)
            }, {
                Log.e("MovieListActivity", "", it)
            })
    }

    private var getDisposable: Disposable? = null

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: MovieListActivity,
        private val values: List<FilmLocation>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as FilmLocation
                if (twoPane) {
                    val fragment = MovieDetailFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("film", item as Parcelable)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, MovieDetailActivity::class.java).apply {
                        putExtra("film", item as Parcelable)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.title.text = "${item.title} (${item.releaseYear})"
            holder.location.text = item.locations

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView by bindView(R.id.txtTitle)
            val location: TextView by bindView(R.id.txtLocation)
        }
    }
}
