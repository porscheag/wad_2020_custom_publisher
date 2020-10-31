package de.porsche.wad2020

import org.testng.annotations.Test
import reactor.test.StepVerifier

class HelloWorldTest {
    @Test
    fun `hello() return correct value`() {
        StepVerifier.create(HelloWorld().hello())
            .expectNext("world")
            .verifyComplete()
    }
}