package de.codemakers.algorithms.astar.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.codemakers.algorithms.astar.ISearchNode;
import java.util.List;

public class ClosedSet implements IClosedSet {

    private final List<ISearchNode> list;
    private final Comparator<ISearchNode> comp;

    public ClosedSet(Comparator<ISearchNode> comp) {
        this.list = new ArrayList<>();
        this.comp = comp;
    }

    @Override
    public boolean contains(ISearchNode node) {
        return this.list.contains(node);
    }

    @Override
    public void add(ISearchNode node) {
        this.list.add(node);

    }

    @Override
    public ISearchNode min() {
        return Collections.min(this.list, this.comp);
    }

}
