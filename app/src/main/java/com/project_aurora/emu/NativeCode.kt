package com.project_aurora.emu

import android.view.Surface

class NativeCode {
   companion object {
      init {
         System.loadLibrary("compositor_wrapper")
      }
   }
   
   external fun surfaceChanged(surface: Surface?, width: Int, height: Int)
   external fun surfaceDestroyed()
}
