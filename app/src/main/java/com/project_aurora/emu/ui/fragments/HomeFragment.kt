package com.project_aurora.emu.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.project_aurora.emu.ui.adapters.AdapterSettings
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project_aurora.emu.R
import com.project_aurora.emu.model.SettingsModel
import info.debatty.java.stringsimilarity.Jaccard
import info.debatty.java.stringsimilarity.JaroWinkler
import kotlinx.coroutines.launch
import android.content.Intent
import com.project_aurora.emu.XserverActivity
import com.project_aurora.emu.vulkan.VulkanWrapperActivity
import androidx.fragment.app.FragmentActivity
import com.project_aurora.emu.databinding.FragmentHomeBinding
import com.project_aurora.emu.ui.adapters.ViewPagerContainerAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        
        val viewPager = binding.containerViewPager
        val adapter = ViewPagerContainerAdapter((requireContext() as FragmentActivity))
        viewPager.adapter = adapter
        
        binding.fabDesktop.setOnClickListener { 
        requireContext().startActivity(Intent(requireContext(), XserverActivity::class.java))
        }
        
        return binding.root
    }
    
}
