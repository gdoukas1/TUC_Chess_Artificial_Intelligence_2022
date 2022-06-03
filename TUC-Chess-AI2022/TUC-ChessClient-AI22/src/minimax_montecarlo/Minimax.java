package minimax_montecarlo;

import tuc_chess.World;

import java.util.*;

public class Minimax {
    private final Integer MAX = Integer.MAX_VALUE;
    private final Integer MIN = Integer.MIN_VALUE;
    private int maximizer;
    private int maxDepth;
    private boolean abPruning;

    public Minimax(int maximizer, int maxDepth, boolean pruning)
    {
        this.maximizer = maximizer;
        this.maxDepth = maxDepth;
        this.abPruning = pruning;
    }

    public String alphaBeta(World w)
    {
        Node rootNode = new Node();
        rootNode.getState().setWorld(w);
        rootNode.getState().setPlayerColor(maximizer);

        Node selectedNode = maxValue(rootNode, MIN, MAX, 0, abPruning);
        return selectedNode.getState().getLastMovePlayed();
    }

    private Node maxValue(Node node, int a, int b, int depth, boolean abPruning)
    {
        World w = node.getState().getWorld();
        if (w.terminalTest() || depth==maxDepth){
            node.getState().setValue(w.evaluate(maximizer));
            return node;
        }

        node.expandNode();
        Node maxNode = new Node();
        maxNode.getState().setValue(Integer.MIN_VALUE);

        for (Node child : node.getChildrenList()){
            Node tmpNode = minValue(child, a, b,depth+1, abPruning);
            if (tmpNode.getState().getValue() > maxNode.getState().getValue())
                maxNode = tmpNode;

            a = Math.max(a,tmpNode.getState().getValue());
            if (b <= a && abPruning){
                break;
            }
        }

        if (node.getParent() != null){
            node.getState().setValue(maxNode.getState().getValue());
            return node;
        }
        else
            return maxNode;
    }

    private Node minValue(Node node, int a, int b, int depth, boolean abPruning)
    {
        World w = node.getState().getWorld();
        if (w.terminalTest() || depth==maxDepth){
            node.getState().setValue(w.evaluate(maximizer));
            return node;
        }

        node.expandNode();
        Node minNode = new Node();
        minNode.getState().setValue(Integer.MAX_VALUE);

        for (Node child : node.getChildrenList()){
            Node tmpNode = maxValue(child, a, b,depth+1, abPruning);
            if (tmpNode.getState().getValue() < minNode.getState().getValue())
                minNode = tmpNode;

            b = Math.min(b,tmpNode.getState().getValue());
            if (b <= a && abPruning){
                break;
            }
        }
        node.getState().setValue(minNode.getState().getValue());
        return node;
    }

}

