package de.porsche.wad2020.fileintreader.async

import arrow.core.Either
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.concurrent.CountDownLatch

class AsyncPullFileIntReaderTest {
    @Test
    fun `simple demo of AsyncPullFileIntReader`() {
        val lock = CountDownLatch(5)

        val intReader = AsyncPullFileIntReader("./data/numbers_0_99_ok.txt")
        intReader.readNumber { it1 ->
            println(it1)
            lock.countDown()
            intReader.readNumber { it2 ->
                println(it2)
                lock.countDown()
                intReader.readNumber { it3 ->
                    println(it3)
                    lock.countDown()
                    intReader.readNumber { it4 ->
                        println(it4)
                        lock.countDown()
                        intReader.readNumber { it5 ->
                            println(it5)
                            lock.countDown()
                            // ...
                        }
                    }
                }
            }
        }
        println("This should be visible before the first number is printed!")

        lock.await()
    }

    private fun readAllNumbers(counter: Int, lock: CountDownLatch, intReader: AsyncPullFileIntReader) {
        intReader.readNumber {
            when(it) {
                is Either.Left -> println("$counter: ${it.a}")
                is Either.Right -> println("$counter: ${it.b}")
            }

            when {
                it is Either.Left -> lock.countDown()
                it is Either.Right && it.b == null -> lock.countDown()
                else -> readAllNumbers(counter + 1, lock, intReader)
            }
        }
    }

    @Test(dataProvider = "filePath")
    fun `demo AsyncPullFileIntReader`(filePath: String) {
        val lock = CountDownLatch(1)
        val intReader = AsyncPullFileIntReader(filePath)
        println("\n\n$filePath:")
        readAllNumbers(0, lock, intReader)

        lock.await()
    }

    @DataProvider(name = "filePath")
    fun getFilePaths(): Array<Array<String>> = arrayOf(
        arrayOf("./data/numbers_0_99_ok.txt"),
        arrayOf("./data/numbers_0_99_nok.txt")
    )
}