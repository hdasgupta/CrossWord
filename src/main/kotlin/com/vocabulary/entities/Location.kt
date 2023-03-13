package com.vocabulary.entities

import javax.persistence.*

@Entity
@Table(name = "locations")
class Location {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    var word: Word? = null

    @Column
    var char: String? = null

    @Column(name = "_row")
    var row: Int? = null

    @Column(name = "_column")
    var column: Int? = null


}
