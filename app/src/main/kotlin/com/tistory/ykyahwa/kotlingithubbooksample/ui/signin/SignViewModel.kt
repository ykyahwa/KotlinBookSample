package com.tistory.ykyahwa.kotlingithubbooksample.ui.signin

import android.arch.lifecycle.ViewModel
import com.tistory.ykyahwa.kotlingithubbooksample.api.AuthApi
import com.tistory.ykyahwa.kotlingithubbooksample.data.AuthTokenProvider
import com.tistory.ykyahwa.kotlingithubbooksample.util.SupportOptional
import com.tistory.ykyahwa.kotlingithubbooksample.util.optionalOf
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class SignViewModel(val api: AuthApi, val authTokenProvider: AuthTokenProvider) : ViewModel() {

    val accessToken : BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()
    val message: PublishSubject<String> = PublishSubject.create()
    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun loadAccessToken(): Disposable = Single.fromCallable { optionalOf(authTokenProvider.token) }
        .subscribeOn(Schedulers.io())
        .subscribe(Consumer<SupportOptional<String>> { accessToken.onNext(it) })

    fun requestAccessToken(clientId: String, clientSecret: String, code: String) : Disposable =
        api.getAccessToken(clientId, clientSecret, code)
            .map { it.accessToken }
            .doOnSubscribe { isLoading.onNext(true) }
            .doOnTerminate { isLoading.onNext(false) }
            .subscribe({ token ->
                authTokenProvider.updateToken(token)
                accessToken.onNext(optionalOf(token))

            }) {
                message.onNext(it.message ?: "Unexpected error")
            }
}