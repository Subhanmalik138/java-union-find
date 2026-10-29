package com.subhanmalik.unionfind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DisjointSetTest {

    @Test
    void freshSetHasOneComponentPerElement() {
        DisjointSet dsu = new DisjointSet(5);
        assertEquals(5, dsu.componentCount());
        assertEquals(5, dsu.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(i, dsu.find(i));
            assertEquals(1, dsu.componentSize(i));
        }
    }

    @Test
    void unionMergesAndReportsWhetherItChangedAnything() {
        DisjointSet dsu = new DisjointSet(4);
        assertTrue(dsu.union(0, 1));
        assertFalse(dsu.union(0, 1)); // already connected: no-op
        assertTrue(dsu.connected(0, 1));
        assertFalse(dsu.connected(0, 2));
        assertEquals(3, dsu.componentCount());
    }

    @Test
    void connectivityIsTransitive() {
        DisjointSet dsu = new DisjointSet(6);
        dsu.union(0, 1);
        dsu.union(1, 2);
        dsu.union(3, 4);
        assertTrue(dsu.connected(0, 2));
        assertFalse(dsu.connected(2, 3));
        assertEquals(3, dsu.componentSize(0));
        assertEquals(2, dsu.componentSize(3));
    }

    @Test
    void componentSizesTrackMerges() {
        DisjointSet dsu = new DisjointSet(6);
        dsu.union(0, 1);
        dsu.union(1, 2);
        assertEquals(3, dsu.componentSize(0));
        assertEquals(3, dsu.componentSize(2));
        dsu.union(3, 4);
        assertEquals(2, dsu.componentSize(4));
    }

    @Test
    void componentsGroupsEveryElementExactlyOnce() {
        DisjointSet dsu = new DisjointSet(5);
        dsu.union(0, 2);
        dsu.union(2, 4);
        dsu.union(1, 3);
        Map<Integer, List<Integer>> groups = dsu.components();
        assertEquals(2, groups.size());
        assertEquals(5, groups.values().stream().mapToInt(List::size).sum());
        assertEquals(List.of(0, 2, 4), groups.get(dsu.find(0)));
        assertEquals(List.of(1, 3), groups.get(dsu.find(1)));
    }

    @Test
    void unionBySizeKeepsChainsShort() {
        // Chained unions in the worst order still leave a shallow tree.
        DisjointSet dsu = new DisjointSet(1000);
        for (int i = 1; i < 1000; i++) {
            dsu.union(0, i);
        }
        assertEquals(1, dsu.componentCount());
        assertEquals(1000, dsu.componentSize(0));
        // After one find pass, every element points straight at the root.
        for (int i = 0; i < 1000; i++) {
            dsu.find(i);
        }
        assertEquals(dsu.find(0), dsu.find(999));
    }

    @Test
    void outOfRangeElementsAreRejected() {
        DisjointSet dsu = new DisjointSet(3);
        assertThrows(IndexOutOfBoundsException.class, () -> dsu.find(3));
        assertThrows(IndexOutOfBoundsException.class, () -> dsu.find(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> dsu.union(0, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> dsu.connected(-1, 0));
    }

    @Test
    void negativeSizeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new DisjointSet(-1));
    }

    @Test
    void emptySetHasNoComponents() {
        DisjointSet dsu = new DisjointSet(0);
        assertEquals(0, dsu.componentCount());
        assertTrue(dsu.components().isEmpty());
    }
}
