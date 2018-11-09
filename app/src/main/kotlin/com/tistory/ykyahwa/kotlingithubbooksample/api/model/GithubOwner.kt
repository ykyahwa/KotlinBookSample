package com.tistory.ykyahwa.kotlingithubbooksample.api.model

import com.google.gson.annotations.SerializedName

class GithubOwner(
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String
)
