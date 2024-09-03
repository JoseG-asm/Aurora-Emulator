package com.project_aurora.emu.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LifecycleOwner
import android.content.Intent
import android.content.Context
import java.io.File
import android.view.View
import com.google.android.material.navigation.NavigationBarView
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController

class LogViewModel : ViewModel() {
    var log = MutableLiveData<List<String>>(emptyList())
    
    fun bindMessages(message: String) {
        log.value = log.value.orEmpty().toMutableList().apply {
            add(message)
        }
    }
}
