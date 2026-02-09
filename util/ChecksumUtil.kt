
package com.example.bleaudiotool.util

object ChecksumUtil {
    /**
     * Calculates XOR checksum of all bytes in the packet except the checksum byte at index 3.
     */
    fun calculateChecksum(packet: ByteArray): Byte {
        var checksum: Int = 0
        for (i in packet.indices) {
            if (i == 3) continue // Skip the checksum field itself
            checksum = checksum xor (packet[i].toInt() and 0xFF)
        }
        return checksum.toByte()
    }
}
