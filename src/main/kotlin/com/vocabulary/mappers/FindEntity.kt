package com.vocabulary.mappers

import com.vocabulary.entities.*
import com.vocabulary.repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class FindEntity {

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
        wordsRepo.findById(entity)
            .map {
                it.boards = boardRepo.findAllBoardByAllWordsIdEquals(entity)

                it.boards!!
                    .map { b->findBoard(b.id!!)!! }
                it

            }

    fun findBoard(entity:Int):Board? =
        boardRepo.findById(entity)
            .map {
                it.words = wordRepo.findAllWordByBoardIdEquals(entity)
                    .map { w-> findWord(w.id!!) }
                it.hiddenLocations = hiddenLocationsRepo.findAllHiddenLocationsByBoardIdEquals(entity)
                    .map { h->  findHiddenLocation(h.id!!) }
                it
            }
            .orElse(null)

    fun findWord(entity:Int):Word =
        wordRepo.findById(entity)
            .map {
                it.locations = locationRepo.findAllLocationByWordIdEquals(entity)
                    .map { l-> findLocation(l.id!!) }
                it
            }
            .get()

    fun findLocation(entity:Int):Location =
        locationRepo.findById(entity).get()

    fun findHiddenLocation(entity:Int):HiddenLocations =
        hiddenLocationsRepo.findById(entity).get()

}