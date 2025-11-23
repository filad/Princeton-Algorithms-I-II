import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Data type that models an n-by-n board with sliding tiles.
 * Part of the 8-puzzle problem.
 *
 * @author Adam Filkor
 */

public class Board {

    private final char[][] board;
    // Should be: To save memory, consider using an n-by-n char[][] array
    private Board twinBoard = null;
    private final int n;
    private int[] blank = new int[2]; // the blank tile, row col

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length; // row length
        board = new char[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = (char) tiles[i][j];

                if (board[i][j] == 0) {
                    blank[0] = i;
                    blank[1] = j;
                }
            }
        }
    }

    // string representation of this board
    public String toString() {
        String s = String.valueOf(n) + '\n';
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s += Integer.toString(board[i][j]);
                if (j == n - 1) {
                    s += "\n";
                }
                else
                    s += " ";
            }
        }
        return s;
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int outOfPlace = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (board[row][col] != n * row + col + 1 && board[row][col] != 0)
                    outOfPlace++;
            }
        }
        return outOfPlace;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int goalRow;
        int goalCol;
        int dist = 0;

        for (int i = 1; i <= n * n; i++) {
            goalRow = i / n; // i == 2 n == 2?
            if (i % n == 0) goalRow--; // when we arrive to the right edge
            goalCol = i % n - 1; // -1 cause it starts from 1
            if (i % n == 0) goalCol += n; // probably there is a bit cleaner way to do this...

            outerloop:
            // for breaking out
            for (int row = 0; row < n; row++) {
                for (int col = 0; col < n; col++) {
                    if (board[row][col] == i) {
                        // calculate distance
                        dist += Math.abs(goalRow - row) + Math.abs(goalCol - col);
                        // StdOut.println("i:" + i+ " -- " + Math.abs(goalRow - row) + Math.abs(goalCol - col));
                        break outerloop;
                    }
                }
            }
        }
        return dist;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return this.hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) return false;
        if (y.getClass() != getClass()) return false; // can't use instanceof in this course
        Board that = (Board) y;
        if (n != that.board.length) return false; // when board sizes m and n are different
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.board[i][j] != that.board[i][j])
                    return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> queue = new Queue<Board>();
        int row = blank[0];
        int col = blank[1];
        int[][] copy = new int[n][n];
        Board neighbour;
        int temp;

        // create a copy
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                copy[i][j] = board[i][j];
            }
        }

        if (col - 1 >= 0) {
            temp = copy[row][col - 1];
            copy[row][col - 1] = 0;
            copy[row][col] = temp;
            // add to the queue
            neighbour = new Board(copy);
            queue.enqueue(neighbour);
            // reset copy
            copy[row][col - 1] = temp;
            copy[row][col] = 0;
        }
        if (col + 1 < n) {
            temp = copy[row][col + 1];
            copy[row][col + 1] = 0;
            copy[row][col] = temp;
            // add to the queue
            neighbour = new Board(copy);
            queue.enqueue(neighbour);
            // reset copy
            copy[row][col + 1] = temp;
            copy[row][col] = 0;
        }
        if (row + 1 < n) {
            temp = copy[row + 1][col];
            copy[row + 1][col] = 0;
            copy[row][col] = temp;
            // add to the queue
            neighbour = new Board(copy);
            queue.enqueue(neighbour);
            // reset copy
            copy[row + 1][col] = temp;
            copy[row][col] = 0;
        }
        if (row - 1 >= 0) {
            temp = copy[row - 1][col];
            copy[row - 1][col] = 0;
            copy[row][col] = temp;
            // add to the queue
            neighbour = new Board(copy);
            queue.enqueue(neighbour);
            // reset copy
            copy[row - 1][col] = temp;
            copy[row][col] = 0;
        }
        return queue;
    }

    // a board that is obtained by exchanging any pair of tiles
    // always return the same twin, upon multiple calls
    public Board twin() {
        if (twinBoard != null) {
            return twinBoard;
        }
        else {
            // else do the same as before see function above...
            int[][] copy = new int[n][n];
            // create a copy
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    copy[i][j] = board[i][j];
                }
            }

            int randRowA; // = StdRandom.uniform(0, n);
            int randColA; // = StdRandom.uniform(0, n);
            int randRowB; // = StdRandom.uniform(0, n);
            int randColB; // = StdRandom.uniform(0, n);

            do {
                randRowA = StdRandom.uniformInt(0, n);
                randColA = StdRandom.uniformInt(0, n);
            } while (board[randRowA][randColA] == 0);
            // StdOut.println("row A: "+ randRowA);
            // StdOut.println("col A: "+ randColA);
            do {
                randRowB = StdRandom.uniformInt(0, n);
                randColB = StdRandom.uniformInt(0, n);
            } while (board[randRowB][randColB] == 0 || board[randRowB][randColB]
                    == board[randRowA][randColA]); // need || here
            // StdOut.println("row B: "+ randRowB);
            // StdOut.println("col B: "+ randColB);

            // swap
            int temp = board[randRowA][randColA];
            copy[randRowA][randColA] = board[randRowB][randColB];
            copy[randRowB][randColB] = temp;

            twinBoard = new Board(copy);
            return twinBoard;
        }
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        StdOut.println(initial);
        StdOut.println("hamming dist: " + initial.hamming());
        StdOut.println("manhattan dist: " + initial.manhattan());

        // Queue<Board> q = new Queue<Board>();
        // q = (Queue<Board>) initial.neighbors(); // have to cast
        StdOut.println("Neightbours: ");
        for (Board neighbour : initial.neighbors()) {
            StdOut.println(neighbour);
        }

        StdOut.println("Random twins (should all be the same):");
        StdOut.println(initial.twin());
        StdOut.println(initial.twin());
        StdOut.println(initial.twin());

        Board second = new Board(tiles);
        StdOut.println("Check equals(): " + second.equals(initial));
    }
}
