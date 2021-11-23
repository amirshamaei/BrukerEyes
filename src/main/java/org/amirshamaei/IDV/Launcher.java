package org.amirshamaei.IDV;

public class Launcher {
    public static void main(String[] args) {
//        createNoise();
//        Main.main(args);
    }

    private static void createNoise() {
        double[][] noiseArray = new double[50][80];
        double[][] noiseArray2 = new double[50][80];
        for (int x = 0; x < 80; x = x + 1) {
            for (int y = 0; y < 50; y = y + 1) {
                noiseArray[y][x] = (float) (((y) + (-x) * Math.sin(Math.PI * 0.1 * x)));
                noiseArray2[y][x] = (float) (((2 * y) + (-x) * Math.sin(Math.PI * 0.1 * x)));

            }
        }


        double[] xArrayy = new double[80];
        double[] yArrayy = new double[50];
        for (int x = 0; x < 80; x = x + 1) {
            xArrayy[x] = x;
        }
        for (int y = 0; y < 50; y = y + 1) {
            yArrayy[y] = y;
        }
        DataHolder.getInstance().dataFDi = noiseArray2;
        DataHolder.getInstance().dataFD = noiseArray;
        DataHolder.getInstance().xArrFD = xArrayy;
        DataHolder.getInstance().yArrFD = yArrayy;
        DataHolder.getInstance().dataFDFit = noiseArray2;
        DataHolder.getInstance().xArrTD = xArrayy;
        DataHolder.getInstance().yArrTD = yArrayy;
        DataHolder.getInstance().dataFDRes = noiseArray;
        DataHolder.getInstance().dataTDFit = noiseArray;
        DataHolder.getInstance().dataTDRes = noiseArray;
        DataHolder.getInstance().dataTD = noiseArray;

    }
}
