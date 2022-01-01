///usr/bin/env jbang "$0" "$@" ; exit $?


import static java.lang.System.*;
import java.util.Scanner;

public class Jour1 {
    public static void main(String... args) {
        int a = 0;
        try (Scanner sc = new Scanner(in)) {
            Integer i = null;
            while (sc.hasNextInt()) {
                int current = sc.nextInt();
                if (i == null) {
                    out.println("value (N/A - no previous measurement)".replaceAll("value", Integer.toString(current)));
                } else {
                    String move = i - current < 0 ? "increase" : "decrease";
                    out.println(
                        "value (move)"
                        .replaceAll("value", Integer.toString(current))
                        .replaceAll("move", move)
                    );
                    
                    if (move.equals("increase")) {
                        a++;
                    }
                }
                i = current;
            }
            out.println("Nb increase %d".replaceFirst("%d", Integer.toString(a)));
        }
    }
}
