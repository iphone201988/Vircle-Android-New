package com.tech.vircle

import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import com.tech.vircle.base.AppLifecycleListener
import dagger.hilt.android.HiltAndroidApp
import android.app.Activity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp

@HiltAndroidApp
class App : Application() ,Application.ActivityLifecycleCallbacks {
    private var isRestarting: Boolean = false

    fun onLogout() {
        restartApp()
    }

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        applyThemeBasedOnDeviceSettings()
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener(this@App))
        registerActivityLifecycleCallbacks(this)
    }




    private fun restartApp() {
        if (!isRestarting) {
            isRestarting = true
            val intent =
                baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Update the theme dynamically when the system theme changes
        applyThemeBasedOnDeviceSettings()
    }


    private fun applyThemeBasedOnDeviceSettings() {
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val mode = when (nightMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }


    fun setDarkMode(enabled: Boolean) {
        val mode = if (enabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }



    fun Activity.updateStatusBarColors() {
        val window = this.window
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                // Dark mode → dark bg + white icons
                window.statusBarColor = ContextCompat.getColor(this, R.color.light_mode)
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                // Light mode → light bg + black icons
                window.statusBarColor = ContextCompat.getColor(this, R.color.light_mode)
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.updateStatusBarColors()
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        activity.updateStatusBarColors()
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

}
