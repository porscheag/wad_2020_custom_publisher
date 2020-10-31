package de.porsche.wad2020.fileintreader.async

import arrow.core.Either
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.concurrent.CountDownLatch

class AsyncPushFileIntReaderTest {
    @Test(dataProvider = "filePath")
    fun `demo AsyncPushFileIntReader`(filePath: String) {
        val lock = CountDownLatch(1)
        val intReader = AsyncPushFileIntReader(filePath)

        println("\n\n$filePath:")
        intReader.readNumber {
            when(it) {
                is Either.Left -> println(it.a)
                is Either.Right -> {
                    println(it.b)
                    if(it.b == null) lock.countDown()
                }
            }
        }
        println("This should be visible before the first number is printed!")

        lock.await()
    }

    @DataProvider(name = "filePath")
    fun getFilePaths(): Array<Array<String>> = arrayOf(
        arrayOf("./data/numbers_0_99_ok.txt"),
        arrayOf("./data/numbers_0_99_nok.txt")
    )
}