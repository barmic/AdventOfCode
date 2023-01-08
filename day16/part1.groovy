import groovy.transform.Canonical
import groovy.transform.CompileStatic

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.function.Function
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

@CompileStatic
record Node(String id, int flow, Set<String> leads) {}

@CompileStatic
record Valve(String id, int flow, Map<String, Integer> leads) {}

Map<String, Node> nodes = readInput('exampleA')

Walker.valves = [:]
for (entry in nodes.entrySet()) {
    def map = walk(nodes, entry.key)
    Walker.valves[entry.key] = new Valve(entry.key, nodes[entry.key].flow(), map)
}

Walker.valves.each {printf("%s -> %s%n", it.key, String.join(";", it.value.leads().collect {it as String}))}

def flowed = ForkJoinPool.commonPool().invoke(Walker.of(Walker.valves['A']))

printf("%d%n", flowed)

////////////////////////////////////////////////////////////////////////////

@CompileStatic
private static Map<String, Node> readInput(String inputFile) {
    Map<String, Node> nodes = [:]
    try (Scanner sc = new Scanner(new File(inputFile))) {
        Pattern pattern = Pattern.compile("Valve (\\w+) has flow rate=(\\d+); tunnel(?:s)? lead(?:s)? to valve(?:s)? (.*)")
        while (sc.hasNextLine()) {
            def line = sc.nextLine()
            Matcher matcher = pattern.matcher(line)
            if (matcher.find()) {
                String id = matcher.group(1)
                int flow = matcher.group(2) as Integer
                def leads = matcher.group(3).split("[, ]+") as Set<String>
                nodes[id] = new Node(id, flow, leads)
            } else {
                throw new RuntimeException("Impossible to parse $line")
            }
        }
    }
    nodes
}

@Canonical
@CompileStatic
class Walker extends RecursiveTask<Long> {
    public static Map<String, Valve> valves
    final Valve here
    Set<Valve> opened = []
    int countdown = 30

    static Walker of(Valve here) {
        Set<Valve> opened = valves.values().findAll { it.flow() <= 0 } as Set<Valve>
        return new Walker(here, opened)
    }

    protected Long compute() {
        int cost = 0
        def newOpened = new HashSet(opened)
        long flowed = 0L
        if (!newOpened.contains(here) && here.flow() > 0) {
            newOpened << here
            cost += 1
            flowed += here.flow() * (countdown - 1)
            if ((valves.keySet() - newOpened).isEmpty()) {
                return flowed
            }
        }
        Map<Valve, RecursiveTask<Long>> subPath = [:]
        for (final def next in here.leads) {
            Valve nextValve = valves[next.key]
            if (countdown - next.value - cost > 0) {
                subPath[nextValve] = new Walker(nextValve, newOpened, countdown - next.value - cost)
                subPath[nextValve].fork()
            }
        }

        long sub = subPath.values().collect {it.join() ?: 0L}.max() ?: 0L
        return flowed + sub
    }
}

static Map<String, Integer> walk(Map<String, Node> nodes, String id) {
    Map<String, Integer> targets = new HashMap<>(nodes[id].leads().stream()
            .collect(Collectors.toMap(Function.identity(), i -> 1)))

    Set<String> passed = [id] as Set<String>
    while (targets.keySet().find { nodes[it].flow() <= 0 }) {
        for (target in new HashMap<>(targets)) {
            Node nodeTarget = nodes[target.key]
            if (nodeTarget.flow() <= 0) {
                targets.remove(nodeTarget.id())
                passed << nodeTarget.id()
                for (next in nodeTarget.leads()) {
                    if (!passed.contains(next)) {
                        targets.compute(next, { k, v -> Math.min(v ?: Integer.MAX_VALUE, target.value + 1) })
                    }
                }
            }
        }
    }

    return targets
}
