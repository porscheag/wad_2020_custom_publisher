package de.porsche.wad2020.fileintreader.reactive


import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

class PublisherFileIntReader : Publisher<Int> {
    private val fakeData = (0..99).asSequence()

    override fun subscribe(subscriber: Subscriber<in Int>) {
        fakeData.forEach(subscriber::onNext)
        subscriber.onComplete()
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