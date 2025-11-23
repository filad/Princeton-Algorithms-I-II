import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Burrows–Wheeler transform.
 * The goal of the Burrows–Wheeler transform is not to compress a message,
 * but rather to transform it into a form that is more amenable for compression.
 * The Burrows–Wheeler transform rearranges the characters in the input so that
 * there are lots of clusters with repeated characters,
 * but in such a way that it is still possible to recover the original input.
 *
 * @author Adam Filkor
 */
public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String origStr = BinaryStdIn.readString();
        CircularSuffixArray suffixArray = new CircularSuffixArray(origStr);

        // in the sorted array is the row number in which the original string ends up.
        int firstIndex = 0;
        for (int i = 0; i < suffixArray.length(); i++) {
            if (suffixArray.index(i) == 0) {
                firstIndex = i;
                break;
            }
        }

        // get last column for the Burrows-Wheeler transform
        char[] lastColumnStr = new char[suffixArray.length()];
        for (int i = 0; i < suffixArray.length(); i++) {
            if (suffixArray.index(i) == 0)
                lastColumnStr[i] = origStr.charAt(suffixArray.length() - 1);
            else
                lastColumnStr[i] = origStr.charAt(suffixArray.index(i) - 1); // just a shift by 1
        }
        BinaryStdOut.write(firstIndex);
        BinaryStdOut.write(String.valueOf(lastColumnStr));

        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int firstIndex = BinaryStdIn.readInt();
        String encdodedText = BinaryStdIn.readString();

        // sort input (the encoded msg) by key-indexed counting
        /* we can deduce the first column of the sorted suffixes because
           it consists of precisely the same characters, but in sorted order. */
        int n = encdodedText.length();
        int R = 256;   // extend ASCII alphabet size
        int[] count = new int[R + 1];
        char[] aux = new char[n];

        // compute frequency counts
        for (int i = 0; i < n; i++) {
            count[encdodedText.charAt(i) + 1]++;
        }

        // compute cumulates
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];

        // now last step of key-indexed counting, move data to an aux[] array. we don't copy back the aux array to the original.
        // aux now will be the first column of chars in the sorted circular arrays,
        // For "ABRACADABRA!" its "!AAAAABBCDRR"
        // we also, at the same time, construct the next[] array
        /**
         * Consider: if you use the key-indexed counting method to sort the t[] array,
         * then you already have all the information you need with no extra memory or code.
         * The value of next[i] is exactly the same as the location in the new array where the value is being copied to.
         * (we don't copy back aux to the original array here, though)
         *
         * Using the example in the specification, t[0] is A. When using key-indexed counting,
         * this character is moved to aux[3]. The value of next[0] is 3. The next character, t[1] = R, is moved to aux[10].
         * The value of next[10] is 1.
         */
        int[] next = new int[n];
        for (int i = 0; i < n; i++) {
            next[count[encdodedText.charAt(i)]] = i;
            aux[count[encdodedText.charAt(i)]++] = encdodedText.charAt(i);
        }

        // reconstructing the original string, and printing it to standard output.
        int currIndex = firstIndex;
        for (int i = 0; i < n; i++) {
            BinaryStdOut.write(aux[currIndex]);
            currIndex = next[currIndex];
        }
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        }
        if (args[0].equals("+")) {
            inverseTransform();
        }
    }
}
