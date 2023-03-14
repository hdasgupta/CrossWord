package com.vocabulary.controller

import com.vocabulary.dto.ValueDTO
import com.vocabulary.mappers.FindEntity
import com.vocabulary.repositories.WordsRepo
import com.vocabulary.words.Permutation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@RestController
class APIRestController {

    @Autowired
    lateinit var permutation: Permutation

    @Autowired
    lateinit var findEntity: FindEntity

    @Autowired
    lateinit var wordsRepo: WordsRepo

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


    @RequestMapping(value = ["/random/board/{prevRandom}"])
    fun randomBoard(@PathVariable prevRandom:Int): ResponseEntity<ValueDTO<Int>> {
        val board = findEntity.findBoard(prevRandom)!!

        val wordCount = board.words!!.size

        val wordList = (0 until wordCount)
            .map { board.words!![it].toString() }

        val descList = (0 until wordCount)
            .map { board.words!![it].description!! }

        val wordPermutation = permutation.words(wordList)
        val descPermutation = permutation.words(descList)

        val wordsListPermutation = wordPermutation
            .map { it.joinToString(",") }

        //wordsRepo.deleteAll()

        var _wordsList = wordsRepo.findAllWordsByWordListInAndIdIsNotNull(wordsListPermutation)
            .mapNotNull { findEntity.findWords(it.id!!).orElse(null) }

        val random = _wordsList
            .flatMap {
                it.boards!!
            }
            .toSet()
            .random()
            .id!!


        return ResponseEntity.ok(
            ValueDTO(
            random
            )
        )
    }
}