package com.project_aurora.emu.ui.adapters.viewholder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

open class GenericHolder<T>(private val itemView : View) : RecyclerView.ViewHolder(itemView) {
     open fun bind(item: T) {}
}
