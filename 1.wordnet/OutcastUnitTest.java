/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OutcastUnitTest {
    private static Outcast outcast;

    @BeforeAll
    public static void init() {
        outcast = new Outcast(new WordNet("test_data/synsets.txt",
                                          "test_data/hypernyms.txt"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // first value is right answer
            "Dostoevsky, Dostoevsky, apple, pear, peach, banana, lemon",
            "Pushkin, Hemingway, Dumas, Vonnegut, Pushkin", // only Pushkin is a poet
            "Siamese_cat, German_shepherd, Doberman, bulldog, Siamese_cat",
            "stone, cow, dog, man, hawk, eagle, snake, stone"
    })
    public void outcastTest(@ConvertWith(CSVtoArray.class) String... words) {
        assertEquals(words[0], outcast.outcast(Arrays.copyOfRange(words, 1, words.length)));
    }

    public static class CSVtoArray extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws
                                                                     ArgumentConversionException {
            String s = (String) source;
            return s.split("\\s*,\\s*");
        }
    }

}
