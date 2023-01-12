import java.io.File
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.regex.Pattern

data class Node(val id: String, val flow: Int, val indice: Int, val leads: Set<String>)

//Map<Set<String>, Integer> distances = distances(Setup.valves)
//def foo = visit('AA', 26)
fun main(args: Array<String>) {
    val start: Instant = Instant.now()
    val valves: Map<String, Node> = readInput("input")
    println("Hello, World!")
    println("kjlkdf")
    println(Duration.between(start, Instant.now()).toString())
}

////////////////////////////////////////////////////////////////////////////

fun readInput(inputFile: String): Map<String, Node> {
    val nodes = HashMap<String, Node>()
    Scanner(File(inputFile)).use {
        val pattern: Pattern = Pattern.compile("Valve (\\w+) has flow rate=(\\d+); tunnel(?:s)? lead(?:s)? to valve(?:s)? (.*)")
        var i = 0
        while (it.hasNextLine()) {
            val matcher = pattern.matcher(it.nextLine())
            if (matcher.find()) {
                val id = matcher.group(1)
                val flow = Integer.parseInt(matcher.group(2))
                val leads = HashSet(matcher.group(3).split("[, ]+"))
                val indice = 1 shl i
                i += 1
                nodes[id] = Node(id, flow, indice, leads)
            }
        }
    }
    return nodes
}

//private static Map<Integer, Integer> visit(String valve, int countdown, int bitmask = 0, Integer flowed = 0, Map<Integer, Integer> answer = [:]) {
//    answer[bitmask] = max(answer.get(bitmask, 0), flowed)
//    for (final def node in Setup.valves.values().findAll { it.id() != valve && it.flow() > 0 }) {
//        int remain = countdown - Setup.distances[Set.of(valve, node.id())] - 1
//        if (remain <= 0 || node.indice() & bitmask) continue
//        visit(node.id(), remain, node.indice() | bitmask, flowed + (node.flow() * remain), answer)
//    }
//    return answer
//}
//
//private static Map<Set<String>, Integer> distances(Map<String, Node> valves) {
//    Map<Set<String>, Integer> distance = [valves.keySet(), valves.keySet()]
//            .combinations()
//            .findAll { distinct(it as List) }
//            .collectEntries { [(it as Set): valves[it[0]].leads().contains(it[1]) ? 1 : Integer.MAX_VALUE >> 1]}
//
//    for (def triple in ([valves.keySet(), valves.keySet(), valves.keySet()].combinations().findAll { distinct(it as List<String>) } as List<List<String>>)) {
//        Set<String> key = triple[0, 1] as Set<String>
//        distance[key] = min(distance[key], distance[triple[0, 2] as Set<String>] + distance[triple[1, 2] as Set<String>])
//    }
//    return distance
//}
//
//private static <T> boolean distinct(Collection<T> a) {
//    a.size() == (a as Set).size()
//}
