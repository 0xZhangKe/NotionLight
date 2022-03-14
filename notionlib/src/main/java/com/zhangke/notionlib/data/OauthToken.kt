package com.zhangke.notionlib.data

import com.google.gson.annotations.SerializedName

/**
 * Created by ZhangKe on 2022/3/13.
 */
data class OauthToken(

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("workspace_id")
    val workspaceId: String,

    @SerializedName("workspace_name")
    val workspaceName: String?,

    @SerializedName("workspace_icon")
    val workspaceIcon: String?,

    @SerializedName("bot_id")
    val botId: String,

    val owner: Owner,
)
