package com.themarkettheory.user.helper

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.themarkettheory.user.newmodels.login.Data


//import com.google.gson.Gson


class Prefs(context: Context) {
    val PREFS_FILENAME = context.packageName + ".prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    fun clear() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun setStringValue(key: String, value: String?) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringValue(key: String): String? {
        return prefs.getString(key, "")
    }

    fun setIntValue(key: String, value: Int) {
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getIntValue(key: String): Int {
        return prefs.getInt(key, 0)
    }

    fun setBooleanValue(key: String, value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBooleanValue(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun setToken(context: Context, token: String) {
        val prefs: SharedPreferences = context.getSharedPreferences("firebase", 0)
        val editor = prefs.edit()
        editor.putString(Constants.fcm_token, token)
        editor.apply()
    }

    fun getToken(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences("firebase", 0)
        return prefs.getString(Constants.fcm_token, "")!!
    }

    fun setAccessToken(context: Context, token: String) {
        val prefs: SharedPreferences = context.getSharedPreferences("accessToken", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(Constants.accessToken, token)
        editor.apply()
    }

    fun getAccessToken(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences("accessToken", MODE_PRIVATE)
        return prefs.getString(Constants.accessToken, "")!!
    }

    fun setRandomString(context: Context, token: String) {
        val prefs: SharedPreferences = context.getSharedPreferences("random", 0)
        val editor = prefs.edit()
        editor.putString(Constants.RANDOM, token)
        editor.apply()
    }

    fun getRandomString(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences("random", 0)
        return prefs.getString(Constants.RANDOM, "")!!
    }

    fun setLoginModel(obj: Data) {
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(obj)
        editor.putString(Constants.login_model, json)
        editor.apply()
    }

    fun getLoginModel(): Data {
        val gson = Gson()
        val json = prefs.getString(Constants.login_model, "")
        if (json.isNullOrEmpty())
            return Data()
        else
            return gson.fromJson<Data>(json, Data::class.java)
    }

}