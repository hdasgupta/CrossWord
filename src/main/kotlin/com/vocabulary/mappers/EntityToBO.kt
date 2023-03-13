package com.vocabulary.mappers

import com.vocabulary.entities.*
import com.vocabulary.words.Board
import com.vocabulary.words.Indices
import org.springframework.stereotype.Service


@Service
class EntityToBO {
    fun map(location: Location) : com.vocabulary.words.Location {
        return com.vocabulary.words.Location(
            location.char!![0],
            Indices(
                location.row!!,
                location.column!!
            )
        )
    }

    fun map(word: com.vocabulary.words.Word, direction: Direction):Unit {
        when(direction) {
            Direction.Horizontal -> word.horizontal()
            else -> word.vertical()
        }
    }

    fun map(word: Word) : com.vocabulary.words.Word {
        val _word = com.vocabulary.words.Word(" ".repeat(word.locations!!.size))
        (0 until word.locations!!.size)
            .forEach {
                _word[it] = map(word.locations!![it])
            }

        map(_word, word.direction!!)

        return _word
    }

    fun map(hiddenLocations: HiddenLocations):Indices {
        return Indices(hiddenLocations.row!!, hiddenLocations.column!!)

    }

    fun map(board: com.vocabulary.entities.Board) : Board {
        val _board = Board()

        _board.addAll(
            board.words!!.map { map(it) }
        )

        _board.locations(
            board.hiddenLocations!!.map { map(it) }
        )
        return _board
    }

    fun map(words: Words):com.vocabulary.words.Words {
        val _words = com.vocabulary.words.Words(
            words.wordList!!.split(",")
        )

        _words.all(words.boards!!.map { map(it) })

        return _words
    }
}