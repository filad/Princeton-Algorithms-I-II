import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Generic data type for a deque.
 *
 * @author Adam Filkor
 */
public class Deque<Item> implements Iterable<Item> {

    private Node first;
    private Node last;
    private int size = 0;

    private class Node {
        Item item;
        Node next;
        Node before;
    }

    // construct an empty deque
    public Deque() {
        last = null;
        first = null;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return first == null;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("null argument addFirst");
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        if (oldfirst != null) { // If we don't have only one node.
            oldfirst.before = first;
        }
        first.before = null;
        if (first.next == null) last = first;
        size++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("null argument addLast");
        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty())
            first = last;
        else
            oldlast.next = last;
        size++;
        if (size > 1) last.before = oldlast;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (size == 0) throw new NoSuchElementException("Queue is empty can't remove from it");
        Item item = first.item;
        first = first.next;
        size--;
        if (isEmpty()) {
            last = null;
        }
        else {
            first.before = null;
        }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (size == 0) throw new NoSuchElementException("Queue is empty can't remove from it");
        Item item = last.item;
        if (size > 1) last = last.before;
        last.next = null;
        size--;
        if (size == 0) {
            last = null;
            first = null;
        }
        if (size == 1) last = first;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported here.");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {

        Deque<String> deque = new Deque<>();

        String str;
        while (!StdIn.isEmpty()) {
            str = StdIn.readString();
            deque.addFirst(str);
        }

        StdOut.println("size: " + deque.size());
        StdOut.println("isEmpty: " + deque.isEmpty());
        deque.addLast("last");


        int size = deque.size();
        for (int i = 0; i < size; i++) {
            str = deque.removeLast();
            StdOut.println(str);
        }

        StdOut.println("------");
        for (String s : deque) {
            StdOut.println(s);
        }

        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("c");
        StdOut.println("remove first: " + deque.removeFirst());
        StdOut.println("remove last: " + deque.removeLast());
        deque.addLast("cat");

        for (String s : deque) {
            StdOut.println(s);
        }


    }
}
