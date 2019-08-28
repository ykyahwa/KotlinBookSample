package com.tistory.ykyahwa.kotlingithubbooksample.di.ui

import com.tistory.ykyahwa.kotlingithubbooksample.api.AuthApi
import com.tistory.ykyahwa.kotlingithubbooksample.data.AuthTokenProvider
import com.tistory.ykyahwa.kotlingithubbooksample.ui.signin.SignViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class SignInModule {

    @Provides
    fun provideViewModelFactory(authApi: AuthApi, authTokenProvider: AuthTokenProvider)
            : SignViewModelFactory = SignViewModelFactory(authApi, authTokenProvider)
}