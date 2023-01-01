import groovy.transform.CompileStatic

import java.util.regex.Matcher
import java.util.regex.Pattern

@CompileStatic
record Node(String id, int flow, List<String> leads) {}

Map<String, Node> nodes = readInput('example')

Node here = Objects.requireNonNull(nodes['AA'])
Set<Node> opened = []
int countdown = 30

flowed = 0

while (countdown > 0) {
    if (!opened.contains(here) && here.flow() > 0) {
        opened << here
        flowed += here.flow() * countdown
        printf("[%d] Open %s flowed:%d%n", countdown, here, flowed)
    } else {
        Node next = here.leads
                .collect { Objects.requireNonNull(nodes[it]) }
                .sort { n1, n2 -> opened.contains(n1) <=> opened.contains(n2) ?: n1.flow() <=> n2.flow() }
                .reverse()
                .head()
        printf("[%d] From %s → %s%n", countdown, here.id(), next)
        here = Objects.requireNonNull(next)
    }
    --countdown
}

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