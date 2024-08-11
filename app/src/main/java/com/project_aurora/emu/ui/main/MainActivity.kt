package com.project_aurora.emu.ui.main

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Bundle
import android.os.Build
import android.view.View
import android.widget.Toast
import android.view.animation.PathInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.project_aurora.emu.R
import com.project_aurora.emu.coreutils.ZipFileExtractor
import com.project_aurora.emu.databinding.ActivityMainBinding
import java.io.File
import android.content.res.Configuration
import java.io.FileOutputStream
import androidx.viewpager2.widget.ViewPager2
import java.io.OutputStreamWriter
import java.security.MessageDigest
import androidx.core.content.ContextCompat
import com.project_aurora.emu.coreutils.AsyncTask
import com.project_aurora.emu.coreutils.DispatchersType
import com.project_aurora.emu.coreutils.ShellLoader
import com.project_aurora.emu.ui.fragments.HomeFragment
import com.project_aurora.emu.ui.fragments.SettingsFragment


public class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null

    private val binding: ActivityMainBinding
        get() = _binding!!
    private val mExtractor = ZipFileExtractor()
    private val asyncTasks = AsyncTask()
    private val shellProcess = ShellLoader()
    
    companion object {
        public val appRootDir = "/data/data/com.project_aurora.emu/files"
        public val usrDir = File("$appRootDir/usr")
        public val wineFolder = File("$appRootDir/wine/wine-9.3-vanilla")
        public val homeDir = File("$appRootDir/files/home")
        public val tmpDir = File("$usrDir/tmp")
        public val wineUtilsFolder = File("$appRootDir/wine-utils")
        public val wine = File("${wineFolder.toString()}/bin/wine")
        public val pulseAudio = File("$usrDir/bin/pulseaudio")
        public val virgl_socket = File("$usrDir/bin/virgl_test_server")
        public val box64 = File("$usrDir/bin/box64")
        public val xkbRootDir = File("$usrDir/share/X11/xkb")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val verifyOs: (Int) -> Unit = { sdk ->
            if(sdk == Build.VERSION_CODES.P) {
            finish()
            }
        }
        verifyOs.invoke(Build.VERSION.SDK_INT)
        
        val nightModeFlags = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        defineStatusBarColor.invoke(nightModeFlags)
       
        val sign: String? = getApkSignature(this)
        writeSignatureToFile(sign ?: " ")

        var mExtractProgress: ProgressBar = findViewById(R.id.progressExtractBar)
        var tvExtractProgress: TextView = findViewById(R.id.updateExtractProgress)
        
        asyncTasks.newCoroutine("Ext", {
        
        if(!usrDir.exists()) {
        mExtractor.extractZip(
                "storage/emulated/0/rootfs.zip",
                appRootDir,
                mExtractProgress,
                tvExtractProgress,
                this
            )
        }
        
            asyncTasks.newCoroutine("RunOnUiThread", {
            mExtractProgress.visibility = View.GONE
            tvExtractProgress.visibility = View.GONE
            }, DispatchersType.MAIN)
            asyncTasks.executeCoroutine("RunOnUiThread")
        }, DispatchersType.DEFAULT)
        asyncTasks.executeCoroutine("Ext")
        
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController
        setUpNavigation(navController)
        showNavigation(true, false)
    }

    private fun setUpNavigation(navController: NavController) {
        (binding.bottomNavigation as NavigationBarView).setupWithNavController(navController)
    }

    private fun showNavigation(visible: Boolean, animated: Boolean) {
        if (!animated) {
            if (visible) {
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                binding.bottomNavigation.visibility = View.INVISIBLE
            }
            return
        }

        binding.bottomNavigation
            .animate()
            .apply {
                if (visible) {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    duration = 300
                    interpolator = PathInterpolator(0.05f, 0.7f, 0.1f, 1f)
                    if (
                        ViewCompat.getLayoutDirection(binding.bottomNavigation) ==
                            ViewCompat.LAYOUT_DIRECTION_LTR
                    ) {
                        binding.bottomNavigation.translationX =
                            binding.bottomNavigation.width.toFloat() * -2
                        translationX(0f)
                    } else {
                        binding.bottomNavigation.translationX =
                            binding.bottomNavigation.width.toFloat() * 2
                        translationX(0f)
                    }
                }
            }
            .withEndAction {
                if (!visible) {
                    binding.bottomNavigation.visibility = View.INVISIBLE
                }
            }
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    
    val defineStatusBarColor: (Int) -> Unit = { flags -> 
        when(flags) {
        Configuration.UI_MODE_NIGHT_NO -> { //Light Mode
        }
        Configuration.UI_MODE_NIGHT_YES -> { //Night Mode
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          window.statusBarColor = ContextCompat.getColor(this, R.color.aurora_secondary)
          }
        }
        }
    }

    fun getClassPath(context: Context): String? {
        return try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            applicationInfo.sourceDir
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Verify integrity apk

    fun isApkTampered(context: Context): Boolean {
        val knownSignature = " " // Substitua pelo valor obtido
        return try {
            val packageManager: PackageManager = context.packageManager
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            val signatures: Array<Signature> = packageInfo.signatures
            for (signature in signatures) {
                val signatureBytes: ByteArray = signature.toByteArray()
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                val digest: ByteArray = md.digest(signatureBytes)
                val currentSignature: String = bytesToHex(digest)
                if (currentSignature != knownSignature) {
                    finish()
                    return true // APK foi modificado
                }
            }
            false // APK é legítimo
        } catch (e: Exception) {
            e.printStackTrace()
            true // Algo deu errado
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (byte in bytes) {
            sb.append(String.format("%02X", byte))
        }
        return sb.toString()
    }

    fun getApkSignature(context: Context): String? {
        return try {
            val packageManager: PackageManager = context.packageManager
            val packageInfo =
                packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            val signatures: Array<Signature> = packageInfo.signatures
            for (signature in signatures) {
                val signatureBytes: ByteArray = signature.toByteArray()
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                val digest: ByteArray = md.digest(signatureBytes)
                return bytesToHex(digest)
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun writeSignatureToFile(signature: String) {
        try {
            val file = File("sdcard/Download", "signature.txt")
            val fos = FileOutputStream(file)
            val osw = OutputStreamWriter(fos)
            osw.write(signature)
            osw.flush()
            osw.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
