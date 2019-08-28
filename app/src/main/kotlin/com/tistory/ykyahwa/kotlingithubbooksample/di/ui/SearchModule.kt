package com.tistory.ykyahwa.kotlingithubbooksample.di.ui

import com.tistory.ykyahwa.kotlingithubbooksample.api.GithubApi
import com.tistory.ykyahwa.kotlingithubbooksample.db.SearchHistoryDao
import com.tistory.ykyahwa.kotlingithubbooksample.ui.search.SearchActivity
import com.tistory.ykyahwa.kotlingithubbooksample.ui.search.SearchAdapter
import com.tistory.ykyahwa.kotlingithubbooksample.ui.search.SearchViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class SearchModule {

    @Provides
    fun provideAdapter(activity: SearchActivity): SearchAdapter =
        SearchAdapter().apply { setItemClickListener(activity) }

    @Provides
    fun provideViewModelFactory(githubApi: GithubApi, searchHistoryDao: SearchHistoryDao): SearchViewModelFactory =
        SearchViewModelFactory(githubApi, searchHistoryDao)

}