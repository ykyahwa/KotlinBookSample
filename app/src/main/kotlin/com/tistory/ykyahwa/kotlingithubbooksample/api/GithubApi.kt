package com.tistory.ykyahwa.kotlingithubbooksample.api

import com.tistory.ykyahwa.kotlingithubbooksample.api.model.GithubRepo
import com.tistory.ykyahwa.kotlingithubbooksample.api.model.RepoSearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("search/repositories")
    fun searchRepository(@Query("q") query: String): Observable<RepoSearchResponse>

    @GET("repos/{owner}/{name}")
    fun getRepository(@Path("owner") ownerLogin: String, @Path("name") repoName: String): Observable<GithubRepo>
}
