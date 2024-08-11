package com.project_aurora.emu.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project_aurora.emu.R
import com.project_aurora.emu.model.SettingsModel
import com.project_aurora.emu.ui.adapters.viewholder.SettingsViewHolder

class AdapterSettings(private val settingsList: List<SettingsModel>, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.settings_item, parent, false)
        return SettingsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val preference = settingsList[position]
        when (holder) {
            is SettingsViewHolder -> holder.bind(preference)
        }
    }

    override fun getItemCount(): Int {
        return settingsList.size
    }
}
