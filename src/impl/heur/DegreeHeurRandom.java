package impl.heur;

import impl.MyGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DegreeHeurRandom extends Heuristic{

    Random r = new Random(1337);
    HashMap<Integer, List<Integer>> degreeToNodes;

    public DegreeHeurRandom(MyGraph g) {
        super(g);
        degreeToNodes = new HashMap<>();
        List<Integer> possInteger =  g.undirectedGraphAL.keySet().stream().filter(x -> !g.adjacent(x, g.getCenter()) && x != g.getCenter()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        for (Integer node : possInteger) {
            int degree = g.undirectedGraphAL.get(node).size();
            if (!degreeToNodes.containsKey(degree)) {
                degreeToNodes.put(degree, new ArrayList<>());
            }
            degreeToNodes.get(degree).add(node);
        }
    }

    @Override
    String getHeuristicName() {
        return "DegreeHeurRandom";
    }

    @Override
    public List<Integer> getKnext(int k) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 1; i <= k; i++) {
            double rand = r.nextDouble();
            int counter = 0;
            while (2 * rand < 1) {
                rand = rand * 2;
                counter++;
            }
            int degree = degreeToNodes.keySet().stream().sorted().toList().get(Math.max(0,degreeToNodes.size() - counter - 1));
            res.add(degreeToNodes.get(degree).remove(0));
            if (degreeToNodes.get(degree).isEmpty()) {
                degreeToNodes.remove(degree);
            }
        }
        return res;
    }
}
