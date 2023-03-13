package com.vocabulary.entities

import javax.persistence.*

@Entity
@Table(name = "hidden_locations")
class HiddenLocations {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "board_id", referencedColumnName = "id")
    var board: Board? = null

    @Column(name = "_row")
    var row: Int? = null

    @Column(name = "_column")
    var column: Int? = null
}