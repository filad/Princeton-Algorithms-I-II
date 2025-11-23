import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

/**
 * PointSET, a mutable data type that represents a set of points in the unit square.
 * This is a brute-force implementation.
 *
 * @author Adam Filkor
 */

public class PointSET {

    private static int xmin = 0, xmax = 1, ymin = 0, ymax = 1; // dimension of the board
    private SET<Point2D> set;

    // construct an empty set of points
    public PointSET() {
        set = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return set.isEmpty();
    }

    // number of points in the set
    public int size() {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("insert() null argument");
        if (p.x() < xmin || p.x() > xmax || p.y() < ymin || p.y() > ymax)
            throw new IndexOutOfBoundsException();
        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("contains() null argument");
        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : set) {
            StdDraw.point(p.x(), p.y());
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("range() null argument");
        Queue<Point2D> q = new Queue<Point2D>();

        for (Point2D p : set) {
            if (p.x() >= rect.xmin()
                    && p.x() <= rect.xmax()
                    && p.y() <= rect.ymax()
                    && p.y() >= rect.ymin()) {
                q.enqueue(p);
            }
        }
        return q;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("nearest() null argument");
        if (set.isEmpty()) return null;

        Point2D nearest = null;
        /*
         * Whenever you need to compare two Euclidean distances, it is often more efficient
         * to compare the squares of the two distances to avoid the
         * expensive operation of taking square roots.
         */
        double distSqrt;
        double champion = Math.sqrt(xmax - xmin + ymax - ymin); // sqrt(2) in case of unit square.
        for (Point2D pt : set) {
            if (nearest == null) {
                nearest = pt;
            }
            distSqrt = Math.pow(p.x() - pt.x(), 2) + Math.pow(p.y() - pt.y(), 2);
            if (distSqrt < champion) {
                champion = distSqrt;
                nearest = pt;
            }
        }
        return nearest;
    }

    public static void main(String[] args) {

    }
}
