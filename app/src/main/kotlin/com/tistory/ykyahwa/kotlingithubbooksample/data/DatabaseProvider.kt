package com.tistory.ykyahwa.kotlingithubbooksample.data

import android.arch.persistence.room.Room
import android.content.Context
import com.tistory.ykyahwa.kotlingithubbooksample.db.SearchHistoryDao

private var instance: SimpleGithubDatabase? = null
fun provideSearchHistoryDao(context: Context) :SearchHistoryDao = provideDatabase(context).searchHistoryDao()

private fun provideDatabase(context: Context) : SimpleGithubDatabase {
    if (null == instance)
        instance = Room.databaseBuilder(context.applicationContext, SimpleGithubDatabase::class.java, "simple_github.db")
            .build()

    return instance!!
}