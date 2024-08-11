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

class SettingsHeaderHolder(private val itemView : View) : RecyclerView.ViewHolder(itemView) {
      var header: TextView = itemView.findViewById(R.id.settings_header)
      
      fun bind(preferencesModel : PreferencesModel) {
      header.text = preferencesModel.preferenceTitle
      }
}
