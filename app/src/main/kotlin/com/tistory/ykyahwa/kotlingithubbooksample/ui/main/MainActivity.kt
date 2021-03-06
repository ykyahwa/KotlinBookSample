package com.tistory.ykyahwa.kotlingithubbooksample.ui.main

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.tistory.ykyahwa.kotlingithubbooksample.R
import com.tistory.ykyahwa.kotlingithubbooksample.api.model.GithubRepo
import com.tistory.ykyahwa.kotlingithubbooksample.db.SearchHistoryDao
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.AutoActivatedDisposable
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.AutoClearedDisposable
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.plusAssign
import com.tistory.ykyahwa.kotlingithubbooksample.ui.repo.RepositoryActivity
import com.tistory.ykyahwa.kotlingithubbooksample.ui.search.SearchActivity
import com.tistory.ykyahwa.kotlingithubbooksample.ui.search.SearchAdapter
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), SearchAdapter.ItemClickListener {

//    internal val adapter by lazy { SearchAdapter().apply { setItemClickListener(this@MainActivity) } }

    internal val disposable = AutoClearedDisposable(this)

    internal val viewDisposable = AutoClearedDisposable(this, alwaysClearOnStop = false)

    @Inject lateinit var adapter: SearchAdapter
    @Inject lateinit var viewModelFactory: MainViewModelFactory
//    internal val viewModelFactory by lazy {
//        MainViewModelFactory(searchHistoryDao)
//    }

    @Inject lateinit var searchHistoryDao: SearchHistoryDao

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]

        lifecycle += disposable

        lifecycle += viewDisposable
        lifecycle += AutoActivatedDisposable(this) {
            viewModel.searchHistory
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { item ->
                    with(adapter) {
                        if (item.isEmpty) {
                            clearItems()
                        } else {
                            setItems(item.value)
                        }
                        notifyDataSetChanged()
                    }
                }
        }

        viewDisposable += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if (message.isEmpty) {
                    hideMessage()
                } else {
                    showMessage(message.value)
                }
            }

        lifecycle += object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun fetch() {
                retchSearchHistory()
            }
        }

        btnActivityMainSearch.setOnClickListener {
            startActivity<SearchActivity>()
        }

        with(rvActivityMainList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (R.id.menu_activity_main_clear_all == item.itemId) {
            disposable += viewModel.clearSearchHistory()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun retchSearchHistory() : Disposable = searchHistoryDao.getHistory()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({items ->

            with(adapter) {
                setItems(items)
                notifyDataSetChanged()
            }

            if (items.isEmpty()) {
                showMessage(getString(R.string.no_recent_repositories))
            } else {
                hideMessage()
            }

        }) {
            showMessage(it.message)
        }

    private fun hideMessage() {
        with(tvActivityMainMessage) {
            text = ""
            visibility = View.GONE
        }
    }

    private fun showMessage(message: String?) {
        with(tvActivityMainMessage) {
            text = message ?: "ERROR"
            visibility = View.VISIBLE
        }
    }

    override fun onItemClick(repository: GithubRepo) {
        startActivity<RepositoryActivity> (RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
            RepositoryActivity.KEY_REPO_NAME to repository.name)
    }
}
