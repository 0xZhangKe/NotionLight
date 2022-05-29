package com.zhangke.notiontodo.auth

import android.content.Intent
import android.net.Uri
import android.view.Gravity
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notiontodo.R
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

    val inputError = MutableLiveData(false)

    fun startAuth() {
        authState.value = 1
        authProcessInfo.value = appContext.getString(R.string.auth_waiting_notion)
        NotionAuthorization.startAuth()
    }

    fun handleIntent(intent: Intent) {
        val data = intent.data
        val code = data?.pickCode()
        if (data == null || code == null) {
            return
        }
        processResponse(code)
    }

    private fun Uri.pickCode(): String? {
        return getQueryParameter("code")
    }

    private fun processResponse(code: String) {
        authProcessInfo.value = appContext.getString(R.string.auth_waiting_api)
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

    fun onInputtedText(text: String): Boolean {
        val uri = text.toUri()
        val code = uri.pickCode()
        if (code.isNullOrEmpty()) {
            toast(R.string.auth_manually_input_error, gravity = Gravity.TOP)
            inputError.value = true
            return false
        }
        authState.value = 1
        inputError.value = false
        processResponse(code)
        return true
    }

    private fun onAuthFailed(message: String) {
        authState.value = 2
        authProcessInfo.value =
            appContext.getString(R.string.auth_failed, message)
    }
}