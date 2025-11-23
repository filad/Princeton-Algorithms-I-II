/**
 * Custom Trie structure for BoggleSolver, with R=26, containing only ASCII letters
 *
 * @author Adam Filkor
 */
public class Trie {
    private static final int R = 26; // Letters

    private Trie.Node root; // root of trie
    private int n; // number of keys in trie
    private boolean hasPrefix;

    // R-way trie node
    private static class Node {
        private Trie.Node[] next = new Trie.Node[R];
        private boolean isString;
    }

    public Trie() {
    }

    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        Trie.Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isString;
    }

    private Trie.Node get(Trie.Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        // char c = key.charAt(d);
        int index = charToIndex(key, d);
        return get(x.next[index], key, d + 1);
    }

    public void add(String key) {
        if (key == null) throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }

    private Trie.Node add(Trie.Node x, String key, int d) {
        if (x == null) x = new Trie.Node();
        if (d == key.length()) {
            if (!x.isString) n++;
            x.isString = true;
        }
        else {
            // char c = key.charAt(d);
            int index = charToIndex(key, d);
            x.next[index] = add(x.next[index], key, d + 1);
        }
        return x;
    }

    private int charToIndex(String key, int d) {
        return (int) key.charAt(d) - 65; // 'A' in ASCII is decimal 65
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    // we are checking only if given prefix exists in a key, but do not return those particular keys
    public boolean hasPrefix(String prefix) {
        hasPrefix = false;
        Trie.Node x = get(root, prefix, 0);
        collect(x, new StringBuilder(prefix));
        return hasPrefix;
    }

    private void collect(Trie.Node x, StringBuilder prefix) {
        if (x == null) return;
        if (hasPrefix) return;
        if (x.isString) {
            hasPrefix = true;
            return;
        }
        for (char c = 0; c < R; c++) {
            prefix.append(c);
            collect(x.next[c], prefix);
            // prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    public static void main(String[] args) {
    }
}
