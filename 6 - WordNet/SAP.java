import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Shortest ancestral path class.
 *
 * @author Adam Filkor
 */

public class SAP {

    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("SAP constructor null argument");
        this.G = new Digraph(G); // deep copy, we want immutable
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {

        // use hasPath on every vertex
        // if both has path to that particular vertex, calculate the shortest path using distTo()

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int minDistance = Integer.MAX_VALUE; // initialize "infinity"
        int distance;

        for (int i = 0; i < this.G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                distance = bfsV.distTo(i) + bfsW.distTo(i);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }

        if (minDistance < Integer.MAX_VALUE) {
            return minDistance;
        }
        else {
            return -1;
        }
    }

    // a common ancestor of v and w that participates in the shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        // almost same as length()
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int minDistance = Integer.MAX_VALUE; // initialize "infinity"
        int distance;
        int ancestor = -1; // init to -1

        for (int i = 0; i < this.G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                distance = bfsV.distTo(i) + bfsW.distTo(i);
                // StdOut.println(i + " d: " + distance + " minDist: " + minDistance);
                if (distance < minDistance) {
                    minDistance = distance;
                    ancestor = i;
                }
            }
        }
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null) throw new IllegalArgumentException("length null argument");
        if (w == null) throw new IllegalArgumentException("length null argument");
        int vertexCount = 0, vertexCountW = 0;
        for (Integer vx : v) {
            vertexCount++;
            if (vx == null) throw new IllegalArgumentException("vertex is null");
        }
        if (vertexCount == 0) return -1;
        for (Integer vx : w) {
            vertexCountW++;
            if (vx == null) throw new IllegalArgumentException("vertex is null");
        }
        if (vertexCountW == 0) return -1;

        /*
            same as in length(), but using the constructor
            in BreadthFirstDirectedPaths that takes an iterable of sources
         */

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int minDistance = Integer.MAX_VALUE; // initialize "infinity"
        int distance;

        for (int i = 0; i < this.G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                distance = bfsV.distTo(i) + bfsW.distTo(i);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }

        if (minDistance < Integer.MAX_VALUE) {
            return minDistance;
        }
        else {
            return -1;
        }
    }

    // a common ancestor that participates in the shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null) throw new IllegalArgumentException("length null argument");
        if (w == null) throw new IllegalArgumentException("length null argument");
        int vertexCount = 0, vertexCountW = 0;
        for (Integer vx : v) {
            vertexCount++;
            if (vx == null) throw new IllegalArgumentException("vertex is null");
        }
        if (vertexCount == 0) return -1;
        for (Integer vx : w) {
            vertexCountW++;
            if (vx == null) throw new IllegalArgumentException("vertex is null");
        }
        if (vertexCountW == 0) return -1;


        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int minDistance = Integer.MAX_VALUE; // initialize "infinity"
        int distance;
        int ancestor = -1; // init to -1

        for (int i = 0; i < this.G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                distance = bfsV.distTo(i) + bfsW.distTo(i);

                if (distance < minDistance) {
                    minDistance = distance;
                    ancestor = i;
                }
            }
        }
        return ancestor;
    }

    public static void main(String[] args) {

        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }

    }
}
