package com.project_aurora.emu.coreutils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import android.util.Log

class ShellLoader { 
    inner class Process(
        var name: String,
        var cmd:  String
    ) {
        private val standardOutput = StringBuilder()
        private val standardOutputError = StringBuilder()
        
        val executeProcess: () -> Unit = {
            executeShellProcess(cmd)
        }
        
        private fun executeShellProcess(cmd: String) {
            try {
                val shellProcess = Runtime.getRuntime().exec("/system/bin/sh")
                val os = DataOutputStream(shellProcess.outputStream)
                
                os.writeBytes("$cmd\n")
                os.flush()
                

                val stdout = BufferedReader(InputStreamReader(shellProcess.inputStream))
                val stderr = BufferedReader(InputStreamReader(shellProcess.errorStream))

                val stdoutThread = Thread {
                    try {
                        var out: String?
                        while (stdout.readLine().also { out = it } != null) {
                            synchronized(standardOutput) {
                                Log.d("ShellLoader", "stdout: $out")
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
                        var s: String?
                        while (stderr.readLine().also { s = it } != null) {
                            synchronized(standardOutputError) {
                                Log.d("ShellLoader", "stderr: $s")
                                standardOutputError.append(s).append("\n")
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
    
    fun newProcess(name: String, cmd: String) {
        mProcessTree[name] = Process(name, cmd)
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