package com.project_aurora.emu.model;

data class SettingsModel(
    var settingsTitle: String,
    var descriptionSettings: String,
    var imageRes: Int,
    var onClickListener: () -> Unit
)

