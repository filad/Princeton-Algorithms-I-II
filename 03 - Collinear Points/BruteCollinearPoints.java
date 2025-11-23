import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

/**
 * Brute-force method.
 * Examines 4 points at a time and checks whether they all lie on the same line segment,
 * returning all such line segments.
 *
 * @author Adam Filkor
 */

public class BruteCollinearPoints {

    private static final int INIT_CAPACITY = 8; // line segments array init capacity

    private int numSegments = 0;
    private LineSegment[] lineSegments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException("points are null");
        /*
        IllegalArgumentException if the argument to the constructor is null,
        if any point in the array is null, or if the argument to the constructor
        contains a repeated point.
        */

        if (points[0] == null) throw new IllegalArgumentException("One of the points is null");

        int n = points.length;
        lineSegments = new LineSegment[INIT_CAPACITY];
        int temp; // for checking duplicate points

        for (int i = 0; i < n; i++) {

            temp = i + 1;

            for (int j = 0; j < n; j++) {
                if (points[j] == null) throw new IllegalArgumentException("One of the points is null");
                if (temp < points.length)
                    if (points[i].compareTo(points[temp]) == 0)
                        throw new IllegalArgumentException("Duplicate points");
                temp++;

                for (int k = 0; k < n; k++) {
                    // Second, you don't need to consider whether 4 points are collinear if you already know that the first 3 are not collinear;
                    // this can save you a factor of N on typical inputs.
                    if (!(points[i].slopeTo(points[j]) == points[i].slopeTo(points[k])))
                        continue;
                    for (int l = 0; l < n; l++) {
                        if (points[l] == null) throw new IllegalArgumentException("One of the points is null");

                        if (
                                points[i].slopeTo(points[j]) == Double.NEGATIVE_INFINITY ||
                                        points[i].slopeTo(points[k]) == Double.NEGATIVE_INFINITY ||
                                        points[j].slopeTo(points[k]) == Double.NEGATIVE_INFINITY ||
                                        points[j].slopeTo(points[l]) == Double.NEGATIVE_INFINITY ||
                                        points[k].slopeTo(points[l]) == Double.NEGATIVE_INFINITY
                        ) continue; // don't count if it's the same point

                        if (
                                points[i].slopeTo(points[j]) == points[i].slopeTo(points[k]) &&
                                        points[i].slopeTo(points[j]) == points[i].slopeTo(points[l])
                        ) {
                            // only count if its ascending order preventing double counting p->q and q->p etc. lines
                            // it still double counts things, see FastCollinearPoints.java there it's better
                            if (
                                    points[i].compareTo(points[j]) < 0 &&
                                            points[j].compareTo(points[k]) < 0 &&
                                            points[k].compareTo(points[l]) < 0
                            ) {
                                // Using dynamic arrays (Resizing arrays). By multiplying the size by 2 - and copying at those times - whenever length == size, the amortized complexity is O(n)
                                if (numSegments == lineSegments.length)
                                    resize(2 * lineSegments.length);

                                lineSegments[numSegments++] = new LineSegment(points[i], points[l]);
                            }
                        }
                    }
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return numSegments;
    }

    // the line segments
    public LineSegment[] segments() {
        // create a new array, with proper size now (numSegments), and copy to prevent NullPointerException
        LineSegment[] ls = new LineSegment[numSegments];
        for (int i = 0; i < numSegments; i++) {
            ls[i] = lineSegments[i];
        }
        return ls;
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        assert capacity >= numSegments;

        // textbook implementation
        LineSegment[] copy = new LineSegment[capacity];
        for (int i = 0; i < numSegments; i++) {
            copy[i] = lineSegments[i];
        }
        lineSegments = copy;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        int segments = collinear.numberOfSegments();
        StdOut.println("Line segments: " + segments);

        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdOut.println("Line segments: " + segments);
        StdDraw.show();
    }
}
