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

class SettingsSwitchHolder(private val itemView : View) : RecyclerView.ViewHolder(itemView) {
     var switch: MaterialSwitch = itemView.findViewById(R.id.switch_widget)
     var settingsNameSwitch: TextView = itemView.findViewById(R.id.text_setting_name_switch)
     var settingsDescriptionSwitch: TextView = itemView.findViewById(R.id.text_setting_description_switch)
     
     fun bind(preferencesModel: PreferencesModel) {
            switch.isChecked = preferencesModel.isActived ?: false
            settingsNameSwitch.text = preferencesModel.preferenceTitle
            settingsDescriptionSwitch.text = preferencesModel.preferenceSummary
            itemView.setOnClickListener {
                switch.isChecked = !switch.isChecked
            }
        }
}
