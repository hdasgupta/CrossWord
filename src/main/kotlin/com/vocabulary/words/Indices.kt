package com.vocabulary.words

class Indices(_row:Int, _column:Int) {
    private var row: Int = _row
    private var column: Int = _column

    fun row():Int = row
    fun column():Int = column

    fun rowInc(inc:Int):Unit {
        row += inc
    }

    fun columnInc(inc:Int):Unit {
        column += inc
    }
}