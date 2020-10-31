package de.porsche.wad2020.fileintreader.async

import arrow.core.Either
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.CountDownLatch

class AsyncPushFileIntReaderTest {
    @ParameterizedTest
    @ValueSource(strings = ["./data/numbers_0_99_ok.txt", "./data/numbers_0_99_nok.txt"])
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
}