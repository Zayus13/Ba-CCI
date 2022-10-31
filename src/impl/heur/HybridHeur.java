package impl.heur;

import impl.MyGraph;

import java.util.List;

public class HybridHeur extends Heuristic {

    List<Integer> remaining;
    DegreeHeur degreeHeur;
    DistanceHeur distanceHeur;


    public HybridHeur(MyGraph g) {
        super((MyGraph) g.getCopy());
        degreeHeur = new DegreeHeur(g);
        distanceHeur = new DistanceHeur(g);
 }



    @Override
    String getHeuristicName() {
        return "DistanceHeuristic";
    }

    @Override
    public List<Integer> getKnext(int k) {
        if (k > degreeHeur.order.size()/2) {
            return degreeHeur.getKnext(k);
        } else {
            return distanceHeur.getKnext(k);
        }
    }

}