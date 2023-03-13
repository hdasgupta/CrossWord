package com.vocabulary.repositories

import com.vocabulary.entities.Board
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardRepo:CrudRepository<Board, Int> {

    fun findAllBoardByAllWordsIdEquals(wordsId: Int):List<Board>

}