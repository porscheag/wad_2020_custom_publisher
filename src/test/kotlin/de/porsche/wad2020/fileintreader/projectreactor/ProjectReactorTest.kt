package de.porsche.wad2020.fileintreader.projectreactor

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.stream.Collectors
import kotlin.random.Random

class ProjectReactorTest {
    @Test
    fun `demo of Mono with one element`() {
        Mono.just(123)
            .map(::println)
            .subscribe()
        Flux.fromIterable(0..99)
            .map(::println)
            .subscribe()
        StepVerifier.create(Mono.just(123))
            .expectNext(123)
            .verifyComplete()
    }

    @Test
    fun `demo of Mono with no element`() {
        StepVerifier.create(Mono.empty<Int>())
            .verifyComplete()
    }

    @Test
    fun `demo of Mono with error`() {
        StepVerifier.create(Mono.error<Int>(IllegalArgumentException()))
            .verifyError(IllegalArgumentException::class.java)
    }

    @Test
    fun `demo of Flux with 3 elements`() {
        StepVerifier.create(Flux.just(1, 2, 3))
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .verifyComplete()
    }

    @Test
    fun `zip 2 Monos`() {
        StepVerifier.create(Mono.zip(Mono.just(1), Mono.just("A")))
            .assertNext {
                assertThat(it.t1).isEqualTo(1)
                assertThat(it.t2).isEqualTo("A")
            }
            .verifyComplete()
    }

    @Test
    fun `merge 3 Monos`() {
        val publisher = Mono.just(1).mergeWith(Mono.just(5).mergeWith(Mono.just(8)))
        StepVerifier.create(publisher)
            .expectNext(1)
            .expectNext(5)
            .expectNext(8)
            .verifyComplete()
    }

    @Test
    fun `merge 3 Monos with delayed element`() {
        val publisher = Mono.just(1).mergeWith(Mono.just(5).delayElement(Duration.ofSeconds(1)).mergeWith(Mono.just(8)))
        StepVerifier.create(publisher)
            .expectNext(1)
            .expectNext(8)
            .expectNext(5)
            .verifyComplete()
    }

    @Test
    fun `merge 2 Fluxes`() {
        val source = Flux.fromIterable(0..9)
        val copy1 = source.map { Pair(it, "copy 1") }
        val copy2 = source.map { Pair(it, "copy 2") }

        StepVerifier.create(copy1.collectList())
            .assertNext {
                assertThat(it.map { elem -> elem.first }.toSet()).isEqualTo((0..9).toSet())
                assertThat(it.map { elem -> elem.second }.toSet()).isEqualTo(setOf("copy 1"))
            }
            .verifyComplete()

        StepVerifier.create(copy2.collectList())
            .assertNext {
                assertThat(it.map { elem -> elem.first }.toSet()).isEqualTo((0..9).toSet())
                assertThat(it.map { elem -> elem.second }.toSet()).isEqualTo(setOf("copy 2"))
            }
            .verifyComplete()
    }

    @Test
    fun `demo of verifyThenAssertThat()`() {
        val publisher = Mono.just(1).delayElement(Duration.ofMillis(600))
        StepVerifier.create(publisher)
            .expectNext(1)
            .expectComplete()
            .verifyThenAssertThat()
            .tookMoreThan(Duration.ofMillis(500))
    }

    @Test
    fun `demo of cold publisher`() {
        fun generateData() = Random.nextInt().also { println("generateData(): $it") }

        val lock = CountDownLatch(2)

        val source = Mono.fromSupplier(::generateData)
        source.subscribe { println("dest1: $it"); lock.countDown() }
        source.subscribe { println("dest2: $it"); lock.countDown() }

        lock.await()
    }

    @Test
    fun `demo of hot publisher`() {
        fun generateData() = Random.nextInt().also { println("generateData(): $it") }

        val lock = CountDownLatch(2)

        val source = Flux.fromIterable(0..9).delayElements(Duration.ofMillis(100)).share()
        source.collectList().subscribe { println("dest1: $it"); lock.countDown() }
        Thread.sleep(500)
        source.collectList().subscribe { println("dest2: $it"); lock.countDown() }


        lock.await()
    }

    @Test(expectedExceptions = [NullPointerException::class])
    fun `null values are invalid`() {
        StepVerifier.create(Mono.just<Int>(null))
            .expectNext(null)
            .verifyComplete()
    }

    private fun requestServiceA() = Mono.just(123).delayElement(Duration.ofMillis(500))
    private fun requestServiceB() = Mono.just("ABC").delayElement(Duration.ofMillis(300))
    private fun requestServiceC() = Mono.empty<Double>()

    @Test
    fun `zip is executed in parallel`() {
        fun <T> Mono<T>.storeInDB() = Mono.empty<T>()

        Mono.zip(
            requestServiceA().defaultIfEmpty(-1),
            requestServiceB().defaultIfEmpty("")
        ).storeInDB()

        val zipped = Mono.zip(requestServiceA(), requestServiceB())
        StepVerifier.create(zipped)
            .assertNext {
                assertThat(it.t1).isEqualTo(123)
                assertThat(it.t2).isEqualTo("ABC")
            }
            .expectComplete()
            .verifyThenAssertThat()
            .tookLessThan(Duration.ofMillis(800))
    }

    @Test
    fun `zip with empty publisher is empty`() {
        val zipped = Mono.zip(requestServiceA(), Mono.empty<String>())
        StepVerifier.create(zipped)
            .expectComplete()
            .verifyThenAssertThat()
            .tookMoreThan(Duration.ofMillis(500))
    }

    @Test
    fun `use mergeWith with empty publishers instead of zip`() {
        val zipped = requestServiceA().cast(Any::class.java).mergeWith(requestServiceB()).mergeWith(requestServiceC())
            .collectList()

        StepVerifier.create(zipped)
            .assertNext {
                assertThat(it.size).isEqualTo(2)
                assertThat(it.first()).isIn(123, "ABC")
                assertThat(it.last()).isIn(123, "ABC")
                assertThat(it.first() != it.last()).isTrue()
            }
            .expectComplete()
            .verifyThenAssertThat()
            .tookLessThan(Duration.ofMillis(800))
    }

    @Test
    fun `use concatWith with empty publishers instead of zip`() {
        val zipped = requestServiceA().cast(Any::class.java)
            .concatWith(requestServiceB())
            .concatWith((Mono.empty<String>()))
            .collectList()

        StepVerifier.create(zipped)
            .assertNext {
                assertThat(it.size).isEqualTo(2)
                assertThat(it.first()).isIn(123, "ABC")
                assertThat(it.last()).isIn(123, "ABC")
                assertThat(it.first() != it.last()).isTrue()
            }
            .expectComplete()
            .verifyThenAssertThat()
            .tookMoreThan(Duration.ofMillis(800))
    }

    private fun processBody() = Mono.just(123.456)
    private fun <T> requestService(d: T) = Mono.empty<T>()
    private fun <T> storeInDB(d: T) = Mono.just(d)

    @Test
    fun `side-effects in switchIfEmpty for empty publisher`() {
        var globalCounter = 0
        fun fnWithSideEffect(): Double {
            ++globalCounter
            return 0.0
        }

        val publisher = processBody()
            .flatMap(::requestService)
            .switchIfEmpty(Mono.just(fnWithSideEffect()))
            .flatMap(::storeInDB)

        StepVerifier.create(publisher)
            .assertNext {
                assertThat(it).isEqualTo(0.0)
                assertThat(globalCounter).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun `side-effects in switchIfEmpty for non-empty publisher`() {
        var globalCounter = 0
        fun fnWithSideEffect(): Double {
            ++globalCounter
            return 0.0
        }

        val publisher = processBody()
            .flatMap(::requestService).then(Mono.just(99.9))
            .switchIfEmpty(Mono.just(fnWithSideEffect()))
            .flatMap(::storeInDB)

        StepVerifier.create(publisher)
            .assertNext {
                assertThat(it).isEqualTo(99.9)
                assertThat(globalCounter).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun `avoid side-effects in switchIfEmpty for empty publisher`() {
        var globalCounter = 0
        fun fnWithSideEffect(): Double {
            ++globalCounter
            return 0.0
        }

        val publisher = processBody()
            .flatMap(::requestService)
            .switchIfEmpty(Mono.defer { Mono.just(fnWithSideEffect()) })
            .flatMap(::storeInDB)

        StepVerifier.create(publisher)
            .assertNext {
                assertThat(it).isEqualTo(0.0)
                assertThat(globalCounter).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun `avoid side-effects in switchIfEmpty for non-empty publisher`() {
        var globalCounter = 0
        fun fnWithSideEffect(): Double {
            ++globalCounter
            return 0.0
        }

        val publisher = processBody()
            .flatMap(::requestService).then(Mono.just(99.9))
            .switchIfEmpty(Mono.defer { Mono.just(fnWithSideEffect()) } )
            .flatMap(::storeInDB)

        StepVerifier.create(publisher)
            .assertNext {
                assertThat(it).isEqualTo(99.9)
                assertThat(globalCounter).isEqualTo(0)
            }
            .verifyComplete()
    }
}