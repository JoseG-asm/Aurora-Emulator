package com.project_aurora.emu.vulkan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project_aurora.emu.databinding.ActivityVulkanWrapperBinding

class VulkanWrapperActivity : AppCompatActivity() {
    private var _binding: ActivityVulkanWrapperBinding? = null
    private val binding: ActivityVulkanWrapperBinding get() = _binding!!
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVulkanWrapperBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
