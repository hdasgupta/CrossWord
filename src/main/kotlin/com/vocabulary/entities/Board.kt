package com.vocabulary.entities

import java.util.stream.Collectors
import javax.persistence.*

@Entity
@Table(name = "boards")
class Board: Comparable<Board> {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "words_id", referencedColumnName = "id")
    var allWords: Words? = null

    @OneToMany(mappedBy = "id")
    var words: List<Word>? = null

    @OneToMany(mappedBy = "id")
    var hiddenLocations: List<HiddenLocations>? = null

    override fun toString(): String {
        val row = words!!.stream().mapToInt {
            it.locations!!.maxOf { loc -> loc.row as Int }
        }
            .max()
            .orElse(0)+1

        val column = words!!.stream().mapToInt {
            it.locations!!.maxOf { loc -> loc.column as Int }
        }
            .max()
            .orElse(0)+1

        val strs = (0 until row)
            .toList()
            .map { StringBuffer(" ".repeat(column)) }

        words!!.forEach {
            it.locations!!.forEach {
                    loc-> strs[loc.row!!].setCharAt(loc.column!!, loc.char!![0])
            }
        }

        return strs.joinToString("\n") {
            it.toString()
        }
    }

    override fun compareTo(other: Board): Int {
        val map1 = words!!.stream()
            .collect(
                Collectors.toMap(
                    fun(w: Word) = w.toString(),
                    fun(w: Word) = w
                )
            )

        val map2 = other.words!!.stream()
            .collect(
                Collectors.toMap(
                    fun(w: Word) = w.toString(),
                    fun(w: Word) = w
                )
            )

        val out = (0 until map1.size)
            .map {
                ArrayList(map1.values)[it].compareTo(ArrayList(map2.values)[it])
            }
            .firstOrNull { it!=0 }

        return out ?: 0

    }

}