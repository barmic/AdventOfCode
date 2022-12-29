import groovy.transform.CompileStatic

import java.time.Duration
import java.time.Instant
import java.util.regex.Pattern

import static java.lang.Math.min
import static java.lang.Math.max

def start = Instant.now()

record Point(int x, int y) {}

@CompileStatic
static Set<Point> readInput(Scanner sc) {
    def pointPattern = Pattern.compile("(\\d+),(\\d+)")
    Set<Point> rocks = []

    while (sc.hasNextLine()) {
        def line = sc.nextLine()
        def matcher = pointPattern.matcher(line)
        Point prev = null
        while (matcher.find()) {
            def current = new Point(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2))
            )
            rocks << current
            if (prev) {
                rocks.addAll((min(prev.x(), current.x())..max(prev.x(), current.x()))
                        .collect {new Point(it, current.y())})
                rocks.addAll((min(prev.y(), current.y())..max(prev.y(), current.y()))
                        .collect {new Point(current.x(), it)})
            }
            prev = current
        }
    }
    return rocks
}

Set<Point> rocks = []
try (def sc = new Scanner(new File('input'))) {
    rocks = readInput(sc)
}

def floor = rocks.max { it.y() }.y() + 2

def sand = 0
def newSands = [new Point(500, 0)]
while (!newSands.isEmpty()) {
    def nextSands = [] as Set<Point>
    for (s in newSands) {
        Set<Point> fallen = fall(s)
                .findAll { !rocks.contains(it) }
                .findAll { it.y() < floor }
        nextSands.addAll(fallen)
    }
    rocks.addAll(nextSands)
    sand += nextSands.size()
    newSands = nextSands
}

println(sand)
def duration = Duration.between(start, Instant.now())
println(duration)

@CompileStatic
static Set<Point> fall(Point sand) {
    return [
            sand,
            new Point(sand.x(), sand.y() + 1),
            new Point(sand.x() - 1, sand.y() + 1),
            new Point(sand.x() + 1, sand.y() + 1)
    ] as Set<Point>
}
