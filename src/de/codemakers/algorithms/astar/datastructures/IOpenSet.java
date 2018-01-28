package de.codemakers.algorithms.astar.datastructures;

import de.codemakers.algorithms.astar.ISearchNode;

public interface IOpenSet {

    public void add(ISearchNode node);

    public void remove(ISearchNode node);

    public ISearchNode poll();
    //returns node if present otherwise null

    public ISearchNode getNode(ISearchNode node);

    public int size();

}
