import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// https://coursera.cs.princeton.edu/algs4/assignments/wordnet/specification.php
// https://www.coursera.org/learn/algorithms-part2/programming/BCNsp/wordnet
public class WordNet {
    private final SAP sap;
    private final Map<String, List<Integer>> nounToVertices;
    private final Map<Integer, String> idToSynset;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        nonNull(synsets, "synsets filename is null");
        nonNull(hypernyms, "hypernyms filename is null");

        nounToVertices = new HashMap<>();
        idToSynset = new HashMap<>();
        In inSynsets = new In(synsets);
        int v = 0;
        try {
            while (inSynsets.hasNextLine()) {
                String[] line = inSynsets.readLine().split(",", 3);

                int synsetId = Integer.parseInt(line[0]);
                idToSynset.put(synsetId, line[1]);

                for (String noun : line[1].split(" ")) {
                    if (!nounToVertices.containsKey(noun)) {
                        LinkedList<Integer> vertices = new LinkedList<>();
                        vertices.add(synsetId);
                        nounToVertices.put(noun, vertices);
                    }
                    else {
                        nounToVertices.get(noun).add(synsetId);
                    }
                }
                v++;
            }
        }
        finally {
            inSynsets.close();
        }

        Digraph digraph = new Digraph(v);
        In inHypernyms = new In(hypernyms);
        try {
            while (inHypernyms.hasNextLine()) {
                String[] line = inHypernyms.readLine().split(",");

                int hyponym = Integer.parseInt(line[0]);
                for (int i = 1; i < line.length; i++) {
                    digraph.addEdge(hyponym, Integer.parseInt(line[i]));
                }
            }
        }
        finally {
            inHypernyms.close();
        }

        int roots = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (!digraph.adj(i).iterator().hasNext()) {
                roots++;
            }
        }

        if (roots != 1 || new DirectedCycle(digraph).hasCycle()) {
            throw new IllegalArgumentException("Given graph isn't rooted DAG");
        }

        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToVertices.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        nonNull(word, "Given word is null");
        return nounToVertices.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        return sap.length(nounToVertices.get(nounA), nounToVertices.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in the shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        return idToSynset.get(
                sap.ancestor(nounToVertices.get(nounA), nounToVertices.get(nounB))
        );
    }

    private void nonNull(Object notNull, String msg) {
        if (notNull == null) throw new IllegalArgumentException(msg);
    }

}
