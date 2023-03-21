package com.vocabulary.controller

import com.vocabulary.entities.Board
import com.vocabulary.entities.HiddenLocations
import com.vocabulary.entities.Location
import com.vocabulary.mappers.BOToEntity
import com.vocabulary.mappers.EntityToBO
import com.vocabulary.mappers.FindEntity
import com.vocabulary.mappers.SaveEntity
import com.vocabulary.repositories.BoardRepo
import com.vocabulary.repositories.WordsRepo
import com.vocabulary.words.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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

    @RequestMapping(value = ["/addDesc"])
    fun addDesc(@RequestParam words:String, map: ModelMap): String {
        val wordList = words.split(",").map { it.uppercase().trim() }.filter { it.isNotEmpty() }

        map["words"] = wordList

        return "Description"
    }


    @PostMapping(value = ["/html"])
    fun html(request: HttpServletRequest, map: ModelMap): String {

        val wordCount = request.getParameter("wordCount").toInt()

        val wordList = (0 until wordCount)
            .map { request.getParameter("word_$it") }

        val descList = (0 until wordCount)
            .map { request.getParameter("desc_$it") }

        val wordPermutation = permutation.words(wordList)
        val descPermutation = permutation.words(descList)

        val wordsListPermutation = wordPermutation
            .map { it.joinToString(",") }

        //wordsRepo.deleteAll()

        var _wordsList = wordsRepo.findAllWordsByWordListInAndIdIsNotNull(wordsListPermutation)
            .mapNotNull { findEntity.findWords(it.id!!).orElse(null) }

        if(_wordsList.isEmpty()) {
            _wordsList = wordPermutation.indices
                .map { Words(wordPermutation[it], descPermutation[it]) }
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

        map["random"] = results.random()

        return "List"
    }

    @RequestMapping(value = ["/cross"])
    fun cross(@RequestParam cross: Int, map: ModelMap): String {
        map["results"] = getResult(findEntity.findBoard(cross))

        return "CrossWord"
    }

    @GetMapping(value = ["/choose"])
    fun choose(@RequestParam choose: Int, @RequestParam clue : Boolean = false, map: ModelMap): String {
        val board = findEntity.findBoard(choose)

        map["results"] =
//            if(!clue)
//                getResult(board)
//            else
                getIndexes(board)

        map["descriptions"] =
                board!!.words!!.map { it.description!! }

        map["clue"] = clue

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

    fun getIndexes(cross: Board?): List<List<Output>> {
        if(cross != null) {
            val locations =  cross.words!!
                .flatMap { it.locations!! }

            val hidden = cross.hiddenLocations!!
                .stream()
                .collect(Collectors.toMap(
                    fun(location:HiddenLocations)=location.row!!,
                    fun(location:HiddenLocations)= listOf(location.column!!),
                    fun(list1:List<Int>, list2:List<Int>):List<Int> {
                        val list = mutableListOf<Int>()
                        list.addAll(list1)
                        list.addAll(list2)
                        return list
                    }
                ))

            val words = cross.words!!.indices
                .toList()
                .stream()
                .collect(Collectors.toMap(
                    fun(i:Int)=cross.words!![i].locations!![0].row!!,
                    fun(i:Int)= mapOf(Pair(cross.words!![i].locations!![0].column!!, listOf(i+1))),
                    fun(map1:Map<Int, List<Int>>, map2:Map<Int, List<Int>>):Map<Int, List<Int>> {
                        val map = mutableMapOf<Int, MutableList<Int>>()
                        map1.forEach {
                            map[it.key] = it.value.toMutableList()
                        }
                        map2.forEach {
                            if(map.containsKey(it.key)) {
                                map[it.key]?.addAll(it.value)
                            } else {
                                map[it.key] = it.value.toMutableList()
                            }
                        }
                        return map.map { Pair<Int, List<Int>>(it.key, it.value) }.toMap()
                    }
                    ))

            val chars = locations.stream()
                .collect(Collectors.toMap(
                    fun(location: Location)=location.row!!,
                    fun(location:Location)=mutableMapOf(Pair(location.column!!, location.char!!)),
                    fun(map1:MutableMap<Int, String>, map2:MutableMap<Int, String>):MutableMap<Int, String> {
                        val map = mutableMapOf<Int, String>()
                        map.putAll(map1)
                        map.putAll(map2)
                        return map
                    }
                ))

            val row:Int = locations.maxOf { location->location.row!! }
            val column:Int = locations.maxOf { location->location.column!! }

            return (0 .. row)
                .map {
                    r->
                    (0 .. column)
                        .map {
                            c->
                            Indexed(
                                if(chars.containsKey(r) && chars[r]!!.containsKey(c)) {
                                    chars[r]!![c]!!
                                } else {
                                    " "
                                },
                                if(words.containsKey(r) && words[r]!!.containsKey(c)) {
                                    words[r]!![c]!!.joinToString(",")
                                } else {
                                    " "
                                },
                                hidden.containsKey(r) && hidden[r]!!.contains(c)
                            )

                        }
                }

        } else {
            return listOf()
        }

    }

    fun getResult(cross: Board?): List<List<Output>>  {

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