import groovy.transform.Canonical
import groovy.transform.CompileStatic

import java.util.regex.Matcher
import java.util.regex.Pattern

@CompileStatic
record Node(String id, int flow, List<String> leads) {}
@Canonical
class Step {
    String id
    boolean opening = false
    long flowed = 0
    Step next = null
}

final Map<String, Node> nodes = readInput('example')

Step flowed = walk(nodes, nodes['AA'], [] as Set<Node>, 5)

while (flowed) {
    printf("%s %d\t%s%n", flowed.id, flowed.flowed, flowed.opening)
    flowed = flowed.next
}


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
                int flow = Integer.parseInt(matcher.group(2))
                List<String> leads = List.of(matcher.group(3).split("[, ]+"))
                nodes[id] = new Node(id, flow, leads)
            } else {
                throw new RuntimeException("Impossible to parse $line")
            }
        }
    }
    nodes
}

@CompileStatic
private static Step walk(Map<String, Node> nodes, Node here, Set<Node> opened = [], int countdown = 30) {
    if (countdown <= 0) return null
//    long flowed = 0
    int increment = 1
    def newOpened = opened
    Step hereStep = new Step(id: here.id())
    if (!opened.contains(here) && here.flow() > 0) {
        hereStep.opening = true
        hereStep.flowed += here.flow() * countdown
        newOpened += here
//        flowed += here.flow() * countdown
        increment += 1
    }
    List<Step> max = []
    for (next in here.leads()) {
        def current = walk(nodes, nodes[next], newOpened, countdown - increment)
        if (current) {
            max << current
        }
    }
    hereStep.next = max.max {it.flowed}
    hereStep
}
