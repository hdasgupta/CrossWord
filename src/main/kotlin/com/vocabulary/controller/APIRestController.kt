package com.vocabulary.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class APIRestController {

    val lines = APIRestController::class.java.classLoader
        .getResourceAsStream("words_alpha.txt")
        .bufferedReader()
        .lines()
        .toList()
    @RequestMapping(value = ["/words/{query}"])
    fun getWords(@PathVariable query:String): ResponseEntity<List<String>> =
        ResponseEntity.ok(
            lines
                .filter {
                        line ->
                    line.length >= 4 &&
                            line.startsWith(query, true)
                }
        )
}