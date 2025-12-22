import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.LongUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Part2 {
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

        List<Monkey> monkeys = loadMonkeys(sc);
        monkeys.forEach(System.out::println);

        int ppcm = ppcm(monkeys.stream().map(Monkey::threshold).toList());

        System.out.printf("ppcm %d%n", ppcm);

        long[] ints = LongStream.range(0, monkeys.size()).map(i -> 0).toArray();

//        var debugRounds = Set.of(1, 20, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000);
        for (int round = 0; round < 10_000; round++) {
            for (Monkey monk : monkeys) {
                ints[monk.id()] += monk.bag().size();
                while (!monk.bag().isEmpty()) {
                    Long item = monk.bag().remove();
                    long newItem = monk.operation().applyAsLong(item) % ppcm;
                    monkeys.get(monk.target().get(newItem % monk.threshold() == 0)).bag().add(newItem);
                }
            }
//            if (debugRounds.contains(round + 1)) {
//                System.out.printf("Round %d%n", round + 1);
//                System.out.printf("%s%n", Arrays.stream(ints).mapToObj(Long::toString).collect(Collectors.joining(", ")));
//
//            }
//            System.out.printf("Round %d%n", round + 1);
//            monkeys.forEach(System.out::println);
        }

        Arrays.stream(ints).boxed().sorted(Comparator.reverseOrder()).limit(2)
                .reduce((integer, integer2) -> integer * integer2)
                .ifPresent(result -> System.out.printf("%d%n", result));
    }

    private static int ppcm(List<Integer> thresholds) {
        int maxThreshold = thresholds.stream().mapToInt(i -> i).max().orElse(0);
        List<Integer> primes = primes(maxThreshold, 2, new ArrayList<>());

        Map<Integer, Integer> ppcmFactors = thresholds.stream()
                .map(threshold -> factors(threshold, primes))
                .flatMap(factors -> factors.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Math::max));
        int ppcm = 1;
        for (var entry : ppcmFactors.entrySet()) {
            ppcm *= entry.getValue() * entry.getKey();
        }
        System.out.printf("%s %d%n", ppcmFactors, ppcm);
        return ppcm;
    }

    private static List<Monkey> loadMonkeys(Scanner sc) {
        List<Monkey> monkeys = new ArrayList<>();
        Monkey monkey;
        do {
            monkey = read(sc);
            if (monkey != null) {
                monkeys.add(monkey);
            }
        } while (monkey != null);
        return monkeys;
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

    static List<Integer> primes(int limit, int current, List<Integer> primes) {
        boolean isPrime = primes.stream().filter(prime -> current % prime == 0).toList().isEmpty();
        if (isPrime) {
            primes.add(current);
        }
        return current > limit
                ? primes
                : primes(limit, current + 1, primes);
    }

    static Map<Integer, Integer> factors(int value, List<Integer> primes) {
        Map<Integer, Integer> factors = new HashMap<>();
        Optional<Integer> divisor;
        do {
            int v = value;
            divisor = primes.stream().sorted(Comparator.reverseOrder())
                    .filter(i -> v % i == 0).findFirst();
            if (divisor.isPresent()) {
                factors.put(divisor.get(), value / divisor.get());
                value -= (value / divisor.get()) * divisor.get();
            }
        } while (value > 1 && divisor.isPresent());
        return factors;
    }
}
