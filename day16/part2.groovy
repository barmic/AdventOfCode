import groovy.transform.CompileStatic

import java.time.Duration
import java.time.Instant
import java.util.regex.Matcher
import java.util.regex.Pattern

import static java.lang.Math.*

@CompileStatic
record Node(String id, int flow, int indice, Set<String> leads) {}

def start = Instant.now()
class Setup {
    static Map<String, Node> valves
    static Map<Set<String>, Integer> distances
}

Setup.valves = readInput('input')
Setup.distances = distances(Setup.valves)
def foo = visit('AA', 26)

printf("%s (%s)%n", foo.values().max(), Duration.between(start, Instant.now()))

////////////////////////////////////////////////////////////////////////////

@CompileStatic
private static Map<String, Node> readInput(String inputFile) {
    Map<String, Node> nodes = [:]
    try (Scanner sc = new Scanner(new File(inputFile))) {
        Pattern pattern = Pattern.compile("Valve (\\w+) has flow rate=(\\d+); tunnel(?:s)? lead(?:s)? to valve(?:s)? (.*)")
        int i = 0
        while (sc.hasNextLine()) {
            Matcher matcher = pattern.matcher(sc.nextLine())
            if (matcher.find()) {
                String id = matcher.group(1)
                int flow = matcher.group(2) as Integer
                Set<String> leads = matcher.group(3).split("[, ]+") as Set<String>
                nodes[id] = new Node(id, flow, 1 << i++, leads)
            }
        }
    }
    nodes
}

@CompileStatic
private static Map<Integer, Integer> visit(String valve, int countdown, int bitmask = 0, Integer flowed = 0, Map<Integer, Integer> answer = [:]) {
    answer[bitmask] = max(answer.get(bitmask, 0), flowed)
    for (final def node in Setup.valves.values().findAll { it.id() != valve && it.flow() > 0 }) {
        int remain = countdown - Setup.distances[Set.of(valve, node.id())] - 1
        if (remain <= 0 || node.indice() & bitmask) continue
        visit(node.id(), remain, node.indice() | bitmask, flowed + (node.flow() * remain), answer)
    }
    return answer
}

//@CompileStatic
private static Map<Set<String>, Integer> distances(Map<String, Node> valves) {
    Map<Set<String>, Integer> distance = [valves.keySet(), valves.keySet()]
            .combinations()
            .findAll { distinct(it as List) }
            .collectEntries { [(it as Set): valves[it[0]].leads().contains(it[1]) ? 1 : Integer.MAX_VALUE >> 1]}

    for (def triple in ([valves.keySet(), valves.keySet(), valves.keySet()].combinations().findAll { distinct(it as List<String>) } as List<List<String>>)) {
        Set<String> key = triple[0, 1] as Set<String>
        distance[key] = min(distance[key], distance[triple[0, 2] as Set<String>] + distance[triple[1, 2] as Set<String>])
    }
    return distance
}

@CompileStatic
private static boolean distinct(Collection a) {
    a.size() == (a as Set).size()
}
