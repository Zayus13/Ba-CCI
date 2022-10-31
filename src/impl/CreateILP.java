package impl;

import gurobi.GRBException;

import java.io.File;
import java.io.FileWriter;

public class CreateILP {

    public static void main(String[] args) throws GRBException {

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
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CcmILP ccmModel = new CcmILP(g, center, true);
        long time = setupModel(ccmModel);
        try (FileWriter logfile = new FileWriter(f, true)) {
            logfile.append("create").append(",")
                    .append("" + time).append("\n");
            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!(new File("ilp").exists())) {
            new File("ilp").mkdirs();
        }
        ccmModel.model.write("ilp/" + graphName + ".mps");
    }


    public static long setupModel(CcmILP model) throws GRBException {
        long start = System.nanoTime();
        model.init();
        model.setK();
        return System.nanoTime() - start;
    }

}
