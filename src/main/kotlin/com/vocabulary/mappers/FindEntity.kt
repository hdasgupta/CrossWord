package com.vocabulary.mappers

import com.vocabulary.entities.*
import com.vocabulary.repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class FindEntity {

    val words = sortedMapOf<Int, Words>()
    val board = sortedMapOf<Int, Board>()
    val word = sortedMapOf<Int, Word>()
    val location = sortedMapOf<Int, Location>()
    val hiddenLocation = sortedMapOf<Int, HiddenLocations>()

    val boards = mutableSetOf<Board>()
    val locations = mutableSetOf<Location>()
    val hiddenLocations = mutableSetOf<HiddenLocations>()

    @Autowired
    lateinit var wordsRepo: WordsRepo

    @Autowired
    lateinit var boardRepo: BoardRepo

    @Autowired
    lateinit var wordRepo: WordRepo

    @Autowired
    lateinit var locationRepo: LocationRepo

    @Autowired
    lateinit var hiddenLocationsRepo: HiddenLocationsRepo

    fun findWords(entity:Int): Optional<Words> =
        if(words.containsKey(entity)) {
            Optional.of(words[entity]!!)
        } else {
            wordsRepo.findById(entity)
                .map {
                    it.boards = boardRepo.findAllBoardByAllWordsIdEquals(entity)

                    it.boards!!
                        .map { b -> findBoard(b.id!!)!! }

                    words[entity] = it

                    it

                }
        }

    fun findBoard(entity:Int):Board? =
        if(board.containsKey(entity)) {
            board[entity]
        } else {
            boardRepo.findById(entity)
                .map {
                    it.words = wordRepo.findAllWordByBoardIdEquals(entity)
                        .map { w -> findWord(w.id!!) }
                    it.hiddenLocations = hiddenLocationsRepo.findAllHiddenLocationsByBoardIdEquals(entity)
                        .map { h -> findHiddenLocation(h.id!!) }

                    boards.add(it)

                    board[entity] = boards.find { b-> b == it }

                    it
                }
                .orElse(null)
        }

    fun findWord(entity:Int):Word =
        if(word.containsKey(entity)) {
            word[entity]!!
        } else {
            wordRepo.findById(entity)
                .map {
                    it.locations = locationRepo.findAllLocationByWordIdEquals(entity)
                        .map { l -> findLocation(l.id!!) }

                    word[entity] = it
                    it
                }
                .get()
        }

    fun findLocation(entity:Int):Location =
        if(location.containsKey(entity)) {
            location[entity]!!
        } else {
            locationRepo.findById(entity)
                .map {
                    locations.add(it)

                    location[entity] = locations.find { loc->loc==it }

                    it
                }
                .get()
        }

    fun findHiddenLocation(entity:Int):HiddenLocations =
        if(hiddenLocation.containsKey(entity)) {
            hiddenLocation[entity]!!
        } else {
            hiddenLocationsRepo.findById(entity)
                .map {
                    hiddenLocations.add(it)

                    hiddenLocation[entity] = hiddenLocations.find { hide->hide==it }

                    it
                }
                .get()
        }

}