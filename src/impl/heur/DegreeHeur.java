package impl.heur;

import impl.MyGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

public class DegreeHeur extends Heuristic {

    PriorityQueue<Integer> order;

    public DegreeHeur(MyGraph g) {
        super(g);
        order = new PriorityQueue<Integer>((a, b) -> g.undirectedGraphAL.get(b).size() - g.undirectedGraphAL.get(a).size());
        List<Integer> possInteger =  g.undirectedGraphAL.keySet().stream().filter(x -> !g.adjacent(x, g.getCenter()) && x != g.getCenter()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        order.addAll(possInteger);
    }

    @Override
    String getHeuristicName() {
        return "DegreeHeur";
    }

    @Override
    public List<Integer> getKnext(int k) {
        return IntStream.range(0, k).mapToObj(i -> order.poll()).toList();
    }


}
