package com.vocabulary.words

import org.hibernate.dialect.H2Dialect
import java.util.HashMap
import java.util.TreeSet
import java.util.stream.Collectors

class Words (val words: List<String>) {
    private val singleChars = words.flatMap { it.asIterable() }
        .stream()
        .collect(Collectors.toMap(fun (c:Char)=c, fun(_:Char)=1, fun (a:Int, b:Int)=a+b))
        .filter { it.value==1 }
        .map { it.key }

    private val positions = (0 until words.maxOf {
        it.length
    })
        .toList()
        .stream()
        .collect(Collectors.toMap(fun (index: Int)=index, fun(index:Int)=
            words.filter { index < it.length - 1 }
                .map { it[index] }
                .toSet()
        ))
        .filter { it.value.size>1 }

    private var combinations = add(words)

    init {
        combinations.forEach {
            it.hide(singleChars, positions)
        }
    }


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

    fun all(combinations: List<Board>): Unit {
        this.combinations = TreeSet(combinations)
    }

}