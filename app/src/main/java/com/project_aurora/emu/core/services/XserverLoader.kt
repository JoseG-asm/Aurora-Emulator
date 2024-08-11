/*
 * Copyright (c) 2024 Projeto Aurora App
 * Copyright (c) 2024 José G.
 *
 * Todos os direitos reservados.
 * Sob os termos da licença MIT.
 */

package com.project_aurora.emu.core.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import com.project_aurora.emu.coreutils.EnvVars
import com.project_aurora.emu.coreutils.ShellLoader
import com.project_aurora.emu.ui.main.MainActivity
import com.project_aurora.emu.CmdEntryPoint
import com.project_aurora.emu.coreutils.AsyncTask
import com.project_aurora.emu.coreutils.DispatchersType

class XserverLoader : Service() {
      
    private val vars = EnvVars()
    private val shellProcess = ShellLoader()
    private val main = MainActivity()
    private val tasker = AsyncTask()
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            tasker.newCoroutine("Xserver", {
                 vars.setEnvVariables()
                 shellProcess.newProcess("X11", cmd =  vars.exportVariables + ";" +
                        "mkdir -p /data/data/com.project_aurora.emu/files/usr/tmp; mkdir -p /data/data/com.project_aurora.emu/files/home; chmod 700 -R /data/data/com.project_aurora.emu/files/usr;" +
                        "/data/data/com.project_aurora.emu/files/usr/generateSymlinks.sh; " + "chmod 755 -R /data/data/com.project_aurora.emu/files/usr/bin/box64" +
                        "export CLASSPATH=" + main.getClassPath(this@XserverLoader) + ";" +  "/system/bin/app_process -Xnoimage-dex2oat / com.project_aurora.emu.CmdEntryPoint :0" + "box64 wine explorer /desktop=shell,1280x720 explorer")
                 shellProcess.executeProcessByName("X11")
            }, DispatchersType.DEFAULT) 
            
            tasker.executeCoroutine("Xserver")
        return START_STICKY
    }
    
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}