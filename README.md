# Custom Publisher Sample for WE ARE DEVELOPERS 2020
## Abstract
This repository solely exists to accompany the presentation [Slip through the boundaries of Legacy Systems with Kotlin and Spring WebFlux](https://wearedevelopers.com/sessions/slip-through-the-boundaries-of-legacy-systems-with-kotlin-and-spring-webflux) at [WAD 2020](https://www.wearedevelopers.com/).

It includes multiple code samples that are used in the presentation. Additionally, a few samples exists for completeness.

## Code samples
The first part is demonstrating how to develop an asynchronous file-IO-based producer.
The latter part is demonstrating how to use base components offered by [Project Reactor](https://projectreactor.io/).

### [SyncFileIntReader](./src/main/kotlin/de/porsche/wad2020/fileintreader/sync/SyncFileIntReader.kt)
A simple and naive implementation of a synchronous file reader producing integers.
The usage is demonstrated at [SyncFileNumberReaderTest](./src/test/kotlin/de/porsche/wad2020/fileintreader/sync/SyncFileNumberReaderTest.kt).

### [AsyncPullFileIntReader](./src/main/kotlin/de/porsche/wad2020/fileintreader/async/AsyncPullFileIntReader.kt)
A simple and naive implementation of an asynchronous file reader producing integers based on a pull model.
The usage is demonstrated at [AsyncPullFileIntReaderTest](./src/test/kotlin/de/porsche/wad2020/fileintreader/async/AsyncPullFileIntReaderTest.kt).

### [AsyncPushFileIntReader](./src/main/kotlin/de/porsche/wad2020/fileintreader/async/AsyncPushFileIntReader.kt)
A simple and naive implementation of an asynchronous file reader producing integers based on a pull-push model.
The usage is demonstrated at [AsyncPushFileIntReaderTest](./src/test/kotlin/de/porsche/wad2020/fileintreader/async/AsyncPushFileIntReaderTest.kt).

### [SubscriptionFileIntReader](./src/main/kotlin/de/porsche/wad2020/fileintreader/reactive/PublisherFileIntReader.kt)
A simple implementation of a Publisher and a Subscription that is producing data based on a fake data generator.
The usage is demonstrated at [PublisherFileIntReaderTest](./src/test/kotlin/de/porsche/wad2020/fileintreader/reactive/PublisherFileIntReaderTest.kt) in addition a sample of the [Reactive Streams Technology Compatibility Kit (TCK)](https://github.com/reactive-streams/reactive-streams-jvm/tree/master/tck) is demonstrated at [PublisherFileIntReaderTckTest](./src/test/kotlin/de/porsche/wad2020/fileintreader/reactive/PublisherFileIntReaderTckTest.kt).

### [Project Reactor Samples](https://projectreactor.io/)
Multiple samples demonstrating the usage of [Project Reactor Samples](https://projectreactor.io/) are available at [ProjectReactorTest](./src/test/kotlin/de/porsche/wad2020/fileintreader/projectreactor/ProjectReactorTest.kt).