package com.project_aurora.emu.core;

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*

import com.project_aurora.emu.coreutils.EnvVars
import com.project_aurora.emu.coreutils.ShellLoader
import com.project_aurora.emu.ui.main.MainActivity
import com.project_aurora.emu.CmdEntryPoint
import com.project_aurora.emu.coreutils.AsyncTask
import com.project_aurora.emu.coreutils.DispatchersType
import com.project_aurora.emu.viewmodel.MainViewModel

import androidx.lifecycle.ViewModelStoreOwner


/**
* @author Jose G.
*
*/
class Init() {
    private val main = MainActivity()
    fun newClientXserver(context: Context, socket: String = ":0") {
        Thread {
        if (true) { 
                ShellLoader().apply {
                    newProcess("xserver", 
                        "mkdir -p /data/data/com.project_aurora.emu/files/usr/tmp; mkdir -p /data/data/com.project_aurora.emu/files/home; chmod 700 -R /data/data/com.project_aurora.emu/files/usr;" +
                        "export CLASSPATH=" + main.getClassPath(context) + "; " +
                        "/system/bin/app_process -Xnoimage-dex2oat / com.project_aurora.emu.CmdEntryPoint $socket", 
                        context as ViewModelStoreOwner
                    )
                }
        } else {
           
        }
        }.start()
    }

    fun stop() {
    // TODO
    }       
}    