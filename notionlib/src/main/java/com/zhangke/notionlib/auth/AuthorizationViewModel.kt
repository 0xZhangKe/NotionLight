package com.zhangke.notionlib.auth

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.R
import kotlinx.coroutines.*

class AuthorizationViewModel : ViewModel() {

    companion object {
        private const val TAG = "AuthorizationViewModel"
    }

    /**
     * 0-等待用户发起授权
     * 1-授权中
     * 2-授权成功
     * 3-授权失败
     */
    val authState = MutableLiveData(0)

    val authProccessInto = MutableLiveData<String?>()

    fun startAuth() {
        authState.value = 1
        authProccessInto.value = appContext.getString(R.string.notion_lib_auth_waiting_notion)
        viewModelScope.launch {
            delay(2000)
            authProccessInto.value = appContext.getString(R.string.notion_lib_auth_waiting_api)
            delay(2000)
            authState.value = 2
//            onAuthFailed("Mock")
        }
    }

    //    fun startAuth() {
//        authState.value = 1
//        authProccessInto.value = appContext.getString(R.string.notion_lib_auth_waiting_notion)
//        NotionAuthorization.startAuth()
//    }

    fun handleIntent(intent: Intent) {
        val data = intent.data
        val code = data?.getQueryParameter("code")
        if (data == null || code == null) {
            Log.d(TAG, "data is null")
            onAuthFailed("未获取到授权信息")
            return
        }

        authProccessInto.value = appContext.getString(R.string.notion_lib_auth_waiting_api)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val response = NotionRepo.requestOathToken(code)
                response.onSuccess {
                    NotionRepo.saveOauthToken(it)
                    authState.value = 2
                }
                response.onError {
                    onAuthFailed(it.message)
                }
            }
        }
    }

    private fun onAuthFailed(message: String) {
        authState.value = 3
        authProccessInto.value =
            appContext.getString(R.string.notion_lib_auth_failed, message)
    }
}