package com.vocabulary.words

open abstract class Output(val char: String, val index: String = "", val inverted: Boolean, var error: Boolean = false)

class Invert( char: String, inverted: Boolean = false, error: Boolean = false):Output(char, "", inverted, error)

class Indexed( char: String, index: String, inverted: Boolean = false, error: Boolean = false):Output(char, index, inverted, error)
