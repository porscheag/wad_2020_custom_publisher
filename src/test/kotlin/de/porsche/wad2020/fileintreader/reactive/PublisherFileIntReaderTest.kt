package de.porsche.wad2020.fileintreader.reactive

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PublisherFileIntReaderTest {
    private open class SubscriberFileIntReader(private val n: Long, private val lock: CountDownLatch) : Subscriber<Int> {
        override fun onComplete() {
            println("onComplete()")
            lock.countDown()
        }

        override fun onError(error: Throwable) {
            println("onError($error)")
            lock.countDown()
        }

        override fun onNext(data: Int) {
            println("onNext($data)")
        }

        override fun onSubscribe(subscription: Subscription) {
            println("onSubscribe($subscription)")
            subscription.request(n)
        }
    }

    @Test(dataProvider = "publisherNs")
    fun `demo of PublisherFileIntReader`(n: Long) {
        val lock = CountDownLatch(1)
        val publisher = PublisherFileIntReader()
        val subscriber = SubscriberFileIntReader(n, lock)
        publisher.subscribe(subscriber)

        lock.await(1000, TimeUnit.MILLISECONDS)
    }

    @DataProvider(name = "publisherNs")
    fun getPublisherNs() = arrayOf(arrayOf(-1L), arrayOf(0L), arrayOf(8L), arrayOf(99L), arrayOf(140L))
}
