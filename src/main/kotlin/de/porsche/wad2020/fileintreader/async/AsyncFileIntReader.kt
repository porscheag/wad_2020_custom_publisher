package de.porsche.wad2020.fileintreader.async

import arrow.core.Either
import java.util.concurrent.Executors

internal val executorService = Executors.newFixedThreadPool(10)

interface AsyncFileIntReader {
    fun readNumber(callback: (Either<Throwable, Int?>) -> Unit)
    fun close()
    val isClosed: Boolean
}
