import java.util.regex.Pattern

def sc = new Scanner(System.in)

record Point(int x, int y) {}

static Set<Point> readInput(Scanner sc) {
    def coordPattern = Pattern.compile("(\\d+),(\\d+)")
    def rocks = [] as Set<Point>

    while (sc.hasNextLine()) {
        def line = sc.nextLine()
        def matcher = coordPattern.matcher(line)
        Point prev = null
        while (matcher.find()) {
            def current = new Point(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2))
            )
            rocks << current
            if (prev != null) {
                for (def i = Math.min(prev.x(), current.x()); i < Math.max(prev.x(), current.x()); i++) {
                    rocks << new Point(i, current.y())
                }
                for (def j = Math.min(prev.y(), current.y()); j < Math.max(prev.y(), current.y()); j++) {
                    rocks << new Point(current.x(), j)
                }

            }
            prev = current
        }
    }
    return rocks
}

def rocks = readInput(sc)

def newSand = new Point(500, 0)

def sand = 0
def newSandFallen = true
while (newSandFallen) {
    def fallen = fall(newSand, rocks)
    if (fallen) {
        rocks << fallen
        sand += 1
    } else {
        newSandFallen = false
    }
}

println(sand)

static Point fall(Point sand, Set<Point> blocks) {
    def impact = blocks
            .findAll {it.x() == sand.x() && it.y() > sand.y()}
            .min {it.y() }
    if (!impact) {
        return null
    }
    if (!blocks.contains(new Point(impact.x() - 1, impact.y()))) {
        return fall(new Point(impact.x() - 1, impact.y()), blocks)
    } else if (!blocks.contains(new Point(impact.x() + 1, impact.y()))) {
        return fall(new Point(impact.x() + 1, impact.y()), blocks)
    } else {
        return new Point(impact.x(), impact.y() - 1)
    }
}