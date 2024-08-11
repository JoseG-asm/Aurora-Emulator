package com.project_aurora.emu.ui.main

import android.content.Context
import android.os.Build
import android.content.res.Configuration
import android.view.Window
import com.project_aurora.emu.R

class ThemeProvider(private val context: Context) {
    fun defineStatusBarColor(window: Window) {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when(nightModeFlags) {
            Configuration.UI_MODE_NIGHT_NO -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.statusBarColor = context.getColor(R.color.aurora_background)
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> { //Night Mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.statusBarColor = context.getColor(R.color.aurora_secondary)
                }
            }
        }
    } 
    
    fun isNightMode() : Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) true else false
    }
}
