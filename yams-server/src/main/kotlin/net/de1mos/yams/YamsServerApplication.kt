package net.de1mos.yams

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class YamsServerApplication

fun main(args: Array<String>) {
    runApplication<YamsServerApplication>(*args)
}