import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * Model a percolation system using an n-by-n grid of sites.
 * <p>
 * Initially done with virtual top and bottom. Now removed them, and using
 * a byte array instead. The individual bits in the bytes store the properties
 * of the corresponding sites as flags like OPEN or FULL.
 * It saves some memory, and the problem of 'backwash' is also solved.
 * Using only one WeightedQuickUnion object.
 * Using multiple roots and continuously merging them as we open more sites.
 *
 * @author Adam Filkor
 */
public class Percolation {

    private static final byte OPEN = 0b01;
    private static final byte FULL = 0b100; // it's connected to top
    private static final byte CONNECTED_TO_BOTTOM = 0b010;

    private int n; // n x n grid size, we will use a 2D to 1D map.
    private WeightedQuickUnionUF wQU;
    private int numOpenedSites = 0;
    private boolean perc = false;  // percolation is false initially
    private byte[] sites; // using byte array, setting bits on and off

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n is less or equal to 0");
        }
        this.n = n;
        sites = new byte[n * n];
        this.wQU = new WeightedQuickUnionUF(n * n);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n)
            throw new IllegalArgumentException("arguments out of bound");

        if (isOpen(row, col)) {
            return;
        }
        sites[xyTo1D(row, col)] |= OPEN;
        numOpenedSites++;

        if (row == 1) {
            sites[xyTo1D(row, col)] |= FULL; // if it's the first row, it's full
        }
        if (row == n) {
            sites[xyTo1D(row, col)] |= CONNECTED_TO_BOTTOM;
        }

        int p, q;
        p = xyTo1D(row, col);

        if (inBounds(row + 1, col) && isOpen(row + 1, col)) {
            q = xyTo1D(row + 1, col);
            myUnion(p, q);
        }
        if (inBounds(row - 1, col) && isOpen(row - 1, col)) {
            q = xyTo1D(row - 1, col);
            myUnion(p, q);
        }
        if (inBounds(row, col + 1) && isOpen(row, col + 1)) {
            q = xyTo1D(row, col + 1);
            myUnion(p, q);
        }
        if (inBounds(row, col - 1) && isOpen(row, col - 1)) {
            q = xyTo1D(row, col - 1);
            myUnion(p, q);
        }
        // now if the new root has all the flags, it percolates
        if (sites[wQU.find(p)] == (OPEN | FULL | CONNECTED_TO_BOTTOM)) {
            perc = true;
        }
    }

    private void myUnion(int p, int q) {
        int rootP, rootQ;
        rootP = wQU.find(p);
        rootQ = wQU.find(q);
        wQU.union(rootP, rootQ); // union the two trees
        /*
            Root for the new union can be anything here: rootP or rootQ.
            After creating a union on two trees, again call find()
            on one (no matter which) to find the new root:
        */
        int rootFinal = wQU.find(rootP);
        // assign the flags to the new root
        sites[rootFinal] |= sites[rootP];
        sites[rootFinal] |= sites[rootQ];
    }

    // we use a 2D to 1D map
    private int xyTo1D(int row, int col) {
        // (1,1) -> 0
        // (2,1) -> N
        // (3,3) -> 2N + 2 -> in case of 3x3 = 8
        return n * (row - 1) + (col - 1);
    }

    // row, col values inside boundaries?
    private boolean inBounds(int row, int col) {
        return row > 0 && col > 0 && row <= n && col <= n;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n)
            throw new IllegalArgumentException("arguments out of bound");
        return (sites[xyTo1D(row, col)] & OPEN) != 0;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n)
            throw new IllegalArgumentException("arguments out of bound");
        // find the root of the tree p belongs to, and check whether it has the 'FULL' flag
        // if yes, this point p is FULL, too
        int p = xyTo1D(row, col);
        int rootP = wQU.find(p);
        return (sites[rootP] & FULL) != 0;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numOpenedSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return perc;
    }

    public static void main(String[] args) {

    }
}