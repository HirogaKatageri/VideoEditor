package com.video.trimmer.utils

import android.content.Context
import android.net.Uri
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.video.trimmer.interfaces.OnCompressVideoListener
import com.video.trimmer.interfaces.OnCropVideoListener
import com.video.trimmer.interfaces.OnTrimVideoListener

class VideoOptions(private var ctx: Context) {
    companion object {
        const val TAG = "VideoOptions"
    }

    fun trimVideo(
        startPosition: String,
        endPosition: String,
        inputPath: String,
        outputPath: String,
        outputFileUri: Uri,
        listener: OnTrimVideoListener?
    ) {
        val command = "-y -i $inputPath -ss $startPosition -to $endPosition -c copy $outputPath"
        FFmpeg.executeAsync(command) { executionId, returnCode ->
            when (returnCode) {
                RETURN_CODE_SUCCESS -> listener?.getResult(outputFileUri)
                RETURN_CODE_CANCEL -> listener?.onError("Trim Cancelled")
                else -> listener?.onError("Unknown Error Trimming")
            }
        }
        listener?.onTrimStarted()
    }

    fun cropVideo(
        width: Int,
        height: Int,
        x: Int,
        y: Int,
        inputPath: String,
        outputPath: String,
        outputFileUri: Uri,
        listener: OnCropVideoListener?,
        frameCount: Int
    ) {
        val command =
            "-i $inputPath -filter:v crop=$width:$height:$x:$y -threads 5 -preset ultrafast -strict -2 -c:a copy $outputPath"
        FFmpeg.executeAsync(command) { executionId, returnCode ->
            when (returnCode) {
                RETURN_CODE_SUCCESS -> {
                    listener?.getResult(outputFileUri)
                }
                RETURN_CODE_CANCEL -> listener?.onError("Trim Cancelled")
                else -> listener?.onError("Unknown Error Trimming")
            }
        }
        listener?.onCropStarted()
    }

    fun compressVideo(
        inputPath: String,
        outputPath: String,
        outputFileUri: Uri,
        width: String,
        height: String,
        listener: OnCompressVideoListener?
    ) {
        val command = "-i $inputPath -vf scale=$width:$height $outputPath"
        FFmpeg.executeAsync(command) { executionId, returnCode ->
            when (returnCode) {
                RETURN_CODE_SUCCESS -> {
                    listener?.getResult(outputFileUri)
                }
                RETURN_CODE_CANCEL -> listener?.onError("Trim Cancelled")
                else -> listener?.onError("Unknown Error Trimming")
            }
        }
        listener?.onCompressStarted()
    }
}