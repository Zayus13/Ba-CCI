package impl;

import gurobi.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CcmILP {

    GRBEnv env = new GRBEnv();
    GRBModel model = new GRBModel(env);
    MyGraph graph;
    int center;
    boolean quick;
    HashMap<Integer, GRBVar> singleEdges = new HashMap<>();
    HashMap<Pair<Integer>, GRBVar> doubleEdges = new HashMap<>();
    HashMap<Pair<Integer>, GRBVar> paths = new HashMap<>();

    public CcmILP(MyGraph g, Integer center, boolean quick) throws GRBException {
        graph = g;
        this.center = center;
        this.quick = quick;
    }

    public CcmILP() throws GRBException {
    }

    public GRBModel init() throws GRBException {

        int center = graph.getCenter();
        int size = graph.getVertices().size();

        graph.allShortestPathFloydWarshall();

        model.set(GRB.DoubleParam.TimeLimit, 1200);
        model.set(GRB.IntParam.Threads, 1);
        //Set Variables: ShortestPath
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (i != center && j != center) {
                    paths.put(new Pair<Integer>(i, j), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "P(" + i + "," + j + ")"));
                }
            }
        }
        //Objective: Maximize number of shortest path over center P(i,j) for i,j, i != j & i != c & j != c
        GRBLinExpr objective = new GRBLinExpr();
        for (Pair<Integer> e : paths.keySet()) {
            objective.addTerm(1.0, paths.get(e));
        }
        model.setObjective(objective, GRB.MAXIMIZE);
        model.update();


        //Set Variables: double edges at center
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (i != center && j != center) {
                    doubleEdges.put(new Pair<Integer>(i, j), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "D(" + i + "," + j + ")"));
                }
            }
        }

        //Set Variables: single edges at center
        // already existing edges = 1
        // potential new edges in {0, 1}, binary
        for (int i = 0; i < size; i++) {
            if (i != center) {
                if (graph.getNeighbors(center).contains(i)) {
                    singleEdges.put(i, model.addVar(1.0, 1.0, 0.0, GRB.BINARY, "S'(" + i + ")"));
                } else {
                    singleEdges.put(i, model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "S(" + i + ")"));
                }
            }
        }

        //Set Constraint on double edge Variables
        for (Pair<Integer> p : doubleEdges.keySet()) {
            int u = p.u;
            int v = p.v;
            GRBLinExpr constraint = new GRBLinExpr();
            constraint.addTerm(0.5, singleEdges.get(u));
            constraint.addTerm(0.5, singleEdges.get(v));
            model.addConstr(doubleEdges.get(p), GRB.LESS_EQUAL, constraint, String.format("doubleEdge(%d,%d)-Constrain", u, v));
        }
        model.update();

        //Path-Constraint: One Edge must be in single edge or double edge constraint
        //Set Variables: double edges at center
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (i != center && j != center) {
                    GRBLinExpr shortestPathConstraint = new GRBLinExpr();
                    List<Integer> goodSingleEdges = graph.shortestPathOneEdge(i, j, center);
                    goodSingleEdges.addAll(graph.shortestPathOneEdge(j, i, center));

                    List<Pair> goodDoubleEdges;
                    if (quick) {
                        goodDoubleEdges = graph.shortestPathTwoEdges(i, j, center, goodSingleEdges);
                    } else {
                        goodDoubleEdges = graph.shortestPathTwoEdges(i, j, center, null);
                    }

                    Set<Integer> goodSingleEdgesSet = new HashSet<>(goodSingleEdges);
                    goodDoubleEdges.forEach(it -> {
                        shortestPathConstraint.addTerm(1.0, doubleEdges.get(it));
                    });
                    goodSingleEdgesSet.forEach(it -> {
                        shortestPathConstraint.addTerm(1.0, singleEdges.get(it));
                    });
                    model.addConstr(paths.get(new Pair<Integer>(i, j)), GRB.LESS_EQUAL, shortestPathConstraint, String.format("shortestPath(%d,%d)", i, j));
                }
            }
        }
        model.update();
        return model;
    }

    public long solveForK(int k, boolean replace) throws GRBException {
        model.update();
        if (replace) {
            GRBConstr constr = model.getConstrByName("MaxEdges");
            if (constr != null) {
                model.remove(constr);
            }
        }
        //Edge Budget
        GRBLinExpr constraint = new GRBLinExpr();
        for (Integer v : singleEdges.keySet()) {
            constraint.addTerm(1.0, singleEdges.get(v));
        }
        model.addConstr(constraint, GRB.LESS_EQUAL, k + graph.getNeighbors(center).size(), "MaxEdges");
        model.update();
        long start = System.nanoTime();
        model.optimize();
        return System.nanoTime() - start;
    }

    public void setK() throws GRBException {
        model.update();
        GRBLinExpr constraint = new GRBLinExpr();
        for (Integer v : singleEdges.keySet()) {
            constraint.addTerm(1.0, singleEdges.get(v));
        }
        model.addConstr(constraint, GRB.LESS_EQUAL, -1 * graph.getNeighbors(center).size(), "MaxEdges");
        model.update();
    }
}
