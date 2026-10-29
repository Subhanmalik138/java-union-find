# java-union-find

Disjoint Set Union (Union-Find) in Java, with the two optimisations that make
it an interview staple: path compression and union by size. On top of it sits
a Kruskal minimum-spanning-tree implementation, because a union-find with no
consumer is just an array with ambition.

## What it does

- `DisjointSet` over elements `0..n-1`: `union`, `find`, `connected`,
  `componentSize`, `componentCount`, and `components()` (every element grouped
  by its root)
- `Kruskal.minimumSpanningForest`: sorts edges by weight and keeps every edge
  that joins two different components — on a disconnected graph it returns one
  minimum tree per component
- A small REPL (`union`, `connected`, `find`, `size`, `count`, `components`)
  plus a scripted `demo` mode

## Design decisions

1. **Path halving, not full recursion.** `find` walks to the root, then
   re-points each node on the path at the root. Same amortised bound as full
   compression, no recursion depth to worry about on long chains.
2. **Union by size, not by rank.** Attaching the smaller tree under the larger
   one keeps tree height logarithmic even before compression kicks in, and
   `size` doubles as the `componentSize` answer for free.
3. **No thread safety, on purpose.** A concurrent union-find needs either
   locking or lock-free tricks that would obscure the core algorithm. The class
   documents that sharing one instance across threads without external
   synchronisation is a bug.
4. **Kruskal ignores self-loops and reports a forest.** Silently dropping a
   self-loop is correct (it can never join two components); failing on a
   disconnected graph would be wrong, since a forest is the right answer.

## Build and run

Requires JDK 17+.

```bash
./build.sh            # compile + build out/union-find.jar
./build.sh test       # compile + run the JUnit 5 suite
java -jar out/union-find.jar demo     # scripted demo: unions + MST example
java -jar out/union-find.jar 6        # REPL over 6 elements
```

The test run uses the JUnit Platform console standalone jar (downloaded from
Maven Central on first use, or set `JUNIT_JAR` to point at one you already
have).

## Usage

```java
DisjointSet dsu = new DisjointSet(6);
dsu.union(0, 1);
dsu.union(1, 2);
dsu.connected(0, 2);      // true
dsu.componentSize(0);     // 3
dsu.components();         // {0=[0, 1, 2], 3=[3], 4=[4], 5=[5]}

Kruskal.Result mst = Kruskal.minimumSpanningForest(4, List.of(
        new Kruskal.Edge(0, 1, 10),
        new Kruskal.Edge(0, 2, 6),
        new Kruskal.Edge(0, 3, 5),
        new Kruskal.Edge(1, 3, 15),
        new Kruskal.Edge(2, 3, 4)));
mst.totalWeight();        // 19
```

## Tests

14 JUnit 5 tests cover union/find semantics, transitivity, component grouping
and sizes, chain flattening on 1,000 elements, out-of-range rejection, and
Kruskal on connected, disconnected, and degenerate graphs.

## Tech stack

Java 17, zero dependencies at runtime, JUnit 5 for tests.
