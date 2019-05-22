package com.furkanaskin.app.podpocket.ui.search

import android.annotation.SuppressLint
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.furkanaskin.app.podpocket.R
import com.furkanaskin.app.podpocket.core.BaseFragment
import com.furkanaskin.app.podpocket.databinding.FragmentSearchBinding
import com.furkanaskin.app.podpocket.service.response.Genres
import com.furkanaskin.app.podpocket.service.response.GenresItem
import com.furkanaskin.app.podpocket.service.response.Search
import com.furkanaskin.app.podpocket.utils.service.CallbackWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Furkan on 16.04.2019
 */

class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>(SearchViewModel::class.java) {
    private val disposable = CompositeDisposable()
    private val genreList = mutableListOf<GenresItem?>()

    override fun initViewModel() {
        mBinding.viewModel = viewModel
    }

    override fun getLayoutRes(): Int = R.layout.fragment_search


    override fun init() {
        getGenres()
        initSearchView()
        initSearchAdapter()

    }

    @SuppressLint("WrongConstant")
    private fun initSearchAdapter() {
        val SearchEpisodeAdapter = SearchResultAdapter { item ->

        }
        mBinding.recyclerViewEpisodeSearchResult.adapter = SearchEpisodeAdapter
        mBinding.recyclerViewEpisodeSearchResult.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val SearchPodcastAdapter = PodcastSearchResultAdapter { item ->
        }

        mBinding.recyclerViewPodcastSearchResult.adapter = SearchPodcastAdapter
        mBinding.recyclerViewPodcastSearchResult.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)

    }

    private fun getGenres() {
        disposable.add(viewModel.getGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Genres>(activity?.application) {
                    override fun onSuccess(t: Genres) {
                        t.genres?.forEach {
                            genreList.add(it)
                        }


                    }

                }))


    }


    private fun initSearchView() {
        val searchEditText: EditText = mBinding.searchView.findViewById(R.id.search_src_text)
        activity?.applicationContext?.let { ContextCompat.getColor(it, R.color.white) }?.let { searchEditText.setTextColor(it) }
        activity?.applicationContext?.let { ContextCompat.getColor(it, R.color.grayTextColor) }?.let { searchEditText.setHintTextColor(it) }
        mBinding.searchView.isActivated = true
        mBinding.searchView.setIconifiedByDefault(false)
        mBinding.searchView.isIconified = false
        val searchViewSearchIcon = mBinding.searchView.findViewById<ImageView>(R.id.search_mag_icon)
        val searchViewCloseIcon = mBinding.searchView.findViewById<ImageView>(R.id.search_close_btn)
        searchViewSearchIcon.setImageResource(R.drawable.ic_search)
        val linearLayoutSearchView: ViewGroup = searchViewSearchIcon.parent as ViewGroup
        linearLayoutSearchView.removeView(searchViewSearchIcon)
        linearLayoutSearchView.addView(searchViewSearchIcon)

        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchViewCloseIcon.visibility = View.GONE
                if (newText?.length!! % 3 == 0)
                    Handler().postDelayed({
                        getSearchResult(newText.toString(), "episode")
                        getSearchResult(newText.toString(), "podcast")

                    }, 1000)
                return true
            }

        })
    }

    private fun getSearchResult(searchText: String, type: String) {

        showProgress()
        disposable.add(viewModel.getSearchResult(searchText, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Search>(viewModel.getApplication()) {
                    override fun onSuccess(t: Search) {
                        hideProgress()
                        if (type.equals("episode")) {
                            (mBinding.recyclerViewEpisodeSearchResult.adapter as SearchResultAdapter).submitList(t.results)
                        } else {
                            (mBinding.recyclerViewPodcastSearchResult.adapter as PodcastSearchResultAdapter).submitList(t.results)

                        }

                    }

                }))
    }


}