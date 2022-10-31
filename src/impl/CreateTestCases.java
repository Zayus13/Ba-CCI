package impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class CreateTestCases {

    public static void main(String[] args) {
        String path = "../../../";
        String pathLater = "../" + path;
        String[] graphs = new String[]{
                //animal - 1
                "data/network repository/animal/reptilia-tortoise-network-fi.edges",
                //konnect - 7
                "data/konect/undirected-simple-small/adjnoun_adjacency/out.adjnoun_adjacency_adjacency",
                "data/konect/undirected-simple-small/arenas-jazz/out.arenas-jazz",
                "data/konect/undirected-simple-small/arenas-email/out.arenas-email",
                "data/konect/undirected-simple-small/contiguous-usa/out.contiguous-usa",
                "data/konect/undirected-simple-small/dolphins/out.dolphins",
                "data/konect/undirected-simple-small/moreno_zebra/out.moreno_zebra_zebra",
                "data/konect/undirected-simple-small/ucidata-zachary/out.ucidata-zachary",

                // social - 3
                "data/network repository/social/soc-anybeat/soc-anybeat.txt",
                "data/network repository/social/soc-brightkite/soc-brightkite.txt",
                "data/network repository/social/tech-RL-caida/tech-RL-caida.txt",
                "data/network repository/social/soc-firm-hi-tech/soc-firm-hi-tech-without-gaps.txt",
                "data/network repository/social/soc-wiki-Vote/soc-wiki-Vote.txt",


                //bio - 4
                "data/network repository/bio/bio-yeast.mtx",
                "data/network repository/bio/bio-diseasome.mtx",
                "data/network repository/bio/bio-celegans.mtx",
                "data/network repository/bio/bio-dmela.mtx",

                //collaboration - 8
                "data/network repository/collaboration/ca-AstroPh.mtx",
                "data/network repository/collaboration/ca-CondMat.mtx",
                "data/network repository/collaboration/ca-CSphd.mtx",
                "data/network repository/collaboration/ca-Erdos992.mtx",
                "data/network repository/collaboration/ca-HepPh.mtx",
                "data/network repository/collaboration/ca-GrQc.mtx",
                "data/network repository/collaboration/ca-netscience.mtx",
                "data/network repository/collaboration/ca-sandi_auths.mtx",

                //econ - 1
                "data/network repository/econ/econ-beause.edges",


                //infrastructure - 3
                "data/network repository/infrastructure/inf-power.mtx",
                "data/network repository/infrastructure/inf-openflights.edges",
                "data/network repository/infrastructure/inf-USAir97.mtx",

                // misc - 1
                "data/network repository/misc/robot24c1_mat5.edges",

                //brain - 1
                "data/network repository/brain/bn-cat-mixed-species_brain_1_2.edges",

                //TriangleClubs - 9
                "data/TriangleClubs/anna.col.metis.edges",
                "data/TriangleClubs/david.col.metis.edges",
                "data/TriangleClubs/mug100_25.col.metis.edges",
                "data/TriangleClubs/mug100_1.col.metis.edges",
                "data/TriangleClubs/mug88_1.col.metis.edges",
                "data/TriangleClubs/1-FullIns_4.col.metis.edges",
                "data/TriangleClubs/3-FullIns_3.col.metis.edges",
                "data/TriangleClubs/4-FullIns_3.col.metis.edges",
                "data/TriangleClubs/5-FullIns_3.col.metis.edges"



        };

        try (FileWriter writer = new FileWriter("data/data_ilp.txt")) {
            for (String g : graphs) {
                MyGraph graph = new MyGraph(path + g);
                String name = g.substring(g.lastIndexOf("/") + 1);
                int center = graph.findHighestDegree();
                System.out.println(name + "," + graph.undirectedGraphAL.size() + "," + graph.undirectedGraphAL.values().stream().mapToInt(Set::size).sum() / 2 + "," + (graph.getVertices().size() - graph.getNeighbors(center).size()));
                String fullPath = "\"" + pathLater + g + "\"".replace("/", "\\");
                //writer.append(fullPath).append(" ").append(String.valueOf(center)).append(" ").append(String.valueOf(graph.getVertices().size() - graph.getNeighbors(center).size()));
                //writer.append(System.lineSeparator());
                //writer.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
