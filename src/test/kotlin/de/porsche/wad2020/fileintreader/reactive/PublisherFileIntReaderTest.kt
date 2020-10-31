package de.porsche.wad2020.fileintreader.reactive

import org.testng.annotations.Test

class PublisherFileIntReaderTest {
    @Test
    fun `demo of PublisherFileIntReader`() {
        val publisher = PublisherFileIntReader()
        val subscriber = SubscriberFileIntReader()
        publisher.subscribe(subscriber)
    }
}
