package de.codemakers.algorithms.astar;

/**
 * GoalNodes don't need as much Information as SearchNodes.
 */
public interface IGoalNode {

    public boolean inGoal(ISearchNode other);
}
