///usr/bin/env jbang "$0" "$@" ; exit $?


import static java.lang.System.*;
import java.util.Scanner;

public class Jour1 {
    private static int SIZE = 4;

    public static void main(String... args) {
        int[] buffer = new int[SIZE];
        int idx = 0;
        int result = 0;
        try (Scanner sc = new Scanner(in)) {
            while (sc.hasNextInt()) {
                buffer[idx % SIZE] = sc.nextInt();
                
                idx++;

                if (idx < SIZE) continue;

                int current = buffer[(idx - 1) % SIZE] + buffer[(idx - 2) % SIZE] + buffer[(idx - 3) % SIZE];
                int prev = buffer[(idx - 2) % SIZE] + buffer[(idx - 3) % SIZE] + buffer[(idx - 4) % SIZE];

                if (current > prev) {
                    result++;
                }
            }
        }
        out.println(result);
    }
}
