package com.tistory.ykyahwa.kotlingithubbooksample.di

import android.app.Application
import com.tistory.ykyahwa.kotlingithubbooksample.SimpleGithubApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, LocalDataModule::class, ApiModule::class, NetworkModule::class, AndroidSupportInjectionModule::class, ActivityBinder:: class])
interface AppComponent : AndroidInjector<SimpleGithubApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application) : Builder

        fun build(): AppComponent
    }
}