import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;

public class SeamCarver {
    private Picture picture;
    private double[][] energyMap;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        nonNull(picture, "Given picture is null");
        this.picture = new Picture(picture);
        calculateEnergyMap();
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateXY(x, y);
        return energyMap[x][y];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        final int H = height();
        final int W = width();
        int[] seam = new int[W];

        if (W > 2) {
            double[][] distTo = new double[W][H];
            int[][] from = new int[W][H];
            for (int i = 1; i < W; i++) {
                Arrays.fill(distTo[i], Double.POSITIVE_INFINITY);
            }

            for (int i = 0; i < W - 1; i++) {
                for (int j = 0; j < H; j++) {
                    if (j > 0 && distTo[i][j] + energyMap[i + 1][j - 1] < distTo[i + 1][j - 1]) {
                        distTo[i + 1][j - 1] = distTo[i][j] + energyMap[i + 1][j - 1];
                        from[i + 1][j - 1] = j;
                    }
                    if (distTo[i][j] + energyMap[i + 1][j] < distTo[i + 1][j]) {
                        distTo[i + 1][j] = distTo[i][j] + energyMap[i + 1][j];
                        from[i + 1][j] = j;
                    }
                    if (j < H - 1 && distTo[i][j] + energyMap[i + 1][j + 1] < distTo[i + 1][j
                            + 1]) {
                        distTo[i + 1][j + 1] = distTo[i][j] + energyMap[i + 1][j + 1];
                        from[i + 1][j + 1] = j;
                    }
                }
            }

            double minDist = Integer.MAX_VALUE;
            int minJ = 0;
            for (int j = 1; j < H - 1; j++) {
                if (distTo[W - 1][j] < minDist) {
                    minDist = distTo[W - 1][j];
                    minJ = j;
                }
            }

            int fromJ = minJ;
            for (int i = W - 1; i >= 0; i--) {
                seam[i] = fromJ;
                fromJ = from[i][fromJ];
            }

        }

        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        final int H = height();
        final int W = width();
        int[] seam = new int[H];

        if (H > 2) {
            double[][] distTo = new double[W][H];
            int[][] from = new int[W][H];
            for (int i = 0; i < W; i++) {
                for (int j = 1; j < H; j++) {
                    distTo[i][j] = Double.POSITIVE_INFINITY;
                }
            }

            for (int j = 0; j < H - 1; j++) {
                for (int i = 0; i < W; i++) {
                    if (i > 0 && distTo[i][j] + energyMap[i - 1][j + 1] < distTo[i - 1][j + 1]) {
                        distTo[i - 1][j + 1] = distTo[i][j] + energyMap[i - 1][j + 1];
                        from[i - 1][j + 1] = i;
                    }
                    if (distTo[i][j] + energyMap[i][j + 1] < distTo[i][j + 1]) {
                        distTo[i][j + 1] = distTo[i][j] + energyMap[i][j + 1];
                        from[i][j + 1] = i;
                    }
                    if (i < W - 1
                            && distTo[i][j] + energyMap[i + 1][j + 1] < distTo[i + 1][j + 1]) {
                        distTo[i + 1][j + 1] = distTo[i][j] + energyMap[i + 1][j + 1];
                        from[i + 1][j + 1] = i;
                    }
                }
            }

            double minDist = Integer.MAX_VALUE;
            int minI = 0;
            for (int i = 1; i < W - 1; i++) {
                if (distTo[i][H - 1] < minDist) {
                    minDist = distTo[i][H - 1];
                    minI = i;
                }
            }

            int fromI = minI;
            for (int j = H - 1; j >= 0; j--) {
                seam[j] = fromI;
                fromI = from[fromI][j];
            }
        }

        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        nonNull(seam);
        validateSeam(seam, false);

        Picture newPicture = new Picture(width(), height() - 1);
        int writePointer = 0;
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (j != seam[i]) {
                    newPicture.setRGB(i, writePointer++, picture.getRGB(i, j));
                }
            }
            writePointer = 0;
        }
        this.picture = newPicture;
        calculateEnergyMap();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, true);

        Picture newPicture = new Picture(width() - 1, height());
        int writePointer = 0;
        for (int j = 0; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                if (i != seam[j]) {
                    newPicture.setRGB(writePointer++, j, picture.getRGB(i, j));
                }
            }
            writePointer = 0;
        }
        this.picture = newPicture;
        calculateEnergyMap();
    }

    private void calculateEnergyMap() {
        final int W = width();
        final int H = height();
        energyMap = new double[W][H];
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                if (i == 0 || j == 0 || i == W - 1 || j == H - 1) {
                    energyMap[i][j] = 1000;
                }
                else {
                    energyMap[i][j] = calculateEnergyPixel(i, j);
                }
            }
        }
    }

    private double calculateEnergyPixel(int x, int y) {
        int lRGB = picture.getRGB(x - 1, y);
        int rRGB = picture.getRGB(x + 1, y);
        int tRGB = picture.getRGB(x, y - 1);
        int bRGB = picture.getRGB(x, y + 1);

        int diffSum = sqr(((lRGB >> 16) & 0xFF) - ((rRGB >> 16) & 0xFF))
                + sqr(((lRGB >> 8) & 0xFF) - (rRGB >> 8 & 0xFF))
                + sqr((lRGB & 0xFF) - (rRGB & 0xFF))
                + sqr(((tRGB >> 16) & 0xFF) - ((bRGB >> 16) & 0xFF))
                + sqr(((tRGB >> 8) & 0xFF) - ((bRGB >> 8) & 0xFF))
                + sqr((tRGB & 0xFF) - (bRGB & 0xFF));

        return Math.sqrt(diffSum);
    }

    private int sqr(int x) {
        return x * x;
    }

    private void nonNull(Object object) {
        if (object == null) throw new IllegalArgumentException();
    }

    private void nonNull(Object object, String msg) {
        if (object == null) throw new IllegalArgumentException(msg);
    }

    private void validateXY(int x, int y) {
        validateX(x);
        validateY(y);
    }

    private void validateX(int x) {
        if (x < 0 || x >= width()) throw new IllegalArgumentException("X is out of the range");
    }

    private void validateY(int y) {
        if (y < 0 || y >= height()) throw new IllegalArgumentException("Y is out of the range");
    }

    private void validateSeam(int[] seam, boolean vertical) {
        nonNull(seam);

        if (seam.length != (vertical ? height() : width()))
            throw new IllegalArgumentException("Seam has wrong length");

        for (int i = 0; i < seam.length; i++) {
            if (vertical) {
                validateX(seam[i]);
            }
            else {
                validateY(seam[i]);
            }
            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException(
                        "Neighboring pixels of the seam differ by more than one unit");
            }
        }
    }

}