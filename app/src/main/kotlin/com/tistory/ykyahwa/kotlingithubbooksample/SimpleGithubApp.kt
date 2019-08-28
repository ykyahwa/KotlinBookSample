package com.tistory.ykyahwa.kotlingithubbooksample

import com.tistory.ykyahwa.kotlingithubbooksample.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class SimpleGithubApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
}