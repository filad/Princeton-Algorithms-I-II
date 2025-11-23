import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

/**
 * Determines which teams have been mathematically eliminated
 * from winning their division, using maxflow - mincut
 *
 * @author Adam Filkor
 */

public class BaseballElimination {
    private int[] wins; // wins
    private int[] losses; // losses
    private int[] remaining; // remaining games
    private int[][] g; // games left to play against team j.
    private ArrayList<String> teams; // team names
    private ArrayList<String> resultSubset; // teams who eliminate x
    private int n; // number of teams

    public BaseballElimination(String filename) {
        if (filename == null) throw new IllegalArgumentException("No filename.");
        In in = new In(filename);
        n = Integer.parseInt(in.readLine());
        if (n == 0) throw new RuntimeException("No teams");
        wins = new int[n];
        losses = new int[n];
        remaining = new int[n];
        g = new int[n][n];
        teams = new ArrayList<String>(n);

        int i = 0;
        while (!in.isEmpty()) {
            String line = in.readLine(); // Baltimore   71 63 28   3 0 2 7 7
            line = line.trim();
            String[] arr = line.split("\\s+");
            teams.add(arr[0]);
            wins[i] = Integer.parseInt(arr[1]);
            losses[i] = Integer.parseInt(arr[2]);
            remaining[i] = Integer.parseInt(arr[3]);
            for (int j = 0; j < n; j++) {
                g[i][j] = Integer.parseInt(arr[j + 4]);
            }
            i++;
        }
    }

    public int numberOfTeams() {
        if (this.n == 0) throw new IllegalArgumentException("No teams.");
        return this.n;
    }

    public Iterable<String> teams() {
        return new ArrayList<>(teams); // copy teams and return the copy
    }

    public int wins(String team) {
        int i = this.teams.indexOf(team);
        if (i == -1) throw new IllegalArgumentException("Wrong team name.");
        return wins[i];
    }

    public int losses(String team) {
        int i = this.teams.indexOf(team);
        if (i == -1) throw new IllegalArgumentException("Wrong team name.");
        return losses[i];
    }

    public int remaining(String team) {
        int i = this.teams.indexOf(team);
        if (i == -1) throw new IllegalArgumentException("Wrong team name.");
        return remaining[i];
    }

    public int against(String team1, String team2) {
        int i = this.teams.indexOf(team1);
        int j = this.teams.indexOf(team2);
        if (i == -1 || j == -1) throw new IllegalArgumentException("Wrong team name.");
        return g[i][j];
    }

    // N choose K -- limited
    private int binomial(final int N, final int K) {
        int nCk = 1;
        for (int k = 0; k < K; k++) {
            nCk = nCk * (N - k) / (k + 1);
        }
        return nCk;
    }

    // is given team eliminated
    public boolean isEliminated(String team) {
        int x = this.teams.indexOf(team);
        if (x == -1) throw new IllegalArgumentException("Wrong team name.");
        if (numberOfTeams() == 1) return false;

        /**
         * check basic elimination condition first
         * if w[x] + r[x] < w[i], then team x has been mathematically eliminated.
         */
        resultSubset = new ArrayList<String>();
        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            if (wins[x] + remaining[x] < wins[i]) {
                resultSubset.add(teams.get(i));
            }
        }
        if (!resultSubset.isEmpty()) {
            return true;
        }

        /**
         * Nontrivial elimination.
         * Build the graph,
         * connect virtual source vertex s to each game vertex i-j and set its capacity to g[i][j],
         * then connect games to teams, then teams to the sink
         */
        int nCk = binomial(n - 1, 2); // n - 1 teams playing each other -> (n - 1 choose 2) pairs
        int maxVertices = 2 + nCk + (n - 1); // source + sink + games + (teams-1)
        int virtualSource = 0;
        int virtualSink = maxVertices - 1;
        FlowNetwork flowNet = new FlowNetwork(maxVertices);
        FlowEdge edge;

        int w = 0;
        int teamVertex = maxVertices
                - numberOfTeams(); // initial value. first team represented as a vertex
        ST<String, Integer> teamsVertices = new ST<>(); // assign the team names to vertices
        ST<Integer, String> verticesTeams = new ST<>(); // assign the vertices to team names
        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            teamsVertices.put(teams.get(i), teamVertex);
            verticesTeams.put(teamVertex, teams.get(i));
            teamVertex++;
        }

        // i: current team, x: team which we check if eliminated
        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            for (int j = i; j < n; j++) {
                if (i == j) continue;
                if (j == x) continue;
                // StdOut.println(i + " - " + j);
                w++;
                edge = new FlowEdge(virtualSource, w, g[i][j]);
                flowNet.addEdge(edge);

                // connect games to the corresponding teams
                edge = new FlowEdge(w, teamsVertices.get(teams.get(i)), Double.POSITIVE_INFINITY);
                flowNet.addEdge(edge);
                edge = new FlowEdge(w, teamsVertices.get(teams.get(j)), Double.POSITIVE_INFINITY);
                flowNet.addEdge(edge);
            }
        }
        // connect teams to virtual sink
        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            edge = new FlowEdge(teamsVertices.get(teams.get(i)), virtualSink,
                                wins[x] + remaining[x] - wins[i]);
            flowNet.addEdge(edge);
        }

        /**
         * If all edges in the maxflow that are pointing from s are full,
         * then this corresponds to assigning winners to all the remaining
         * games in such a way that no team wins more games than x.
         * If some edges pointing from s are not full,
         * then there is no scenario in which team x can win the division.
         */
        FordFulkerson maxflow = new FordFulkerson(flowNet, virtualSource, virtualSink);
        for (FlowEdge e : flowNet.adj(virtualSource)) {
            if (e.flow() != e.capacity()) {
                /**
                 * Subset of teams who eliminate x,
                 * can always find such a subset R by choosing the team vertices on the source side
                 * of a min s-t cut in the baseball elimination network.
                 */
                for (int v = 0; v < flowNet.V(); v++) {
                    // inCut(v) is true if the specified vertex is on the source side of the mincut.
                    if (maxflow.inCut(v)) {
                        String s = verticesTeams.get(v);
                        if (s != null) resultSubset.add(s);
                    }
                }
                return true;
            }
        }
        return false;
    }

    // subset R of teams that eliminates given team x; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team)) return null;
        return resultSubset.isEmpty() ? null : resultSubset;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
