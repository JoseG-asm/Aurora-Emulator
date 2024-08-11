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
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.project_aurora.emu.R
import java.io.File
import android.content.res.Configuration
import java.io.FileOutputStream
import androidx.viewpager2.widget.ViewPager2
import java.io.OutputStreamWriter
import java.security.MessageDigest
import androidx.core.content.ContextCompat

import com.project_aurora.emu.databinding.ActivityMainBinding
import com.project_aurora.emu.coreutils.AsyncTask
import com.project_aurora.emu.coreutils.DispatchersType
import com.project_aurora.emu.coreutils.ShellLoader
import com.project_aurora.emu.ui.fragments.HomeFragment
import com.project_aurora.emu.ui.fragments.SettingsFragment
import com.project_aurora.emu.viewmodel.MainViewModel

public class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private val shellProcess = ShellLoader()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        
        val verifyOs: (Int) -> Unit = { sdk ->
            if(sdk == Build.VERSION_CODES.P) {
            finish()
            }
        }
        verifyOs.invoke(Build.VERSION.SDK_INT)
        
        val nightModeFlags = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        defineStatusBarColor.invoke(nightModeFlags)
        
        viewModel.apply {
            extractResources(this@MainActivity, binding)
        }
        
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
}
