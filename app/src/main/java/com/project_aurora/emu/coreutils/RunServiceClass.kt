package com.project_aurora.emu.coreutils

import android.content.Context
import android.content.Intent

class RunServiceClass {
    private lateinit var ctx: Context
    private lateinit var service: Intent
    
    fun <T> runService(service: Class<T>, ctx: Context) {
        this.ctx = ctx
        this.service = Intent(ctx, service)
        ctx.startService(this.service)
    }

    fun stopAllServices() {
        ctx.stopService(this.service)
    }
}