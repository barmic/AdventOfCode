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
Walker.valves = readInput('input')

Result flowed = ForkJoinPool.commonPool().invoke(Walker.of(Walker.valves['AA']))

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

@CompileStatic
record Result(long value, String path, List<Long> parts = []) implements Comparable<Result> {
    @Override
    int compareTo(Result o) {
        return Long.compare(value, o.value)
    }

    Result add(Result o) {
        o == null ? this : new Result(value + o.value, path + o.path, parts + o.parts)
    }
}

@Canonical
@CompileStatic
class Walker extends RecursiveTask<Result> {
    public static Map<String, Node> valves
    final Node here
    Set<Node> opened = []
    int countdown = 30

    static Walker of(Node here) {
        Set<Node> opened = valves.values().findAll { it.flow() <= 0 } as Set<Node>
        return new Walker(here, opened)
    }

    protected Result compute() {
        int cost = 0
        Set<Node> newOpened = new HashSet<Node>(opened)
        long flowed = 0L

        def hereResult = new Result(flowed, here.id(), [flowed])
        if (!newOpened.contains(here) && here.flow() > 0) {
            newOpened << here
            cost += 1
            flowed += here.flow() * (countdown - 1)
            hereResult = new Result(flowed, here.id(), [flowed])
            if ((valves.keySet() - newOpened).isEmpty()) {
                return hereResult
            }
        }
        Map<String, Integer> leads = walk(valves, here.id, newOpened.collect { it.id() } as Set<String>)
        if (leads.isEmpty()) {
            return hereResult
        }

        Collection<RecursiveTask<Result>> subPath = []
        for (Map.Entry<String, Integer> next in leads) {
            Node nextValve = valves[next.key]
            if (countdown - next.value - cost > 0) {
                subPath << new Walker(nextValve, newOpened, countdown - next.value - cost)
            }
        }
        switch (subPath.size()) {
            case 0 -> hereResult
            case 1 -> hereResult.add(subPath.head().compute())
            default -> hereResult.add(invokeAll(subPath).collect { it.join() }.max())
        }
    }

    static Map<String, Integer> walk(Map<String, Node> nodes, String id, Set<String> opened) {
        Map<String, Integer> targets = nodes[id].leads().collectEntries { [(it): 1] }

        Set<String> passed = [id] as Set<String>
        Set<String> previousKeys = [] as Set<String>
        while (targets.keySet() - previousKeys) {
            previousKeys = new HashSet<>(targets.keySet())
            for (Map.Entry<String, Integer> target in new HashMap<>(targets)) {
                Node nodeTarget = nodes[target.key]
                if (nodeTarget.flow() <= 0 || opened.contains(target.key)) {
                    targets.remove(nodeTarget.id())
                }
                passed << nodeTarget.id()
                nodeTarget.leads()
                        .findAll { !passed.contains(it)}
                        .each {
                            targets.compute(it, { k, v ->
                                Math.min(
                                        v ?: Integer.MAX_VALUE,
                                        target.value + 1
                                )
                            })
                        }
            }
        }

        return targets
    }
}

