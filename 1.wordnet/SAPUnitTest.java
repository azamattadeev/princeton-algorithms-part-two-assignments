import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class SAPUnitTest {
    private Digraph simpleDigraph;
    private Digraph digraph1;
    private Digraph deadEndsGraph;
    private Digraph tournament;

    @BeforeEach
    public void initDiraphs() {
        simpleDigraph = new Digraph(new In("test_data/simpleDigraph.txt"));
        digraph1 = new Digraph(new In("test_data/digraph1.txt"));
        deadEndsGraph = new Digraph(new In("test_data/dead_ends_graph.txt"));

        tournament = new Digraph(new In("test_data/tournament.txt"));
    }

    @ParameterizedTest
    @CsvSource({
            "9, 10, 1, 4",
            "10, 9, 1, 4",
            "4, 9, 4, 1",
            "10, 8, 0, 4",
            "8, 7, 3, 2",
            "3, 8, 3, 1"
    })
    public void singleVerticesSimpleGraphTest(int v1, int v2, int expectedAncestor,
                                              int expectedLength) {
        SAP sap = new SAP(simpleDigraph);

        assertEquals(expectedAncestor, sap.ancestor(v1, v2),
                     "Ancestor isn't equal to the reference value");
        assertEquals(expectedLength, sap.length(v1, v2),
                     "The length between vertices isn't equal to the reference value");
    }

    @ParameterizedTest
    @CsvSource({
            "3, 11, 1, 4",
            "9, 12, 5, 3",
            "7, 2, 0, 4",
            "1, 6, -1, -1"
    })
    public void singleVerticesAncestorDigraph1Test(int v1, int v2, int expectedAncestor,
                                                   int expectedLength) {
        SAP sap = new SAP(digraph1);

        assertEquals(expectedAncestor, sap.ancestor(v1, v2),
                     "Ancestor isn't equal to the reference value");
        assertEquals(expectedLength, sap.length(v1, v2),
                     "The length between vertices isn't equal to the reference value");
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, -1, -1",
            "3, 6, 6, 1",
            "6, 3, 6, 1",
            "1, 3, 3 ,4"
    })
    public void singleVerticesDeadEndGraphTest(int v1, int v2, int expectedAncestor,
                                               int expectedLength) {
        SAP sap = new SAP(deadEndsGraph);

        assertEquals(expectedAncestor, sap.ancestor(v1, v2),
                     "Ancestor isn't equal to the reference value");
        assertEquals(expectedLength, sap.length(v1, v2),
                     "The length between vertices isn't equal to the reference value");
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2, 1",
            "4, 2, 4",
            "0, 3, 3",
            "3, 0, 3"
    })
    public void singleVerticesTournamentTest(int v1, int v2, int expectedAncestor) {
        SAP sap = new SAP(tournament);

        assertEquals(expectedAncestor, sap.ancestor(v1, v2),
                     "Ancestor isn't equal to the reference value");
        assertEquals(1, sap.length(v1, v2),
                     "The length between vertices isn't equal to the reference value");
    }

    @Test
    public void ancestorNullArgumentsTest() {
        SAP sap = new SAP(simpleDigraph);
        assertThrows(IllegalArgumentException.class,
                     () -> sap.ancestor(Arrays.asList(3, 4, 5), Arrays.asList(1, null)));
        assertThrows(IllegalArgumentException.class,
                     () -> sap.ancestor(null, Arrays.asList(3, 4, 5)));
    }

    @Test
    public void ancestorLengthArgumentsTest() {
        SAP sap = new SAP(simpleDigraph);
        assertThrows(IllegalArgumentException.class,
                     () -> sap.length(Arrays.asList(3, 4, 5), Arrays.asList(7, null)));
        assertThrows(IllegalArgumentException.class,
                     () -> sap.length(null, Arrays.asList(3, 4, 5)));
    }

    @Test
    public void ancestorWrongVertexIdTest() {
        SAP sap = new SAP(simpleDigraph);
        assertThrows(IllegalArgumentException.class,
                     () -> sap.ancestor(Arrays.asList(34444, 4, 5), Arrays.asList(3, 7)));
        assertThrows(IllegalArgumentException.class,
                     () -> sap.ancestor(3, -43));
    }

    @Test
    public void lengthWrongVertexIdTest() {
        SAP sap = new SAP(simpleDigraph);
        assertThrows(IllegalArgumentException.class,
                     () -> sap.length(Arrays.asList(34444, 4, 5), Arrays.asList(3, 7)));
        assertThrows(IllegalArgumentException.class,
                     () -> sap.length(3, -43));
    }

}
