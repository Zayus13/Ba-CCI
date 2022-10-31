package impl;

import gurobi.*;

import java.io.*;

public class SolveILP {

    public static void main(String[] args) throws GRBException, IOException {

        String path = args[0];
        int center = Integer.parseInt(args[1]);
        int k = Integer.parseInt(args[2]);

        MyGraph g = new MyGraph(center);
        g.addFromString(new File(path));

        String graphName = path.substring(path.lastIndexOf("/") + 1);
        String resPath = "testResults/" + "results_" + graphName + ".csv";
        File testResults = new File("testResults");
        if (!testResults.exists()) {
            testResults.mkdir();
        }
        File f = new File(resPath);
        String filename = "ilp/" + graphName + ".mps";

        GRBEnv env = new GRBEnv();
        GRBModel model = new GRBModel(env, filename);
        model.getConstrByName("MaxEdges").set(GRB.DoubleAttr.RHS, model.getConstrByName("MaxEdges").get(GRB.DoubleAttr.RHS)*-1 + k);
        model.set(GRB.DoubleParam.TimeLimit, 1200);
        model.set(GRB.IntParam.Threads, 1);
        model.update();
        long start = System.nanoTime();
        model.optimize();
        long time = System.nanoTime() - start;
        double ilpRes = ((GRBLinExpr) model.getObjective()).getValue();
        try (FileWriter logfile = new FileWriter(f, true)) {
            logfile.append("" + k).append(",")
                    .append("" + ilpRes).append(",")
                    .append("" + time).append(",").append("\n");
            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.dispose();
        env.dispose();
    }

    public static long setupModel(CcmILP model) throws GRBException {
        long start = System.nanoTime();
        model.init();
        return System.nanoTime() - start;
    }

}
