//usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Part2 {
    record Tree(int x, int y) implements Comparable<Tree> {

        public static final Comparator<Tree> TREE_COMPARATOR = Comparator.comparing(Tree::x).thenComparing(Tree::y);

        @Override
        public int compareTo(Tree o) {
            return TREE_COMPARATOR.compare(this, o);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        var sc = args.length >= 1
                ? new Scanner(new File(args[0]))
                : new Scanner(System.in);

        Map<Tree, Byte> forest = new HashMap<>();

        int li = 0;
        int col = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            col = 0;
            for (byte aByte : line.getBytes(UTF_8)) {
                forest.put(new Tree(li, col++), aByte);
            }

            li++;
        }

        long max = Long.MIN_VALUE;
        for (var entry : forest.entrySet()) {
            long up = extracted(forest, entry.getKey().x - 1, -1, entry.getValue(), i -> new Tree(i, entry.getKey().y));
            long left = extracted(forest, entry.getKey().y - 1, -1, entry.getValue(), i -> new Tree(entry.getKey().x, i));
            long down = extracted(forest, entry.getKey().x + 1, li, entry.getValue(), i -> new Tree(i, entry.getKey().y));
            long right = extracted(forest, entry.getKey().y + 1, col, entry.getValue(), i -> new Tree(entry.getKey().x, i));
            long value = up * down * left * right;
//            System.out.printf("%s => %d%n", entry.getKey(), value);
            max = Math.max(value, max);
        }

        System.out.println(max);
    }

    private static long extracted(Map<Tree, Byte> forest, int from, int to, byte ref, IntFunction<Tree> treeFactory) {
        var trees = range(from, to)
                .mapToObj(treeFactory)
                .takeWhile(forest::containsKey)
                .toList();
        long count = 0;
        for (var tree : trees) {
            if (forest.get(tree) < ref) {
                count++;
            } else {
                return 1 + count;
            }
        }
        return count;
    }

    private static IntStream range(int from, int to) {
        return IntStream.iterate(from, operand -> from < to ? operand + 1 : operand - 1);
                //.limit(Math.abs(from - to));
    }

    static class Bigger implements IntPredicate {
        int max = Integer.MIN_VALUE;
        byte[] bytes;

        public Bigger(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public boolean test(int value) {
            if (value < bytes.length && (max < 0 || bytes[max] < bytes[value])) {
                max = value;
                return true;
            }
            return false;
        }
    }
}
