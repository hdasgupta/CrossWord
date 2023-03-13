package com.vocabulary.entities

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "words_combinations")
class Words {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null

    @Column
    var wordList:String? = null

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: Date = Date()

    @OneToMany(mappedBy = "id")
    var boards: List<Board>? = null
}