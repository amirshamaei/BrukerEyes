package org.amirshamaei.IDV;

public class DataHolder {
    private static DataHolder instance = new DataHolder();
    double[] xArrTD;
    double[] yArrTD;
    double[] xArrFD;
    double[] yArrFD;
    double[] xArrPPM;

    double[][] dataTD;
    double[][] dataTDFit;
    double[][] dataTDRes;

    double[][] dataFD;
    double[][] dataFDFit;
    double[][] dataFDRes;

    double[][] dataTDi;
    double[][] dataTDFiti;
    double[][] dataTDResi;

    double[][] dataFDi;
    double[][] dataFDFiti;
    double[][] dataFDResi;





    public static DataHolder getInstance() {
        return instance;
    }

    public DataHolder() {
    }

    public void setData(double[] xArrTD, double[] yArrTD, double[] xArrFD, double[] yArrFD, double[] xArrPPM, double[][] dataTD, double[][] dataTDFit, double[][] dataTDRes, double[][] dataFD, double[][] dataFDFit, double[][] dataFDRes, double[][] dataTDi, double[][] dataTDFiti, double[][] dataTDResi, double[][] dataFDi, double[][] dataFDFiti, double[][] dataFDResi) {
        this.xArrTD = xArrTD;
        this.yArrTD = yArrTD;
        this.xArrFD = xArrFD;
        this.yArrFD = yArrFD;
        this.xArrPPM = xArrPPM;
        this.dataTD = dataTD;
        this.dataTDFit = dataTDFit;
        this.dataTDRes = dataTDRes;
        this.dataFD = dataFD;
        this.dataFDFit = dataFDFit;
        this.dataFDRes = dataFDRes;
        this.dataTDi = dataTDi;
        this.dataTDFiti = dataTDFiti;
        this.dataTDResi = dataTDResi;
        this.dataFDi = dataFDi;
        this.dataFDFiti = dataFDFiti;
        this.dataFDResi = dataFDResi;
    }

    public double[] getxArrTD() {
        return xArrTD;
    }

    public void setxArrTD(double[] xArrTD) {
        this.xArrTD = xArrTD;
    }

    public double[] getyArrTD() {
        return yArrTD;
    }

    public void setyArrTD(double[] yArrTD) {
        this.yArrTD = yArrTD;
    }

    public double[] getxArrFD() {
        return xArrFD;
    }

    public void setxArrFD(double[] xArrFD) {
        this.xArrFD = xArrFD;
    }

    public double[] getyArrFD() {
        return yArrFD;
    }

    public void setyArrFD(double[] yArrFD) {
        this.yArrFD = yArrFD;
    }

    public double[] getxArrPPM() {
        return xArrPPM;
    }

    public void setxArrPPM(double[] xArrPPM) {
        this.xArrPPM = xArrPPM;
    }

    public double[][] getDataTD() {
        return dataTD;
    }

    public void setDataTD(double[][] dataTD) {
        this.dataTD = dataTD;
    }

    public double[][] getDataTDFit() {
        return dataTDFit;
    }

    public void setDataTDFit(double[][] dataTDFit) {
        this.dataTDFit = dataTDFit;
    }

    public double[][] getDataTDRes() {
        return dataTDRes;
    }

    public void setDataTDRes(double[][] dataTDRes) {
        this.dataTDRes = dataTDRes;
    }

    public double[][] getDataFD() {
        return dataFD;
    }

    public void setDataFD(double[][] dataFD) {
        this.dataFD = dataFD;
    }

    public double[][] getDataFDFit() {
        return dataFDFit;
    }

    public void setDataFDFit(double[][] dataFDFit) {
        this.dataFDFit = dataFDFit;
    }

    public double[][] getDataFDRes() {
        return dataFDRes;
    }

    public void setDataFDRes(double[][] dataFDRes) {
        this.dataFDRes = dataFDRes;
    }

    public double[][] getDataTDi() {
        return dataTDi;
    }

    public void setDataTDi(double[][] dataTDi) {
        this.dataTDi = dataTDi;
    }

    public double[][] getDataTDFiti() {
        return dataTDFiti;
    }

    public void setDataTDFiti(double[][] dataTDFiti) {
        this.dataTDFiti = dataTDFiti;
    }

    public double[][] getDataTDResi() {
        return dataTDResi;
    }

    public void setDataTDResi(double[][] dataTDResi) {
        this.dataTDResi = dataTDResi;
    }

    public double[][] getDataFDi() {
        return dataFDi;
    }

    public void setDataFDi(double[][] dataFDi) {
        this.dataFDi = dataFDi;
    }

    public double[][] getDataFDFiti() {
        return dataFDFiti;
    }

    public void setDataFDFiti(double[][] dataFDFiti) {
        this.dataFDFiti = dataFDFiti;
    }

    public double[][] getDataFDResi() {
        return dataFDResi;
    }

    public void setDataFDResi(double[][] dataFDResi) {
        this.dataFDResi = dataFDResi;
    }
}
