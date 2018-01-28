package de.codemakers.algorithms.astar.tests;

import de.codemakers.algorithms.astar.IGoalNode;
import de.codemakers.algorithms.astar.ISearchNode;

public class GoalNode2D implements IGoalNode {

    private final int x;
    private final int y;

    public GoalNode2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public final boolean inGoal(ISearchNode other) {
        if (other instanceof SearchNode2D) {
            final SearchNode2D otherNode = (SearchNode2D) other;
            return (x == otherNode.getX()) && (y == otherNode.getY());
        }
        return false;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }
}
