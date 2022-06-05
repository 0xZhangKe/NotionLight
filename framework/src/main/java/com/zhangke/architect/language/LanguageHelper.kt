package com.zhangke.architect.language

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.zhangke.architect.coroutines.ApplicationScope
import com.zhangke.architect.datastore.dataStore
import com.zhangke.architect.datastore.getInt
import com.zhangke.architect.datastore.putInt
import com.zhangke.architect.language.LanguageHelper.systemLocale
import com.zhangke.framework.utils.OnActivityCreated
import com.zhangke.framework.utils.SimpleActivityLifecycle
import com.zhangke.framework.utils.appContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import java.util.*

object LanguageHelper {

    private const val LANGUAGE_SETTING = "app_language_setting"

    lateinit var currentType: LanguageSettingType
        private set

    private val pausedActivityList = mutableListOf<WeakReference<Activity>>()

    lateinit var systemLocale: Locale

    private lateinit var application: Application

    fun prepare(application: Application) {
        this.application = application
        systemLocale = Locale.getDefault()
        currentType = readLocalFromStorage() ?: LanguageSettingType.SYSTEM
        setupLanguage(application)
        application.registerActivityLifecycleCallbacks(OnActivityCreated {
            setupLanguage(it)
        })

        application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycle() {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                setupLanguage(activity)
                super.onActivityCreated(activity, savedInstanceState)
            }

            override fun onActivityPaused(activity: Activity) {
                val savedValue = pausedActivityList.findLast { it.get() == activity }
                if (savedValue == null) {
                    pausedActivityList += WeakReference(activity)
                }
                super.onActivityPaused(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                pausedActivityList.removeAll {
                    it.get() == activity
                }
                super.onActivityDestroyed(activity)
            }
        })
    }

    private fun setupLanguage(context: Context) {
        setLanguage(context, currentType, false)
    }

    fun setLanguage(context: Context, type: LanguageSettingType, needSave: Boolean = true) {
        this.currentType = type
        val resources = context.resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(type.toLocale())
        resources.updateConfiguration(configuration, metrics)
        if (needSave) {
            saveLocalToStorage(type)
        }
        if (context != application) {
            setLanguage(application, currentType, false)
            pausedActivityList.mapNotNull { it.get() }
                .forEach { it.recreate() }
        }
    }

    private fun saveLocalToStorage(type: LanguageSettingType) {
        ApplicationScope.launch {
            appContext.dataStore.putInt(LANGUAGE_SETTING, type.value)
        }
    }

    private fun readLocalFromStorage(): LanguageSettingType? {
        return runBlocking {
            when (appContext.dataStore.getInt(LANGUAGE_SETTING)) {
                LanguageSettingType.CN.value -> LanguageSettingType.CN
                LanguageSettingType.EN.value -> LanguageSettingType.EN
                LanguageSettingType.SYSTEM.value -> LanguageSettingType.SYSTEM
                else -> null
            }
        }
    }
}

enum class LanguageSettingType(val value: Int) {

    CN(1),
    EN(2),
    SYSTEM(3),

    ;

    fun toLocale(): Locale {
        return when (this) {
            CN -> Locale.SIMPLIFIED_CHINESE
            EN -> Locale.ENGLISH
            SYSTEM -> systemLocale
        }
    }
}