package de.porsche.wad2020.fileintreader.reactive

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.atomic.AtomicBoolean

class PublisherFileIntReader(elements: Int = 99) : Publisher<Int> {
    private val fakeData = (0 until elements).asSequence()

    override fun subscribe(subscriber: Subscriber<in Int>) {
        subscriber.onSubscribe(SubscriptionFileIntReader(subscriber, fakeData.iterator()))
    }
}

class SubscriptionFileIntReader(
    private val subscriber: Subscriber<in Int>,
    private val iterator: Iterator<Int>
) : Subscription {
    private var isTerminated: AtomicBoolean = AtomicBoolean(false)

    override fun cancel() {
        isTerminated.getAndSet(true)
    }

    override fun request(n: Long) {
        if(n <= 0 && !isTerminated.get()) {
            subscriber.onError(IllegalArgumentException("n must be greater 0, but is $n"))
        } else {
            tailrec fun recFn(counter: Int) {
                if(counter < n && iterator.hasNext() && !isTerminated.get()) {
                    subscriber.onNext(iterator.next())
                    recFn(counter + 1)
                }
                if(!iterator.hasNext() && !isTerminated.get()) {
                    subscriber.onComplete()
                    isTerminated.getAndSet(true)
                }
            }
            recFn(0)
        }
    }
}