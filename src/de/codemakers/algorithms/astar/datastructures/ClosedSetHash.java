package de.codemakers.algorithms.astar.datastructures;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.codemakers.algorithms.astar.ISearchNode;
import java.util.Map;

public class ClosedSetHash implements IClosedSet {

    private final Map<Integer, ISearchNode> hashMap;
    private final Comparator<ISearchNode> comp;

    public ClosedSetHash(Comparator<ISearchNode> comp) {
        this.hashMap = new HashMap<>();
        this.comp = comp;

    }

    @Override
    public boolean contains(ISearchNode node) {
        return this.hashMap.containsKey(node.keyCode());
    }

    @Override
    public void add(ISearchNode node) {
        this.hashMap.put(node.keyCode(), node);
    }

    @Override
    public ISearchNode min() {
        return Collections.min(hashMap.values(), comp);
    }

}
