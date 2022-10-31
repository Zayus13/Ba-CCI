package impl;

import impl.heur.*;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class GreedyCCI {

    static long time2;
    static long time3;
    public static void main(String[] args) {
        System.out.println("start");
        String path = args[0];
        int center = Integer.parseInt(args[1]);
        int k = Integer.parseInt(args[2]);

        MyGraph g = new MyGraph(center);
        g.addFromString(new File(path));

        String problemPath = "testResults_" + args[3] +"_LZ";
        String graphName = path.substring(path.lastIndexOf("/") + 1);
        String resPath = problemPath + "/results_" + graphName + ".csv";
        File testResults = new File(problemPath);
        if (!testResults.exists()) {
            testResults.mkdir();
        }
        File f = new File(resPath);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Heuristic h = switch (args[3]) {
            case "degree" -> new DegreeHeur(g);
            case "degreeRandom" -> new DegreeHeurRandom(g);
            case "degreeInverse" -> new DegreeInverseHeur(g);
            case "distance" -> new DistanceHeur(g);
            case "inorder" -> new InorderHeur(g);
            case "hybrid" -> new HybridHeur(g);
            default -> throw new IllegalStateException("Unexpected value: " + args[3]);
        };

        long start = System.nanoTime();
        int res = solveForKHeur(g, k, h);
        long time = System.nanoTime() - start;
        System.out.println("time: " + time);
        System.out.println("res: " + res);
        try (FileWriter logfile = new FileWriter(f, true)) {
            logfile.append(""+ k).append(",")
                    .append("" + res).append(",")
                    .append("" + time2).append(",").append("" + time3).
                    append("\n");
            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int solveForKHeur(MyGraph g, int k, Heuristic h) {
        long start = System.nanoTime();
        List<Integer> newEdges = h.getKnext(k);
        time2 = System.nanoTime() - start;
        long start2 = System.nanoTime();
        for (Integer v : newEdges) {
            g.addEdge(v, g.getCenter());
        }
        g.allShortestPathFloydWarshall();
        int cc =  g.coverageCentrality();
        time3 = System.nanoTime() - start2;
        return cc;
    }

}
