package com.tistory.ykyahwa.kotlingithubbooksample.ui.repo

import android.arch.lifecycle.ViewModel
import com.tistory.ykyahwa.kotlingithubbooksample.api.GithubApi
import com.tistory.ykyahwa.kotlingithubbooksample.api.model.GithubRepo
import com.tistory.ykyahwa.kotlingithubbooksample.util.SupportOptional
import com.tistory.ykyahwa.kotlingithubbooksample.util.optionalOf
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class RepositoryViewModel(val api: GithubApi) : ViewModel() {

    val repository: BehaviorSubject<SupportOptional<GithubRepo>> = BehaviorSubject.create()

    val message: BehaviorSubject<String> = BehaviorSubject.create()

    val isContentVisible: BehaviorSubject<Boolean> = BehaviorSubject.create()

    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun requestRepositoryInfo(login: String, repoName: String) : Disposable {
        val repoObservable = if (!repository.hasValue()) {
            api.getRepository(login, repoName)
        } else {
            Observable.empty()
        }

        return repoObservable
            .doOnSubscribe { isLoading.onNext(true) }
            .doOnTerminate { isLoading.onNext(false) }
            .subscribeOn(Schedulers.io())
            .subscribe({repo ->
                repository.onNext(optionalOf(repo))
                isContentVisible.onNext(true)
            }){
                message.onNext(it.message?:"Unexpected error")
            }
    }
}