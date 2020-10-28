package de.porsche.wad2020.fileintreader

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SyncFileNumberReaderTest  {
    @ParameterizedTest
    @ValueSource(strings = ["./data/numbers_0_99_ok.txt", "./data/numbers_0_99_nok.txt"])
    fun `demo SyncFileNumberReaderTest`(filePath: String) {
        val intReader = SyncFileIntReader(filePath)
        println("\n\n$filePath:")
        repeat(110) {
            try {
                println("$it: ${intReader.readNumber()}")
            } catch(err: FileIntReaderException) {
                println("$it: $err")
            }
        }
    }
}