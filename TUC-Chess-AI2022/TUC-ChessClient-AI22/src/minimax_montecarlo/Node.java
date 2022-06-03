package minimax_montecarlo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Node {
    State state;
    Node parent;
    ArrayList<Node> childrenList;

    public Node(){
        this.state = new State();
        childrenList = new ArrayList<>();
    }

    public Node(State state){
        this.state = state;
        childrenList = new ArrayList<>();
    }

    public Node(State state, Node parent, ArrayList<Node> childrenList) {
        this.state = state;
        this.parent = parent;
        this.childrenList = childrenList;
    }

    public Node(Node node){
        this.state = new State(node.getState());
        if (node.getParent() != null)
            this.parent = node.getParent();
        this.childrenList = new ArrayList<>();
        ArrayList<Node> childrenArray = node.getChildrenList();
        for (Node child : childrenArray) {
            this.childrenList.add(new Node(child));
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public ArrayList<Node> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(ArrayList<Node> childrenList) {
        this.childrenList = childrenList;
    }


    public void expandNode() {
        ArrayList<State> possibleStates = this.getState().getAllPossibleStates();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state);
            newNode.setParent(this);
            this.getChildrenList().add(newNode);
        });
    }

    public Node getRandomChildNode() {
        Random ran = new Random();
        int x = ran.nextInt(this.childrenList.size());

        return this.childrenList.get(x);
    }

    public Node getChildWithMaxValue(){
        return Collections.max(this.childrenList, Comparator.comparing(c -> averageReward(c.getState().getValue(),c.getState().getVisitCount())));
    }

    private int averageReward(int value, int visitCount) {
        if (visitCount == 0)
            return Integer.MAX_VALUE;
        return value/visitCount;
    }
}
