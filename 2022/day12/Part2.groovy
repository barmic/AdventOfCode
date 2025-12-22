Scanner sc = new Scanner(new File('input'))

def width = 0
String land = ''
while (sc.hasNextLine()) {
    def line = sc.nextLine()
    width = line.size()
    land += line + '\n'
}

def target = land.indexOf('E')

if (target < 0) {
    System.exit(-1)
}

land = land.replace('S', 'a').replace('E', 'z')

def pathSize = Integer.MAX_VALUE
def start = land.indexOf('a', 0)
while (start >= 0 && start < land.size()) {
    pathSize = Math.min(pathSize, path(start, target, land, width))
    start = land.indexOf('a', start + 1)
}

println pathSize

def path(int start, int target, String land, int width) {
    Map<Integer, Integer> positions = [(start): 0]
    def updatedPos = [start] as Set

    while (!updatedPos.isEmpty()) {
        def posSet = new ArrayList<>(updatedPos)
        updatedPos.clear()
        for (int pos in posSet) {
            int nextStep = positions[pos] + 1
            for (n in next(pos, width, land)) {
                def old = positions.get(n, Integer.MAX_VALUE)
                positions[n] = Math.min(old, nextStep)
                if (old != positions[n]) {
                    updatedPos.add(n)
                }
            }
        }
    }

    return positions.get(target, Integer.MAX_VALUE)
}

def next(int pos, int width, String land) {
    return [pos - 1, pos + 1, pos - width - 1, pos + width + 1].stream()
            .filter { it >= 0 && it < land.size() }
            .filter { land.charAt(it) <= land.charAt(pos) + 1 && land.charAt(it) >= ('a' as Character) }
            .toList()
}