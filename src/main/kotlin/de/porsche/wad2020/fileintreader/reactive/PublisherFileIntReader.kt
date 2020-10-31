package de.porsche.wad2020.fileintreader.reactive


import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.atomic.AtomicBoolean

class PublisherFileIntReader() : Publisher<Int> {
    private val fakeData = (0..99).asSequence()

    override fun subscribe(subscriber: Subscriber<in Int>) {
        subscriber.onSubscribe(SubscriptionFileIntReader(subscriber, fakeData.iterator()))
    }
}

class SubscriberFileIntReader : Subscriber<Int> {
    override fun onComplete() {
        println("onComplete()")
    }

    override fun onError(error: Throwable) {
        TODO("Not yet implemented")
    }

    override fun onNext(data: Int) {
        println("onNext($data)")
    }

    override fun onSubscribe(subscription: Subscription) {
        TODO("Not yet implemented")
    }
}

class SubscriptionFileIntReader(
    private val subscriber: Subscriber<in Int>,
    private val iterator: Iterator<Int>
) : Subscription {
    private var isCancelled: AtomicBoolean = AtomicBoolean(false)

    override fun cancel() {
        isCancelled.getAndSet(true)
    }

    override fun request(n: Long) {
        if(n <= 0) {
            subscriber.onError(IllegalArgumentException("n must be greater 0, but is $n"))
        } else {
            tailrec fun recFn(counter: Int): Unit {
                if(counter < n && iterator.hasNext() && !isCancelled.get()) {
                    subscriber.onNext(iterator.next())
                    recFn(counter + 1)
                }
            }
            recFn(0)
        }
    }
}