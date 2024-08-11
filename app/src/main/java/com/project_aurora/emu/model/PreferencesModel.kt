package com.project_aurora.emu.models

enum class PreferenceType {
   SWITCH, // go
   CHECK,
   SETTINGS, // go
   HEADER // go
}

data class PreferencesModel (
var preferenceTitle : String,
var preferenceSummary : String? = null,
var preferenceType : PreferenceType,
var content : String? = null,
var isActived : Boolean? = false,
var onClickListener: () -> Unit
) {
   fun returnType() : PreferenceType {
   return preferenceType
   }
}

