package com.project_aurora.emu

import android.view.Surface

class NativeCode {
   companion object {
      init {
         System.loadLibrary("wsi_wrapper")
      }
   }
   
   external fun setSurface(surface: Surface?)
   external fun surfaceChanged(width: Int, height: Int)
   external fun render()
   external fun cleanup()
}
