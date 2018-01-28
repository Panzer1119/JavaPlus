package de.codemakers.algorithms.astar.tests;

import de.codemakers.algorithms.astar.ASearchNode;
import de.codemakers.algorithms.astar.ISearchNode;
import java.util.ArrayList;
import java.util.List;

public class SearchNode2D extends ASearchNode {

    private final int x;
    private final int y;
    private SearchNode2D parent;
    private final GoalNode2D goal;

    public SearchNode2D(int x, int y, SearchNode2D parent, GoalNode2D goal) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.goal = goal;

    }

    public final SearchNode2D getParent() {
        return this.parent;
    }

    public final List<ISearchNode> getSuccessors() {
        final List<ISearchNode> successors = new ArrayList<>();
        successors.add(new SearchNode2D(this.x - 1, this.y, this, this.goal));
        successors.add(new SearchNode2D(this.x + 1, this.y, this, this.goal));
        successors.add(new SearchNode2D(this.x, this.y + 1, this, this.goal));
        successors.add(new SearchNode2D(this.x, this.y - 1, this, this.goal));
        return successors;
    }

    public final double h() {
        return this.distance(goal.getX(), goal.getY());
    }

    public final double c(ISearchNode successor) {
        SearchNode2D successorNode = this.castToSearchNode2D(successor);
        return 1;
    }

    public final void setParent(ISearchNode parent) {
        this.parent = this.castToSearchNode2D(parent);
    }

    public final boolean equals(Object other) {
        if (other instanceof SearchNode2D) {
            SearchNode2D otherNode = (SearchNode2D) other;
            return (x == otherNode.getX()) && (y == otherNode.getY());
        }
        return false;
    }

    public final int hashCode() {
        return (41 * (41 + this.x + this.y));
    }

    public final double distance(int otherX, int otherY) {
        return Math.sqrt(Math.pow(this.x - otherX, 2) + Math.pow(this.y - otherY, 2));
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final String toString() {
        return "(" + Integer.toString(this.x) + ";" + Integer.toString(this.y)
                + ";h:" + Double.toString(this.h())
                + ";g:" + Double.toString(this.g()) + ")";
    }

    private final SearchNode2D castToSearchNode2D(ISearchNode other) {
        return (SearchNode2D) other;
    }

    public final Integer keyCode() {
        return null;
    }
}
