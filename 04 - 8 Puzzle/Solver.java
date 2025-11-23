import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 * Solve the 8-puzzle problem (and its natural generalizations) using the A* search algorithm.
 *
 * @author Adam Filkor
 */
public class Solver {

    private SearchNode goalNode;
    private boolean solvable = true;

    // custom class
    private class SearchNode implements Comparable<SearchNode> {
        Board board;
        SearchNode prev; // previous search node
        int moves = 0; // the number of moves made to reach the board
        int manhattanCache;
        // Optimization: caching the Hamming and Manhattan priorities. see: compareTo method

        // we need a compareTo function, MinPQ doesn't work otherwise
        public int compareTo(SearchNode that) {
            int priority,
                    thatPriority; // this is the "heuristic" manhattan dist to goal + moves done so far.
            priority = this.manhattanCache + this.moves;
            thatPriority = that.manhattanCache + that.moves;

            if (priority > thatPriority) return +1;
            else if (priority < thatPriority) return -1;
            else return 0;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();

        SearchNode searchNode = new SearchNode();
        // minimum priority queue
        MinPQ<SearchNode> minPQ = new MinPQ<SearchNode>();

        searchNode.board = initial;
        searchNode.prev = null;
        searchNode.moves = 0;
        searchNode.manhattanCache = searchNode.board.manhattan();
        minPQ.insert(searchNode);

        /**
         * Detecting unsolvable boards. To apply the fact, run the A* algorithm
         * on two puzzle instances—one with the initial board and one with the initial board modified
         * by swapping a pair of tiles—in lockstep (alternating back and forth between exploring search
         * nodes in each of the two game trees). Exactly one of the two will lead to the goal board.
         */
        SearchNode twinSearchNode = new SearchNode();
        // minimum priority queue
        MinPQ<SearchNode> twinMinPQ = new MinPQ<SearchNode>();

        twinSearchNode.board = initial.twin();
        twinSearchNode.prev = null;
        twinSearchNode.moves = 0;
        twinSearchNode.manhattanCache = twinSearchNode.board.manhattan();
        twinMinPQ.insert(twinSearchNode);

        /**
         * at each step, the A* algorithm removes the node with the smallest priority from the priority queue
         * and processes it (by adding it's children to both the game tree and the priority queue).
         */
        while (true) {
            searchNode = minPQ.delMin();

            if (searchNode.manhattanCache == 0) {
                goalNode = searchNode;
                break; // if it's the goal board, break.
            }
            for (Board neigh : searchNode.board.neighbors()) {

                // critical optimization, to reduce unnecessary exploration of useless search nodes.
                // don’t enqueue a neighbor if it's board is the same as the board of the previous search node in the game tree.
                if (searchNode.prev != null && neigh.equals(searchNode.prev.board))
                    continue;

                SearchNode node = new SearchNode();
                node.board = neigh;
                node.prev = searchNode;
                node.moves = searchNode.moves + 1;  // ++ and + 1 ,they are not equal. JESUS
                node.manhattanCache = node.board.manhattan();
                minPQ.insert(node);

            }

            /*
                same but for twin node, for detecting unsolvable boards.
            */
            twinSearchNode = twinMinPQ.delMin();

            if (twinSearchNode.manhattanCache == 0) {
                goalNode = twinSearchNode;
                solvable = false;
                break; // if it's the goal board, break.
            }
            for (Board neigh : twinSearchNode.board.neighbors()) {

                // critical optimization, to reduce unnecessary exploration of useless search nodes.
                // don’t enqueue a neighbor if its board is the same as the board of the previous search node in the game tree.
                if (twinSearchNode.prev != null && neigh.equals(twinSearchNode.prev.board))
                    continue;

                SearchNode node = new SearchNode();
                node.board = neigh;
                node.prev = twinSearchNode;
                node.moves = twinSearchNode.moves + 1;
                node.manhattanCache = node.board.manhattan();
                twinMinPQ.insert(node);
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) return -1;
        return goalNode.moves; // returns the last dequeued node's move
    }

    // sequence of boards in the shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        Stack<Board> stack
                = new Stack<Board>(); // stack, because I want the goal to be the last to "pop()", doesn't really matter
        SearchNode node = goalNode;
        stack.push(goalNode.board);

        while (node.prev != null) {
            node = node.prev;
            stack.push(node.board);
        }
        return stack;
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

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
        StdOut.println("Minimum number of moves = " + solver.moves());
    }
}