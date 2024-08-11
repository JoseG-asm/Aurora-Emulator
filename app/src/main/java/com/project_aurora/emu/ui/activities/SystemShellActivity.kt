package com.project_aurora.emu.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationBarView
import com.project_aurora.emu.databinding.ActivitySystemShellBinding
import com.project_aurora.emu.R
import android.view.animation.PathInterpolator
import android.view.View
import androidx.core.view.ViewCompat
import android.view.inputmethod.EditorInfo
import android.content.Context
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import com.project_aurora.emu.coreutils.ShellLoader
import android.util.Log

public class SystemShellActivity : AppCompatActivity() {

    private var _binding: ActivitySystemShellBinding? = null
    private val binding: ActivitySystemShellBinding get() = _binding!!
    private val mShellClient = ShellLoader()
    private var stdErr: String? = null
    private var stdOut: String? = null
    private var lastCommands: MutableList<String> = mutableListOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySystemShellBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbarShell)
        binding.toolbarShellLayout.title = "/system/bin/sh"
        
        resetInputAndOutput()
        init()

        binding.command.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val shellCommand = binding.command.text.toString()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.command.windowToken, 0)
                Log.d("SystemShellActivity", "Shell command: $shellCommand")
                true
            } else {
                false
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    
    private fun resetInputAndOutput() {
        binding.tvOutput.text = " "
        binding.command.text = Editable.Factory.getInstance().newEditable(" ")
    }
    
    private fun init() {
        binding.tvOutput.text = "shell started..."
    }
}