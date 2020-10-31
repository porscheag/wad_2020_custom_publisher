package de.porsche.wad2020.fileintreader.async

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import de.porsche.wad2020.fileintreader.FileIntReaderException
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class AsyncPushFileIntReader(val filePath: String) : AsyncFileIntReader {
    companion object {
        const val BUFFER_SIZE = 32
    }

    private val buffer = ByteBuffer.allocate(BUFFER_SIZE)
    private var position = 0L
    private val channel = AsynchronousFileChannel.open(Paths.get(filePath), setOf(StandardOpenOption.READ), executorService)

    override fun readNumber(callback: (Either<Throwable, Int?>) -> Unit) {
        channel.read(buffer, position, StringBuilder(), FileCompletionHandler(callback))
    }

    private inner class FileCompletionHandler(private val callback: (Either<Throwable, Int?>) -> Unit) : CompletionHandler<Int, StringBuilder> {
        override fun completed(bytesRead: Int, acc: StringBuilder) {
            if(bytesRead > 0) for(pos in 0 until bytesRead) acc.append(buffer[pos].toChar())
            buffer.clear()
            position += if(bytesRead > 0) bytesRead.toLong() else 0L
            if(bytesRead == BUFFER_SIZE) {
                channel.read(buffer, position, acc, this)
            } else {
                channel.close()
                parseInts(StringBuilder(), acc, emptyList()).forEach(callback)
            }
        }

        override fun failed(error: Throwable, acc: StringBuilder) {
            callback(Left(FileIntReaderException("""Cannot read Int from "$filePath": $error""")))
            try { channel.close() } catch (_: IOException) { }
        }
    }

    private tailrec fun parseInts(
        acc: StringBuilder,
        source: StringBuilder,
        destination: List<Either<Throwable, Int?>>
    ): List<Either<Throwable, Int?>> = when {
        source.isEmpty() && acc.isEmpty() -> {
            destination + Right(null)
        }
        source.first().isWhitespace() && acc.isEmpty() -> {
            parseInts(acc, source.deleteCharAt(0), destination)
        }
        source.isEmpty() || source.first().isWhitespace() -> {
            if(source.isNotEmpty()) source.deleteCharAt(0)

            parseInts(
                acc.clear(),
                source,
                destination + try {
                    Right(acc.toString().toInt())
                } catch (error: Throwable) {
                    Left(FileIntReaderException("""Cannot read Int from "$filePath": $error"""))
                }
            )
        }
        source.first().isDigit() -> {
            parseInts(acc.append(source.first()), source.deleteCharAt(0), destination)
        }
        else -> {
            parseInts(
                acc.clear(),
                source.deleteCharAt(0),
                destination + Left(FileIntReaderException("""Cannot read Int from "$filePath": ${source.firstOrNull()}"""))
            )
        }
    }
}
