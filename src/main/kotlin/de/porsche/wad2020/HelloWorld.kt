package de.porsche.wad2020

import reactor.core.publisher.toMono

class HelloWorld {
    fun hello() = "world".toMono()
}