package de.porsche.wad2020.fileintreader.async

import arrow.core.Either

class AsyncPushFileIntReader(val filePath: String) : AsyncFileIntReader {
    companion object {
        const val BUFFER_SIZE = 32
    }


    override fun readNumber(callback: (Either<Throwable, Int?>) -> Unit) {


    }

    override fun close() {
        if(!_isClosed) {
            // TODO: channel.close()
            _isClosed = true
        }
    }

    private var _isClosed = false
    override val isClosed get() = _isClosed
}
