import groovy.transform.Canonical
import groovy.transform.CompileStatic

import java.time.Duration
import java.time.Instant
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.regex.Matcher
import java.util.regex.Pattern

@CompileStatic
record Node(String id, int flow, Set<String> leads) {}

def start = Instant.now()
Walker.valves = readInput('example')

Walker.entries = Walker.valves.values().findAll { it.id() == 'AA' || it.flow() > 0 }.collectEntries { [(it.id()): walk(Walker.valves, it.id())] }

long flowed = ForkJoinPool.commonPool().invoke(Walker.of(Walker.valves['AA']))

printf("%s (%s)%n", flowed, Duration.between(start, Instant.now()))

////////////////////////////////////////////////////////////////////////////

@CompileStatic
private static Map<String, Node> readInput(String inputFile) {
    Map<String, Node> nodes = [:]
    try (Scanner sc = new Scanner(new File(inputFile))) {
        Pattern pattern = Pattern.compile("Valve (\\w+) has flow rate=(\\d+); tunnel(?:s)? lead(?:s)? to valve(?:s)? (.*)")
        while (sc.hasNextLine()) {
            String line = sc.nextLine()
            Matcher matcher = pattern.matcher(line)
            if (matcher.find()) {
                String id = matcher.group(1)
                int flow = matcher.group(2) as Integer
                Set<String> leads = matcher.group(3).split("[, ]+") as Set<String>
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
    public static Map<String, Node> valves
    public static Map<String, Map<String, Integer>> entries
    final Node me
    final Node elephant
    Set<String> opened = []
    int countdown = 26

    static Walker of(Node here) {
        Set<String> opened = valves.values().findAll { it.flow() <= 0 }.collect{it.id()} as Set<String>
        return new Walker(here, here, opened)
    }

    @CompileStatic
    protected Long compute() {
        Set<String> newOpened = new HashSet<String>(opened)
        long flowed = 0L

        for (final def here in [me, elephant]) {
            if (!newOpened.contains(here) && here.flow() > 0) {
                newOpened << here.id()
                flowed += here.flow() * (countdown - 1)
                if (valves.size() <= newOpened.size()) {
                    return flowed
                }
            }
        }
        Map<String, Integer> myLeads = entries[me.id()]
                .findAll { !newOpened.contains(it.key) && valves[it.key].flow() > 0 }
        Map<String, Integer> elephantLeads = entries[elephant.id()]
                .findAll { !newOpened.contains(it.key) && valves[it.key].flow() > 0 }
        if (myLeads.isEmpty() && elephantLeads.isEmpty()) {
            return flowed
        }

        Collection<RecursiveTask<Long>> subPath = []
        int cost = newOpened.size() - opened.size()
        for (Map.Entry<String, Integer> myNext in myLeads.findAll {it.value < (countdown - cost)}) {
            for (Map.Entry<String, Integer> elephantNext in elephantLeads.findAll {it.value < (countdown - cost)}) {
                subPath << new Walker(valves[myNext.key], valves[elephantNext.key], newOpened, countdown - myNext.value - cost)
            }
        }
        switch (subPath.size()) {
            case 0 -> flowed
            case 1 -> flowed + subPath.head().compute()
            default -> flowed + invokeAll(subPath).collect { it.join() }.max()
        }
    }
}

static Map<String, Integer> walk(Map<String, Node> nodes, String id) {
    Map<String, Integer> targets = nodes[id].leads().collectEntries { [(it): 1] }

    Set<String> passed = [id] as Set<String>
    Set<String> previousKeys = [] as Set<String>
    while (targets.keySet() - previousKeys) {
        previousKeys = new HashSet<>(targets.keySet())
        for (Map.Entry<String, Integer> target in new HashMap<>(targets)) {
            Node nodeTarget = nodes[target.key]
            if (nodeTarget.flow() <= 0) {
                targets.remove(nodeTarget.id())
            }
            passed << nodeTarget.id()
            nodeTarget.leads()
                    .findAll { !passed.contains(it) && targets.get(it, Integer.MAX_VALUE) > targets + 1}
                    .each {targets[it] = target.value + 1}
        }
    }

    return targets
}

