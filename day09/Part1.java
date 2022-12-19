//usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Part1 {
    record Point(int x, int y) implements Comparable<Point> {

        public static final Comparator<Point> POINT_COMPARATOR = Comparator.comparing(Point::x).thenComparing(Point::y);

        Point move(Move m) {
            return switch (m) {
                case U -> new Point(this.x, this.y + 1);
                case D -> new Point(this.x, this.y - 1);
                case L -> new Point(this.x - 1, this.y);
                case R -> new Point(this.x + 1, this.y);
            };
        }

        Point moveTo(Point target) {
            Set<Move> path = this.pathTo(target);
            var next = this;
            for (Move move : path) {
                Point candidate = next.move(move);
                next = candidate.equals(target) ? next : candidate;
            }
            return next;
        }

        private Set<Move> pathTo(Point target) {
            int diag = this.x != target.x && this.y != target.y ? 0 : 1;

            Set<Move> moves = new HashSet<>(2);
            if (this.x > target.x + diag) {
                moves.add(Move.L);
            }
            if (target.x > this.x + diag) {
                moves.add(Move.R);
            }
            if (this.y > target.y + diag) {
                moves.add(Move.D);
            }
            if (target.y > this.y + diag) {
                moves.add(Move.U);
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

        Point start = new Point(0, 0);
        Point head = start;
        Point tail = start;
        passed.add(tail);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] s = line.split(" ", 2);
            Move m = Move.valueOf(s[0]);
            int offset = Integer.parseInt(s[1]);

            for (int i = 0; i < offset; ++i) {
                head = head.move(m);

                tail = tail.moveTo(head);
                passed.add(tail);
            }
            System.getLogger("main").log(System.Logger.Level.INFO, "Result " + passed.size());
        }

        System.getLogger("main").log(System.Logger.Level.INFO, "Result " + passed.size());
    }
}
