package com.subhanmalik.unionfind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class KruskalTest {

    @Test
    void findsMinimumSpanningTreeOfKnownGraph() {
        // Classic 4-node example: MST weight is 5 + 4 + 10 = 19.
        Kruskal.Result result = Kruskal.minimumSpanningForest(4, List.of(
                new Kruskal.Edge(0, 1, 10),
                new Kruskal.Edge(0, 2, 6),
                new Kruskal.Edge(0, 3, 5),
                new Kruskal.Edge(1, 3, 15),
                new Kruskal.Edge(2, 3, 4)));
        assertEquals(3, result.edges().size());
        assertEquals(19, result.totalWeight());
    }

    @Test
    void disconnectedGraphYieldsAForest() {
        Kruskal.Result result = Kruskal.minimumSpanningForest(5, List.of(
                new Kruskal.Edge(0, 1, 3),
                new Kruskal.Edge(3, 4, 7)));
        assertEquals(2, result.edges().size());
        assertEquals(10, result.totalWeight());
    }

    @Test
    void selfLoopsAreIgnoredAndEmptyGraphHasZeroWeight() {
        Kruskal.Result result = Kruskal.minimumSpanningForest(3, List.of(
                new Kruskal.Edge(0, 0, 1),
                new Kruskal.Edge(1, 1, 1)));
        assertTrue(result.edges().isEmpty());
        assertEquals(0, result.totalWeight());
        assertEquals(0, Kruskal.minimumSpanningForest(0, List.of()).totalWeight());
    }

    @Test
    void edgeOutsideGraphIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                Kruskal.minimumSpanningForest(2, List.of(new Kruskal.Edge(0, 5, 1))));
    }

    @Test
    void equalWeightEdgesStillSpanEveryComponent() {
        Kruskal.Result result = Kruskal.minimumSpanningForest(4, List.of(
                new Kruskal.Edge(0, 1, 1),
                new Kruskal.Edge(1, 2, 1),
                new Kruskal.Edge(2, 3, 1),
                new Kruskal.Edge(0, 3, 1)));
        assertEquals(3, result.edges().size());
        assertEquals(3, result.totalWeight());
    }
}
