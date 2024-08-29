package com.project_aurora.emu.core

import com.project_aurora.emu.coreutils.ShellLoader
import com.project_aurora.emu.coreutils.EnvVars
import com.project_aurora.emu.viewmodel.MainViewModel

class WineContext {
    private val shellProcess = ShellLoader()
    
    fun wine(arg: String) {
        shellProcess.apply {
            newProcess("wine",
                EnvVars().apply {
                    setEnvVariables()
                }.exportVariables + "; " +
                "chmod 755 -R ${MainViewModel.box64}; " +
                "chmod 755 -R ${MainViewModel.wine}; " +
                "${MainViewModel.box64} ${MainViewModel.wine} $arg"
            )    
            executeProcessByName("wine")
        }
    }
}
