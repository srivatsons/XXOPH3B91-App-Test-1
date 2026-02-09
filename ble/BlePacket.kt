
package com.example.bleaudiotool.ble

import com.example.bleaudiotool.util.ChecksumUtil

enum class PacketType(val value: Byte) {
    AUDIO(0x01.toByte()),
    BRIGHTNESS(0x02.toByte()),
    MOTOR(0x03.toByte()),
    END(0xFF.toByte())
}

class BlePacket(
    val type: PacketType,
    val sequenceId: Int,
    val payload: ByteArray
) {
    companion object {
        const val MAX_PACKET_SIZE = 180
        const val HEADER_SIZE = 4
        const val MAX_PAYLOAD_SIZE = MAX_PACKET_SIZE - HEADER_SIZE
    }

    fun toByteArray(): ByteArray {
        val size = HEADER_SIZE + payload.size
        val packet = ByteArray(size)
        
        packet[0] = type.value
        packet[1] = (sequenceId and 0xFF).toByte()
        packet[2] = ((sequenceId shr 8) and 0xFF).toByte()
        // packet[3] is checksum, computed later
        
        System.arraycopy(payload, 0, packet, HEADER_SIZE, payload.size)
        
        packet[3] = ChecksumUtil.calculateChecksum(packet)
        return packet
    }
}
