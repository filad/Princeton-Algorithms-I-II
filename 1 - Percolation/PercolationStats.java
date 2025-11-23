import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * Write a program to estimate the value of the percolation threshold via Monte Carlo simulation.
 *
 * @author Adam Filkor
 */

public class PercolationStats {

    private int T; // trials
    private double mean;
    private double stddev = Double.NaN;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("n is less or equal to 0");
        }

        this.T = trials;
        int randX, randY;
        int opened = 0;
        int[][] openedSites = new int[n + 1][n + 1]; // not ideal, but whatever

        double[] tresholds = new double[T];

        // for trials
        for (int i = 0; i < T; i++) {
            Percolation perc = new Percolation(n);

            while (!perc.percolates()) {
                randX = StdRandom.uniformInt(n) + 1;
                randY = StdRandom.uniformInt(n) + 1;

                if (openedSites[randX][randY] == 0) {
                    perc.open(randX, randY);
                    openedSites[randX][randY] = 1;
                    opened++;
                }
                // StdDraw.pause(400);
            }
            tresholds[i] = opened / ((double) n * n);
            openedSites = new int[n + 1][n + 1];  // HAD  TO RESET
            opened = 0;
        }

        this.mean = StdStats.mean(tresholds);
        if (T != 1) this.stddev = StdStats.stddev(tresholds);
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean - 1.96 * stddev() / Math.sqrt(T);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean + 1.96 * stddev() / Math.sqrt(T);
    }

    public static void main(String[] args) {
        PercolationStats PS = new PercolationStats(Integer.parseInt(args[0]),
                                                   Integer.parseInt(args[1]));

        System.out.println("mean                     = " + PS.mean());
        System.out.println("stddev                   = " + PS.stddev());
        System.out.println(
                "95% confidence interval  = [" + PS.confidenceLo() + ", " + PS.confidenceHi()
                        + "]");
    }
}
