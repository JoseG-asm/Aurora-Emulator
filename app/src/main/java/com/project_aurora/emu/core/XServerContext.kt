/*
 * Copyright (c) 2024 Projeto Aurora
 * Copyright (c) 2024 José G.
 * 
 * Todos os direitos reservados.
 * Sob os termos da licença MIT.
 */

package com.project_aurora.emu.core

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import com.project_aurora.emu.coreutils.EnvVars
import com.project_aurora.emu.ui.main.MainActivity

class XserverLoader : Service() {
      
    private val vars = EnvVars()
    private val main = MainActivity()
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            vars.setEnvVariables()
            executeCommand(
                    vars.exportVariables + ";" +
                        "unset LD_LIBRARY_PATH LIBGL_DRIVERS_PATH;" + "mkdir -p /data/data/com.micewine.emu/files/usr/tmp; mkdir -p /data/data/com.micewine.emu/files/home; chmod 700 -R /data/data/com.micewine.emu/files/usr;" +
                        "export CLASSPATH=" + main.getClassPath(this) + ";" +
                        "/system/bin/app_process -Xnoimage-dex2oat / com.micewine.emu.CmdEntryPoint :0"
            )
        }.start()
        return START_STICKY
    }

    private fun executeCommand(cmd: String) {
        try {
            Log.v("Xserver", "Trying to exec: $cmd")
            val shell = Runtime.getRuntime().exec("/system/bin/sh")
            val os = DataOutputStream(shell.outputStream)

            os.writeBytes("$cmd\n exit\n")
            os.flush()

            val stdout = BufferedReader(InputStreamReader(shell.inputStream))
            val stderr = BufferedReader(InputStreamReader(shell.errorStream))
            Thread {
                try {
                    var out: String?
                    while (stdout.readLine().also { out = it } != null) {
                        Log.v("Xserver stdout: ", out ?: "")
                    }
                } catch (ignored: Exception) {
                }
            }.start()
            try {
                var err: String?
                while (stderr.readLine().also { err = it } != null) {
                    Log.e("Error to execute Xserver: ", err ?: "")
                }
            } catch (ignored: Exception) {
            }
            shell.destroy()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}