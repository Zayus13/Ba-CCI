package impl.heur;

import impl.MyGraph;

import java.util.ArrayList;
import java.util.List;

public class InorderHeur extends Heuristic {

    ArrayList<Integer> inorder;

    public InorderHeur(MyGraph g) {
        super(g);
        inorder =  g.undirectedGraphAL.keySet().stream().filter(x -> !g.adjacent(x, g.getCenter()) && x != g.getCenter()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    String getHeuristicName() {
        return "inorderHeuristic";
    }

    @Override
    public List<Integer> getKnext(int k) {
        return inorder.subList(0, k);
    }
}
