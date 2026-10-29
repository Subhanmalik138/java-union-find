package com.subhanmalik.unionfind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Kruskal's minimum spanning tree (or forest, for disconnected graphs) built
 * on top of {@link DisjointSet}. Sorting edges by weight and greedily keeping
 * every edge that joins two different components is the whole algorithm — the
 * union-find is what makes the "different components?" check cheap.
 */
public final class Kruskal {

    /** An undirected weighted edge between two vertices. */
    public record Edge(int from, int to, int weight) {
        public Edge {
            if (from < 0 || to < 0) {
                throw new IllegalArgumentException("vertex indices must be non-negative");
            }
        }
    }

    /** The edges of the MST/forest plus their total weight. */
    public record Result(List<Edge> edges, int totalWeight) {}

    private Kruskal() {
    }

    /**
     * Computes a minimum spanning forest of a graph with {@code vertices}
     * vertices and the given edges. Self-loops are ignored; on a disconnected
     * graph the result is one minimum tree per component.
     */
    public static Result minimumSpanningForest(int vertices, List<Edge> edges) {
        if (vertices < 0) {
            throw new IllegalArgumentException("vertices must not be negative: " + vertices);
        }
        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingInt(Edge::weight));

        DisjointSet dsu = new DisjointSet(vertices);
        List<Edge> chosen = new ArrayList<>();
        int total = 0;
        for (Edge edge : sorted) {
            if (edge.from() >= vertices || edge.to() >= vertices) {
                throw new IllegalArgumentException("edge references vertex outside graph: " + edge);
            }
            if (edge.from() == edge.to()) {
                continue; // self-loop can never join two components
            }
            if (dsu.union(edge.from(), edge.to())) {
                chosen.add(edge);
                total += edge.weight();
            }
        }
        return new Result(List.copyOf(chosen), total);
    }
}
