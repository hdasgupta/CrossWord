package com.vocabulary.words

import java.util.HashMap
import java.util.TreeSet

class Words (words: List<String>) {
    private val combinations = add(words)

    private fun add(words: List<String>, index: Int=0, boards :TreeSet<Board> = TreeSet()):TreeSet<Board> =
        if(words.size==index) {
            boards
        } else {
            val _boards = TreeSet<Board>()

            if(index==0) {
                val board = Board()
                _boards.addAll(
                    board.add(words[index])
                )
            } else {
                boards.map {
                    board ->
                    _boards.addAll(
                        board.add(words[index])
                    )
                }
            }

            add(words, index + 1, _boards)
        }

    fun all(): Set<Board> = combinations

}