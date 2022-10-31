package impl.heur;

import impl.MyGraph;

import java.util.List;

public abstract class Heuristic {

    MyGraph g;

    public Heuristic(MyGraph g) {
        this.g = g;
    }

    abstract String getHeuristicName();

    public abstract List<Integer> getKnext(int k);

}
