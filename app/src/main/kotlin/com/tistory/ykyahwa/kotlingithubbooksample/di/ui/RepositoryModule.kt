package com.tistory.ykyahwa.kotlingithubbooksample.di.ui

import com.tistory.ykyahwa.kotlingithubbooksample.api.GithubApi
import com.tistory.ykyahwa.kotlingithubbooksample.ui.repo.RepositoryViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideViewModelFactory(githubApi: GithubApi): RepositoryViewModelFactory =
        RepositoryViewModelFactory(githubApi)

}