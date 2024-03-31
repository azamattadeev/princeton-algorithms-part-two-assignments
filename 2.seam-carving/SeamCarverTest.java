/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeamCarverTest {
    private SeamCarver seamCarver;

    public static final String PATH_3x4 = "test_data/3x4.png";
    public static final String PATH_6x5 = "test_data/6x5.png";


    @Test
    public void sizeMethodsTest() {
        createSeamCarver(PATH_3x4);
        assertEquals(4, seamCarver.height(), "Height is wrong");
        assertEquals(3, seamCarver.width(), "Width is wrong");
    }

    @Test
    public void energy3x4Test() {
        createSeamCarver(PATH_3x4);
        assertEquals(228.53, seamCarver.energy(1, 1), 0.005,
                     "Energy of pixel (1, 1) is wrong");
        assertEquals(228.09, seamCarver.energy(1, 2), 0.005,
                     "Energy of pixel (1, 2) is wrong");
    }

    @Test
    public void energy6x5Test() {
        createSeamCarver(PATH_6x5);
        assertEquals(284.01, seamCarver.energy(3, 3), 0.005,
                     "Energy of pixel (3, 3) is wrong");
        assertEquals(133.07, seamCarver.energy(3, 2), 0.005,
                     "Energy of pixel (3, 2) is wrong");
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0", "0, 1", "0, 2", "0, 3",
            "1, 0", "1, 3",
            "2, 0", "2, 1", "2, 2", "2, 3"
    })
    public void energyBorderTest(int x, int y) {
        createSeamCarver(PATH_3x4);
        assertEquals(1000, seamCarver.energy(x, y), 0.005,
                     "Border pixel energy isn't equal 1000");
    }

    @ParameterizedTest
    @CsvSource({
            "6, 4", "3, 5", "33, 2", "2, 33", "7, 5",
            "-3, 3", "3, -3", "-2, -3", "-1, 3"
    })
    public void wrongCoordinatesEnergyTest(int x, int y) {
        createSeamCarver(PATH_6x5);
        assertThrows(IllegalArgumentException.class, () -> seamCarver.energy(x, y));
    }


    @Test
    public void findVerticalSeamTest() {
        createSeamCarver(PATH_6x5);

        int[] expected = { 4, 4, 3, 2, 2 };
        int[] result = seamCarver.findVerticalSeam();
        assertEquals(expected.length, result.length);

        for (int i = 0; i < result.length; i++) {
            if (i == 0 || i == result.length - 1) {
                assertTrue(Math.abs(expected[i] - result[i]) <= 1);
            }
            else {
                assertEquals(expected[i], result[i]);
            }
        }
    }

    @Test
    public void findHorizontalSeamTest() {
        createSeamCarver(PATH_6x5);

        int[] expected = { 2, 2, 1, 2, 1, 2 };
        int[] result = seamCarver.findHorizontalSeam();
        assertEquals(expected.length, result.length);

        for (int i = 0; i < result.length; i++) {
            if (i == 0 || i == result.length - 1) {
                assertTrue(Math.abs(expected[i] - result[i]) <= 1);
            }
            else {
                assertEquals(expected[i], result[i]);
            }
        }
    }

    @Test
    public void removeVerticalSeamTest() {
        createSeamCarver(PATH_6x5);
        int[] seam = { 4, 4, 3, 2, 2 };
        seamCarver.removeVerticalSeam(seam);
        Picture picture = seamCarver.picture();

        Color secondRowMovedPixel = new Color(142, 151, 142);
        Color fourthRowMovedPixel = new Color(158, 143, 79);
        assertEquals(secondRowMovedPixel, picture.get(4, 1));
        assertEquals(fourthRowMovedPixel, picture.get(2, 3));
        assertNotEquals(fourthRowMovedPixel, picture.get(3, 3));
    }

    @Test
    public void removeHorizontalSeamTest() {
        createSeamCarver(PATH_6x5);
        int[] seam = { 2, 2, 1, 2, 1, 2 };
        seamCarver.removeHorizontalSeam(seam);
        Picture picture = seamCarver.picture();

        Color secondColMovedPixel = new Color(187, 117, 183);
        Color fifthColMovedPixel = new Color(120, 105, 138);
        assertEquals(secondColMovedPixel, picture.get(1, 2));
        assertEquals(fifthColMovedPixel, picture.get(4, 1));
        assertNotEquals(secondColMovedPixel, picture.get(1, 3));
        assertNotEquals(fifthColMovedPixel, picture.get(4, 2));
    }

    @Test
    public void removeVerticalSeamWrongLengthTest() {
        createSeamCarver(PATH_6x5);
        assertThrows(IllegalArgumentException.class,
                     () -> seamCarver.removeVerticalSeam(new int[] { 4, 4, 3, 2 }));
        assertThrows(IllegalArgumentException.class,
                     () -> seamCarver.removeVerticalSeam(new int[] { 4, 4, 3, 2, 1, 2 }));
    }

    @Test
    public void removeHorizontalSeamWrongLengthTest() {
        createSeamCarver(PATH_6x5);
        assertThrows(IllegalArgumentException.class,
                     () -> seamCarver.removeHorizontalSeam(new int[] { 2, 2, 1, 2, 1, 2, 3 }));
        assertThrows(IllegalArgumentException.class,
                     () -> seamCarver.removeHorizontalSeam(new int[] { 2, 2, 1, 2, 1 }));
    }

    @Test
    public void removeSeamNullTest() {
        createSeamCarver(PATH_6x5);
        assertThrows(IllegalArgumentException.class, () -> seamCarver.removeHorizontalSeam(null));
        assertThrows(IllegalArgumentException.class, () -> seamCarver.removeVerticalSeam(null));
    }

    @Test
    public void removeDiffMoreOneSeamTest() {
        createSeamCarver(PATH_6x5);
        assertThrows(IllegalArgumentException.class,
                     () -> seamCarver.removeHorizontalSeam(new int[] { 2, 2, 4, 2, 1, 2 }));
        assertThrows(IllegalArgumentException.class,
                     () -> seamCarver.removeVerticalSeam(new int[] { 4, 4, 2, 2, 2 }));
    }

    @Test
    public void nullPictureConstructorTest() {
        assertThrows(IllegalArgumentException.class,
                     () -> new SeamCarver(null));
    }


    private void createSeamCarver(String path) {
        seamCarver = new SeamCarver(new Picture(path));
    }

}
