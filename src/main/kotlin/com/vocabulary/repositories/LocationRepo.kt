package com.vocabulary.repositories

import com.vocabulary.entities.Board
import com.vocabulary.entities.Location
import com.vocabulary.entities.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationRepo:JpaRepository<Location, Int>  {
    fun findAllLocationByWordIdEquals(word: Int):List<Location>
}