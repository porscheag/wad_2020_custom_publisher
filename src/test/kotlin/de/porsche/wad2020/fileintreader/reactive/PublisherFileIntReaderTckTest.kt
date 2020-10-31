package de.porsche.wad2020.fileintreader.reactive

import org.reactivestreams.Publisher
import org.reactivestreams.tck.PublisherVerification
import org.reactivestreams.tck.TestEnvironment
import org.testng.annotations.Test

@Test
class PublisherFileIntReaderTckTest : PublisherVerification<Int>(TestEnvironment()) {
    override fun createFailedPublisher(): Publisher<Int> {
        return PublisherFileIntReader()
    }

    override fun createPublisher(elements: Long): Publisher<Int> {
        return PublisherFileIntReader()
    }
}
