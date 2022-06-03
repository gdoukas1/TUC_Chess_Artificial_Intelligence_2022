package minimax_montecarlo;

import tuc_chess.World;

import java.util.ArrayList;

public class MonteCarloTreeSearch {
    private final int seconds;
    private final int noOfIterations;
    private final int playerColor;

    private final int maxDepth = 10;

    public MonteCarloTreeSearch(int playerColor, int seconds, int noOfIterations){
        this.playerColor = playerColor;
        this.seconds = seconds;
        this.noOfIterations = noOfIterations;
    }

    public String findNextMove(World world){
        long start = System.currentTimeMillis();
        long end = start + seconds * 1000L;
        int iterations = 0;

        Node rootNode = new Node();
        rootNode.getState().setWorld(world);
        rootNode.getState().setPlayerColor(playerColor);

        rootNode.expandNode();

        while (iterations < noOfIterations && System.currentTimeMillis() < end){
            iterations++;
            Node selectionNode;
            if (rootNode.getState().getVisitCount() == 0)
                selectionNode = rootNode.getRandomChildNode();
            else
                selectionNode = selectPromisingNode(rootNode);

            if (selectionNode.getState().getVisitCount() != 0)
                selectionNode.expandNode();
            Node nodeToExplore = selectionNode;
            if (selectionNode.getChildrenList().size() > 0) {
                nodeToExplore = selectionNode.getRandomChildNode();
            }
            int rolloutResult = simulateRollout(nodeToExplore);
            backPropagation(nodeToExplore, rolloutResult);
        }

        //System.out.println(iterations);
        Node bestNode = rootNode.getChildWithMaxValue();
        return bestNode.getState().getLastMovePlayed();
    }

    private Node selectPromisingNode(Node rootNode) {
        Node currentNode = rootNode;
        // while node is not a leaf node
        while (currentNode.getChildrenList().size() != 0){
            currentNode = UCT.findNodeWithMaxUCT(currentNode);
        }
        return currentNode;
    }

    private int simulateRollout(Node node) {
        int depth = 0;
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();

        while (!tempState.getWorld().terminalTest() && depth < maxDepth){
            tempState.randomPlay();
            depth++;
        }
        return tempState.getWorld().evaluate(playerColor);
    }

    private void backPropagation(Node nodeToUpdate, int value) {
        Node currNode = nodeToUpdate;
        while (currNode != null) {
            currNode.getState().increaseVisit();
            if (currNode.getState().getPlayerColor() != playerColor)
                currNode.getState().updateValue(value);
            currNode = currNode.getParent();
        }
    }

}
