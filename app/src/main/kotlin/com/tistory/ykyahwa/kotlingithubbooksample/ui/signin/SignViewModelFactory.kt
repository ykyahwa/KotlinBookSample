package com.tistory.ykyahwa.kotlingithubbooksample.ui.signin

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.tistory.ykyahwa.kotlingithubbooksample.api.AuthApi
import com.tistory.ykyahwa.kotlingithubbooksample.data.AuthTokenProvider

class SignViewModelFactory(val api: AuthApi, val authTokenProvider: AuthTokenProvider): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SignViewModel(api, authTokenProvider) as T
    }
}