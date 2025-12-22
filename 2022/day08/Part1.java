//usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Part1 {
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

        Set<Tree> visibles = new TreeSet<>();

        List<String> columns = new ArrayList<>();

        int li = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            int lineNumber = li;
            visibles.addAll(extracted(line, 0, line.length(), i -> new Tree(lineNumber, i)));
            visibles.addAll(extracted(line, line.length(), 0, i -> new Tree(lineNumber, i)));

            if (columns.isEmpty()) {
                columns = new ArrayList<>(line.chars().mapToObj(i -> "" + (char)i).toList());
            } else {
                var chars = line.chars().mapToObj(i -> "" + (char)i).toList();
                for (var i = 0; i < chars.size(); ++i) {
                    columns.set(i, columns.get(i) + chars.get(i));
                }

            }

            li++;
        }

        int col = 0;
        for (String column : columns) {
            int colNumber = col;
            visibles.addAll(extracted(column, 0, column.length(), i -> new Tree(i, colNumber)));
            visibles.addAll(extracted(column, column.length(), 0, i -> new Tree(i, colNumber)));
            ++col;
        }

        System.getLogger("main").log(System.Logger.Level.INFO, "Result " + visibles.size());
    }

    private static Set<Tree> extracted(String line, int fromY, int toY, IntFunction<Tree> treeFactory) {
        byte[] bytes = line.getBytes(UTF_8);

        return IntStream.iterate(fromY, operand -> operand != toY, operand -> fromY < toY ? operand + 1 : operand - 1)
                .filter(new Bigger(bytes))
                .mapToObj(treeFactory)
                .collect(Collectors.toSet());
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
