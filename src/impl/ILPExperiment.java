package impl;

import gurobi.GRBException;
import gurobi.GRBLinExpr;

import java.io.File;
import java.io.FileWriter;

public class ILPExperiment {

    public static void main(String[] args) throws GRBException {

        String path = args[0];
        int center = Integer.parseInt(args[1]);
        int k = Integer.parseInt(args[2]);

        MyGraph g = new MyGraph(center);
        g.addFromString(new File(path));

        String graphName = path.substring(path.lastIndexOf("/") + 1);
        File testResults = new File("testResults");
        String resPath = "testResults\\" + "results_" + graphName + ".csv";
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
        CcmILP ccmModel = new CcmILP(g, center, true);
        long time = setupModel(ccmModel);
        long timeForK = ccmModel.solveForK(k, true);
        double ilpRes = ((GRBLinExpr) ccmModel.model.getObjective()).getValue();
        try (FileWriter logfile = new FileWriter(f, true)) {
            logfile.append("" + k).append(",")
                    .append("" + ilpRes).append(",")
                    .append("" + time).append(",")
                    .append("" + timeForK).append("\n");
            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ccmModel.model.dispose();
        ccmModel.env.dispose();
    }

    public static long setupModel(CcmILP model) throws GRBException {
        long start = System.nanoTime();
        model.init();
        return System.nanoTime() - start;
    }

}
