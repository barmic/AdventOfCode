///usr/bin/env jbang "$0" "$@" ; exit $?

import java.util.Scanner;
import static java.lang.System.*;

public class Jour2 {

    public static void main(String... args) {
        int depth = 0;
        int position = 0;
        int aim = 0;
        try (Scanner sc = new Scanner(in)) {
            while (sc.hasNextLine()) {
                String current = sc.nextLine();
                var cmd = current.split(" ");
                var value = Integer.parseInt(cmd[1]);
                switch (cmd[0]) {
                    case "up" -> aim -= value;
                    case "down" -> aim += value;
                    case "forward" -> {
                        position += value;
                        depth += value * aim;
                    }
                }
            }
            out.println("%d, %d".formatted(position, depth));
        }
    }
}
