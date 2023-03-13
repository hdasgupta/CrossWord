package com.vocabulary.mappers

import com.vocabulary.entities.HiddenLocations
import com.vocabulary.entities.Words
import com.vocabulary.words.*
import org.springframework.stereotype.Service

@Service
class BOToEntity {
    fun map(location: Location):com.vocabulary.entities.Location {
        val _location = com.vocabulary.entities.Location()

        _location.char = location.char.toString()
        _location.row = location.indices!!.row()
        _location.column = location.indices!!.column()

        return _location
    }

    fun map(direction: Direction): com.vocabulary.entities.Direction {
        return when(direction) {
            Direction.Horizontal->com.vocabulary.entities.Direction.Horizontal
            else -> com.vocabulary.entities.Direction.Vertical
        }
    }

    fun map(word: Word): com.vocabulary.entities.Word {
        val _word = com.vocabulary.entities.Word()

        _word.direction = map(word.direction()!!)

        _word.description = word.desc

        _word.locations = word.map { map(it) }

        _word.locations!!.forEach {
            it.word = _word
        }

        return _word
    }

    fun map(indices: Indices):HiddenLocations {
        val hiddenLocations = HiddenLocations()

        hiddenLocations.row = indices.row()
        hiddenLocations.column = indices.column()

        return hiddenLocations
    }

    fun map(board: Board):com.vocabulary.entities.Board {
        val _board = com.vocabulary.entities.Board()

        _board.words = board.map { map(it) }

        _board.hiddenLocations = board.locations().map { map(it) }

        _board.words!!.forEach {
            it.board = _board
        }

        _board.hiddenLocations!!.forEach {
            it.board = _board
        }

        return _board
    }

    fun map(words: com.vocabulary.words.Words):Words {
        val _words = Words()

        _words.wordList = words.words.joinToString(",")

        _words.boards = words.all()
            .map { map(it) }

        _words.boards!!.forEach {
            it.allWords = _words
        }

        return _words
    }
}