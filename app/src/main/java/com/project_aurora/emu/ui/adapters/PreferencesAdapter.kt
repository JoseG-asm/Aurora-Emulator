package com.project_aurora.emu.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project_aurora.emu.R
import com.project_aurora.emu.models.PreferenceType
import com.project_aurora.emu.models.PreferencesModel
import com.project_aurora.emu.ui.adapters.viewholder.SettingsAbstractHolder
import com.project_aurora.emu.ui.adapters.viewholder.SettingsHeaderHolder
import com.project_aurora.emu.ui.adapters.viewholder.SettingsSwitchHolder

class PreferencesAdapter(
    private val preferencesList: List<PreferencesModel>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_SETTINGS = 0
        private const val VIEW_TYPE_SWITCH = 1
        private const val VIEW_TYPE_HEADER = 2
    }
    private lateinit var itemView: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SETTINGS -> {
                itemView = inflater.inflate(R.layout.preferences_item, parent, false)
                SettingsAbstractHolder(itemView)
            }
            VIEW_TYPE_SWITCH -> {
                itemView = inflater.inflate(R.layout.preferences_switch, parent, false)
                SettingsSwitchHolder(itemView)
            }
            VIEW_TYPE_HEADER -> {
                itemView = inflater.inflate(R.layout.preferences_header, parent, false)
                SettingsHeaderHolder(itemView)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val preference = preferencesList[position]
        when (holder) {
            is SettingsAbstractHolder -> {
                holder.bind(preference)
            }
            is SettingsSwitchHolder -> {
                holder.bind(preference)
            }
            is SettingsHeaderHolder -> {
                holder.bind(preference)
            }
            else -> throw IllegalArgumentException("Unknown view holder type")
        }
    }

    override fun getItemCount(): Int {
        return preferencesList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (preferencesList[position].returnType()) {
            PreferenceType.SETTINGS -> VIEW_TYPE_SETTINGS
            PreferenceType.SWITCH -> VIEW_TYPE_SWITCH
            PreferenceType.HEADER -> VIEW_TYPE_HEADER
            else -> 0
        }
    }
}
