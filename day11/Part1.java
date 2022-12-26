import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.LongUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Part1 {
    private record Monkey(int id, Queue<Long> bag, LongUnaryOperator operation, int threshold, Map<Boolean, Integer> target) {
    }

    static final Pattern monkeyPattern = Pattern.compile("Monkey (\\d):");
    static final Pattern bagPattern = Pattern.compile("(\\d+)");
    static final Pattern operationPattern = Pattern.compile("Operation: new = old ([*+]) (.+)$");
    static final Pattern thresholdPattern = Pattern.compile("Test: divisible by (\\d+)$");
    static final Pattern targetPattern = Pattern.compile("If (true|false): throw to monkey (\\d+)$");

    public static void main(String[] args) throws FileNotFoundException {
        var sc = args.length >= 1
                ? new Scanner(new File(args[0]))
                : new Scanner(System.in);

        List<Monkey> monkeys = new ArrayList<>();
        Monkey monkey;
        do {
            monkey = read(sc);
            if (monkey != null) {
                monkeys.add(monkey);
            }
        } while (monkey != null);
        monkeys.forEach(System.out::println);

        int[] ints = IntStream.range(0, monkeys.size()).map(i -> 0).toArray();

        for (int round = 0; round < 20; round++) {
            for (Monkey monk : monkeys) {
                ints[monk.id()] += monk.bag().size();
                while (!monk.bag().isEmpty()) {
                    Long item = monk.bag().remove();
                    item = monk.operation().applyAsLong(item) / 3;
                    monkeys.get(monk.target().get(item % monk.threshold() == 0)).bag().add(item);
                }
            }
            System.out.printf("Round %d%n", round + 1);
            monkeys.forEach(System.out::println);
        }

        Arrays.stream(ints).boxed().sorted(Comparator.reverseOrder()).limit(2)
                .reduce((integer, integer2) -> integer * integer2)
                .ifPresent(result -> System.out.printf("%d%n", result));
    }

    static Monkey read(Scanner sc) {
        OptionalInt optionalId = read(sc, monkeyPattern)
                .flatMap(Collection::stream)
                .mapToInt(Integer::parseInt)
                .findFirst();
        if (optionalId.isEmpty()) {
            return null;
        }

        List<Long> bag = read(sc, bagPattern)
                .flatMap(Collection::stream)
                .map(Long::parseLong)
                .toList();

        Optional<LongUnaryOperator> operation = read(sc, operationPattern)
                .map(groups -> op(groups.get(0), groups.get(1)))
                .findFirst();
        if (operation.isEmpty()) {
            return null;
        }

        var threshold = read(sc, thresholdPattern)
                .flatMap(Collection::stream)
                .mapToInt(Integer::parseInt)
                .findFirst();
        if (threshold.isEmpty()) {
            return null;
        }

        Map<Boolean, Integer> targets = new HashMap<>();
        targets.putAll(read(sc, targetPattern).collect(Collectors.toMap(
                groups -> Boolean.parseBoolean(groups.get(0)),
                groups -> Integer.parseInt(groups.get(1))
        )));
        targets.putAll(read(sc, targetPattern).collect(Collectors.toMap(
                groups -> Boolean.parseBoolean(groups.get(0)),
                groups -> Integer.parseInt(groups.get(1))
        )));
        return new Monkey(optionalId.getAsInt(), new ArrayDeque<>(bag), operation.get(), threshold.getAsInt(), targets);
    }

    static Stream<List<String>> read(Scanner sc, Pattern pattern) {
        String line = "";
        while (sc.hasNextLine() && line.isBlank()) {
            line = sc.nextLine();
        }
        if (line.isBlank()) {
            return Stream.empty();
        }
        return Stream.of(line)
                .mapMulti((l, s) -> {
                    Matcher matcher = pattern.matcher(l);
                    while (matcher.find()) {
                        List<String> groups = IntStream
                                .rangeClosed(1, matcher.groupCount())
                                .mapToObj(matcher::group)
                                .toList();
                        if (!groups.isEmpty()) {
                            s.accept(groups);
                        }
                    }
                });
    }

    static LongUnaryOperator op(String operator, String operand) {
        if (operator.equals("*") && operand.equals("old")) {
            return x -> x * x;
        }
        int anInt = Integer.parseInt(operand);
        return switch (operator) {
            case "*" -> x -> x * anInt;
            case "+" -> x -> x + anInt;
            default -> throw new RuntimeException();
        };
    }
}
