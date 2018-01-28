package de.codemakers.algorithms.astar.datastructures;

import de.codemakers.algorithms.astar.ISearchNode;

public interface IClosedSet {

    public boolean contains(ISearchNode node);

    public void add(ISearchNode node);

    public ISearchNode min();

}
