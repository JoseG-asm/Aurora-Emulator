package com.project_aurora.emu.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LifecycleOwner
import android.content.Intent
import android.content.Context

class MainViewModel : ViewModel() {
    fun <T> gotoActivity(owner: Context, clazz: Class<T>) {
         owner.startActivity(Intent(owner, clazz))
    }
}
