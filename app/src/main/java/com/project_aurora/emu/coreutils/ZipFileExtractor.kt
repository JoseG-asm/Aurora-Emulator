package com.project_aurora.emu.coreutils

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.project_aurora.emu.ui.main.MainActivity
import java.io.File
import java.io.IOException
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor

class ZipFileExtractor {
    fun <T> extractZip(
        zipFilePath: String?,
        destinationPath: String,
        progressExtractBar: ProgressBar?,
        progressText: TextView?,
        activity: T
    ) {
        try {
            Log.v("Arquive Extract", "Start Extract")

            val zipFile = ZipFile(zipFilePath)

            zipFile.isRunInThread = true

            val progressMonitor = zipFile.progressMonitor
            zipFile.extractAll(destinationPath)

            while (!progressMonitor.state.equals(ProgressMonitor.State.READY)) {
                (activity as? MainActivity)?.runOnUiThread {
                    progressText?.text = progressMonitor.percentDone.toString() + "%"
                    progressExtractBar?.progress = progressMonitor.percentDone
                }

                Thread.sleep(100)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
