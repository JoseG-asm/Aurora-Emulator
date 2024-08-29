package com.project_aurora.emu.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LifecycleOwner
import android.content.Intent
import android.content.Context
import java.io.File
import android.view.View
import com.google.android.material.navigation.NavigationBarView
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController

import com.project_aurora.emu.databinding.ActivityMainBinding
import com.project_aurora.emu.coreutils.ZipFileExtractor
import com.project_aurora.emu.coreutils.AsyncTask
import com.project_aurora.emu.coreutils.DispatchersType

class MainViewModel : ViewModel() {
    private val asyncTasker = AsyncTask()
    companion object FilePath {
        public val appRootDir = "/data/data/com.project_aurora.emu/files"
        public val usrDir = File("$appRootDir/usr").apply { mkdirs() }
        public val wineFolder = File("$appRootDir/wine")
        public val homeDir = File("$appRootDir/files/home").apply { mkdirs() }
        public val tmpDir = File("$usrDir/tmp").apply { mkdirs() }
        public val wineUtilsFolder = File("$appRootDir/wine-utils")
        public val wine = File("${wineFolder.toString()}/bin/wine")
        public val pulseAudio = File("$usrDir/bin/pulseaudio")
        public val virgl_socket = File("$usrDir/bin/virgl_test_server")
        public val box64 = File("$usrDir/bin/box64")
        public val vkcube = File("$usrDir/bin/vkcube")
        public val xkbRootDir = File("$usrDir/share/X11/xkb")
    }

    fun <T> gotoActivity(owner: Context, clazz: Class<T>) {
         owner.startActivity(Intent(owner, clazz))
    }
    
    fun <T> extractResources(owner: T, binding: ActivityMainBinding) { 
        asyncTasker.newCoroutine("res", {
            if(!usrDir.exists()) {
                 ZipFileExtractor().extractZip(
                    "storage/emulated/0/rootfs.zip",
                    appRootDir,
                    binding.progressExtractBar,
                    binding.updateExtractProgress,
                    owner
                )
            }
        
            asyncTasker.newCoroutine("RunOnUiThread", {
            binding.progressExtractBar.visibility = View.GONE
            binding.updateExtractProgress.visibility = View.GONE
            }, DispatchersType.MAIN)
            
            asyncTasker.executeCoroutine("RunOnUiThread") 
        }, DispatchersType.DEFAULT)
        
        asyncTasker.executeCoroutine("res") 
    }
    
    fun setUpNavigation(navController: NavController, binding: ActivityMainBinding) {
        (binding.bottomNavigation as NavigationBarView).setupWithNavController(navController)
    }
}
