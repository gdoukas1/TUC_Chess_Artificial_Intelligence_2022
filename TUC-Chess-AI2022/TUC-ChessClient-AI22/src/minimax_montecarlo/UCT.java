package minimax_montecarlo;

import java.util.Collections;
import java.util.Comparator;

public class UCT {
    private static final double Cp = Math.sqrt(2);

    public static double uctValue(int value, int parentVisits, int nodeVisits){
        if (nodeVisits == 0)
            return Integer.MAX_VALUE;
        return (double)(value/nodeVisits) + Cp * Math.sqrt(Math.log(parentVisits)/nodeVisits);
    }

    public static Node findNodeWithMaxUCT(Node node){
        Comparator<Node> compareUCTs = new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                int parentVisits = n1.getParent().getState().getVisitCount();
                double uct1 = uctValue(n1.getState().getValue(), parentVisits, n1.getState().getVisitCount());
                double uct2 = uctValue(n2.getState().getValue(), parentVisits, n2.getState().getVisitCount());

                return Double.compare(uct1,uct2);
            }
        };
        return Collections.max(node.getChildrenList(), compareUCTs);
    }
}