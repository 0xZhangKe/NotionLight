package com.zhangke.notiontodo.setting

import androidx.lifecycle.ViewModel
import com.zhangke.notionlib.auth.NotionAuthorization

class SettingViewModel: ViewModel() {

    val userInfo = NotionAuthorization.getOauthToken()

}