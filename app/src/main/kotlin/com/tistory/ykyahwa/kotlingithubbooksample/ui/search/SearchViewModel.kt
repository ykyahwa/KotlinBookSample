package com.tistory.ykyahwa.kotlingithubbooksample.ui.search

import android.arch.lifecycle.ViewModel
import com.tistory.ykyahwa.kotlingithubbooksample.api.GithubApi
import com.tistory.ykyahwa.kotlingithubbooksample.api.model.GithubRepo
import com.tistory.ykyahwa.kotlingithubbooksample.db.SearchHistoryDao
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.runOnIoScheduler
import com.tistory.ykyahwa.kotlingithubbooksample.util.SupportOptional
import com.tistory.ykyahwa.kotlingithubbooksample.util.emptyOptional
import com.tistory.ykyahwa.kotlingithubbooksample.util.optionalOf
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class SearchViewModel(val api: GithubApi, val searchHistoryDao: SearchHistoryDao) : ViewModel() {

    val searchResult: BehaviorSubject<SupportOptional<List<GithubRepo>>> = BehaviorSubject.createDefault(emptyOptional())

    val lastSearchKeyword: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()

    val message: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()

    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun searchRepository(query: String) : Disposable = api.searchRepository(query)
        .doOnNext { lastSearchKeyword.onNext(optionalOf(query)) }
        .flatMap {
            if (0 == it.totalCount) {
                Observable.error(IllegalStateException("No search Result"))
            } else {
                Observable.just(it.items)
            }
        }
        .doOnSubscribe {
            searchResult.onNext(emptyOptional())
            message.onNext(emptyOptional())
            isLoading.onNext(true)
        }
        .doOnTerminate { isLoading.onNext(false) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ items ->
            searchResult.onNext(optionalOf(items))
        }) {
            message.onNext(optionalOf(it.message ?: "Unexpected error"))
        }

    fun addToSearchHistory(repository: GithubRepo) : Disposable = runOnIoScheduler { searchHistoryDao.add(repository) }
}