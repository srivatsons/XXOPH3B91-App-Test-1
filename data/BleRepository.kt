
package com.example.bleaudiotool.data

import android.bluetooth.BluetoothDevice
import com.example.bleaudiotool.ble.BleManager
import com.example.bleaudiotool.ble.BlePacket
import com.example.bleaudiotool.ble.PacketType
import kotlinx.coroutines.flow.StateFlow

class BleRepository(private val bleManager: BleManager) {
    val connectionState: StateFlow<Int> = bleManager.connectionState
    val isStreaming: StateFlow<Boolean> = bleManager.isStreaming
    val scannedDevices: StateFlow<List<BluetoothDevice>> = bleManager.scannedDevices
    val isScanning: StateFlow<Boolean> = bleManager.isScanning

    fun startScan() = bleManager.startScan()
    fun stopScan() = bleManager.stopScan()
    
    fun connect(device: BluetoothDevice) = bleManager.connect(device)
    fun disconnect() = bleManager.disconnect()

    suspend fun streamData(
        audioData: ByteArray,
        brightnessData: ByteArray,
        motorFast: Boolean
    ) {
        var seqId = 0
        
        // 1. Send Audio Chunks
        audioData.toList().chunked(BlePacket.MAX_PAYLOAD_SIZE).forEach { chunk ->
            val packet = BlePacket(PacketType.AUDIO, seqId++, chunk.toByteArray())
            bleManager.sendPacket(packet)
        }

        // 2. Send Brightness Chunks
        brightnessData.toList().chunked(BlePacket.MAX_PAYLOAD_SIZE).forEach { chunk ->
            val packet = BlePacket(PacketType.BRIGHTNESS, seqId++, chunk.toByteArray())
            bleManager.sendPacket(packet)
        }

        // 3. Send Motor Command
        val motorPayload = byteArrayOf(if (motorFast) 0x01 else 0x00)
        bleManager.sendPacket(BlePacket(PacketType.MOTOR, seqId++, motorPayload))

        // 4. Send End of Transmission
        bleManager.sendPacket(BlePacket(PacketType.END, seqId++, byteArrayOf()))
    }
}
