package de.porsche.wad2020.fileintreader.sync

import de.porsche.wad2020.fileintreader.FileIntReaderException
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class SyncFileNumberReaderTest  {
    @Test(dataProvider = "filePath")
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

    @DataProvider(name = "filePath")
    fun getFilePaths(): Array<Array<String>> = arrayOf(
        arrayOf("./data/numbers_0_99_ok.txt"),
        arrayOf("./data/numbers_0_99_nok.txt")
    )
}