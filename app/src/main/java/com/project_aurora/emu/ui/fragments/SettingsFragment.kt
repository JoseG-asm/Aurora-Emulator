package com.project_aurora.emu.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.project_aurora.emu.LoriePreferences
import com.project_aurora.emu.R
import com.project_aurora.emu.databinding.FragmentSettingsBinding
import com.project_aurora.emu.model.SettingsModel
import com.project_aurora.emu.ui.activities.SystemShellActivity
import com.project_aurora.emu.ui.activities.LogActivity
import com.project_aurora.emu.ui.adapters.AdapterSettings

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding!!
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        recyclerView = binding.root.findViewById<RecyclerView>(R.id.recyclerViewSettings)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter(recyclerView)
    }

    private fun setAdapter(recyclerView: RecyclerView?) {
        val settingsList =
            mutableListOf(
                SettingsModel(
                    getString(R.string.preferences_system_shell_title),
                    getString(R.string.preferences_system_shell_description),
                    R.drawable.ic_settings,
                    {
                        // onclick
                        requireContext()
                            .startActivity(
                                Intent(requireContext(), SystemShellActivity::class.java)
                            )
                    }
                ),
                SettingsModel(
                    getString(R.string.preferences_system_shell_title),
                    getString(R.string.preferences_system_shell_description),
                    R.drawable.ic_settings,
                    {
                        // onclick
                        requireContext()
                            .startActivity(
                                Intent(requireContext(), LogActivity::class.java)
                            )
                    }
                ),
                SettingsModel(
                    getString(R.string.preferences_xserver_title),
                    getString(R.string.preferences_xserver_description),
                    R.drawable.ic_display,
                    {
                        // onclick
                        requireContext()
                            .startActivity(
                                Intent(requireContext(), LoriePreferences::class.java)
                            )
                    }
                )
            )
        val adapterSettings = AdapterSettings(settingsList, requireContext())
        recyclerView!!.adapter = adapterSettings
    }
}
