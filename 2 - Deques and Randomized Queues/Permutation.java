import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Takes an integer k as a command-line argument;
 * reads a sequence of strings from standard input using StdIn.readString(); and
 * prints exactly k of them, uniformly at random.
 *
 * @author Adam Filkor
 */

public class Permutation {
    public static void main(String[] args) {
        /**
         *  You may assume that 0 ≤ k ≤ n, where n is the number of string on standard input. Note that you are not given n.
         *  For an extra challenge and a small amount of extra credit,
         *  use only one Deque or RandomizedQueue object of maximum size at most k.
         */
        int k = Integer.parseInt(args[0]);
        if (k <= 0) return;

        /*

        Firstly create a RandomizedQueue object, execute enqueue() k times to put the first k elements of the string sequence into RandomizedQueue,
        then read the next string to put it into RandomizedQueue with some probability after execute dequeue().

        For this we use a form of Knuth’s method  (read a sequence of words
        from standard input and prints one of those words uniformly at random.)

        */

        RandomizedQueue<String> rQueue = new RandomizedQueue<String>();

        int i = 0;
        String tempStr;
        while (!StdIn.isEmpty()) {
            i++;
            tempStr = StdIn.readString();


            if (i <= k) {
                rQueue.enqueue(tempStr);
            }
            else {
                // reservoir sampling
                // probability: (k / (double) i);
                if (StdRandom.bernoulli(k / (double) i)) {
                    rQueue.dequeue();
                    rQueue.enqueue(tempStr);
                }
            }
        }
        for (String s : rQueue) {
            StdOut.println(s);
        }
    }
}
