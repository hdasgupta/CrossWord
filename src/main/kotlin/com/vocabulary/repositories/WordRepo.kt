package com.vocabulary.repositories

import com.vocabulary.entities.Board
import com.vocabulary.entities.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WordRepo:JpaRepository<Word, Int> {
    fun findAllWordByBoardIdEquals(board: Int):List<Word>
}