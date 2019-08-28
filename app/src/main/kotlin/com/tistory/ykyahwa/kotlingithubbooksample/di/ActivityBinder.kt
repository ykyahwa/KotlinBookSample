package com.tistory.ykyahwa.kotlingithubbooksample.di

import com.tistory.ykyahwa.kotlingithubbooksample.di.ui.MainModule
import com.tistory.ykyahwa.kotlingithubbooksample.di.ui.RepositoryModule
import com.tistory.ykyahwa.kotlingithubbooksample.di.ui.SearchModule
import com.tistory.ykyahwa.kotlingithubbooksample.di.ui.SignInModule
import com.tistory.ykyahwa.kotlingithubbooksample.ui.main.MainActivity
import com.tistory.ykyahwa.kotlingithubbooksample.ui.repo.RepositoryActivity
import com.tistory.ykyahwa.kotlingithubbooksample.ui.search.SearchActivity
import com.tistory.ykyahwa.kotlingithubbooksample.ui.signin.SignInActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBinder {

    @ContributesAndroidInjector(modules = [SignInModule::class])
    abstract fun bindSignInActivity(): SignInActivity

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [SearchModule::class])
    abstract fun bindSearchActivity() : SearchActivity

    @ContributesAndroidInjector(modules = [RepositoryModule::class])
    abstract fun bindRepositoryActivity() : RepositoryActivity
}