package com.vocabulary.words

import org.springframework.stereotype.Component


@Component
class Permutation {

    fun words(allWords: List<String>):List<List<String>> {
        val out: MutableList<MutableList<Int>> = mutableListOf()
        permutation(allWords.size  , out)

        return out.map { it.map { i->allWords[i] } }
    }

    private fun permutation(count:Int, out: MutableList<MutableList<Int>>,index:Int = 0) {
        if(index==count) return
        else if(out.isEmpty()) {
            out.add(mutableListOf(index))
        } else {
            val _out = mutableListOf<MutableList<Int>>()

            for(array in out) {
                for(idx in (0..array.size).reversed()) {
                    val _array = array.toMutableList()
                    _array.add(idx, index)
                    _out.add(_array)
                }
            }
            out.clear()
            out.addAll(_out)
        }
        permutation(count, out, index+1)
    }

}