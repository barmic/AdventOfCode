import java.util.regex.Pattern

import static java.lang.Math.*

record RangeP1(long start, long endInclusive) {
    long size() {
        return endInclusive() - start() + 1
    }
}

class LandP1 {
    List<RangeP1> ranges = []

    void add(RangeP1 range) {
        def idx = ranges.findIndexOf { it.start() <= range.endInclusive() && it.endInclusive() >= range.start() }
        if (idx >= 0) {
            def old = ranges.removeAt(idx)
            add(new RangeP1(min(old.start(), range.start()), max(old.endInclusive(), range.endInclusive())))
        } else {
            ranges << range
        }
    }
}

def input = [] as List<List<Long>>
try (Scanner sc = new Scanner(new File('input'))) {
    def coord = Pattern.compile("Sensor at x=(\\d+), y=(\\d+): closest beacon is at x=(\\d+), y=(\\d+)")
    while (sc.hasNextLine()) {
        def matcher = coord.matcher(sc.nextLine())
        if (matcher.find()) {
            List<Long> line = [
                    Long.parseLong(matcher.group(1)),
                    Long.parseLong(matcher.group(2)),
                    Long.parseLong(matcher.group(3)),
                    Long.parseLong(matcher.group(4))
            ]
            input << line
        }
    }
}
long Y = 2_000_000L
def land = new LandP1()

Set<Long> beaconsOnY = []

for (sensor in input) {
    def d = abs(sensor[0] - sensor[2]) + abs(sensor[1] - sensor[3])
    long dy = d - abs(sensor[1] - Y)
    if (dy <= 0) continue

    if (sensor[3] == Y) {
        beaconsOnY << sensor[2]
    }

    land.add(new RangeP1(sensor[0] - dy, sensor[0] + dy))
}

long free = land.ranges.sum {it.size()} as long

println(free - beaconsOnY.size())
