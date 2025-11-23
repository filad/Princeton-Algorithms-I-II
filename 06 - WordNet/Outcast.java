import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Outcast detection.
 * Given a list of WordNet nouns x1, x2, ..., xn, which noun is the least related to the others?
 *
 * @author Adam Filkor
 */

public class Outcast {

    private  WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) throw new IllegalArgumentException("outcast null argument");

        String outcast = "";
        int dist = 0;
        int maxDist = 0;

        for (int i = 0; i < nouns.length; i++) {
            for (int j = 0; j < nouns.length; j++) {
                if (i == j) continue; // distance(xi, xi) = 0, so it will not contribute to the sum.
                dist = dist + this.wordnet.distance(nouns[i], nouns[j]);
            }
            if (dist > maxDist) {
                maxDist = dist;
                outcast = nouns[i];
            }
            dist = 0;
        }
        return outcast;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
