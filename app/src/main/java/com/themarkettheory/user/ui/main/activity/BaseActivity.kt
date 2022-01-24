package com.themarkettheory.user.ui.main.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.helper.Prefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var myRoomDatabase: MyRoomDatabase

    lateinit var prefs: Prefs

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 0.85f
        // applyOverrideConfiguration(newOverride)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*adjustFontScale(resources.configuration)*/
        prefs = Prefs(this)
    }

    fun setStatusBarColor(color: Int) {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

/*    open fun adjustFontScale(configuration: Configuration) {
        if (configuration.fontScale > 1.30) {
            configuration.fontScale = 1.30f
            val metrics = resources.displayMetrics
            *//*val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)*//*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this.display?.getRealMetrics(DisplayMetrics())
            } else {
                @Suppress("DEPRECATION")
                this.windowManager.defaultDisplay.getMetrics(DisplayMetrics())
            }
            metrics.scaledDensity = configuration.fontScale * metrics.density
            baseContext.resources.updateConfiguration(configuration, metrics)
        }
    }*/
}