package com.bayocode.kappwrite

import io.ktor.utils.io.bits.Memory
import io.ktor.utils.io.bits.storeByteArray
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.internal.ChunkBuffer
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray

class FileReadInput(private val source: Source): Input(pool = ChunkBuffer.Pool) {
    override fun closeSource() {
        source.close()
    }

    override fun fill(destination: Memory, offset: Int, length: Int): Int {
        val buffer = Buffer()
        val readCount = source.readAtMostTo(buffer, length.toLong())
        if (readCount > 0) {
            destination.storeByteArray(offset, buffer.readByteArray())
        }
        return readCount.toInt()
    }

}
