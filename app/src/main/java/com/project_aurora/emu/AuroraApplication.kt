package com.project_aurora.emu

import android.app.Application

class AuroraApplication : Application() {

    companion object {
        @Volatile
        lateinit var instance: AuroraApplication
            private set
    }
    
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}