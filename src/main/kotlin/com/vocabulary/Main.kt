package com.vocabulary

import com.vocabulary.words.Words
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.stream.Collectors
import kotlin.math.min

@SpringBootApplication
class CrossWordMain

fun main(args: Array<String>) {
    Words(listOf("last","test","rest","week")).all()
        .forEach {
            println(it)
            println()
        }
    runApplication<CrossWordMain>(*args)
}

/*
fun main(vararg args: String) {
    _words(args.toList())
        .flatMap { Words(it).all() }
        .toSet()
        .forEach {
            println(it)
            println()
        }

}
*/
