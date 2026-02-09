
package com.example.bleaudiotool.audio

import kotlin.math.sqrt

object AmplitudeExtractor {
    /**
     * Calculates RMS for a window of PCM 16-bit mono samples and maps it to 0-255 brightness.
     * N = window size in samples (for 8kHz, 100ms = 800 samples)
     */
    fun calculateBrightness(pcmData: ShortArray): Int {
        if (pcmData.isEmpty()) return 0
        
        var sumSquares = 0.0
        for (sample in pcmData) {
            sumSquares += sample.toDouble() * sample.toDouble()
        }
        
        val rms = sqrt(sumSquares / pcmData.size)
        
        // Normalization: (rms / 15000.0) * 255
        val brightness = (rms / 15000.0) * 255.0
        
        return brightness.toInt().coerceIn(0, 255)
    }

    /**
     * Converts raw byte array (PCM 16-bit) to ShortArray for easier math.
     */
    fun bytesToShorts(bytes: ByteArray): ShortArray {
        val shorts = ShortArray(bytes.size / 2)
        for (i in shorts.indices) {
            val low = bytes[i * 2].toInt() and 0xFF
            val high = bytes[i * 2 + 1].toInt()
            shorts[i] = ((high shl 8) or low).toShort()
        }
        return shorts
    }
}
