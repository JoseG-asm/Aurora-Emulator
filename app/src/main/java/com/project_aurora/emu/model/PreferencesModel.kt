package com.project_aurora.emu.models

enum class PreferenceType { SWITCH, CHECK, SETTINGS, HEADER }

data class PreferencesModel (
    var preferenceTitle : String,
    var preferenceSummary : String? = null,
    var preferenceType : PreferenceType,
    var content : String? = null,
    var isActived : Boolean? = false,
    var onClickListener: () -> Unit
) {
   fun returnType() = preferenceType
   
}

