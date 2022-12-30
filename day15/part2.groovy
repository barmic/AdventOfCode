import groovy.transform.CompileStatic

import java.time.Duration
import java.time.Instant
import java.util.regex.Matcher
import java.util.regex.Pattern

import static java.lang.Math.*

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

@CompileStatic
static long coord(long x, long y) {
    long validx = max(min(4_000_000, x), 0)
    long validy = max(min(4_000_000, y), 0)
    return validx * 4_000_000 + validy
}

@CompileStatic
static List<Long> rcoord(long coord) {
    long x = coord / 4_000_000L as long
    long y = coord % 4_000_000L
    return [x, y]
}

def startProg = Instant.now()
def land = new LandP2()
try (Scanner sc = new Scanner(new File('input'))) {
    def coordPattern = Pattern.compile("Sensor at x=(\\d+), y=(\\d+): closest beacon is at x=(\\d+), y=(\\d+)")
    int idxLine = 0
    while (sc.hasNextLine()) {
        Matcher matcher = coordPattern.matcher(sc.nextLine())
        if (matcher.find()) {
            long sx = Long.parseLong(matcher.group(1))
            long sy = Long.parseLong(matcher.group(2))
            long bx = Long.parseLong(matcher.group(3))
            long by = Long.parseLong(matcher.group(4))

            long d = manhatan(sx, sy, bx, by)
            long left = sx - d
            long right = sx + d
            if (idxLine == 6) printf("[%d;%d] => [%d;%d]%n", left, sy, right, sy)
            land.add(coord(left, sy)..coord(right, sy))
            for (y in 1..d) {
                long bottom = sy + y
                long top = sy - y
                if (idxLine == 6) printf("[%d;%d] => [%d;%d]%n", left + y, bottom, right - y, bottom)
                land.add(coord(left + y, bottom)..coord(right - y, bottom))
                if (idxLine == 6) printf("[%d;%d] => [%d;%d]%n", left + y, top, right - y, top)
                land.add(coord(left + y, top)..coord(right - y, top))
            }
        }
        idxLine++
    }
}
c = 0
while (c < 4_000_000) {
    def find = land.ranges.find { it.containsWithinBounds(c) }
    if (find) {
        c = find.getTo() + 1
    } else {
        def start = rcoord(c)
        printf("[%d;%d] => %d%n", start[0], start[1], c)
    }
}
long expected = 13_081_194_638_237
def exp = rcoord(expected)
printf("[%d;%d] => %d%n", exp[0], exp[1], expected)
for (r in land.ranges) {
    def begin = rcoord(r.getFrom())
    def end = rcoord(r.getTo())
    printf("from [%d;%d] => [%d;%d]%n", begin[0], begin[1], end[0], end[1])
}
//3_270_298
//2_638_237
println(Duration.between(startProg, Instant.now()))
