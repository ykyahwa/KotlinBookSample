package com.tistory.ykyahwa.kotlingithubbooksample.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.tistory.ykyahwa.kotlingithubbooksample.api.model.GithubRepo
import com.tistory.ykyahwa.kotlingithubbooksample.db.SearchHistoryDao

@Database(entities = arrayOf(GithubRepo::class), version = 1)
abstract class SimpleGithubDatabase: RoomDatabase() {

    abstract fun searchHistoryDao() : SearchHistoryDao
}