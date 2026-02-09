
package com.example.bleaudiotool.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AudioRecorder {
    private val sampleRate = 8000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    
    // 100ms buffer size: 8000 samples/sec * 0.1 sec * 2 bytes/sample = 1600 bytes
    private val bufferSizeInBytes = 1600 
    
    private var recorder: AudioRecord? = null
    private var isRecording = false
    private val outputStream = ByteArrayOutputStream()

    @SuppressLint("MissingPermission")
    suspend fun startRecording() = withContext(Dispatchers.IO) {
        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val actualBufferSize = maxOf(minBufferSize, bufferSizeInBytes)

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            actualBufferSize
        )

        outputStream.reset()
        val buffer = ByteArray(bufferSizeInBytes)
        
        recorder?.startRecording()
        isRecording = true

        while (isRecording) {
            val read = recorder?.read(buffer, 0, buffer.size) ?: -1
            if (read > 0) {
                outputStream.write(buffer, 0, read)
            }
        }
        
        recorder?.stop()
        recorder?.release()
        recorder = null
    }

    fun stopRecording(): ByteArray {
        isRecording = false
        return outputStream.toByteArray()
    }
}
