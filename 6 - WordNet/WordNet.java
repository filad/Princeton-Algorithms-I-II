import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

/**
 * Creates the WordNet digraph (directed graph).
 * <p>
 * <a href="https://wordnet.princeton.edu/">WordNet</a> is a semantic lexicon for the English
 * language that computational linguists and cognitive scientists use extensively.
 * Nouns, verbs, adjectives and adverbs are grouped into sets of cognitive synonyms (synsets),
 * each expressing a distinct concept.
 *
 * @author Adam Filkor
 */

public class WordNet {

    // Symbol table key -> value data type. noun -> {collection of id's}. "jump" -> {15, 40, 132, ..}
    private ST<String, SET<Integer>> nouns = new ST<String, SET<Integer>>();
    private ArrayList<String> synsetsArray = new ArrayList<String>(); // resizable array for synsets
    private SAP sap; // shortest ancestral path object

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null) throw new IllegalArgumentException("synsets null argument");
        if (hypernyms == null) throw new IllegalArgumentException("synsets null argument");
        In synsetsFile = new In(synsets);
        In hypernymsFile = new In(hypernyms);
        int numSynsets = 0; // number of synsets

        while (!synsetsFile.isEmpty()) {
            String s = synsetsFile.readLine();
            String[] parts = s.split(",");
            String synset = parts[1]; // One synonym set. Separated by spaces. ex.: "despair desperation"
            this.synsetsArray.add(synset);

            int id = Integer.parseInt(parts[0]);

            String[] synsetNouns = synset.split(" ");
            for (String n : synsetNouns) {
                if (nouns.contains(n)) {
                    // if we already have the noun as a key
                    nouns.get(n).add(id);
                }
                else {
                    SET<Integer> newId = new SET<Integer>();
                    newId.add(id);
                    nouns.put(n, newId);
                }
            }
            numSynsets++;
        }
        synsetsArray.trimToSize();

        // build the digraph from the hypernyms
        Digraph G = new Digraph(numSynsets);
        boolean isRootedDAG = false;

        while (!hypernymsFile.isEmpty()) {
            String s = hypernymsFile.readLine();

            // Split the string, the first field is the synset id,
            // subsequent fields are the id numbers of the synset’s hypernyms or "parents".
            String[] parts = s.split(",");

            if (parts.length == 1) {
                isRootedDAG = true;
                continue; // found the root, and root has no out edges
            }

            int v = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                int w = Integer.parseInt(parts[i]);
                G.addEdge(v, w);
            }
        }
        if (!isRootedDAG) throw new IllegalArgumentException("The input to the constructor does not correspond to a rooted DAG.");
        this.sap = new SAP(G);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("word argument is null");
        if (nouns.contains(word)) {
            return true;
        }
        return false;
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (nounA == null) throw new IllegalArgumentException("argument in distance() is null");
        if (nounB == null) throw new IllegalArgumentException("argument in distance() is null");
        if (!isNoun(nounA)) throw new IllegalArgumentException("argument in distance() is not a noun");
        if (!isNoun(nounB)) throw new IllegalArgumentException("argument in distance() is not a noun");

        return this.sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in the shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null) throw new IllegalArgumentException("argument in sap() is null");
        if (nounB == null) throw new IllegalArgumentException("argument in sap() is null");
        if (!isNoun(nounA)) throw new IllegalArgumentException("argument in sap() is not a noun");
        if (!isNoun(nounB)) throw new IllegalArgumentException("argument in sap() is not a noun");

        int ancestor = this.sap.ancestor(nouns.get(nounA), nouns.get(nounB));
        return synsetsArray.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");

        StdOut.println("------ Is noun? -----");
        StdOut.println(wordnet.isNoun("NGF"));
        StdOut.println(wordnet.isNoun("nerve_growth_factor"));

        String nounA = "central_nervous_system";
        String nounB = "nerve_pathway";

        StdOut.println("---- Distance ----");
        StdOut.println(wordnet.distance(nounA, nounB));

        StdOut.println("---- SAP ----");
        StdOut.println(wordnet.sap(nounA, nounB));
    }
}
