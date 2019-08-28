package com.tistory.ykyahwa.kotlingithubbooksample.di

import android.arch.persistence.room.Room
import android.content.Context
import com.tistory.ykyahwa.kotlingithubbooksample.data.AuthTokenProvider
import com.tistory.ykyahwa.kotlingithubbooksample.data.SimpleGithubDatabase
import com.tistory.ykyahwa.kotlingithubbooksample.db.SearchHistoryDao
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class LocalDataModule {

    @Provides
    @Singleton
    fun provideAuthTokenProvider(@Named("appContext") context: Context): AuthTokenProvider = AuthTokenProvider(context)

    @Provides
    @Singleton
    fun provideSearchHistoryDao(db: SimpleGithubDatabase) : SearchHistoryDao = db.searchHistoryDao()

    @Provides
    @Singleton
    fun provideDatabase(@Named("appContext") context: Context) : SimpleGithubDatabase =
        Room.databaseBuilder(context, SimpleGithubDatabase::class.java, "simple_github.db")
            .build()

}