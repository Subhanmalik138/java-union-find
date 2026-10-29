package com.subhanmalik.unionfind;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Disjoint Set Union (Union-Find) over the elements {@code 0..n-1}.
 *
 * <p>Two classic optimisations keep every operation effectively constant time
 * (inverse Ackermann): path compression on {@link #find(int)} and union by
 * size on {@link #union(int, int)}. The structure is not thread-safe by
 * design — sharing one instance across threads without external synchronisation
 * is a bug, not a feature.
 */
public final class DisjointSet {

    private final int[] parent;
    private final int[] size;
    private int components;

    public DisjointSet(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("size must not be negative: " + n);
        }
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
        components = n;
    }

    /** Number of elements this set was created with. */
    public int size() {
        return parent.length;
    }

    /** Number of disjoint components currently remaining. */
    public int componentCount() {
        return components;
    }

    /**
     * Returns the representative (root) of {@code x}'s component, flattening
     * the path as it goes: every node visited is re-pointed directly at the
     * root, so a second lookup on the same path is a single hop.
     */
    public int find(int x) {
        checkIndex(x);
        int root = x;
        while (parent[root] != root) {
            root = parent[root];
        }
        // Path halving: point each node on the way at its grandparent.
        while (parent[x] != x) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    /** Returns true if {@code a} and {@code b} are in the same component. */
    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    /**
     * Merges the components of {@code a} and {@code b}. The smaller tree is
     * always attached under the larger one, so tree height stays logarithmic
     * even before path compression does its work.
     *
     * @return true if a merge happened, false if they were already connected
     */
    public boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) {
            return false;
        }
        if (size[rootA] < size[rootB]) {
            int tmp = rootA;
            rootA = rootB;
            rootB = tmp;
        }
        parent[rootB] = rootA;
        size[rootA] += size[rootB];
        components--;
        return true;
    }

    /** Number of elements in {@code x}'s component. */
    public int componentSize(int x) {
        return size[find(x)];
    }

    /**
     * Groups every element by its component root. Roots appear in first-seen
     * element order, which keeps the output stable for a given union sequence.
     */
    public Map<Integer, List<Integer>> components() {
        Map<Integer, List<Integer>> groups = new LinkedHashMap<>();
        for (int i = 0; i < parent.length; i++) {
            groups.computeIfAbsent(find(i), k -> new ArrayList<>()).add(i);
        }
        return groups;
    }

    private void checkIndex(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException(
                    "element out of range: " + x + " (size " + parent.length + ")");
        }
    }
}
