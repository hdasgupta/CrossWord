package com.vocabulary.controller

import com.vocabulary.entities.Board
import com.vocabulary.entities.Location
import com.vocabulary.entities.Word
import com.vocabulary.mappers.BOToEntity
import com.vocabulary.mappers.EntityToBO
import com.vocabulary.mappers.FindEntity
import com.vocabulary.mappers.SaveEntity
import com.vocabulary.repositories.BoardRepo
import com.vocabulary.repositories.WordsRepo
import com.vocabulary.words.Indices
import com.vocabulary.words.Invert
import com.vocabulary.words.Permutation
import com.vocabulary.words.Words
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Optional
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@Controller
class CrossWordController {

    @Autowired
    lateinit var permutation: Permutation

    @Autowired
    lateinit var wordsRepo: WordsRepo

    @Autowired
    lateinit var boardRepo: BoardRepo

    @Autowired
    lateinit var saveEntity: SaveEntity

    @Autowired
    lateinit var findEntity: FindEntity

    @Autowired
    lateinit var boToEntity: BOToEntity

    @Autowired
    lateinit var entityToBO: EntityToBO

    @RequestMapping(value = ["/html"])
    fun html(@RequestParam words:String, map: ModelMap): String {

        val wordList = words.split(",").map { it.uppercase().trim() }.filter { it.isNotEmpty() }
        val wordPermutation = permutation.words(wordList)

        val wordsListPermutation = wordPermutation
            .map { it.joinToString(",") }

        //wordsRepo.deleteAll()

        var _wordsList = wordsRepo.findAllWordsByWordListInAndIdIsNotNull(wordsListPermutation)
            .mapNotNull { findEntity.findWords(it.id!!).orElse(null) }

        if(_wordsList.isEmpty()) {
            _wordsList = wordPermutation
                .map { Words(it) }
                .map {
                    val word = boToEntity.map(it)
                    word
                }
                .filter { it.boards!!.isNotEmpty() }


            _wordsList.forEach {
                saveEntity.save(it)
            }
        }

        val results: List<Int> = _wordsList
            .flatMap {
                it.boards!!
            }
            .toSet()
            .map { it.id!! }

        map["words"] = wordList

        map["results"] = results

        return "List"
    }

    @RequestMapping(value = ["/cross"])
    fun cross(@RequestParam cross: Int, map: ModelMap): String {
        map["results"] = getResult(findEntity.findBoard(cross))

        return "CrossWord"
    }

    @RequestMapping(value = ["/choose"])
    fun choose(@RequestParam choose: Int, map: ModelMap): String {
        map["results"] = getResult(findEntity.findBoard(choose))
        map["choose"] = choose
        return "Choose"
    }

    @RequestMapping(value = ["/verify"])
    fun verify(request: HttpServletRequest, map: ModelMap): String {
        val cross = findEntity.findBoard(request.getParameter("choose").toInt())
        val chars = getChars(cross)
        val result = getResult(cross)
        val valid = request.parameterNames.toList()
            .filter { it.startsWith("char_") }
            .filter { chars[it]!=request.getParameter(it) }
            .map { it.split("_") }
            .map { Pair(it[1].toInt(), it[2].toInt()) }

        valid.forEach {
            result[it.first][it.second].error = true
        }

        map["valid"] = valid.isEmpty()
        map["errors"] = valid
        map["results"] = result

        return "Valid"
    }

    fun getChars(cross: Board?): Map<String, String> {
        if(cross != null) {
            return cross.words!!
                .flatMap { it.locations!! }
                .map { loc->"char_${loc.row}_${loc.column}=${loc.char}" }
                .distinct()
                .map {loc->loc.split("=")}
                .stream()

                .collect(Collectors.toMap(
                    fun(loc: List<String>)=loc[0],
                    fun(loc: List<String>)=loc[1]
                )
                )
        } else {
            return mapOf<String, String>()
        }

    }
    fun getResult(cross: Board?): List<List<Invert>>  {

        if(cross != null) {
            val _cross = entityToBO!!.map(cross).strings()
            return _cross!!.indices
                .map {
                    _cross!![it]!!.indices.toList().map {
                            i->
                        Invert(
                            _cross!![it]!![i].toString(),
                            cross.hiddenLocations!!.any {
                                    location->location.row==it &&
                                    location.column==i
                            }
                        )
                    }
                }
        } else {
            return listOf()
        }


    }
}