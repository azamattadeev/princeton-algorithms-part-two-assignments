import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class WordNetUnitTest {
    private static WordNet wordNet;

    @BeforeAll
    public static void init() {
        wordNet = new WordNet("test_data/synsets.txt", "test_data/hypernyms.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = { "apple", "person", "miracle", "misfire", "Tower_of_London" })
    public void correctNounsTest(String noun) {
        assertTrue(wordNet.isNoun(noun));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Jolie", "dfa33", "32123", "Pinochet", "Towel_of_London" })
    public void missingNounsTest(String noun) {
        assertFalse(wordNet.isNoun(noun));
    }

    @ParameterizedTest
    @CsvSource({
            "apple, peach, edible_fruit, 2",
            "Dostoevsky, Hemingway, writer, 2",
            "Kennedy, Tutsi, person, 9",
            "USS_Cole, B-52, military_vehicle, 6",
            "person, kindness, entity, 9",
    })
    public void sapAndDistanceTest(String nounA, String nounB,
                                   String expectedAncestorContains, int expectedDistance) {
        assertTrue(wordNet.sap(nounA, nounB).contains(expectedAncestorContains));
        assertEquals(expectedDistance, wordNet.distance(nounA, nounB));
    }

    @Test
    public void sapThrowsTest() {
        assertThrows(IllegalArgumentException.class, () -> wordNet.sap(null, "person"));
        assertThrows(IllegalArgumentException.class, () -> wordNet.sap("person", "21d32f4"));
    }

    @Test
    public void distanceThrowsTest() {
        assertThrows(IllegalArgumentException.class, () -> wordNet.distance("entity", null));
        assertThrows(IllegalArgumentException.class, () -> wordNet.distance("d32d234f", "cow"));
    }

}
