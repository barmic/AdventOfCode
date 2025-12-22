import groovy.transform.CompileStatic

import java.time.Duration
import java.time.Instant
import java.util.regex.Matcher
import java.util.regex.Pattern

import static java.lang.Math.*

@CompileStatic
record Sensor(long x, long y, long d) {}

@CompileStatic
static long manhatan(long x1, long y1, long x2, long y2) {
    return abs(x2 - x1) + abs(y2 - y1)
}

@CompileStatic
class LandP2 {
    List<Range<Long>> ranges = []

    void add(Range<Long> range) {
        int idx = 0
        List<Integer> idsToRemove = []
        long startRange = range.getFrom()
        def endRange = range.getTo()
        for (r in ranges) {
            if (r.getFrom() <= range.getTo() && r.getTo() >= range.getFrom()) {
                idsToRemove << idx
                startRange = min(startRange, r.getFrom())
                endRange = max(endRange, r.getTo())
            }
            idx += 1
        }
        idsToRemove.reverseEach { ranges.remove(it) }
        ranges << (startRange..endRange)
    }
}

List<Sensor> sensors = []

def startProg = Instant.now()
try (Scanner sc = new Scanner(new File('input'))) {
    def coordPattern = Pattern.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
    int idxLine = 0
    while (sc.hasNextLine()) {
        String line = sc.nextLine()
        Matcher matcher = coordPattern.matcher(line)
        if (matcher.find()) {
            long sx = Long.parseLong(matcher.group(1))
            long sy = Long.parseLong(matcher.group(2))
            long bx = Long.parseLong(matcher.group(3))
            long by = Long.parseLong(matcher.group(4))

            long d = manhatan(sx, sy, bx, by)

            sensors << new Sensor(sx, sy, d)
        } else {
            println(line)
            System.exit(1)
        }
        idxLine++
    }
}

for (long y in 0..4_000_000L) {
    def land = new LandP2()
    for (sensor in sensors) {
        long dy = abs(sensor.y() - y)
        if (dy > sensor.d()) continue

        long left = sensor.x() - (sensor.d() - dy)
        long right = sensor.x() + (sensor.d() - dy)
        land.add(left..right)
    }
    long x = searchX(land)
    if (x < 4_000_000L) {
        printf("y=%d x=%s %d%n", y, x, x * 4_000_000 + y)
        println(Duration.between(startProg, Instant.now()))
        System.exit(0)
    }
}

System.exit(1)

@CompileStatic
private static long searchX(LandP2 land) {
    long x = 0
    Range<Long> find
    do {
        find = land.ranges.find { it.containsWithinBounds(x) }
        if (find) {
            x = find.to + 1
        }
    } while (find)
    x
}
