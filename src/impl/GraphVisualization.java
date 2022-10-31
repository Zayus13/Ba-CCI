package impl;


import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.File;

public class GraphVisualization {
    public static void main(String[] args) {
        int center = 4;
        MyGraph g = new MyGraph(center);
        /*
        for (int i = 0; i < 8; i++) {
            g.addVertex(i);
        }
        g.addEdge(1, 2);
        g.addEdge(1, 5);
        g.addEdge(2, 7);
        g.addEdge(0, 5);
        g.addEdge(1, 3);
        g.addEdge(3, 4);
        g.addEdge(3, 6);
        g.addEdge(6, 7);
        */
        g.addFromString(new File("konect/undirected-simple-small/contiguous-usa/out.contiguous-usa"));
        g.allShortestPathFloydWarshall();

        Graph vis = new SingleGraph("Test");
        g.undirectedGraphAL.forEach((k, v) -> {
            vis.addNode("Node:" + k);
        });

        g.undirectedGraphAL.forEach((k, v) -> {
            v.forEach(v1 -> {
                if (k < v1) {
                    vis.addEdge(k + "<->" + v1, k, v1);
                }

            });
        });


        vis.nodes().forEach(it -> it.setAttribute("ui.label", it.getId()));
        vis.getNode(center).setAttribute("ui.style", "fill-color: red;");
        System.setProperty("org.graphstream.ui", "swing");
        vis.display();

    }
}
