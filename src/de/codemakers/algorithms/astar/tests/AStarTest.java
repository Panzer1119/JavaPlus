package de.codemakers.algorithms.astar.tests;

import de.codemakers.algorithms.astar.AStar;
import de.codemakers.algorithms.astar.ISearchNode;
import java.util.List;

public class AStarTest {

    public static final void main(String[] args) {
        final GoalNode2D goalNode = new GoalNode2D(3, 3);
        final SearchNode2D initialNode = new SearchNode2D(1, 1, null, goalNode);
        final List<ISearchNode> path = new AStar().shortestPath(initialNode, goalNode);
        System.out.println(path);
    }

}
