//usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Part2 {
    record Point(int x, int y) implements Comparable<Point> {

        public static final Comparator<Point> POINT_COMPARATOR = Comparator.comparing(Point::x).thenComparing(Point::y);

        Point move(Move m) {
            return switch (m) {
                case R -> new Point(this.x, this.y + 1);
                case L -> new Point(this.x, this.y - 1);
                case D -> new Point(this.x - 1, this.y);
                case U -> new Point(this.x + 1, this.y);
            };
        }

        Point moveTo(Point target) {
            Collection<Move> path = this.pathTo(target);
            var next = this;
            for (Move move : path) {
                Point candidate = next.move(move);
                next = candidate.equals(target) ? next : candidate;
            }
            return next;
        }

        private Collection<Move> pathTo(Point target) {
            if (Math.abs(target.x - this.x) == 1 && Math.abs(target.y - this.y) == 1) {
                return List.of();
            }

            Set<Move> moves = new HashSet<>(2);
            if (this.x > target.x) {
                moves.add(Move.D);
            }
            if (target.x > this.x) {
                moves.add(Move.U);
            }
            if (this.y > target.y) {
                moves.add(Move.L);
            }
            if (target.y > this.y) {
                moves.add(Move.R);
            }
            return moves;
        }

        @Override
        public int compareTo(Point o) {
            return POINT_COMPARATOR.compare(this, o);
        }
    }
    enum Move {U, D, L, R}

    public static void main(String[] args) throws FileNotFoundException {
        var sc = args.length >= 1
                ? new Scanner(new File(args[0]))
                : new Scanner(System.in);

        Set<Point> passed = new TreeSet<>();

        List<Point> rope = new ArrayList<>(IntStream.range(0, 10).mapToObj(i -> new Point(0, 0)).toList());
        passed.add(rope.get(rope.size() - 1));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] s = line.split(" ", 2);
            Move m = Move.valueOf(s[0]);
            int offset = Integer.parseInt(s[1]);

            for (int i = 0; i < offset; ++i) {
                rope.set(0, rope.get(0).move(m));

                for (int j = 1; j < rope.size(); ++j) {
                    rope.set(j, rope.get(j).moveTo(rope.get(j - 1)));
                }
                passed.add(rope.get(rope.size() - 1));
            }
//            print(passed, rope);
        }

        System.getLogger("main").log(System.Logger.Level.INFO, "Result " + passed.size());
    }

    private static void print(Set<Point> passed, List<Point> rope) {
        int maxX = Stream.concat(passed.stream(), rope.stream()).mapToInt(Point::x).max().orElse(0);
        int maxY = Stream.concat(passed.stream(), rope.stream()).mapToInt(Point::y).max().orElse(0);
        int minX = Stream.concat(passed.stream(), rope.stream()).mapToInt(Point::x).min().orElse(0);
        int minY = Stream.concat(passed.stream(), rope.stream()).mapToInt(Point::y).min().orElse(0);

        for (int i = maxX; i >= minX; --i) {
            StringBuilder line = new StringBuilder();
            for (int j = minY; j <= maxY; ++j) {
                if (i == 0 && j == 0) {
                    line.append('s');
                    continue;
                }
                Point c = new Point(i, j);
                int i2 = rope.indexOf(c);
                if (i2 == 0) {
                    line.append('H');
                } else if (i2 >= 0) {
                    line.append(i2);
                } else if (passed.contains(c)) {
                    line.append('#');
                } else {
                    line.append('⋅');
                }
            }
            System.out.println(line);
        }
        System.out.println("====");
    }
}
