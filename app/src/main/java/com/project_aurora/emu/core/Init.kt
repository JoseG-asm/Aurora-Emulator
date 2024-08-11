package com.project_aurora.emu.core;

import android.content.Context
import com.project_aurora.emu.coreutils.RunServiceClass
import com.project_aurora.emu.core.services.XserverLoader
import android.util.Log
import com.project_aurora.emu.ui.main.MainActivity
import com.project_aurora.emu.coreutils.EnvVars
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

class Init {
    private val runServices = RunServiceClass()
    private lateinit var ctx: Context
    private val vars = EnvVars()
    
    fun newClientXserver(callCtx: Context) {
        ctx = callCtx
        runServices.runService(XserverLoader::class.java, ctx)
    }

    fun stop() {
        runServices.stopAllServices()
    }       
}