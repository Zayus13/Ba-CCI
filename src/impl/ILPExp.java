package impl;

import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ILPExp {
    public static void main(String[] args) throws IOException, GRBException {
        int center = 4;
        String[] locs = new String[]{
                "C:/Users/sven_/Desktop/Info/5/BA/data/network repository/social/soc-firm-hi-tech/soc-firm-hi-tech-without-gaps.txt"};
        for (String loc : locs) {
            MyGraph g = new MyGraph(center);
            g.addFromString(new File(loc));
            System.out.println(Arrays.toString(g.undirectedGraphAM));
            g.allShortestPathFloydWarshall();
            String graphName = loc.substring(loc.lastIndexOf("/") + 1);
            String folder = "testResults/konect";
            String resPath = folder + "/" + graphName + "/" + "results_" + graphName + ".csv";
            String lpPath = folder + "/" + graphName + "/" + graphName + ".lp";
            File f = new File(resPath);
            File lp = new File(lpPath);
            f.getParentFile().mkdirs();
            if (!f.exists()) {
                f.createNewFile();
            }
            CcmILP ccmModel = new CcmILP(g, center, false);
            CcmILP ccmModelQuick = new CcmILP(g, center, true);

            long time = setupModel(ccmModel);

            long timeQuick = setupModel(ccmModelQuick);
            try (FileWriter logfile = new FileWriter(f, true)) {
                logfile.append("-1").append(",")
                        .append("-1").append(",")
                        .append("" + time).append(",")
                        .append("-1").append(",")
                        .append("" + timeQuick).append("\n");
                logfile.flush();
                for (int k = 1; k <= 1; k++) {
                    System.out.println("k = " + k);

                    time = ccmModel.solveForK(k, true);
                    GRBModel model = ccmModel.model;
                    double ilpRes = ((GRBLinExpr) model.getObjective()).getValue();

                    timeQuick = ccmModelQuick.solveForK(k, true);
                    ccmModelQuick.model.write(lpPath);
                    GRBModel modelQuick = ccmModelQuick.model;
                    double ilpResQuick = ((GRBLinExpr) modelQuick.getObjective()).getValue();

                    modelQuick.write(resPath + graphName + "_" + k + ".sol");
                    if (ilpRes != ilpResQuick) System.out.println("wrong solution");
                    logfile.append("" + k).append(",")
                            .append("" + ilpRes).append(",")
                            .append("" + time).append(",")
                            .append("" + ilpResQuick).append(",")
                            .append("" + timeQuick).append("\n");
                    logfile.flush();
                }
                ccmModel.model.dispose();
                ccmModel.env.dispose();
                ccmModelQuick.model.dispose();
                ccmModelQuick.env.dispose();
            } catch (IOException e) {
                ccmModel.model.dispose();
                ccmModel.env.dispose();
                ccmModelQuick.model.dispose();
                ccmModelQuick.env.dispose();
                e.printStackTrace();
            }

        }
    }

    public static long setupModel(CcmILP model) throws GRBException {
        long start = System.nanoTime();
        model.init();
        return System.nanoTime() - start;
    }

}





