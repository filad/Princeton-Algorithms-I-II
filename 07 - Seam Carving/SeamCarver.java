import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implements "seam-carving", a content-aware image resizing technique.
 * <p>
 * The technique was not discovered until 2007. Now, it's a core feature
 * in Adobe Photoshop and other computer graphics applications.
 *
 * @author Adam Filkor
 */
public class SeamCarver {
    private int[][] pic; // stores the RGB values of the picture
    private final int virtualSource;
    private final int virtualSink;
    private final int maxVertices;
    private boolean isTransposed = false;
    private boolean calledFromHorizontalFn = false;
    private boolean calledFromTransposeFn = false;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Constructor argument is null");
        // create a new int[][] matrix which stores the raw RGB values (32 bit),
        // manipulating the Picture object itself would be too slow
        pic = new int[picture.height()][picture.width()];
        for (int j = 0; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                pic[j][i] = picture.getRGB(i, j);
            }
        }
        maxVertices = width() * height() + 2; // + 2 cause virtual source, and sink
        virtualSource = 0;
        virtualSink = maxVertices - 1;
    }

    private Queue<Integer> topologicalOrder() {
        // Don't need a dfs recursion to compute topological order, because the underlying DAG has a special structure.
        // Iterate through diagonals, for example at 3x4 dimensions: 1 - 2 - 4 - 3 - 5 - 7 - 6 - 8 - 10 - 9 - 11 - 12
        // The rule remains the same: Sum of element indexes from the same diagonal is constant; i + j = k constant
        Queue<Integer> reversePost = new Queue<Integer>();
        reversePost.enqueue(virtualSource);
        for (int k = 0; k <= width() + height() - 2; k++) {
            for (int i = k; i >= 0; i--) {
                int j = k - i;
                if (j < height() && i < width()) {
                    reversePost.enqueue(coordToVertex(i, j));
                }
            }
        }
        reversePost.enqueue(virtualSink);
        return reversePost;
    }

    // returns adjacent vertices for vertical seam calculation, considers boundary conditions
    private Iterable<Integer> adjVertical(int v) {
        int column = vertexToCoord(v)[0];
        int row = vertexToCoord(v)[1];
        ArrayList<Integer> arr = new ArrayList<Integer>();
        boolean verticalCondition = (row < height() - 1);

        // connect vertexes to the virtual source and virtual sink
        if (v == virtualSource) {
            for (int i = 0; i < width(); i++) {
                arr.add(coordToVertex(i, 0));
            }
            return arr;
        }
        if (row == height() - 1) {
            arr.add(virtualSink);
            return arr;
        }

        if (column > 0 && verticalCondition)
            arr.add(coordToVertex(column - 1, row + 1));
        if (verticalCondition)
            arr.add(coordToVertex(column, row + 1));
        if ((column + 1 < width()) && verticalCondition)
            arr.add(coordToVertex(column + 1, row + 1));
        return arr;
    }

    // v -> w
    private void relax(int v, int w, double[] distTo, int[] edgeTo) {
        int col = vertexToCoord(w)[0];
        int row = vertexToCoord(w)[1];
        double weight;

        if (v == virtualSource) {
            distTo[w] = 1000;
            edgeTo[w] = v;
            return;
        }
        if (w == virtualSink)
            weight = 0;
        else
            weight = energy(col, row);
        // relaxing
        if (distTo[w] > distTo[v] + weight) {
            distTo[w] = distTo[v] + weight;
            edgeTo[w] = v;
        }
    }

    // params column x and row y. return the vertex name, start from 1...maxPixels
    private int coordToVertex(int x, int y) {
        return y * width() + x + 1;
    }

    // returns [col, row]
    private int[] vertexToCoord(int v) {
        int[] coord = new int[2];
        coord[0] = (v - 1) % width();
        coord[1] = (v - 1) / width();
        return coord;
    }

    // current picture
    public Picture picture() {
        if (isTransposed) {
            // transpose back
            this.pic = transposeMatrix(this.pic);
        }
        Picture p = new Picture(width(), height());
        // j : row, i: col
        for (int j = 0; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                p.setRGB(i, j, pic[j][i]);
            }
        }
        return p;
    }

    // width of current picture
    public int width() {
        if (isTransposed && !calledFromHorizontalFn && !calledFromTransposeFn) {
            // transpose back, if not called from removeHorizontalSeam
            this.pic = transposeMatrix(this.pic);
        }

        return pic[0].length;
    }

    // height of current picture
    public int height() {
        if (isTransposed && !calledFromHorizontalFn && !calledFromTransposeFn) {
            // transpose back, if not called from removeHorizontalSeam
            this.pic = transposeMatrix(this.pic);
        }
        return pic.length;
    }

    // energy of the pixel at column x and row y
    public double energy(int x, int y) {
        if (x > width() - 1 || x < 0)
            throw new IllegalArgumentException("x is outside its prescribed range");
        if (y > height() - 1 || y < 0)
            throw new IllegalArgumentException("y is outside its prescribed range");

        if (isTransposed && !calledFromHorizontalFn) {
            // transpose back, if not called from removeHorizontalSeam
            this.pic = transposeMatrix(this.pic);
        }

        if (x == 0 || x == width() - 1) return 1000;
        if (y == 0 || y == height() - 1) return 1000;

        int leftPixelRGB = pic[y][x - 1];
        int redLeftPixel = (leftPixelRGB >> 16) & 0xFF;
        int greenLeftPixel = (leftPixelRGB >> 8) & 0xFF;
        int blueLeftPixel = (leftPixelRGB) & 0xFF;

        int rightPixelRGB = pic[y][x + 1];
        int redRightPixel = (rightPixelRGB >> 16) & 0xFF;
        int greenRightPixel = (rightPixelRGB >> 8) & 0xFF;
        int blueRightPixel = (rightPixelRGB) & 0xFF;

        int topPixelRGB = pic[y - 1][x];
        int redTopPixel = (topPixelRGB >> 16) & 0xFF;
        int greenTopPixel = (topPixelRGB >> 8) & 0xFF;
        int blueTopPixel = (topPixelRGB) & 0xFF;

        int bottomPixelRGB = pic[y + 1][x];
        int redBottomPixel = (bottomPixelRGB >> 16) & 0xFF;
        int greenBottomPixel = (bottomPixelRGB >> 8) & 0xFF;
        int blueBottomPixel = (bottomPixelRGB) & 0xFF;

        // calculate "central differences"
        int rx = redRightPixel - redLeftPixel;
        int gx = greenRightPixel - greenLeftPixel;
        int bx = blueRightPixel - blueLeftPixel;

        int ry = redBottomPixel - redTopPixel;
        int gy = greenBottomPixel - greenTopPixel;
        int by = blueBottomPixel - blueTopPixel;

        // square of the x-gradient Δ2x(x,y)=Rx(x,y)2+Gx(x,y)2+Bx(x,y)2
        double deltaX2 = Math.pow(rx, 2) + Math.pow(gx, 2) + Math.pow(bx, 2);
        double deltaY2 = Math.pow(ry, 2) + Math.pow(gy, 2) + Math.pow(by, 2);

        double e = Math.sqrt(deltaX2 + deltaY2);
        return e;
    }

    private int[][] transposeMatrix(int[][] p) {
        calledFromTransposeFn = true;
        int[][] transposed = new int[width()][height()];
        for (int j = 0; j < width(); j++) {
            for (int i = 0; i < height(); i++) {
                transposed[j][i] = p[i][j];
            }
        }
        isTransposed = !isTransposed;
        calledFromTransposeFn = false;
        return transposed;
    }

    // sequence of indices for horizontal seam
    // returns an array of length width such that entry x is the row number of the pixel to be removed from column x of the image.
    public int[] findHorizontalSeam() {
        // to find horizontalSeam, transpose the image, call findVerticalSeam(), and transpose it back.
        calledFromHorizontalFn = true;
        // transpose
        if (!isTransposed) {
            this.pic = transposeMatrix(this.pic);
        }
        // find vertical seam on transposed picture
        int[] seam = findVerticalSeam();
        calledFromHorizontalFn = false;

        return seam;
    }

    // sequence of indices for vertical seam
    // returns an array of length height such that entry y is the column number of the pixel to be removed from row y of the image.
    public int[] findVerticalSeam() {
        // don't transpose the Picture until you need to do so
        // if we perform a lot of consecutive horizontal seam removals, it's going to be faster this way
        if (isTransposed && !calledFromHorizontalFn) {
            // transpose back, if not called from findHorizontalSeam
            this.pic = transposeMatrix(this.pic);
        }

        int[] seam = new int[height()];
        Stack<Integer> edgeStack = new Stack<Integer>();

        // init distTo, edgeTo arrays
        double[] distTo = new double[maxVertices]; // distTo[v] = distance  of shortest s->v path
        int[] edgeTo = new int[maxVertices]; // edgeTo[v] = last edge on shortest s->v path
        edgeTo[virtualSource] = -1;

        for (int i = 0; i < maxVertices; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }
        distTo[virtualSource] = 0.0;

        Queue<Integer> reversePost = topologicalOrder();
        for (int v : reversePost) {
            for (int w : adjVertical(v)) {
                relax(v, w, distTo, edgeTo);
            }
        }

        // going backwards in edgeTo array, starting from virtualSink
        for (int i = edgeTo[virtualSink]; edgeTo[i] != -1; i = edgeTo[i]) {
            edgeStack.push(i);
        }
        for (int v : edgeStack) {
            int column = vertexToCoord(v)[0];
            int row = vertexToCoord(v)[1];
            seam[row] = column;
        }
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("argument is null");
        calledFromHorizontalFn = true;

        if (isTransposed) {
            if (width() <= 1) throw new IllegalArgumentException("height is too small: " + width());
        }
        else {
            if (height() <= 1)
                throw new IllegalArgumentException("height is too small: " + height());
        }
        // transpose
        if (!isTransposed) {
            this.pic = transposeMatrix(this.pic);
        }
        // remove vertical seam from transposed picture
        removeVerticalSeam(seam);
        calledFromHorizontalFn = false;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("argument is null");
        // if the array is not a valid seam (i.e., either an entry is outside its prescribed range or two adjacent entries differ by more than 1).
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width())
                throw new IllegalArgumentException("not valid seam, out of bounds");

            if (i < seam.length - 1) {
                if (Math.abs(seam[i + 1] - seam[i]) > 1)
                    throw new IllegalArgumentException(
                            "not valid seam, adjacent entries differ by more than 1");
            }
        }
        // don't transpose the Picture until you need to do so
        // if we perform a lot of consecutive horizontal seam removals, it's going to be faster this way
        if (isTransposed && !calledFromHorizontalFn) {
            // transpose back, if not called from removeHorizontalSeam
            this.pic = transposeMatrix(this.pic);
        }

        if (width() <= 1) throw new IllegalArgumentException("width is too small");
        if (seam.length != height()) throw new IllegalArgumentException(
                "incorrect seam length: " + seam.length + " height: " + height());

        // create new pic, copy every pixel, but leave out the seam
        int[][] newPic = new int[height()][width() - 1];
        int column = 0;
        // j: row , i: col
        for (int j = 0; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                if (i == seam[j]) continue; // skip adding this px

                int px = pic[j][i];
                newPic[j][column] = px;
                column++;
            }
            column = 0; // reset
        }
        this.pic = newPic;

        /*
        // another way of doing it
        int[][] newPic = new int[height()][width() - 1];
        for (int j = 0; j < height(); j++) {
            int columnToBeRemoved = seam[j];
            System.arraycopy(pic[j], 0, newPic[j], 0, columnToBeRemoved);
            System.arraycopy(pic[j], columnToBeRemoved + 1, newPic[j], columnToBeRemoved,
                             pic[j].length - columnToBeRemoved - 1);
        }
        this.pic = newPic;
        */
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(picture);
        sc.picture().show();
        StdOut.println("width: " + sc.width() + " height: " + sc.height());

        StdOut.println("-- Vertical Seam --");
        StdOut.println(Arrays.toString(sc.findVerticalSeam()));

        StdOut.println("-- Horizontal  Seam --");
        StdOut.println(Arrays.toString(sc.findHorizontalSeam()));

        sc.removeVerticalSeam(sc.findVerticalSeam());
        sc.removeHorizontalSeam(sc.findHorizontalSeam());
        sc.picture().show();
    }
}
