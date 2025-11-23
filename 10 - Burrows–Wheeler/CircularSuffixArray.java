import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/**
 * CircularSuffixArray is a key component in the Burrows–Wheeler transform.
 * It describes the abstraction of a sorted array of the n circular suffixes of a string
 * of length n.
 * Note, it's perhaps the cleanest solution, but it is not the fastest.
 *
 * @author Adam Filkor
 */
public class CircularSuffixArray {

    private final CircularSuffix[] circularSuffixes;

    // circular suffix array of text s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("no argument");
        int n = s.length();
        this.circularSuffixes = new CircularSuffix[n];
        for (int i = 0; i < n; i++) {
            circularSuffixes[i] = new CircularSuffixArray.CircularSuffix(s, i);
        }
        Arrays.sort(circularSuffixes);
    }

    private static class CircularSuffix implements Comparable<CircularSuffix> {
        private final String text;
        private final int index;

        private CircularSuffix(String text, int index) {
            this.text = text;
            this.index = index;
        }

        private int length() {
            return text.length();
        }

        private char charAt(int i) {
            if ((index + i) <= length() - 1) return text.charAt(index + i);
            else return text.charAt((index + i) % length());

        }

        public int compareTo(CircularSuffixArray.CircularSuffix that) {
            if (this == that) return 0;  // optimization
            int n = this.length();
            for (int i = 0; i < n; i++) {
                if (this.charAt(i) < that.charAt(i)) return -1;
                if (this.charAt(i) > that.charAt(i)) return +1;
            }
            return 0;
        }
    }

    // length of s
    public int length() {
        return circularSuffixes.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > length() - 1) throw new IllegalArgumentException("args range");
        return circularSuffixes[i].index;
    }

    // unit testing (required)
    public static void main(String[] args) {
        In in = new In(args[0]);
        String s = in.readAll();
        CircularSuffixArray circular = new CircularSuffixArray(s);
        for (int i = 0; i < circular.length(); i++) {
            StdOut.println(circular.index(i));
        }
    }
}
