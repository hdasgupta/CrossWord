package com.vocabulary.words

import java.awt.Color
import java.awt.FontMetrics
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.util.stream.Collectors
import kotlin.math.roundToInt


class Board: ArrayList<Word>(), Comparable<Board> {
    private val direction = 30.0
    private lateinit var locations:MutableList<Indices>

    fun hide(singleChars:List<Char>, positions: Map<Int, Set<Char>>):Unit {

        val included = StringBuffer("N".repeat(size))

        val locations = mutableListOf<Indices>()

        singleChars.forEach {
            indices.forEach {
                i->
                if(this[i].any { location -> location.char==it } && included[i]=='N') {
                    locations.add(this[i].first { location -> location.char==it }.indices!!)
                    included.setCharAt(i, 'Y')
                }
            }
        }

        included.indices
            .filter { included[it]=='M' }
            .forEach {
                for(index in this[it].indices) {
                        if(positions.containsKey(index)) {
                            locations.add(this[it][index].indices!!)
                            break
                        }
                    }
                included.setCharAt(it, 'Y')
            }

        this.locations = locations
    }

    fun locations(): List<Indices> = locations.toList()

    fun locations(indices: List<Indices>):Unit {
        this.locations = indices.toMutableList()
    }

    fun add(word: String, desc: String): List<Board> =
        if(isEmpty()) {
            val word1 = Word(word, desc)
            val word2 = Word(word, desc)

            val board1 = Board()
            val board2 = Board()

            word1.horizontal()
            word2.vertical()

            word.indices
                .forEach {
                    word1[it].indices = Indices(0, it)
                    word2[it].indices = Indices(it,0)
                }

            board1.add(word1)
            board2.add(word2)

            listOf(board1, board2)
        }
        else {
            val matches = word.chars()
                .mapToObj {
                    c->
                    stream()
                        .flatMap {
                            it.indices
                                .filter { index -> it[index].char == c.toChar()}
                                .map { index -> MatchIndex(it, index) }
                                .stream()
                        }
                        .toList()

                }
                .toList()

            val list = matches.indices
                .flatMap {
                    matches[it]
                        .map {
                            match ->
                            when(match.word.direction()) {
                                Direction.Horizontal-> {
                                    val prependRow = (it-match.word[match.index].indices!!.row())
                                        var board:Board? = Board()
                                        board!!.addAll(
                                            stream()
                                                .map {
                                                        w->
                                                    val _word = w.copy()
                                                    if (prependRow>0)
                                                        _word.forEach {
                                                            location->
                                                            location.indices!!.rowInc(prependRow)
                                                        }

                                                    _word
                                                }
                                                .toList()
                                        )

                                        val _word = Word(word, desc)
                                    _word.indices
                                        .forEach {
                                                i->
                                            _word[i].indices = Indices(if(prependRow>=0) i else i-prependRow, match.word[match.index].indices!!.column())
                                        }

                                    val valid= _word.indices
                                            .toList()
                                            .stream()
                                            .map {
                                                    i->
                                                board!!.flatMap { _w ->
                                                    _w.filter { loc ->
                                                        loc.indices!!.row() == _word[i].indices!!.row() &&
                                                                loc.indices!!.column() == _word[i].indices!!.column()
                                                    }
                                                }
                                                    .sumOf {
                                                            location ->
                                                        if (location.char != _word[i].char)
                                                            2 as Int
                                                        else
                                                            1
                                                    }
                                            }
                                            .max(fun(o1, o2)=o1.compareTo(o2))
                                            .orElse(0) == 1

                                    val validTop= _word.indices
                                        .toList()
                                        .stream()
                                        .map {
                                                i->
                                            if (i==it)
                                                0
                                            else
                                                board!!.flatMap { _w ->
                                                    _w.filter { loc ->
                                                        loc.indices!!.row() == _word[i].indices!!.row() &&
                                                                loc.indices!!.column() == _word[i].indices!!.column() - 1
                                                    }
                                                }
                                                    .count()
                                        }
                                        .max(fun(o1, o2)=o1.compareTo(o2))
                                        .orElse(0) == 0

                                    val validBottom= _word.indices
                                        .toList()
                                        .stream()
                                        .map {
                                                i->
                                            if (i==it)
                                                0
                                            else
                                                board!!.flatMap { _w ->
                                                    _w.filter { loc ->
                                                        loc.indices!!.row() == _word[i].indices!!.row() &&
                                                                loc.indices!!.column() == _word[i].indices!!.column() + 1
                                                    }
                                                }
                                                    .count()
                                        }
                                        .max(fun(o1, o2)=o1.compareTo(o2))
                                        .orElse(0) == 0


                                    val emptyPrevCell = board.flatMap {
                                            _w ->
                                        _w.filter { loc ->

                                                    loc.indices!!.row() == _word.first().indices!!.row() - 1 &&
                                                    loc.indices!!.column() == _word.first().indices!!.column()
                                        }
                                    }.isEmpty()

                                    val emptyNextCell = board.flatMap {
                                            _w ->
                                        _w.filter { loc ->
                                                    loc.indices!!.row() == _word.last().indices!!.row() + 1 &&
                                                    loc.indices!!.column() == _word.last().indices!!.column()
                                        }
                                    }.isEmpty()

                                        if(!valid || !validTop || !validBottom || !emptyPrevCell || !emptyNextCell)
                                            board = null
                                        else {
                                            _word.vertical()

                                            board.add(_word)
                                        }

                                        board

                                    }
                                else-> {
                                    val prependColumn = (it-match.word[match.index].indices!!.column())
                                    var board:Board? = Board()
                                    board!!.addAll(
                                        stream()
                                            .map {
                                                    w->
                                                val _word = w.copy()
                                                if (prependColumn>0)
                                                    _word.forEach {
                                                        location ->
                                                        location.indices!!.columnInc(prependColumn)
                                                    }

                                                _word
                                            }
                                            .toList()
                                    )

                                    val _word = Word(word, desc)
                                    _word.indices
                                        .forEach {
                                                i->
                                            _word[i].indices = Indices(match.word[match.index].indices!!.row(), if (prependColumn>=0) i else i-prependColumn)

                                        }

                                    val valid= _word.indices
                                        .toList()
                                        .stream()
                                        .map {
                                                i->
                                            board!!.flatMap { _w ->
                                                _w.filter { loc ->
                                                    loc.indices!!.row() == _word[i].indices!!.row() &&
                                                            loc.indices!!.column() == _word[i].indices!!.column()
                                                }
                                            }
                                                .sumOf {
                                                        location ->
                                                    if (location.char != _word[i].char)
                                                        2 as Int
                                                    else
                                                        1 as Int
                                                }
                                        }
                                        .max(fun(o1, o2)=o1.compareTo(o2))
                                        .orElse(0) == 1

                                    val validPrev= _word.indices
                                        .toList()
                                        .stream()
                                        .map {
                                                i->
                                            if (i==it)
                                                0
                                            else
                                                board!!.flatMap { _w ->
                                                    _w.filter { loc ->
                                                        loc.indices!!.row() == _word[i].indices!!.row()  - 1 &&
                                                                loc.indices!!.column() == _word[i].indices!!.column()
                                                    }
                                                }
                                                    .count()
                                        }
                                        .max(fun(o1, o2)=o1.compareTo(o2))
                                        .orElse(0) == 0

                                    val validNext= _word.indices
                                        .toList()
                                        .stream()
                                        .map {
                                                i->
                                            if (i==it)
                                                0
                                            else
                                                board!!.flatMap { _w ->
                                                    _w.filter { loc ->
                                                        loc.indices!!.row() == _word[i].indices!!.row() + 1 &&
                                                                loc.indices!!.column() == _word[i].indices!!.column()
                                                    }
                                                }
                                                    .count()
                                        }
                                        .max(fun(o1, o2)=o1.compareTo(o2))
                                        .orElse(0) == 0

                                    val emptyTopCell = board.flatMap {
                                            _w ->
                                        _w.filter { loc ->

                                            loc.indices!!.row() == _word.first().indices!!.row() &&
                                                    loc.indices!!.column() == _word.first().indices!!.column()  - 1
                                        }
                                    }.isEmpty()

                                    val emptyBottomCell = board.flatMap {
                                            _w ->
                                        _w.filter { loc ->
                                            loc.indices!!.row() == _word.last().indices!!.row() &&
                                                    loc.indices!!.column() == _word.last().indices!!.column() + 1
                                        }
                                    }.isEmpty()


                                    if(!valid || !validPrev ||!validNext || !emptyTopCell || !emptyBottomCell)
                                        board = null
                                    else {
                                        _word.horizontal()

                                        board.add(_word)
                                    }

                                    board
                                }
                            }
                        }
                }
                .filterNotNull()
                .toList()

            list
        }

    override fun compareTo(other: Board): Int {
            val map1 = stream()
                .collect(
                    Collectors.toMap(
                        fun(w:Word) = w.toString(),
                        fun(w:Word) = w
                    )
                )

            val map2 = other.stream()
                .collect(
                    Collectors.toMap(
                        fun(w:Word) = w.toString(),
                        fun(w:Word) = w
                    )
                )

                val out = (0 until map1.size)
                    .map {
                        ArrayList(map1.values)[it].compareTo(ArrayList(map2.values)[it])
                    }
                    .firstOrNull { it!=0 }

            return out ?: 0

        }

    override fun toString(): String {
        val row = stream().mapToInt {
            it.maxOf { loc -> loc.indices!!.row() }
        }
            .max()
            .orElse(0)+1

        val column = stream().mapToInt {
            it.maxOf { loc -> loc.indices!!.column() }
        }
            .max()
            .orElse(0)+1

        val strs = (0 until row)
            .toList()
            .map { StringBuffer(" ".repeat(column)) }

        forEach {
            it.forEach {
                loc-> strs[loc.indices!!.row()].setCharAt(loc.indices!!.column(), loc.char)
            }
        }

        return strs.joinToString("\n") {
            it.toString()
        }
    }

    fun strings(): List<String> {
        val row = stream().mapToInt {
            it.maxOf { loc -> loc.indices!!.row() }
        }
            .max()
            .orElse(0)+1

        val column = stream().mapToInt {
            it.maxOf { loc -> loc.indices!!.column() }
        }
            .max()
            .orElse(0)+1

        val strs = (0 until row)
            .toList()
            .map { StringBuffer(" ".repeat(column)) }

        forEach {
            it.forEach {
                    loc-> strs[loc.indices!!.row()].setCharAt(loc.indices!!.column(), loc.char.uppercaseChar())
            }
        }



        return strs.map { it.toString() }
    }


    fun image(): BufferedImage {
        val row = stream().mapToInt {
            it.maxOf { loc -> loc.indices!!.row() }
        }
            .max()
            .orElse(0)+1

        val column = stream().mapToInt {
            it.maxOf { loc -> loc.indices!!.column() }
        }
            .max()
            .orElse(0)+1

        val image = BufferedImage(row*direction.toInt(), column*direction.toInt(), BufferedImage.TYPE_4BYTE_ABGR)

        val g2d = image.createGraphics()
        g2d.background = Color.BLUE


        forEach {
            it.forEach {
                    loc->
                    val shape = Rectangle2D.Double(
                        loc.indices!!.row()*direction,
                        loc.indices!!.column()*direction,
                        direction,
                        direction
                    )

                    val metrics: FontMetrics = g2d.getFontMetrics(g2d.font)

                    val x: Int = (shape.x + (shape.width - metrics.stringWidth(loc.char.toString())) / 2).roundToInt()
                    val y: Int = (shape.y + (shape.height - metrics.height) / 2 + metrics.ascent).roundToInt()

                    g2d.draw(shape)

                    g2d.drawString(loc.char.toString(), x, y);
            }
        }

        return image
    }


}