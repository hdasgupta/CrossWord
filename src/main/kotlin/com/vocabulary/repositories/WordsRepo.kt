package com.vocabulary.repositories

import com.vocabulary.entities.Words
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WordsRepo:JpaRepository<Words, Int> {
    fun findAllWordsByWordListInAndIdIsNotNull(wordList:List<String>): List<Words>
}