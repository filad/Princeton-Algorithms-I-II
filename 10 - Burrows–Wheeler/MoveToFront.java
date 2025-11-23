import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Main idea of move-to-front encoding is to maintain an ordered sequence of the characters
 * in the alphabet by repeatedly reading a character from the input message;
 * printing the position in the sequence in which that character appears;
 * and moving that character to the front of the sequence.
 * Provides exactly the kind of input for which Huffman coding achieves favorable compression
 * ratios.
 *
 * @author Adam Filkor
 */

public class MoveToFront {

    // alphabet size of extended ASCII
    private static final int R = 256;

    // Do not instantiate.
    private MoveToFront() {
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        // init 256 extended ASCII characters sequence
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < R; i++) {
            char c = (char) i;
            stringBuilder.append(c);
        }
        while (!BinaryStdIn.isEmpty()) {
            // read each 8-bit character c from standard input, one at a time
            char ch = BinaryStdIn.readChar();

            // output the 8-bit index in the sequence where c appears
            String str = String.valueOf(ch);
            int index = stringBuilder.indexOf(str);
            BinaryStdOut.write(index, 8); // 8 relevant bits in char

            // and move c to the front
            stringBuilder.deleteCharAt(index);
            stringBuilder.insert(0, ch);
        }
        BinaryStdOut.flush(); // important
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        // init 256 extended ASCII characters sequence
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < R; i++) {
            char c = (char) i;
            stringBuilder.append(c);
        }
        /**
         * read each 8-bit character i (but treat it as an integer between 0 and 255)
         * from standard input one at a time;
         * write the ith character in the sequence;
         * and move that character to the front.
         */
        while (!BinaryStdIn.isEmpty()) {
            char index = BinaryStdIn.readChar(8);
            char ch = stringBuilder.charAt(index);
            BinaryStdOut.write(ch);

            stringBuilder.deleteCharAt(index);
            stringBuilder.insert(0, ch);
        }
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        if (args[0].equals("+")) {
            decode();
        }


    }
}
