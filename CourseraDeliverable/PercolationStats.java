import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final double trialsMean;
    private final double trialsStddev;
    private final double trialsConfidenceLo;
    private final double trialsConfidenceHi;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n < 1 || trials < 1) {
            throw new IllegalArgumentException("The sides number n and the trials number must be greater than 0");
        }
        
        Percolation percolation;
        double[] trialsPercolationThreshold = new double[trials];

        for (int i = 0 ; i < trials ; i++) {
            percolation = new Percolation(n);

            while (!percolation.percolates()) {
                // Random number between 1 and n inclusive
                int row = StdRandom.uniform(1, n+1);
                // Random number between 1 and n inclusive
                int col = StdRandom.uniform(1, n+1);

                percolation.open(row, col);
            }

            trialsPercolationThreshold[i] = (double) percolation.numberOfOpenSites()/(n*n);
        }

        double confidenceConst = 1.96;
        trialsMean = StdStats.mean(trialsPercolationThreshold);
        trialsStddev = StdStats.stddev(trialsPercolationThreshold);
        trialsConfidenceLo = trialsMean - (confidenceConst*trialsStddev)/Math.sqrt(trialsPercolationThreshold.length);
        trialsConfidenceHi = trialsMean + (confidenceConst*trialsStddev)/Math.sqrt(trialsPercolationThreshold.length);
    }

    // sample mean of percolation threshold
    public double mean() {
        return this.trialsMean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return this.trialsStddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return this.trialsConfidenceLo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return this.trialsConfidenceHi;
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats percolationStats = new PercolationStats(n, t);

        System.out.println("mean                    = " + percolationStats.mean());
        System.out.println("stddev                  = " + percolationStats.stddev());
        System.out.println("95% confidence interval = [" + percolationStats.confidenceLo() + ", " + percolationStats.confidenceHi() + "]");
    }
}