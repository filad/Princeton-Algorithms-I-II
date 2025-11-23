import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

/**
 * Uses a 2d-tree to implement the same API as PointSET.
 * A 2d-tree is a generalization of a BST to two-dimensional keys.
 * <p>
 * Now we use a BST (binary search tree) with points in the nodes,
 * using the x- and y-coordinates of the points as keys in strictly alternating sequence.
 *
 * @author Adam Filkor
 */
public class KdTree {

    private Node root = null; // main root
    private int size = 0;
    private boolean contains = false;

    /*
     * @var rect - each node corresponds to an axis-aligned rectangle in the unit square,
     * which encloses all the points in its subtree.
     * The root corresponds to the unit square; the left and right children of the root corresponds
     * to the two rectangles split by the x-coordinate of the point at the root; and so forth.
     *
     * Unlike the Node class for BST, this Node class is static, because we don't want to
     * reference any data from the parent class.
     */
    private static class Node {
        Point2D p;
        Node left; // the left/bottom subtree
        Node right; // the right/top subtree
        RectHV rect = null; // the axis-aligned rectangle corresponding to this node
    }

    // construct an empty set of points
    public KdTree() {
    }

    // is the set empty?
    public boolean isEmpty() {
        return (root == null);
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("insert() null argument");
        int xmin = 0, xmax = 1, ymin = 0, ymax = 1; // dimensions of the board
        if (p.x() < xmin || p.x() > xmax || p.y() < ymin || p.y() > ymax)
            throw new IndexOutOfBoundsException();

        if (root == null) {
            root = new Node();
            root.p = p;
            root.left = null;
            root.right = null;
            root.rect = new RectHV(xmin, ymin, xmax, ymax);
            size++;
            return;
        }

        double px = p.x(), py = p.y(); // cache, decrease number of calls to methods in Point2D
        char orientation = 0; // orientation 0 -> compare by x; orientation 1 -> compare by y
        insert(root, p, px, py, orientation);
    }

    // recursive helper
    private void insert(Node parent, Point2D p, double px, double py, int orientation) {
        if (parent.p.equals(p))
            return; // if equal, just return

        /*
         at even orientations we compare by x, at odd orientation we compare by y coordinates.
         keep track which orientation we are on, what we compared last, with the -orientation- variable
         it's easy, cause we always start form the top root node
        */
        if (orientation == 0) {
            if (px < parent.p.x()) {
                // left
                if (parent.left == null) {
                    Node node = new Node();
                    node.p = p;
                    node.left = null;
                    node.right = null;
                    node.rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.p.x(), parent.rect.ymax());

                    size++;
                    parent.left = node;
                    return;
                }
                insert(parent.left, p, px, py, 1);
            }
            else {
                // right
                if (parent.right == null) {
                    Node node = new Node();
                    node.p = p;
                    node.left = null;
                    node.right = null;
                    node.rect = new RectHV(parent.p.x(), parent.rect.ymin(), parent.rect.xmax(), parent.rect.ymax());

                    size++;
                    parent.right = node;
                    return;
                }
                insert(parent.right, p, px, py, 1);
            }
        }
        if (orientation == 1) {
            // if orientation is 1, we compare by y
            if (py < parent.p.y()) {
                // left
                if (parent.left == null) {
                    Node node = new Node();
                    node.p = p;
                    node.left = null;
                    node.right = null;
                    node.rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.rect.xmax(), parent.p.y());

                    size++;
                    parent.left = node;
                    return;
                }
                insert(parent.left, p, px, py, 0);
            }
            else {
                // right
                if (parent.right == null) {
                    Node node = new Node();
                    node.p = p;
                    node.left = null;
                    node.right = null;
                    node.rect = new RectHV(parent.rect.xmin(), parent.p.y(), parent.rect.xmax(), parent.rect.ymax());

                    size++;
                    parent.right = node;
                    return;
                }
                insert(parent.right, p, px, py, 0);
            }
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("contains() null argument");
        if (size() == 0) {
            return false;
        }

        Node parent = root; // parent node
        char orientation = 0; // orientation 0 -> compare by x; orientation 1 -> compare by y
        contains = false;

        double px = p.x(), py = p.y(); // cache, decrease number of calls to methods in Point2D
        contains(parent, px, py, orientation);
        return contains;
    }

    // recursive helper
    private void contains(Node parent, double px, double py, char orientation) {
        if (parent == null) return;

        double parentX = parent.p.x();
        double parentY = parent.p.y();
        if (px == parentX && py == parentY) {
            contains = true;
            return;
        }

        // search for point
        if (orientation == 0) {
            orientation = 1; // passing the next orientation
            if (px < parentX) {
                contains(parent.left, px, py, orientation);
            }
            else {
                contains(parent.right, px, py, orientation);
            }
            return;
        }
        if (orientation == 1) {
            orientation = 0;
            if (py < parentY) {
                contains(parent.left, px, py, orientation);
            }
            else {
                contains(parent.right, px, py, orientation);
            }
        }
    }

    // draw all points to standard draw
    public void draw() {
        char orientation = 'v';
        Node parent = root; // current node
        draw(parent, orientation);
    }

    // recursive search for draw.
    private void draw(Node parent, char orientation) {
        if (parent == null) return;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(parent.p.x(), parent.p.y());

        if (orientation == 'v') {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            StdDraw.line(parent.p.x(), parent.rect.ymin(), parent.p.x(), parent.rect.ymax());
            draw(parent.left, 'h');
            draw(parent.right, 'h');
        }
        if (orientation == 'h') {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            StdDraw.line(parent.rect.xmin(), parent.p.y(), parent.rect.xmax(), parent.p.y());
            draw(parent.left, 'v');
            draw(parent.right, 'v');
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("range() null argument");
        Queue<Point2D> queue = new Queue<Point2D>();
        range(root, queue, rect);
        return queue;
    }

    private void range(Node parent, Queue<Point2D> queue, RectHV rect) {
        if (parent == null) return;
        if (!parent.rect.intersects(rect)) return; // pruning
        if (rect.contains(parent.p)) queue.enqueue(parent.p);
        range(parent.left, queue, rect);
        range(parent.right, queue, rect);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("nearest() null argument");
        if (size() == 0) {
            return null;
        }

        Point2D champion = root.p;
        champion = nearest(root, p, champion, 0);
        return champion;
    }

    private Point2D nearest(Node parent, Point2D p, Point2D champion, int orientation) {
        if (parent == null) return champion;

        double championDist = p.distanceSquaredTo(champion);
        double parentDist = p.distanceSquaredTo(parent.p);
        if (parentDist == 0) return parent.p; // query point equals to the parent node, stop searching, found the 'nearest' point.
        if (parentDist < championDist) {
            champion = parent.p;
        }

        /*
            Goes all the way in first, by comparing x y coordinates of the query point, just like in contains().
            Finds the closest point it can. Now it comes back out from the recursive depths.
            On the way out it checks, at every point, whether the other half rectangle is closer than
            the closest point so far found. It that rectangle is closer, it goes into that subtree, too.
            Test input 10.txt with query point (0.95, 0.39) is a good example, you can think through.
        */

        if (orientation == 0) {
            if (p.x() < parent.p.x()) {
                champion = nearest(parent.left, p, champion, 1);
                if (parent.right != null && p.distanceSquaredTo(champion) >= parent.right.rect.distanceSquaredTo(p)) {
                    champion = nearest(parent.right, p, champion, 1);
                }

            }
            else {
                champion = nearest(parent.right, p, champion, 1);
                if (parent.left != null && p.distanceSquaredTo(champion) >= parent.left.rect.distanceSquaredTo(p)) {
                    champion = nearest(parent.left, p, champion, 1);
                }
            }
        }

        if (orientation == 1) {
            if (p.y() < parent.p.y()) {
                champion = nearest(parent.left, p, champion, 0);
                if (parent.right != null && p.distanceSquaredTo(champion) >= parent.right.rect.distanceSquaredTo(p)) {
                    champion = nearest(parent.right, p, champion, 0);
                }

            }
            else {
                champion = nearest(parent.right, p, champion, 0);
                if (parent.left != null && p.distanceSquaredTo(champion) >= parent.left.rect.distanceSquaredTo(p)) {
                    champion = nearest(parent.left, p, champion, 0);
                }
            }
        }
        return champion;
    }


    public static void main(String[] args) {
        // initialize the data structure with points from a file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        Point2D p1 = new Point2D(0.95, 0.39);
        StdOut.println("Contains? " + (kdtree.contains(p1) ? "Yes" : "No"));
        StdDraw.clear();
        kdtree.draw();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        p1.draw();
        StdOut.println("Nearest: " + kdtree.nearest(p1));
    }
}
