package com.tistory.ykyahwa.kotlingithubbooksample.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.tistory.ykyahwa.kotlingithubbooksample.api.model.GithubRepo
import io.reactivex.Flowable

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(repo: GithubRepo)

    @Query("SELECT * FROM repositories")
    fun getHistory() : Flowable<List<GithubRepo>>

    @Query("DELETE FROM repositories")
    fun clearAll()
}