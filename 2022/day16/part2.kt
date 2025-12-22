import java.io.File
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

data class Node(val id: String, val flow: Int, val indice: Long, val leads: Set<String>)

fun main() {
    var prevduration = Duration.ofDays(1)
    do {
        val start: Instant = Instant.now()
        day16()
        val duration = Duration.between(start, Instant.now())
        println(duration)
    } while (duration.minus(prevduration).abs().toMillis() > 500)
}
fun day16() {
    // prepare
    val valves: List<Node> = readInput("input")
//    val start1: Instant = Instant.now()
    val distances = distances(valves)

    // search
    val result1 = visit(valves, distances, "AA", 30).values.max()

    // result analyse
//    val duration = Duration.between(start1, Instant.now())
//    println("$visited $duration")

    // Part2
//    val start2: Instant = Instant.now()
    val visited2 = visit(valves, distances, "AA", 26)
    val result2 = cartesianProduct2(visited2.entries)
        .filter { entries -> entries.map { it.key }.reduce { acc, entry -> entry and acc } == 0L }
        .map { entries -> entries.sumOf { it.value } }
        .max()
//    val duration2 = Duration.between(start2, Instant.now())
    println("$result1 $result2")
}

////////////////////////////////////////////////////////////////////////////

fun readInput(inputFile: String): List<Node> {
    val nodes = ArrayList<Node>()
    Scanner(File(inputFile)).use {
        val pattern: Pattern =
            Pattern.compile("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)")
        while (it.hasNextLine()) {
            val matcher = pattern.matcher(it.nextLine())
            if (matcher.find()) {
                val id = matcher.group(1)
                val flow = Integer.parseInt(matcher.group(2))
                val leads = HashSet(matcher.group(3).split("[, ]+".toRegex()))
                val indice = (nodes.lastOrNull()?.indice ?: 1) shl 1
                nodes.add(Node(id, flow, indice, leads))
            }
        }
    }
    return nodes
}

fun visit(
    valves: List<Node>,
    distances: Map<Set<String>, Int>,
    valve: String,
    minutes: Int,
    bitmask: Long = 0,
    pressure: Int = 0,
    answer: MutableMap<Long, Int> = mutableMapOf()
): Map<Long, Int> {
    answer[bitmask] = max(answer[bitmask] ?: 0, pressure)
    valves.filter { it.flow > 0 && it.id != valve && it.indice and bitmask == 0L }.forEach {
        val remaining = minutes - distances[setOf(valve, it.id)]!! - 1
        if (remaining > 0) {
            visit(valves, distances, it.id, remaining, bitmask or it.indice, pressure + it.flow * remaining, answer)
        }
    }
    return answer
}

fun distances(valves: List<Node>): Map<Set<String>, Int> {
    val infinity = Integer.MAX_VALUE / 4
    val distance: MutableMap<Set<String>, Int> = cartesianProduct2(valves)
        .groupingBy { it.map { n -> n.id }.toSet() }
        .fold(infinity) { acc, next -> min(acc, if (next.first().leads.contains(next.last().id)) 1 else infinity) }
        .toMutableMap()

    cartesianProduct3(valves.map { it.id }).forEach {
        val (t, a, b) = listOf(setOf(it.first(), it.last()), it.minus(it.last()), it.minus(it.first()))
            .sortedByDescending { k -> distance.getOrDefault(k, infinity) }
        distance[t] = min(distance[t] ?: infinity, (distance[a] ?: infinity) + (distance[b] ?: infinity))
    }
    return distance
}

fun <A> cartesianProduct2(listA: Iterable<A>): Sequence<Set<A>> =
    sequence {
        listA.forEach { a ->
            listA.forEach { b ->
                if (a != b) {
                    yield(setOf(a, b))
                }
            }
        }
    }

fun <A> cartesianProduct3(listA: Iterable<A>): Sequence<Set<A>> =
    sequence {
        listA.forEach { a ->
            listA.forEach { b ->
                listA.forEach { c ->
                    val s = setOf(a, b, c)
                    if (s.size == 3) {
                        yield(s)
                    }
                }
            }
        }
    }

