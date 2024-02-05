import edu.princeton.cs.algs4.Point2D;
import java.util.ArrayList;
import edu.princeton.cs.algs4.StdOut;

/**
 * The BoostingAlgorithm class.
 */
public class BoostingAlgorithm {

    // Store the input
    private double[][] input;

    // Store the labels
    private int[] labels;

    // Store the weights
    private double[] weights;

    // Clustering object used for dimensionality reduction
    private Clustering clust;

    // Store the weak learners after each training iteration
    private ArrayList<WeakLearner> weakLearners;

    /**
     * BoostingAlgorithm constructor
     *
     * @param input
     * @param labels
     * @param locations
     * @param k
     */
    public BoostingAlgorithm(double[][] input, int[] labels,
                             Point2D[] locations, int k) {

        if (input == null || labels == null || locations == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        for (Point2D p : locations) {
            if (p == null) {
                throw new IllegalArgumentException("Location cannot be null");
            }
        }

        if (k < 1 || k > locations.length) {
            throw new IllegalArgumentException("Invalid number of clusters");
        }

        for (int i: labels) {
            if (i != 0 && i != 1) {
                throw new IllegalArgumentException("Labels must be 0 or 1");
            }
        }

        if (input.length != labels.length) {
            throw new IllegalArgumentException("Input and labels must have
                                               same number of samples");
        }

        if (input[0].length != locations.length) {
            throw new IllegalArgumentException("Input and locations must have
                                               same number of samples");
        }

        int n = input.length;
        clust = new Clustering(locations, k);

        double[][] reducedInput = new double[n][k];
        for (int i = 0; i < n; i++) {
            reducedInput[i] = clust.reduceDimensions(input[i]);
        }

        weights = new double[n];
        for (int i = 0; i < n; i++) {
            weights[i] = 1.0 / n;
        }

        weakLearners = new ArrayList<WeakLearner>();

        this.input = reducedInput;
        this.labels = labels;
    }

    /**
     * Returns the weights
     *
     * @return weights
     */
    public double[] weights() {
        return weights;
    }

    /**
     * Returns the weak learners
     *
     * @return weak learners
     */
    public void iterate() {
        WeakLearner wl = new WeakLearner(input, weights, labels);
        weakLearners.add(wl);
        for (int i = 0; i < input.length; i++) {
            if (wl.predict(input[i]) != labels[i]) {
                weights[i] *= 2;
            }
        }

        double sum = 0;
        for (double w : weights) {
            sum += w;
        }

        for (int i = 0; i < input.length; i++) {
            weights[i] /= sum;
        }
    }

    /**
     * Returns the prediction of a sample
     *
     * @param sample
     * @return prediction
     */
    public int predict(double[] sample) {
        if (sample == null) {
            throw new IllegalArgumentException("Sample cannot be null");
        }

        sample = clust.reduceDimensions(sample);

        if (sample.length != input[0].length) {
            throw new IllegalArgumentException("Sample must have same number
                                               of dimensions as input");
        }

        int sum = 0;
        for (WeakLearner wl : weakLearners) {
            int p = wl.predict(sample);
            if (p == 0) {sum--;}
            else {sum++;}
        }

        if (sum <= 0) {return 0;}
        else {return 1;}
    }

    /**
     * Returns the number of weak learners
     *
     * @return number of weak learners
     */
    public static void main(String[] args) {
        double[][] input = {{1, 6},
                            {2, 6},
                            {3, 2},
                            {4, 1},
                            {4, 4},
                            {5, 5},
                            {6, 3},
                            {6, 7}
        };
        int[] labels = {0, 1, 1, 0, 1, 0, 1, 0};
        Point2D[] locations = {new Point2D(1, 6),
                               new Point2D(2, 6)
        };

        BoostingAlgorithm ba = new BoostingAlgorithm(input, labels,
                                                     locations, 2);

        StdOut.println("Iterate the BoostingAlgorithm 10 times");
        for (int i = 0; i < 10; i++) {
            ba.iterate();
        }

        double[] sample = {5, 6};
        StdOut.println("\nPrediction (5,6): " + ba.predict(sample));

        StdOut.println("\nWeights:");
        for (int i = 0; i < ba.weights.length; i++) {
            StdOut.println(ba.weights[i]);
        }
    }
}
