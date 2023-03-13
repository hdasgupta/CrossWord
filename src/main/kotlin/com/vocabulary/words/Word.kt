package com.vocabulary.words

import java.util.stream.Collectors

class Word(str: String, val desc: String):
    ArrayList<Location>(str.chars().mapToObj { Location(it.toChar()) }.toList()),
    Comparable<Word> {

    private var direction: Direction? = null

    fun horizontal(): Unit {
        direction = Direction.Horizontal
    }

    fun vertical(): Unit {
        direction = Direction.Vertical
    }

    fun direction(): Direction? = direction

    override fun toString(): String {
        return stream()
            .map { it.char.toString() }
            .collect(Collectors.joining(""))
    }

    override fun compareTo(other: Word): Int =
            if(toString() == other.toString()) {
                val out = (0 until size)
                    .map {
                        if(this[it].char==other[it].char) {
                            if(this[it].indices!!.row()==other[it].indices!!.row()) {
                                this[it].indices!!.column().compareTo(other[it].indices!!.column())
                            } else {
                                this[it].indices!!.row().compareTo(other[it].indices!!.row())
                            }
                        } else {
                            this[it].char.compareTo(other[it].char)
                        }
                    }
                    .firstOrNull { it!=0 }

                out ?: 0

            } else {
                toString().compareTo(other.toString())
            }


    override fun equals(other: Any?): Boolean =
        other is Word &&
                size == other.size &&
                (0 until size)
                    .all {
                        this[it].char==other[it].char &&
                                this[it].indices!!.row()==other[it].indices!!.row() &&
                                this[it].indices!!.column()==other[it].indices!!.column()
                    }

    fun copy(): Word {
        val word = Word(toString(), desc)
        word.direction = direction
        word.indices.forEach {
            word[it] = this[it].copy()
        }
        return word
    }

}





