package impl.heur;

import impl.MyGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * It's a heuristic that adds the node that is furthest from the center to the center
 */
public class DistanceHeur extends Heuristic {


    List<Integer> remaining;


    public DistanceHeur(MyGraph g) {
        super((MyGraph) g.getCopy());
        remaining = g.undirectedGraphAL.keySet().stream().filter(x -> !g.adjacent(x, g.getCenter()) && x != g.getCenter()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    String getHeuristicName() {
        return "DistanceHeuristic";
    }

    @Override
    public List<Integer> getKnext(int k) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 1; i <= k; i++) {
            int max = -1;
            int argmax = -1;
            g.allShortestPathFloydWarshall();
            for (Integer rem : remaining) {
                if (g.dist[rem][g.getCenter()] > max) {
                    max = g.dist[rem][g.getCenter()];
                    argmax = rem;
                }
            }
            if (argmax >= 0) {
                res.add(argmax);
                remaining.remove((Integer) argmax);
            }
        }
        return res;
    }

}

