package com.zhangke.notionlib.auth

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.appContext
import com.zhangke.notionlib.R
import kotlinx.coroutines.launch

class AuthorizationViewModel : ViewModel() {

    /**
     * 0-等待用户发起授权
     * 1-授权中
     * 2-授权失败
     */
    val authState = MutableLiveData(0)

    val authSuccess = MutableLiveData(false)

    val authProcessInfo = MutableLiveData<String?>()

    fun startAuth() {
        authState.value = 1
        authProcessInfo.value = appContext.getString(R.string.notion_lib_auth_waiting_notion)
        NotionAuthorization.startAuth()
    }

    fun handleIntent(intent: Intent) {
        val data = intent.data
        val code = data?.getQueryParameter("code")
        if (data == null || code == null) {
            return
        }

        authProcessInfo.value = appContext.getString(R.string.notion_lib_auth_waiting_api)

        viewModelScope.launch {
            NotionAuthorization.startRequestOauthToken(code) { errorMessage ->
                if (errorMessage.isNullOrEmpty()) {
                    authSuccess.value = true
                } else {
                    onAuthFailed(errorMessage)
                }
            }
        }
    }

    private fun onAuthFailed(message: String) {
        authState.value = 2
        authProcessInfo.value =
            appContext.getString(R.string.notion_lib_auth_failed, message)
    }
}