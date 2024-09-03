package com.project_aurora.emu.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project_aurora.emu.databinding.ActivityLogBinding
import com.project_aurora.emu.viewmodel.LogViewModel
import androidx.lifecycle.ViewModelProvider

class LogActivity : AppCompatActivity() {
    private var _binding: ActivityLogBinding? = null
    private val binding: ActivityLogBinding get() = _binding!!
    
    private lateinit var viewModel: LogViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        _binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this@LogActivity).get(LogViewModel::class.java)
        
        viewModel.bindMessages("Starting log.... \n")
        viewModel.apply {
            log.observe(this@LogActivity) { log -> 
                binding.apply {
                    tvLogShell.text = log.toString()
                }
            }
        }
        
    }
}
