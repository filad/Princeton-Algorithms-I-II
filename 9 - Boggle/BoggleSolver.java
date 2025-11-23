import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * For solving Hasbro-type Boggle boards.
 * Using efficient Trie data structure for string storage, and search.
 *
 * @author Adam Filkor
 */

public class BoggleSolver {
    private BoggleBoard boggleBoard;
    private int width = 4;
    private int size;
    private Trie dict; // the dictionary we currently use
    private Stack<Integer> stack;
    private char[] vToLetter;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dict = new Trie();
        for (String word : dictionary) {
            dict.add(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        this.boggleBoard = board;
        this.width = board.cols();
        int height = board.rows();
        size = width * height;
        Graph G = new Graph(size);
        SET<String> results = new SET<>();

        // create the graph representing the boggle board
        // x: column, y: row
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x > 0 && y < height - 1)
                    G.addEdge(coordToVertex(x, y), coordToVertex(x - 1, y + 1));
                if (y < height - 1)
                    G.addEdge(coordToVertex(x, y), coordToVertex(x, y + 1));
                if (x < width - 1 && y < height - 1)
                    G.addEdge(coordToVertex(x, y), coordToVertex(x + 1, y + 1));
                if (x < width - 1)
                    G.addEdge(coordToVertex(x, y), coordToVertex(x + 1, y));
            }
        }

        vToLetter = new char[size];
        for (int i = 0; i < size; i++) {
            vToLetter[i] = vertexToLetter(i);
        }

        // Starting with every letter on the board,
        // go through every vertex with a custom depth first search method.
        boolean[] marked = new boolean[size];
        for (int v = 0; v < size; v++) {
            stack = new Stack<>();
            dfs(G, v, marked, results);
        }
        return results;
    }

    // Optimized depth first search method. Searching for all possible valid words in the board
    private void dfs(Graph G, int v, boolean[] alreadyMarked, SET<String> results) {
        boolean[] marked = new boolean[size];
        for (int i = 0; i < marked.length; i++) {
            marked[i] = alreadyMarked[i];
        }
        marked[v] = true;
        stack.push(v);

        char[] charArray = new char[stack.size()];
        int i = charArray.length - 1;
        for (int e : stack) {
            charArray[i] = vToLetter[e];
            i--;
        }
        String key = new String(charArray);
        // handle Q = Qu case
        if (key.contains("Q")) {
            key = key.replace("Q", "QU");
        }

        /*
         Critical backtracking optimization: when the current path corresponds to a string
         that is not a prefix of any word in the dictionary, there is no need to expand
         the path further.
         */
        if (!dict.hasPrefix(key)) return;

        if (key.length() >= 3) {
            if (dict.contains(key))
                results.add(key);
        }

        for (int w : G.adj(v)) {
            if (!marked[w]) {
                dfs(G, w, marked, results);
                stack.pop();
            }
        }
    }

    // params column x and row y. return the vertex name, start from 0..V - 1
    private int coordToVertex(int x, int y) {
        return y * width + x;
    }

    // returns [col, row]
    private int[] vertexToCoord(int v) {
        int[] coord = new int[2];
        coord[0] = v % width;
        coord[1] = v / width;
        return coord;
    }

    private char vertexToLetter(int v) {
        int column = vertexToCoord(v)[0];
        int row = vertexToCoord(v)[1];
        // note: this returns 'Q' representing the two-letter sequence "Qu".
        return boggleBoard.getLetter(row, column);
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word.length() == 0) throw new IllegalArgumentException("Word is too short");
        if (word.length() < 3 || !dict.contains(word)) return 0;
        if (word.length() == 3 || word.length() == 4) return 1;
        if (word.length() == 5) return 2;
        if (word.length() == 6) return 3;
        if (word.length() == 7) return 5;
        else return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        Stopwatch sw = new Stopwatch();
        solver.getAllValidWords(board);
        StdOut.println("Get all valid words time: " + sw.elapsedTime() + " seconds.");

        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
