import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/**
 * Given a set of n distinct points in the plane, find every (maximal) line segment
 * that connects a subset of 4 or more of the points.
 *
 * @author Adam Filkor
 */

public class FastCollinearPoints {

    private static final int INIT_CAPACITY = 8; // line segments array init capacity

    private int numSegments = 0;
    private LineSegment[] lineSegments;


    public FastCollinearPoints(Point[] pts) {
        if (pts == null) throw new IllegalArgumentException("points are null");
        Point[] points = new Point[pts.length];
        for (int i = 0; i < points.length; i++) {
            points[i] = pts[i];
        }

        /*

        IllegalArgumentException if the argument to the constructor is null,
        if any point in the array is null, or if the argument to the constructor
        contains a repeated point.

        */

        lineSegments = new LineSegment[INIT_CAPACITY];
        Point[] pointsCopy = new Point[points.length]; // create a copy, we want a copy for the original array, the original order.
        Stack<Double> visitedSlopes = new Stack<>();
        Stack<Point> visitedStartPoints = new Stack<>();
        Stack<Point> visitedEndPoints = new Stack<>();

        for (int i = 0; i < points.length; i++) {

            for (int j = i + 1; j < points.length; j++) {
                if (i == 0 && points[j] == null || points[0] == null) throw new IllegalArgumentException("One of the points is null");
                if (points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException("Duplicate points");
            }
            pointsCopy[i] = points[i];
        }

        for (int z = 0; z < pointsCopy.length; z++) {

            Point p0 = pointsCopy[z];
            Arrays.sort(points, p0.slopeOrder());

            int k = 1;

            // every round we compare all other points to p0
            for (int i = 0; i < points.length; i++) {

                if (points[i] == p0)
                    continue; // don't count if it's the same point

                double slope = p0.slopeTo(points[i]);

                if (i + 1 < points.length && p0.slopeTo(points[i]) == p0.slopeTo(points[i + 1])) {
                    // checking and counting adjacent points - in the sorted  array - where slopes are equal
                    k++;
                }
                else {
                    int maxPoints = k + 1; // max points on the given line, + 1 cause the p0
                    if (maxPoints >= 4) {

                        Point[] linePoints = new Point[maxPoints];
                        linePoints[0] = p0;
                        for (int j = 1; j < maxPoints; j++) {
                            linePoints[j] = points[i + j - k];
                        }

                        /*
                            Sort the array which contains the line points, by natural order,
                            so it's first element is always the same, given we are on the same line.
                        */
                        Arrays.sort(linePoints);

                        // start and endpoints of the given maximal line.
                        Point startPoint = linePoints[0];
                        Point endPoint = linePoints[maxPoints - 1];

                        boolean slopeVisited = false;
                        boolean endPointVisited = false;
                        boolean startPointVisited = false;
                        for (double slp : visitedSlopes) {
                            if (slp == slope) slopeVisited = true;
                        }
                        for (Point p : visitedStartPoints) {
                            if (p == startPoint) startPointVisited = true;
                        }
                        for (Point p : visitedEndPoints) {
                            if (p == endPoint) endPointVisited = true;
                        }

                        /*
                            Cutting down on the unnecessary lines
                            The combination of the slope, start, endpoints is a pretty unique value.
                            Break out if we've been visited them already
                            // TODO missing lines on rare cases, better conditions are needed
                        */
                        if (slopeVisited && startPointVisited && endPointVisited) {
                            break;
                        }
                        if (!slopeVisited)
                            visitedSlopes.push(slope);
                        if (!startPointVisited)
                            visitedStartPoints.push(linePoints[0]);
                        if (!endPointVisited)
                            visitedEndPoints.push(linePoints[maxPoints - 1]);

                        // Resizing arrays. By multiplying the size by 2 - and copying
                        // at those times - whenever length == size, the amortized complexity is O(n)
                        if (numSegments == lineSegments.length)
                            resize(2 * lineSegments.length);

                        lineSegments[numSegments++] = new LineSegment(linePoints[0], linePoints[maxPoints - 1]);
                    }

                    k = 1; // reset
                }
            }
        }
    }

    public int numberOfSegments() {
        return numSegments;
    }

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

        FastCollinearPoints collinear = new FastCollinearPoints(points);
        int segments = collinear.numberOfSegments();

        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdOut.println("Line segments: " + segments);
        StdDraw.show();
    }
}
