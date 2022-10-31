package impl;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            switch (args[0]) {
                case "create" -> CreateILP.main(Arrays.copyOfRange(args, 1, args.length));
                case "solve" -> SolveILP.main(Arrays.copyOfRange(args, 1, args.length));
                case "exp" -> ILPExperiment.main(Arrays.copyOfRange(args, 1, args.length));
                case "greedy" -> GreedyCCI.main(Arrays.copyOfRange(args, 1, args.length));
            }
        } catch (Exception e) {
            System.out.println(Arrays.toString(args));
            e.printStackTrace();

        }
    }


}
