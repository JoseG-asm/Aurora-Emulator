package com.project_aurora.emu.coreutils

import com.project_aurora.emu.ui.main.MainActivity

class EnvVars(val mEnvironmentVariables: MutableMap<String, String> = mutableMapOf()) {
    val exportVariables: String get() {
        val vars = mEnvironmentVariables.values.joinToString(" ")
        return "export " + vars.ifEmpty { " " }
    }
    
    fun putVar(key: String, value: Any) {
        mEnvironmentVariables[key] = value.toString()
    }
    
    fun setEnvVariables() {
        putVar("TMPDIR", "TMPDIR=" + MainActivity.tmpDir)
        putVar("XKB_CONFIG_ROOT", "XKB_CONFIG_ROOT=" + MainActivity.xkbRootDir)
        putVar("HOME", "HOME=" + MainActivity.homeDir)
        putVar("LANG", "LANG=en_US.UTF-8")
        putVar("BOX64_MMAP32", "BOX64_MMAP32=1")
        putVar("DISPLAY", "DISPLAY=:0")
        
        // vk wsi layer
        putVar("ZINK", "GALLIUM_DRIVER=zink")
        putVar("MESA_LOADER_DRIVER_OVERRIDE", "MESA_LOADER_DRIVER_OVERRIDE=zink")
        putVar("ICD", "VK_ICD_FILENAMES=${MainActivity.usrDir}/share/vulkan/icd.d/sysvk_icd.json")
        putVar("MESA_GL_VERSION_OVERRIDE", "MESA_GL_VERSION_OVERRIDE=4.6")
        putVar("MESA_GLSL_VERSION_OVERRIDE", "MESA_GLSL_VERSION_OVERRIDE=460")
    }
}
