package com.zhangke.framework.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


suspend fun DataStore<Preferences>.putString(key: String, value: String) {
    val preferenceKey = stringPreferencesKey(key)
    edit {
        it[preferenceKey] = value
    }
}

suspend fun DataStore<Preferences>.getString(key: String): String? {
    val preferenceKey = stringPreferencesKey(key)
    return data.firstOrNull()?.get(preferenceKey)
}