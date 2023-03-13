package com.vocabulary.mappers

import com.vocabulary.entities.*
import com.vocabulary.repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SaveEntity {

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

    fun save(entity:Words) {
        wordsRepo.save(entity)
        entity.boards!!.forEach {
            save(it)
        }
        wordsRepo.save(entity)
    }

    fun save(entity:Board) {
        boardRepo.save(entity)
        entity.words!!.forEach {
            save(it)
        }

        entity.hiddenLocations!!.forEach {
            save(it)
        }
        boardRepo.save(entity)
    }

    fun save(entity:Word) {
        wordRepo.save(entity)
        entity.locations!!.forEach {
            save(it)
        }
        wordRepo.save(entity)
    }

    fun save(entity:Location) {
        locationRepo.save(entity)
    }

    fun save(entity:HiddenLocations) {
        hiddenLocationsRepo.save(entity)
    }

}