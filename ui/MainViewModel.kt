
package com.example.bleaudiotool.ui

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bleaudiotool.audio.AmplitudeExtractor
import com.example.bleaudiotool.audio.AudioConverter
import com.example.bleaudiotool.audio.AudioRecorder
import com.example.bleaudiotool.data.BleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val bleRepository: BleRepository,
    private val audioConverter: AudioConverter,
    private val audioRecorder: AudioRecorder = AudioRecorder()
) : ViewModel() {

    private val _statusText = MutableStateFlow("Idle")
    val statusText: StateFlow<String> = _statusText

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _readyToSend = MutableStateFlow(false)
    val readyToSend: StateFlow<Boolean> = _readyToSend

    val scannedDevices: StateFlow<List<BluetoothDevice>> = bleRepository.scannedDevices
    val isScanning: StateFlow<Boolean> = bleRepository.isScanning
    val connectionState: StateFlow<Int> = bleRepository.connectionState

    private var currentAudioPcm: ByteArray? = null
    var isMotorFast = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            connectionState.collect { state ->
                when (state) {
                    BluetoothProfile.STATE_CONNECTED -> _statusText.value = "Connected"
                    BluetoothProfile.STATE_CONNECTING -> _statusText.value = "Connecting..."
                    BluetoothProfile.STATE_DISCONNECTED -> _statusText.value = "Disconnected"
                    BluetoothProfile.STATE_DISCONNECTING -> _statusText.value = "Disconnecting..."
                }
            }
        }
    }

    fun startScan() {
        _statusText.value = "Scanning..."
        bleRepository.startScan()
    }

    fun connectToDevice(device: BluetoothDevice) {
        bleRepository.connect(device)
        _statusText.value = "Connecting to ${device.name ?: "Unknown"}..."
    }

    fun handleFileUpload(uri: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            _statusText.value = "Converting file..."
            try {
                currentAudioPcm = audioConverter.convertTo8kHzPcm(uri)
                _readyToSend.value = true
                _statusText.value = "Audio Ready"
            } catch (e: Exception) {
                _statusText.value = "Error: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun toggleRecording() {
        if (_isRecording.value) {
            currentAudioPcm = audioRecorder.stopRecording()
            _isRecording.value = false
            _readyToSend.value = true
            _statusText.value = "Mic Recording Ready"
        } else {
            viewModelScope.launch {
                _isRecording.value = true
                _readyToSend.value = false
                _statusText.value = "Recording Mic..."
                audioRecorder.startRecording()
            }
        }
    }

    fun sendToDevice() {
        val pcm = currentAudioPcm ?: return
        viewModelScope.launch {
            _statusText.value = "Streaming Data..."
            _isProcessing.value = true
            
            // Extract brightness mapping (800 samples = 100ms at 8kHz)
            val shorts = AmplitudeExtractor.bytesToShorts(pcm)
            val windowSize = 800 
            val brightnessList = mutableListOf<Byte>()
            
            for (i in 0 until shorts.size step windowSize) {
                val end = (i + windowSize).coerceAtMost(shorts.size)
                val window = shorts.copyOfRange(i, end)
                brightnessList.add(AmplitudeExtractor.calculateBrightness(window).toByte())
            }

            bleRepository.streamData(pcm, brightnessList.toByteArray(), isMotorFast.value)
            
            _isProcessing.value = false
            _statusText.value = "Transmission Complete"
        }
    }
}
