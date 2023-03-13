package com.vocabulary.entities

import java.util.stream.Collectors
import javax.persistence.*

@Entity
@Table(name = "words")
class Word:Comparable<Word> {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null

    @Column
    var direction: Direction? = null

    @ManyToOne
    @JoinColumn(name = "board_id", referencedColumnName = "id")
    var board: Board? = null

    @OneToMany(mappedBy = "id")
    var locations: List<Location>? = null

    override fun toString(): String {
        return locations!!.stream()
            .map { it.char.toString() }
            .collect(Collectors.joining(""))
    }

    override fun compareTo(other: Word): Int =
        if(toString() == other.toString()) {
            val out = (0 until locations!!.size)
                .map {
                    if(locations!![it].char==other.locations!![it].char) {
                        if(locations!![it].row==other.locations!![it].row) {
                            locations!![it].column!!.compareTo(other.locations!![it].column!!)
                        } else {
                            locations!![it].row!!.compareTo(other.locations!![it].row!!)
                        }
                    } else {
                        locations!![it].char!!.compareTo(other.locations!![it].char!!)
                    }
                }
                .firstOrNull { it!=0 }

            out ?: 0

        } else {
            toString().compareTo(other.toString())
        }

}