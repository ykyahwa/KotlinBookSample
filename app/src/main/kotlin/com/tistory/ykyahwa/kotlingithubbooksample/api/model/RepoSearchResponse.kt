package com.tistory.ykyahwa.kotlingithubbooksample.api.model

import com.google.gson.annotations.SerializedName

class RepoSearchResponse(
    @field:SerializedName("total_count") val totalCount: Int,
    val items: List<GithubRepo>
)
