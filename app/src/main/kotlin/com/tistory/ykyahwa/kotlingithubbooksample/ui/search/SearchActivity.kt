package com.tistory.ykyahwa.kotlingithubbooksample.ui.search

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import com.tistory.ykyahwa.kotlingithubbooksample.R
import com.tistory.ykyahwa.kotlingithubbooksample.api.model.GithubRepo
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.AutoClearedDisposable
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.plusAssign
import com.tistory.ykyahwa.kotlingithubbooksample.ui.repo.RepositoryActivity
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class SearchActivity : DaggerAppCompatActivity(), SearchAdapter.ItemClickListener {

    internal lateinit var menuSearch: MenuItem

    internal lateinit var searchView: SearchView

//    internal val adapter by lazy { SearchAdapter().apply { setItemClickListener(this@SearchActivity) } }

    internal val disposables = AutoClearedDisposable(this)

    internal val viewDisposables = AutoClearedDisposable(lifeCycleOwner = this, alwaysClearOnStop = false)

    @Inject lateinit var adapter: SearchAdapter
    @Inject lateinit var viewModelFactory: SearchViewModelFactory


//    internal val viewModelFactory by lazy {
//        SearchViewModelFactory(githubApi, searchHistoryDao)
//    }

//    @Inject lateinit var githubApi: GithubApi
//    @Inject lateinit var searchHistoryDao: SearchHistoryDao

    lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SearchViewModel::class.java]


        lifecycle += disposables
        lifecycle += viewDisposables

        with(rvActivitySearchList) {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }

        viewDisposables += viewModel.searchResult
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { items ->
                with(adapter) {
                    if (items.isEmpty) {
                        clearItems()
                    } else {
                        setItems(items.value)
                    }
                    notifyDataSetChanged()
                }
            }

        viewDisposables += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if (message.isEmpty) {
                    hideError()
                } else {
                    showError(message.value)
                }
            }

        viewDisposables += viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isLoading ->
                if (isLoading) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        menuSearch = menu.findItem(R.id.menu_activity_search_query)

        searchView = menuSearch.actionView as SearchView

        viewDisposables += searchView.queryTextChangeEvents()
            .filter { it.isSubmitted }
            .map { it.queryText() }
            .filter { it.isNotEmpty()}
            .map { it.toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { query ->
                updateTitle(query)
                hideSoftKeyboard()
                collapseSearchView()
                searchRepository(query)
            }

        menuSearch.expandActionView()

        viewDisposables += viewModel.lastSearchKeyword
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { keyword ->
                if (keyword.isEmpty) {
                    menuSearch.expandActionView()
                } else {
                    updateTitle(keyword.value)
                }
            }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_activity_search_query == item.itemId) {
            item.expandActionView()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(repository: GithubRepo) {

        disposables += viewModel.addToSearchHistory(repository)

        startActivity<RepositoryActivity>(
            RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
            RepositoryActivity.KEY_REPO_NAME to repository.name
        )
    }

    private fun searchRepository(query: String) {
        disposables += viewModel.searchRepository(query)
    }

    private fun updateTitle(query: String) {
        supportActionBar?.run { subtitle = query }
    }


    private fun hideSoftKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(searchView.windowToken, 0)}
    }


    private fun collapseSearchView() {
        menuSearch.collapseActionView()
    }

    private fun clearResults() {
        with(adapter) {
            clearItems()
            notifyDataSetChanged()
        }
    }

    private fun showProgress() {
        pbActivitySearch.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        pbActivitySearch.visibility = View.GONE
    }

    private fun showError(message: String?) {
        with(tvActivitySearchMessage) {
            text = message ?: "error"
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        with(tvActivitySearchMessage) {
            text = ""
            visibility = View.GONE
        }
    }
}
