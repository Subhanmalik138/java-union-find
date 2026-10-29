package com.subhanmalik.unionfind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Small command-line front end.
 *
 * <p>{@code demo} prints a scripted union-find session and a Kruskal example.
 * With no arguments it starts a REPL over {@code n} elements:
 *
 * <pre>
 *   java -jar out/union-find.jar 6
 *   &gt; union 0 1
 *   &gt; connected 0 1
 *   true
 *   &gt; components
 *   [0, 1] [2] [3] [4] [5]
 *   &gt; exit
 * </pre>
 *
 * REPL commands: {@code union a b}, {@code connected a b}, {@code find a},
 * {@code size a}, {@code count}, {@code components}, {@code help},
 * {@code exit}.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("demo")) {
            runDemo();
            return;
        }
        int n = args.length > 0 ? parseNonNegative(args[0]) : 10;
        repl(new DisjointSet(n));
    }

    private static void runDemo() {
        DisjointSet dsu = new DisjointSet(6);
        dsu.union(0, 1);
        dsu.union(1, 2);
        dsu.union(3, 4);
        System.out.println("components: " + dsu.componentCount());
        System.out.println("connected(0, 2): " + dsu.connected(0, 2));
        System.out.println("connected(0, 3): " + dsu.connected(0, 3));
        System.out.println("groups: " + dsu.components().values());

        Kruskal.Result mst = Kruskal.minimumSpanningForest(4, List.of(
                new Kruskal.Edge(0, 1, 10),
                new Kruskal.Edge(0, 2, 6),
                new Kruskal.Edge(0, 3, 5),
                new Kruskal.Edge(1, 3, 15),
                new Kruskal.Edge(2, 3, 4)));
        System.out.println("MST edges: " + mst.edges());
        System.out.println("MST total weight: " + mst.totalWeight());
    }

    private static void repl(DisjointSet dsu) throws IOException {
        System.out.println("Union-Find REPL over " + dsu.size()
                + " elements (0.." + (dsu.size() - 1) + "). Type 'help'.");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (true) {
            System.out.print("> ");
            line = in.readLine();
            if (line == null || line.trim().equals("exit")) {
                break;
            }
            String[] parts = line.trim().split("\\s+");
            if (parts[0].isEmpty()) {
                continue;
            }
            try {
                switch (parts[0]) {
                    case "help" -> printHelp();
                    case "union" -> System.out.println(dsu.union(arg(parts, 1), arg(parts, 2)));
                    case "connected" -> System.out.println(dsu.connected(arg(parts, 1), arg(parts, 2)));
                    case "find" -> System.out.println(dsu.find(arg(parts, 1)));
                    case "size" -> System.out.println(dsu.componentSize(arg(parts, 1)));
                    case "count" -> System.out.println(dsu.componentCount());
                    case "components" -> System.out.println(dsu.components().values());
                    default -> System.out.println("unknown command (try 'help')");
                }
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }

    private static void printHelp() {
        System.out.println("union a b | connected a b | find a | size a | count | components | exit");
    }

    private static int arg(String[] parts, int i) {
        if (parts.length <= i) {
            throw new IllegalArgumentException("missing argument");
        }
        return Integer.parseInt(parts[i]);
    }

    private static int parseNonNegative(String s) {
        int n = Integer.parseInt(s);
        if (n < 0) {
            throw new IllegalArgumentException("size must not be negative: " + n);
        }
        return n;
    }
}
