import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseballElimination {
    private final Team[] teams;
    private final HashMap<String, Integer> nameToIndex = new HashMap<>();
    private final int n;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        try {
            n = Integer.parseInt(in.readLine());
            teams = new Team[n];
            int i = 0;
            while (in.hasNextLine()) {
                String readLine = in.readLine();
                if (readLine == null) throw new IllegalArgumentException("Input file is empty");
                String row = readLine.trim();
                String[] values = row.split("\\s+");

                int arg = 0;
                String name = values[arg++].trim();
                int wins = Integer.parseInt(values[arg++].trim());
                int losses = Integer.parseInt(values[arg++].trim());
                int remaining = Integer.parseInt(values[arg++].trim());
                int[] remainingAgainst = new int[teams.length];
                for (int j = 0; j < numberOfTeams(); j++) {
                    remainingAgainst[j] = Integer.parseInt(values[j + arg].trim());
                }

                teams[i] = new Team(name, wins, losses, remaining, remainingAgainst);
                nameToIndex.put(name, i++);
            }
        }
        finally {
            in.close();
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.length;
    }

    // all teams
    public Iterable<String> teams() {
        List<String> teamNames = new ArrayList<>(numberOfTeams());
        for (Team team : teams) {
            teamNames.add(team.getName());
        }
        return teamNames;
    }

    // number of wins for given team
    public int wins(String team) {
        validateName(team);
        return teams[nameToIndex.get(team)].getWins();
    }

    // number of losses for given team
    public int losses(String team) {
        validateName(team);
        return teams[nameToIndex.get(team)].getLosses();
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validateName(team);
        return teams[nameToIndex.get(team)].getRemaining();
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateName(team1);
        validateName(team2);
        return against(nameToIndex.get(team1), nameToIndex.get(team2));
    }

    private int against(int index1, int index2) {
        return teams[index1].getRemainingAgainst()[index2];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validateName(team);
        Set<Integer> trivialEliminators = trivialEliminators(team);
        if (!trivialEliminators.isEmpty()) {
            return true;
        }

        FlowNetwork G = generateG(team, trivialEliminators);
        FordFulkerson ff = new FordFulkerson(G, n, n + 1);
        if (ff.value() < 0) throw new IllegalArgumentException("Wrong graph");

        return isEliminatedGraph(G);
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validateName(team);
        Set<Integer> trivialEliminators = trivialEliminators(team);
        List<String> subsetR = new ArrayList<>();

        FlowNetwork G = generateG(team, trivialEliminators);
        FordFulkerson ff = new FordFulkerson(G, n, n + 1);

        if (trivialEliminators.size() == n - 1 || !isEliminatedGraph(G)) {
            return (!trivialEliminators.isEmpty())
                   ? indicesToNames(trivialEliminators)
                   : null;
        }

        for (int i = 0; i < n; i++) {
            if (ff.inCut(i)) subsetR.add(teams[i].getName());
        }

        return subsetR;
    }

    private Set<Integer> trivialEliminators(String team) {
        Set<Integer> eliminators = new HashSet<>();
        Team current = teams[nameToIndex.get(team)];
        int currentPotential = current.getWins() + current.getRemaining();

        for (int i = 0; i < n; i++) {
            if (currentPotential < teams[i].getWins()) {
                eliminators.add(i);
            }
        }
        return eliminators;
    }

    private FlowNetwork generateG(String name, Set<Integer> trivialEliminators) {
        // (n^2 - n)/2 - game vertices, n - team vertices, 2 - source and target
        final int V = ((n * n - n) / 2) + n + 2;

        int teamV = nameToIndex.get(name);
        Team team = teams[teamV];

        // Vertices ranges:
        // Team vertices: [0; N - 1]
        // Source: N
        // Target: N + 1
        // Game vertices: [N + 2; (N^2 - N)/2 + N + 1]
        FlowNetwork G = new FlowNetwork(V);
        for (int i = 0; i < n; i++) {
            if (i != teamV) {
                int capacity = team.getWins() + team.getRemaining() - teams[i].getWins();
                if (capacity >= 0) {
                    G.addEdge(new FlowEdge(i, n + 1, capacity));
                }
            }
        }

        int gameV = n + 2;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (i != teamV && j != teamV) {
                    G.addEdge(new FlowEdge(n, gameV, against(i, j)));
                    if (!trivialEliminators.contains(j))
                        G.addEdge(new FlowEdge(gameV, i, Double.POSITIVE_INFINITY));
                    if (!trivialEliminators.contains(i))
                        G.addEdge(new FlowEdge(gameV, j, Double.POSITIVE_INFINITY));
                }
                gameV++;
            }
        }

        return G;
    }

    private boolean isEliminatedGraph(FlowNetwork G) {
        for (FlowEdge e : G.adj(n)) {
            if (e.flow() < e.capacity()) {
                return true;
            }
        }
        return false;
    }

    private Iterable<String> indicesToNames(Set<Integer> indices) {
        return indices.stream()
                      .map((i) -> teams[i].getName())
                      .collect(Collectors.toList());
    }

    private void validateName(String name) {
        if (!nameToIndex.containsKey(name))
            throw new IllegalArgumentException("Wrong name");
    }

}
