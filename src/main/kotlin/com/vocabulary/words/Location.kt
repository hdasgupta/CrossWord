package com.vocabulary.words

class Location(val char: Char, var indices: Indices? = null) {
    fun copy(): Location =
        Location(char, if(indices!=null) Indices(indices!!.row(), indices!!.column()) else null)

}