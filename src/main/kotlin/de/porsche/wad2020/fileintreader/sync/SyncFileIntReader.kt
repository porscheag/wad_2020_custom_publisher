package de.porsche.wad2020.fileintreader.sync

import de.porsche.wad2020.fileintreader.FileIntReaderException
import java.io.File
import java.io.IOException

class SyncFileIntReader(val filePath: String) {
    private val inputStream = File(filePath).inputStream()
    private var isClosed = false

    fun readNumber(): Int? {
        tailrec fun recFn(acc: StringBuilder): Int? {
            val code = inputStream.read()
            return when {
                code.toChar().isDigit() -> recFn(acc.append(code.toChar()))
                (code == -1 || code.toChar().isWhitespace()) && acc.isNotEmpty() -> acc.toString().toInt()
                code.toChar().isWhitespace() -> recFn(acc)
                code == -1 -> { close(); null }
                else -> throw FileIntReaderException("""Cannot read Int from "$filePath": ${code.toChar()}""")
            }
        }
        return if(isClosed) null else recFn(StringBuilder())
    }

    fun close() {
        if(!isClosed) {
            try { inputStream.close() } catch(_: IOException) { }
            isClosed = true
        }
    }
}