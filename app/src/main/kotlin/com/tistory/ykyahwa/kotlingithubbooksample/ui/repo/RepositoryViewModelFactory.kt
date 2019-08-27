package com.tistory.ykyahwa.kotlingithubbooksample.ui.repo

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.tistory.ykyahwa.kotlingithubbooksample.api.GithubApi

class RepositoryViewModelFactory(val api: GithubApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RepositoryViewModel(api) as T
    }

}