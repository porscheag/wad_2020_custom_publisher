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

class AsyncPullFileIntReader(val filePath: String) : AsyncFileIntReader {
    private val channel = AsynchronousFileChannel.open(Paths.get(filePath), setOf(StandardOpenOption.READ), executorService)
    private val acc = StringBuilder()
    private var position = 0L

    override fun readNumber(callback: (Either<Throwable, Int?>) -> Unit) {
        if(isClosed) {
            callback(Right(null))
        } else {
            val buffer = ByteBuffer.allocate(1)
            channel.read(buffer, position, buffer, object : CompletionHandler<Int, ByteBuffer> {
                override fun completed(bytesRead: Int, buffer: ByteBuffer) {
                    val c = if (bytesRead == 1) buffer.get(0).toChar() else null
                    when {
                        c?.isDigit() == true -> {
                            ++position
                            acc.append(c)
                            readNumber(callback)
                        }
                        (c == null || c.isWhitespace()) && acc.isNotEmpty() -> {
                            ++position
                            try {
                                val n = Right(acc.toString().toInt())
                                acc.clear()
                                callback(n)
                            } catch (error: Throwable) {
                                acc.clear()
                                callback(Left(FileIntReaderException("""Cannot read Int from "$filePath": $error""")))
                            }
                        }
                        c?.isWhitespace() == true -> {
                            ++position
                            readNumber(callback)
                        }
                        c == null -> {
                            close()
                            acc.clear()
                            callback(Right(null))
                        }
                        else -> {
                            acc.clear()
                            callback(Left(FileIntReaderException("""Cannot read Int from "$filePath": $c""")))
                        }
                    }
                }

                override fun failed(error: Throwable, buffer: ByteBuffer) {
                    close()
                    callback(Left(FileIntReaderException("""Cannot read Int from "$filePath": $error""")))
                }
            })
        }
    }

    fun close() {
        if(!_isClosed) {
            try { channel.close() } catch(_: IOException) {}
            _isClosed = true
        }
    }

    private var _isClosed = false
    val isClosed get() = _isClosed
}
