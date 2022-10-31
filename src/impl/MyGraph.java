package impl;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MyGraph implements GraphInterface {

    public HashMap<Integer, Set<Integer>> undirectedGraphAL = new HashMap<>();
    public int[][] undirectedGraphAM;
    public int[][] dist;
    public int[][] distRectified;

    public int getCenter() {
        return center;
    }

    // A variable that stores the ID of the center vertex.
    private int center;

    public MyGraph() {
    }

    public MyGraph(int center) {
        this.center = center;
    }

    public MyGraph(Map<Integer, Set<Integer>> g) {
        undirectedGraphAL = (HashMap<Integer, Set<Integer>>)g.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> (Set<Integer>) new HashSet<>(e.getValue())));
    }

    public MyGraph(String filename) {
        File file = new File(filename);
        addFromString(file);
    }


    /**
     * Adds a new vertex to the graph
     *
     * @param v Id of the new added vertex
     */
    @Override
    public void addVertex(Integer v) {
        undirectedGraphAL.put(v, new HashSet<>());
    }

    /**
     * Adds a new edge to the graph
     *
     * @param v First Id of incident vertex
     * @param w Second ID of incident vertex
     */
    @Override
    public void addEdge(Integer v, Integer w) {
        if (!undirectedGraphAL.containsKey(v)) {
            addVertex(v);
        }
        if (!undirectedGraphAL.containsKey(w)) {
            addVertex(w);
        }
        undirectedGraphAL.get(v).add(w);
        undirectedGraphAL.get(w).add(v);
    }


    public void addDirectedEdge(Integer v, Integer u) {
        if (!undirectedGraphAL.containsKey(u)) {
            addVertex(u);
        }
        undirectedGraphAL.get(u).add(v);
    }


    public void addNeighbors(Integer v, Set<Integer> n) {
        undirectedGraphAL.put(v, n);
    }

    /**
     * Delets a vertex from the graph
     *
     * @param v Id of the new added vertex
     */
    @Override
    public void deleteVertex(Integer v) {
        undirectedGraphAL.get(v).forEach(x ->
                deleteEdge(v, x));
        undirectedGraphAL.remove(v);
    }

    public void deleteSingleton(Integer v) {
        undirectedGraphAL.remove(v);
    }


    /**
     * Delets an edge from the graph
     *
     * @param u
     * @param v First Id of incident vertex
     */
    @Override
    public void deleteEdge(Integer u, Integer v) {
        undirectedGraphAL.get(u).remove(v);
        undirectedGraphAL.get(v).remove(u);
    }

    /**
     * Return whether the given vertex belongs to the graph
     *
     * @param v Vertex ID which will be checked
     * @return True if the ID belongs to the graph, False if not
     */
    @Override
    public boolean contains(Integer v) {
        return undirectedGraphAL.containsKey(v);
    }

    /**
     * Return the degree of a vertex in the graph
     *
     * @param v Vertex ID
     * @return Degree of vertex v if v in the graph, null else
     */
    @Override
    public int degree(Integer v) {
        var elem = undirectedGraphAL.get(v);
        return (elem != null) ? elem.size() : Integer.MIN_VALUE;
    }

    /**
     * Returns whether vertices v and w are adjacent
     *
     * @param v ID of first vertex
     * @param w ID of second vertex
     * @return True if v and w are adjacent, False else
     */
    @Override
    public boolean adjacent(Integer v, Integer w) {
        return undirectedGraphAL.get(v).contains(w);
    }

    /**
     * Creats a copy of the graph
     *
     * @return Copy of the graph
     */
    @Override
    public GraphInterface getCopy() {
        MyGraph g = new MyGraph(this.undirectedGraphAL);
        g.center = this.getCenter();
        return g;
    }

    /**
     * Returns the neighbors of a vertex
     *
     * @param v ID of the vertex
     * @return Set of the neighbors of vertex v
     */
    @Override
    public Set<Integer> getNeighbors(Integer v) {
        return undirectedGraphAL.get(v);
    }


    /**
     * Returns the vertex set
     *
     * @return Set of the vertices of the graph
     */
    @Override
    public Set<Integer> getVertices() {
        return undirectedGraphAL.keySet();
    }

    public void addFromString(File file) {
        Stream<String> lines;
        try {
            lines = Files.lines(file.toPath());
            lines.forEach(line -> {
                if (!line.isEmpty() && line.matches("^\\d+\\s+\\d+\\s*((\\d+\\s*)|(\\.\\d+\\s*))?$")) {
                    String[] edge = line.split("\\s+");
                    addEdge(Integer.parseInt(edge[0]) - 1, Integer.parseInt(edge[1]) - 1);
                }
            });
            lines.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("MyGraph \n");
        for (Integer s : undirectedGraphAL.keySet()) {
            sb.append(s + ":\t" + undirectedGraphAL.get(s).toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * For each vertex in the adjacency list, we add an edge to the adjacency matrix for each of its neighbors
     */
    public void createAMfromAL() {
        undirectedGraphAM = new int[undirectedGraphAL.size()][undirectedGraphAL.size()];
        undirectedGraphAL.forEach((k, v) -> {
            v.forEach(v1 -> {
                        undirectedGraphAM[v1][k] = 1;
                        undirectedGraphAM[k][v1] = 1;
                    }
            );
        });
        for (int i = 0; i < undirectedGraphAM.length; i++) {
            for (int j = i; j < undirectedGraphAM.length; j++) {
                if (undirectedGraphAM[i][j] == 0) {
                    undirectedGraphAM[i][j] = -1;
                    undirectedGraphAM[j][i] = -1;
                }
            }
        }
    }


    /**
     * For every pair of vertices, check if the distance between them is greater than the sum of the distances between them
     * and a third vertex. If so, update the distance between them
     * <p>
     * based on Floyd-Warshall (O(|V|^3))
     */
    public void allShortestPathFloydWarshall() {
        createAMfromAL();
        dist = Arrays.stream(undirectedGraphAM).map(int[]::clone).toArray(int[][]::new);
        for (int k = 0; k < undirectedGraphAM.length; k++) {
            for (int i = 0; i < undirectedGraphAM.length; i++) {
                for (int j = i + 1; j < undirectedGraphAM.length; j++) {
                    if ((dist[i][j] > dist[i][k] + dist[k][j] || dist[i][j] < 0) && dist[i][k] > 0 && dist[k][j] > 0) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        dist[j][i] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        distRectified = deepCopy(dist);
        for (int i = 0; i < undirectedGraphAM.length; i++) {
            distRectified[i][i] = 0;
        }
    }

    <T> int[][] deepCopy(int[][] matrix) {
        return java.util.Arrays.stream(matrix).map(int[]::clone).toArray($ -> matrix.clone());
    }

    /**
     * For every pair of vertices, if the distance between them is equal to the sum of the distances from each vertex to
     * the center, then increment the count
     *
     * @return The number of shortest paths that pass through the center.
     */
    public int coverageCentrality() {
        int count = 0;
        List<Map.Entry<Integer, Integer>> shortestPairs = new ArrayList<>();
        for (int i = 0; i < undirectedGraphAM.length; i++) {
            for (int j = i + 1; j < undirectedGraphAM.length; j++) {
                if (i != center && dist[i][j] > 0 && dist[i][center] > 0 && dist[center][j] > 0) {
                    if (dist[i][j] == dist[i][center] + dist[center][j]) {
                        count++;
                        shortestPairs.add(new AbstractMap.SimpleEntry<>(i, j));
                    } else if (dist[i][j] > dist[i][center] + dist[center][j]) {
                        System.out.println("something went wrong!");
                    }
                }

            }
        }
        return count;
    }

    public List<Integer> shortestPathOneEdge(int u, int v, int c) {
        allShortestPathFloydWarshall();
        return IntStream.range(0, getVertices().size()).filter(it -> it != c).filter(it -> distRectified[u][it] + 1 + distRectified[c][v] <= distRectified[u][v]).boxed().collect(Collectors.toList());
    }

    public List<Pair> shortestPathTwoEdges(int u, int v, int c, List<Integer> lI) {
        List<Pair> l = new ArrayList<>();
        int size = getVertices().size();
        allShortestPathFloydWarshall();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != c && j != c && i != j) {
                    if (lI == null || !(lI.contains(i) || lI.contains(j))) {
                        if (distRectified[u][i] + 2 + distRectified[j][v] <= distRectified[u][v]) {
                            l.add(new Pair(i, j));
                        }
                    }
                }
            }
        }
        return l;
    }

    public int findHighestDegree() {
        final int[] highestDegree = {0};
        final int[] vertex = {0};
        undirectedGraphAL.forEach((k, v) -> {
            if (v.size() > highestDegree[0]) {
                highestDegree[0] = v.size();
                vertex[0] = k;
            }
        });
        return vertex[0];
    }


}