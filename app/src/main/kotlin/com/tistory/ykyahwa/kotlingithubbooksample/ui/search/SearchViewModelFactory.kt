package com.tistory.ykyahwa.kotlingithubbooksample.ui.search

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.tistory.ykyahwa.kotlingithubbooksample.api.GithubApi
import com.tistory.ykyahwa.kotlingithubbooksample.db.SearchHistoryDao

class SearchViewModelFactory(val api: GithubApi, val searchHistoryDao: SearchHistoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SearchViewModel(api, searchHistoryDao) as T
    }
}