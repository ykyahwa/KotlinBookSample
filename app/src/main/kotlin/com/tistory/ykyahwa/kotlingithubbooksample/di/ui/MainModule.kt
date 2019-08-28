package com.tistory.ykyahwa.kotlingithubbooksample.di.ui

import com.tistory.ykyahwa.kotlingithubbooksample.db.SearchHistoryDao
import com.tistory.ykyahwa.kotlingithubbooksample.ui.main.MainActivity
import com.tistory.ykyahwa.kotlingithubbooksample.ui.main.MainViewModelFactory
import com.tistory.ykyahwa.kotlingithubbooksample.ui.search.SearchAdapter
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides
    fun providesAdapter(activity: MainActivity): SearchAdapter =
        SearchAdapter().apply { setItemClickListener(activity) }

    @Provides
    fun provideViewModelFactory(searchHistoryDao: SearchHistoryDao): MainViewModelFactory =
        MainViewModelFactory(searchHistoryDao)

}