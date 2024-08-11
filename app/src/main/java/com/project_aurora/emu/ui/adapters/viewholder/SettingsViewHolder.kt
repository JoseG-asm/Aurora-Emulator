package com.project_aurora.emu.ui.adapters.viewholder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project_aurora.emu.model.SettingsModel
import com.google.android.material.materialswitch.MaterialSwitch
import com.project_aurora.emu.R

class SettingsViewHolder(private val itemView : View) : RecyclerView.ViewHolder(itemView) {
   val settingsName: TextView = itemView.findViewById(R.id.title_preferences_model)
   val settingsDescription: TextView = itemView.findViewById(R.id.description_preferences_model)
   val imageRes: ImageView = itemView.findViewById(R.id.set_img)

   fun bind(preferencesModel: SettingsModel) {
        settingsName.text = preferencesModel.settingsTitle
        settingsDescription.text = preferencesModel.descriptionSettings
        imageRes.setImageResource(preferencesModel.imageRes)

        itemView.setOnClickListener {
            preferencesModel.onClickListener.invoke()
        }
    }
}