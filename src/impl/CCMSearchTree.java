package impl;


import java.util.*;

public class CCMSearchTree {
    private MyGraph g;
    public int k;
    private int center;
    Stack<Integer> added = new Stack<>();
    List<List<Integer>> best = new ArrayList<>();
    int maxCoverage;

    public CCMSearchTree(MyGraph g, int k, int center) {
        this.g = g;
        this.k = k;
        this.center = center;
    }

    public Set<Integer> getEdgeOptions() {
        Set<Integer> res = new HashSet<>(g.getVertices());
        res.remove(center);
        res.removeAll(g.getNeighbors(center));
        return res;
    }

    public void addEdge(Integer v) {
        k--;
        g.addEdge(center, v);
    }

    public void removeEdge(Integer v) {
        k++;
        g.deleteEdge(center, v);
    }

    public void restore(int counter) {
        while (counter > 0) {
            removeEdge(added.pop());
            counter--;
        }
    }

    public int solve(int remainingSteps) {
        Set<Integer> options = getEdgeOptions();
        int res = 0;
        if (remainingSteps > 0) {
            for (Integer option : options) {
                added.push(option);
                g.addEdge(center, option);
                int r = solve(remainingSteps - 1);
                if (r > res) {
                    res = r;
                }
                g.deleteEdge(center, added.pop());
            }
            return res;
        } else {
            g.createAMfromAL();
            g.allShortestPathFloydWarshall();
            int cov = g.coverageCentrality();
            if (cov > maxCoverage) {
                maxCoverage = cov;
                best = new ArrayList<>();
                best.add(new ArrayList<>(added));
            } else if (cov == maxCoverage) {
                best.add(new ArrayList<>(added));
            }
            return cov;
        }
    }

    public int solve() {
        maxCoverage = g.coverageCentrality();
        System.out.println("Original Coverage: " + maxCoverage);
        int newCoverage = solve(k);
        System.out.println("New Coverage: " + newCoverage);
        System.out.println("Best Edges:");
        best.forEach(it -> System.out.println(it));
        return newCoverage;
    }

}
