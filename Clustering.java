import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;

public class Clustering {

    // Number of Clusters
    private int k;

    // Cluster of each point
    private int[] clusters;

    /**
     * Clustering constructor
     *
     * @param locations
     * @param k
     */
    public Clustering(Point2D[] locations, int k) {
        if (locations == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }

        for (Point2D p : locations) {
            if (p == null) {
                throw new IllegalArgumentException("Location cannot be null");
            }
        }

        int m = locations.length;

        if (k < 1 || k > m) {
            throw new IllegalArgumentException("Invalid number of clusters");
        }

        EdgeWeightedGraph graph = new EdgeWeightedGraph(m);
        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                Edge e = new Edge(i, j, locations[i].distanceTo(locations[j]));
                graph.addEdge(e);
            }
        }

        KruskalMST mst = new KruskalMST(graph);
        EdgeWeightedGraph clusterGraph = new EdgeWeightedGraph(m);
        int counter = 0;
        for (Edge e : mst.edges()) {
            if (counter > m - k - 1) {
                break;
            }
            clusterGraph.addEdge(e);
            counter++;
        }

        CC cc = new CC(clusterGraph);
        clusters = new int[m];
        for (int i = 0; i < m; i++) {
            clusters[i] = cc.id(i);
        }

        this.k = k;
    }

    /**
     * Returns the cluster id of a point
     *
     * @return cluster id
     */
    public int clusterOf(int i) {
        if (i < 0 || i >= clusters.length) {
            throw new IllegalArgumentException("Invalid point");
        }

        return clusters[i];
    }

    /**
     * Reduces an input based on the clusters.
     *
     * @param input
     * @return reduced input
     */
    public double[] reduceDimensions(double[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        if (input.length != clusters.length) {
            throw new IllegalArgumentException("Invalid input");
        }

        double[] output = new double[k];
        for (int i = 0; i < input.length; i++) {
            output[clusterOf(i)] += input[i];
        }
        return output;
    }

    /**
     * Unit tests the Clustering data type.
     *
     * @param args
     */
    public static void main(String[] args) {
        In datafile = new In(args[0]);
        int m = datafile.readInt();

        Point2D[] locations = new Point2D[m];
        for (int i = 0; i < m; i++) {
            double x = datafile.readDouble();
            double y = datafile.readDouble();
            locations[i] = new Point2D(x, y);
        }

        Clustering c = new Clustering(locations, 5);

        StdOut.println("Cluster of each point:");
        for (int i = 0; i < m; i++) {
            StdOut.print(c.clusterOf(i) + " ");
        }

        StdOut.println("\n\nReduced Input:");
        double[] input = {
                5.0, 6.0, 7.0, 0.0, 6.0, 7.0,
                5.0, 6.0, 7.0, 0.0, 6.0, 7.0,
                0.0, 6.0, 7.0, 0.0, 6.0, 7.0,
                0.0, 6.0, 7.0
        };
        for (double d : c.reduceDimensions(input)) {
            StdOut.print(d + " ");
        }
    }
}
