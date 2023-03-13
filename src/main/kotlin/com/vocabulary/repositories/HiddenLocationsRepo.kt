package com.vocabulary.repositories

import com.vocabulary.entities.Board
import com.vocabulary.entities.HiddenLocations
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HiddenLocationsRepo:JpaRepository<HiddenLocations, Int> {
    fun findAllHiddenLocationsByBoardIdEquals(word: Int): List<HiddenLocations>
}