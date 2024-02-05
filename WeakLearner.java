import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/**
 * The WeakLearner class.
 */
public class WeakLearner {

    // Dimension predictor
    private int dp;

    // Value predictor
    private double vp;

    // Sign predictor
    private int sp;

    // store the input
    private double[][] input;

    /**
     * WeakLearner constructor
     *
     * @param input
     * @param weights
     * @param labels
     */
    public WeakLearner(double[][] input, double[] weights, int[] labels) {
        if (input == null || weights == null || labels == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        for (int i = 0; i < weights.length; i++) {
            if (weights[i] < 0) {
                throw new IllegalArgumentException(
                        "Weights cannot be negative"
                );
            }
        }

        if (input.length != weights.length) {
            throw new IllegalArgumentException(
                    "Input and weights must have same number of samples"
            );
        }

        if (input.length != labels.length) {
            throw new IllegalArgumentException(
                    "Input and labels must have same number of samples"
            );
        }

        int bestDp = -1;
        double bestVp = -1;
        int bestSp = -1;
        double champWeight = -1.0;

        this.input = input;

        int n = input.length;
        int k = input[0].length;


        for (int d = 0; d < k; d++) {

            ValuePair[] valuePairs = new ValuePair[n];
            for (int i = 0; i < n; i++) {
                valuePairs[i] = new ValuePair(input[i][d],
                                              labels[i],
                                              weights[i]);
            }

            Arrays.sort(valuePairs);

            for (int s = 0; s < 2; s++) {

                double currWeight = 0;
                for (ValuePair valuePair : valuePairs) {
                    if (s == 0) {
                        if (valuePair.value <= valuePairs[0].value
                                && valuePair.label == 0) {
                            currWeight += valuePair.weight;
                        }
                        else if (valuePair.value > valuePairs[0].value
                                && valuePair.label == 1) {
                            currWeight += valuePair.weight;
                        }
                    }
                    else {
                        if (valuePair.value <= valuePairs[0].value
                                && valuePair.label == 1) {
                            currWeight += valuePair.weight;
                        }
                        else if (valuePair.value > valuePairs[0].value
                                && valuePair.label == 0) {
                            currWeight += valuePair.weight;
                        }
                    }
                }
                if (currWeight > champWeight) {
                    champWeight = currWeight;
                    bestDp = d;
                    bestVp = valuePairs[0].value;
                    bestSp = s;
                }

                double dupeWeight = 0;
                for (int i = 1; i < n; i++) {
                    if (i + 1 < n && valuePairs[i].value == valuePairs[i + 1].value) {
                        if (s == 0 && valuePairs[i].label == 1) {
                            dupeWeight -= valuePairs[i].weight;
                        }
                        else if (s == 1 && valuePairs[i].label == 0) {
                            dupeWeight -= valuePairs[i].weight;
                        }
                        else {
                            dupeWeight += valuePairs[i].weight;
                        }
                        continue;
                    }

                    if (s == 0 && valuePairs[i].label == 1) {
                        currWeight -= (valuePairs[i].weight + dupeWeight);
                    }
                    else if (s == 1 && valuePairs[i].label == 0) {
                        currWeight -= (valuePairs[i].weight + dupeWeight);
                    }
                    else {
                        currWeight += (valuePairs[i].weight + dupeWeight);
                    }

                    dupeWeight = 0;

                    if (currWeight > champWeight) {
                        champWeight = currWeight;
                        bestDp = d;
                        bestVp = valuePairs[i].value;
                        bestSp = s;
                    }
                }
            }
        }

        dp = bestDp;
        vp = bestVp;
        sp = bestSp;
    }

    /**
     * Private helper class to store the value, label, and weight of a single
     * observation. Implements the Comparable interface.
     */
    private class ValuePair implements Comparable<ValuePair> {

        // Value of the observation
        double value;

        // Label of the observation
        int label;

        // Weight of the observation
        double weight;

        /**
         * ValuePair constructor
         *
         * @param value
         * @param label
         * @param weight
         */
        public ValuePair(double value, int label, double weight) {
            this.value = value;
            this.label = label;
            this.weight = weight;
        }

        /**
         * Compare two ValuePairs by their value
         *
         * @param other
         */
        public int compareTo(ValuePair other) {
            return Double.compare(this.value, other.value);
        }
    }

    /**
     * Predicts the label of a sample
     *
     * @param sample
     * @return
     */
    public int predict(double[] sample) {
        if (sample == null) {
            throw new IllegalArgumentException("Sample cannot be null");
        }

        if (sample.length != input[0].length) {
            throw new IllegalArgumentException(
                    "Sample must have same number of dimensions as input"
            );
        }

        if ((sp == 0 && sample[dp] <= vp) || (sp == 1 && sample[dp] > vp)) {
            return 0;
        }
        else {
            return 1;
        }
    }

    /*
     * Returns the dimension predictor
     */
    public int dimensionPredictor() {
        return dp;
    }

    /*
     * Returns the value predictor
     */
    public double valuePredictor() {
        return vp;
    }

    /*
     * Returns the sign predictor
     */
    public int signPredictor() {
        return sp;
    }

    /**
     * Unit tests the WeakLearner data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        double[][] input = {
                { 1, 6 },
                { 2, 6 },
                { 3, 2 },
                { 4, 1 },
                { 4, 4 },
                { 5, 5 },
                { 6, 3 },
                { 6, 7 }
        };

        double[] weights = { 1, 1, 1, 1, 1, 1, 1, 1 };
        int[] labels = { 0, 1, 1, 0, 1, 0, 1, 0 };

        WeakLearner wl = new WeakLearner(input, weights, labels);

        StdOut.println("dp: " + wl.dimensionPredictor());
        StdOut.println("vp: " + wl.valuePredictor());
        StdOut.println("sp: " + wl.signPredictor());

        StdOut.println("Predict (1, 6): " + wl.predict(new double[] { 1, 6 }));
    }
}
