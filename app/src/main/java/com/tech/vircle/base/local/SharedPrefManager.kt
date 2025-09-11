package com.tech.vircle.base.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.tech.vircle.data.model.ProfileUser
import com.tech.vircle.data.model.UserRegistrationData
import javax.inject.Inject

class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    object KEY {
        const val IS_FIRST = "is_first"
        const val USER_DATA = "user_data"
        const val PROFILE_DATA = "profile_data"
        const val TOKEN = "token"
        const val DARK_MODE = "dark_mode"
    }

    private val gson = Gson()

    // ---------------------- User Data ---------------------- //
    fun setLoginData(data: ProfileUser) {
        sharedPreferences.edit().putString(KEY.USER_DATA, gson.toJson(data)).apply()
    }

    fun getLoginData(): ProfileUser? {
        val json = sharedPreferences.getString(KEY.USER_DATA, null)
        return json?.let { gson.fromJson(it, ProfileUser::class.java) }
    }
    // ---------------------- Profile Data ---------------------- //
    fun setProfileData(data: ProfileUser?) {
        sharedPreferences.edit().putString(KEY.PROFILE_DATA, gson.toJson(data)).apply()
    }

    fun getProfileData(): ProfileUser? {
        val json = sharedPreferences.getString(KEY.PROFILE_DATA, null)
        return json?.let { gson.fromJson(it, ProfileUser::class.java) }
    }


    // ---------------------- Token ---------------------- //
    fun setToken(token: String?) {
        sharedPreferences.edit().putString(KEY.TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY.TOKEN, null)
    }

    // ---------------------- Dark Mode ---------------------- //
    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY.DARK_MODE, enabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY.DARK_MODE, false)
    }

    // ---------------------- Clear ---------------------- //
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}