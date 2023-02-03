import java.time.Duration
import java.time.Instant

fun main() {
    val blocks = Infinity("_+⅃|⎕".toCharArray().toTypedArray())
    val shifts = Infinity(">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>".toCharArray().toTypedArray())

    val numberBlocks = 2022
    val maxSize = (numberBlocks / 4 * (1 + 3 + 3 + 4 + 2)) + 4
    val bottom = ByteArray(maxSize + 1) { 0 }
    bottom[0] = 0b11111110.toByte()
    var size = 1
    val start = Instant.now()

    for (i in 0 until numberBlocks) {
        var block = newBlock(blocks.next())
        var coord = size + 3

        var newCoord = coord
        do {
            coord = newCoord
            block = shift(block, shifts.next(), bottom, coord)

            if (block and current(bottom, coord - 1) == 0) {
                newCoord = coord.dec()
            }

        } while (newCoord != coord)

        for (j in 0..3) bottom[coord + j] = (bottom[coord + j].toInt() or (block shr (j * 8))).toByte()

        size = bottom.takeWhile { it.toInt() != 0 }.size
    }
//    display(bottom)
//    println("1234567")
    println(size - 1)
    println(Duration.between(start, Instant.now()))
}

private fun shift(block: Int, move: Char, bottom: ByteArray, coord: Int): Int {
    if (move == '>' && block and 0b0000001_1__0000001_1__0000001_1__0000001_1 == 0) {
        val shifted = block shr 1
        if (shifted and current(bottom, coord) == 0) {
            return shifted
        }
    } else if (move == '<' && block and 0b10000000_10000000_10000000_10000001.toInt() == 0) {
        val shifted = block shl 1
        if (shifted and current(bottom, coord) == 0) {
            return shifted
        }
    }
    return block
}

private fun current(bottom: ByteArray, size: Int) = bottom
    .sliceArray(size until size + 4)
    .mapIndexed { idx, b -> b.toInt() and 0xff shl (idx * 8) }
    .reduceRight { a, b -> a or b }

fun newBlock(block: Char): Int {
    return when (block) {
        '_' -> 0b0000000_0__0000000_0__0000000_0__0011110_0
        '|' -> 0b0010000_0__0010000_0__0010000_0__0010000_0
        '+' -> 0b0000000_0__0001000_0__0011100_0__0001000_0
        '⅃' -> 0b0000000_0__0000100_0__0000100_0__0011100_0
        '⎕' -> 0b0000000_0__0000000_0__0011000_0__0011000_0
        else -> throw RuntimeException()
    }
}

class Infinity<T>(private val datas: Array<T>) : Iterator<T> {
    private var i: Int = 0
    override fun hasNext(): Boolean = true
    override fun next(): T = datas[i].also {
        i = i.inc() % datas.size
    }
}

fun display(map: ByteArray) {
    for (l in map.sliceArray(1 until map.size).reversed()) {
        if (l.toInt() == 0) continue
        println(CharArray(8) { if (l.toInt() and (1 shl it) == 0) '.' else '#' }.reversed().joinToString(""))
    }
}
