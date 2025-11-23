import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A randomized queue is similar to a stack or queue, except that the item removed is
 * chosen uniformly at random among items in the data structure.
 * Using a dynamic array here, resizing it based on required capacity. (grow, shrink)
 *
 * @author Adam Filkor
 */

public class RandomizedQueue<Item> implements Iterable<Item> {

    // initial capacity of underlying resizing array
    private static final int INIT_CAPACITY = 8;

    private Item[] q;
    private int N = 0; // the size


    // construct an empty randomized queue
    public RandomizedQueue() {
        q = (Item[]) new Object[INIT_CAPACITY];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return N == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return N;
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        assert capacity >= N;

        // textbook implementation
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < N; i++) {
            copy[i] = q[i];
        }
        q = copy;

        // alternative implementation
        // a = java.util.Arrays.copyOf(a, capacity);
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("null argument enqueue");

        if (N == q.length) resize(2 * q.length); // https://algs4.cs.princeton.edu/13stacks/ResizingArrayStack.java.html
        q[N++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        int rand = StdRandom.uniformInt(N);  // Returns a random integer uniformly in [0, n).
        Item item = q[rand];
        Item lastItem = q[N - 1];

        // swap the last item into the dequeued item's place. this way, we don't have to go over the whole array and remove nulls
        q[rand] = lastItem;
        q[N - 1] = null; // make the last item null
        N--; // decrease size

        // shrink size of array if necessary
        if (N > 0 && N == q.length / 4) resize(q.length / 2);
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        int rand = StdRandom.uniformInt(N);  // Returns a random integer uniformly in [0, n).
        return q[rand];
    }


    // return an independent iterator over items in random order (!!)
    public Iterator<Item> iterator() {
        return new RandomArrayIterator();
    }

    // this is a custom class, it can have any name.
    private class RandomArrayIterator implements Iterator<Item> {
        int i;
        private Item[] copy;

        public RandomArrayIterator() {
            /*
                two iterators should return the same set
                of values but in a different order
                (nested iterators or parallel iterators)
            */
            copy = (Item[]) new Object[N];
            int j = 0;
            for (int k = 0; k < N; k++) {
                if (q[k] == null) continue; // remove nulls
                copy[j++] = q[k];
            }

            StdRandom.shuffle(copy);
            i = 0;
        }

        public boolean hasNext() {
            return i < N; // slides use current!= null;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported here.");
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy[i++];
        }
    }

    // unit testing (required)

    /**
     * Your main() method must call directly
     * every public constructor and method to help verify that they work as prescribed
     * (e.g., by printing results to standard output).
     */
    public static void main(String[] args) {

        int n = 5;
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();
        for (int i = 0; i < n; i++)
            queue.enqueue(i);
        for (int a : queue) {
            for (int b : queue)
                StdOut.print(a + "-" + b + " ");
            StdOut.println();
        }

        RandomizedQueue<String> rQueue = new RandomizedQueue<String>();

        String str;
        while (!StdIn.isEmpty()) {
            str = StdIn.readString();
            rQueue.enqueue(str);
        }

        StdOut.println(rQueue.dequeue());

        StdOut.println("sample: " + rQueue.sample());
        StdOut.println("empty: " + rQueue.isEmpty());
        StdOut.println("size: " + rQueue.size());

        StdOut.println("--------");
        for (String s : rQueue) {
            StdOut.println(s);
        }
    }
}
