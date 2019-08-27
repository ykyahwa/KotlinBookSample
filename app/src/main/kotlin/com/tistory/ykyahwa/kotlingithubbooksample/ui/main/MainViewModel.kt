package com.tistory.ykyahwa.kotlingithubbooksample.ui.main

import android.arch.lifecycle.ViewModel
import com.tistory.ykyahwa.kotlingithubbooksample.api.model.GithubRepo
import com.tistory.ykyahwa.kotlingithubbooksample.db.SearchHistoryDao
import com.tistory.ykyahwa.kotlingithubbooksample.extensions.runOnIoScheduler
import com.tistory.ykyahwa.kotlingithubbooksample.util.SupportOptional
import com.tistory.ykyahwa.kotlingithubbooksample.util.emptyOptional
import com.tistory.ykyahwa.kotlingithubbooksample.util.optionalOf
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MainViewModel(val searchHistoryDao: SearchHistoryDao) : ViewModel()  {

    val message : BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()
    fun clearSearchHistory(): Disposable = runOnIoScheduler { searchHistoryDao.clearAll() }

    val searchHistory: Flowable<SupportOptional<List<GithubRepo>>>
        get() = searchHistoryDao.getHistory()
            .map { optionalOf(it) }
            .doOnNext { optional ->
                if (optional.value.isEmpty()) {
                    message.onNext(optionalOf("No recent repositories."))
                }
                    message.onNext(emptyOptional())
            }
            .doOnError {
                message.onNext(optionalOf(it.message ?: "Unexpected error"))
            }
            .onErrorReturn { emptyOptional() }
}