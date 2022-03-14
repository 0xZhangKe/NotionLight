package com.zhangke.notionlib.data

import com.google.gson.annotations.SerializedName

data class Owner(
    val type: String,
    val workspace: Boolean = true,
    val user: User
)

data class User(

    @SerializedName("object")
    val objectType: String,

    val id: String,

    val type: String?,

    val name: String?,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    val person: Person?,
)

data class Person(val email: String?)