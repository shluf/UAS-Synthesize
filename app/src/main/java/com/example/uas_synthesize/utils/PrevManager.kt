package com.example.uas_synthesize.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,
        Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_FILENAME = "AuthAppPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_IS_GUEST = "isGuest"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ID = "userId"
        private const val KEY_NAME = "name"
        private const val KEY_AVATAR = "avatar"
        private const val KEY_BIO = "bio"
        @Volatile
        private var instance: PrefManager? = null
        fun getInstance(context: Context): PrefManager {
            return instance ?: synchronized(this) {
                instance ?: PrefManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    fun setGuest(isGuest: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_GUEST, isGuest)
        editor.apply()
    }
    fun isGuest(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false)
    }
    fun saveUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }
    fun saveUserId(password: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, password)
        editor.apply()
    }
    fun saveAvatar(avatar: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_AVATAR, avatar)
        editor.apply()
    }
    fun saveProfile(name: String, avatar: String, bio: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_AVATAR, avatar)
        editor.putString(KEY_BIO, bio)
        editor.apply()
    }
    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }
    fun getUserId(): String {
        return sharedPreferences.getString(KEY_USER_ID, "") ?: ""
    }
    fun getName(): String {
        return sharedPreferences.getString(KEY_NAME, "") ?: ""
    }
    fun getAvatar(): String {
        return sharedPreferences.getString(KEY_AVATAR, "") ?: ""
    }
    fun getBio(): String {
        return sharedPreferences.getString(KEY_BIO, "") ?: ""
    }
    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}