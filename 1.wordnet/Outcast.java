public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDist = -1;
        String outcast = null;
        for (int i = 0; i < nouns.length; i++) {
            int dist = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (i != j) {
                    dist += wordnet.distance(nouns[i], nouns[j]);
                }
            }
            if (dist > maxDist) {
                maxDist = dist;
                outcast = nouns[i];
            }
        }

        return outcast;
    }

}
