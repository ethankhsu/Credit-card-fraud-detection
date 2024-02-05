import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;

public class DataSet {
    public int n, m;
    public Point2D[] locations;
    public double[][] input;
    public int[] labels;

    public DataSet(String filename) {
        In datafile = new In(filename);

        n = datafile.readInt();
        m = datafile.readInt();

        locations = new Point2D[m];
        for (int i = 0; i < m; i++) {
            double x = datafile.readDouble();
            double y = datafile.readDouble();
            locations[i] = new Point2D(x, y);
        }

        labels = new int[n];
        for (int i = 0; i < n; i++) {
            labels[i] = datafile.readInt();
        }

        input = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                input[i][j] = datafile.readDouble();
            }
        }
    }

    public static void main(String[] args) {

    }
}
