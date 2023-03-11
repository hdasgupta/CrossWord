package com.vocabulary.controller

import com.vocabulary.words.Permutation
import com.vocabulary.words.Words
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class CrossWordController {

    @Autowired
    lateinit var permutation: Permutation

    @RequestMapping(value = ["/html"])
    fun html(@RequestParam words:String, map: ModelMap): String {

        val wordList = words.split(",").map { it.uppercase().trim() }

        map["words"] = wordList

        map["results"] = permutation.words(wordList)
            .flatMap { Words(it).all() }
            .toSet()
            .map { it.strings()}

        return "List"
    }

    @RequestMapping(value = ["/cross"])
    fun cross(@RequestParam cross: String, map: ModelMap): String {

        map["results"] = cross.split(",")
            .map { it.chars().mapToObj { c->c.toChar().toString() }.toList() }

        return "CrossWord"
    }
}