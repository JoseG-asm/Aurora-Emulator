package com.project_aurora.emu.ui.adapters.viewholder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project_aurora.emu.R
import com.project_aurora.emu.models.PreferencesModel
import com.project_aurora.emu.models.PreferenceType
import com.google.android.material.materialswitch.MaterialSwitch


class SettingsAbstractHolder(private val itemView : View) : RecyclerView.ViewHolder(itemView) {
   var settingsName: TextView = itemView.findViewById(R.id.text_setting_name)
   var settingsDescription: TextView = itemView.findViewById(R.id.text_setting_description)
   var content: TextView = itemView.findViewById(R.id.text_setting_value)
   
   fun bind(preferencesModel: PreferencesModel) {
            settingsName.text = preferencesModel.preferenceTitle
            settingsDescription.text = preferencesModel.preferenceSummary 
            content.text = preferencesModel.content
            itemView.setOnClickListener {
                preferencesModel.onClickListener.invoke()
            }
        }
}
