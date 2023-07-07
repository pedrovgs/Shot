package com.karumi.shot

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.os.Bundle
import androidx.test.internal.runner.RunnerArgs
import java.io.File

class OrchestratorScreenshotSaver {
    companion object {

        var packageName: String? = null
        var orchestrated: Boolean = false

        @SuppressLint("RestrictedApi")
        fun onCreate(instrumentation: Instrumentation, args: Bundle) {
            orchestrated = RunnerArgs.Builder()
                .fromManifest(instrumentation)
                .fromBundle(instrumentation, args)
                .build()
                .orchestratorService != null
            packageName = instrumentation.context.packageName
        }

        fun onDestroy() {
            if (orchestrated) {
                copyScreenshots("screenshots-default")
                copyScreenshots(
                    screenshotDirName = "screenshots-compose-default",
                    onlyMetadata = true,
                    fileSuffix = "_compose"
                )
            }
        }

        private fun copyScreenshots(
            screenshotDirName: String,
            onlyMetadata: Boolean = false,
            fileSuffix: String = ""
        ) {
            val orchestratedSuffix = "-orchestrated"

            val screenshotsFolderPath =
                "${AndroidStorageInfo.storageBaseUrl}/screenshots/$packageName/$screenshotDirName/"
            val orchestratedFolderPath =
                "${AndroidStorageInfo.storageBaseUrl}/screenshots/$packageName/$screenshotDirName$orchestratedSuffix/"

            File(orchestratedFolderPath).mkdirs()
            val screenshotsFolder = File(screenshotsFolderPath)

            screenshotsFolder.listFiles()?.forEach { file ->
                if (!onlyMetadata || file.name.contains("metadata$fileSuffix.json")) {
                    moveFile(file, orchestratedFolderPath)
                }
            }
        }

        private fun moveFile(file: File, targetDirPath: String) {
            // Under orchestrated tests metadata files are created for every test.
            // It requires to change their names to achieve unique files and avoid
            // FileExistsException be thrown during copying.
            var counter = 0
            var targetFile: File
            do {
                val counterSuffix = if (counter == 0) "" else "_$counter"
                val newPath = "$targetDirPath${file.nameWithoutExtension}.${file.extension}$counterSuffix"
                targetFile = File(newPath)
                counter = counter.inc()
            } while (targetFile.exists())

            file.renameTo(targetFile)
        }
    }
}
