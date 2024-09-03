package com.project_aurora.emu.coreutils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import android.util.Log
import android.content.Context
import com.project_aurora.emu.viewmodel.LogViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.ViewModelProvider

class ShellLoader { 
    inner class Process(
        var name: String,
        var cmd: String,
        var owner: ViewModelStoreOwner
    ) {
        private val standardOutput = StringBuilder()
        private val standardOutputError = StringBuilder()
        private var log = ViewModelProvider(owner).get(LogViewModel::class.java)
        
        val executeProcess: () -> Unit = {
            executeShellProcess(cmd)
        }
        
        private fun executeShellProcess(cmd: String) {
            try {
                Log.e("Trying to exec", cmd)
                val shellProcess = Runtime.getRuntime().exec("/system/bin/sh")
                val os = DataOutputStream(shellProcess.outputStream)
                
                os.writeBytes("$cmd\nexit\n")
                os.flush()
                
                val stdout = BufferedReader(InputStreamReader(shellProcess.inputStream))
                val stderr = BufferedReader(InputStreamReader(shellProcess.errorStream))

                val stdoutThread = Thread {
                    try {
                        var out: String?
                        while (stdout.readLine().also { out = it } != null) {
                            synchronized(standardOutput) {
                                Log.w("Xserver output thread:", "$out \n")
                                log.bindMessages("out: $out \n")
                                standardOutput.append(out).append("\n")
                            }
                        }
                    } catch (ignored: Exception) {
                        ignored.printStackTrace()
                    }
                }
                stdoutThread.start()

                val stderrThread = Thread {
                    try {
                        var err: String?
                        while (stderr.readLine().also { err = it } != null) {
                            synchronized(standardOutputError) {
                                Log.e("Xserver err thread:", "$err \n")
                                log.bindMessages("err: $err \n")
                                standardOutputError.append(err).append("\n")
                            }
                        }
                    } catch (ignored: Exception) {
                        ignored.printStackTrace()
                    }
                }
                stderrThread.start()

                stdoutThread.join()
                stderrThread.join()

                shellProcess.waitFor()
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }
        
        fun getStandardOutput(): String {
            return standardOutput.toString()
        }
        
        fun getStandardOutputErr() : String {
            return standardOutputError.toString()
        }
    }
    
    private var mProcessTree: MutableMap<String, Process> = mutableMapOf()
    
    fun newProcess(name: String, cmd: String, owner: ViewModelStoreOwner) {
        mProcessTree[name] = Process(name, cmd, owner)
    }
    
    fun executeProcessByName(name: String) {
        val process = mProcessTree[name]
        process?.executeProcess?.invoke()
    }
    
    val getOutputProcessByName: (String) -> String = { name ->
        val process = mProcessTree[name]
        process?.getStandardOutput() ?: "null"
    }
    
    val getOutputErrProcessByName: (String) -> String = { name ->
        val process = mProcessTree[name] 
        process?.getStandardOutputErr() ?: "null"
    }
}