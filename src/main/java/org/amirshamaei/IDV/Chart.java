package org.amirshamaei.IDV;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Chart {
    private final Rectangle zoomRect = new Rectangle();

    private final ContextMenu contextMenu;
    private NumberAxis xAxis = new NumberAxis(0,100,10);
    private NumberAxis yAxis = new NumberAxis(0,100,10);
    private StackPane chart = new StackPane();
    private LineChart linechart;

    public LineChart getLinechart() {
        return linechart;
    }

    private XYChart.Series series = new XYChart.Series();
    private int xAxislowerBound;
    private int xAxisUpperBound;
    private double pre_view_xL;
    private double pre_view_xU;
    private double pre_view_yL;
    private double pre_view_yU;
    private double ini_view_xL;
    private double ini_view_xU;
    private double ini_view_yL;
    private double ini_view_yU;
    private boolean dataTips;
    ArrayList<Label> labelsPosition = new ArrayList<>();
    private double minX;
    private double maxY;

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    private Window window;

    public Chart() {
        zoomRect.setManaged(false);
        zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
        linechart = new LineChart(xAxis, yAxis);
        linechart.getXAxis().setTickLength(1);
        chart.getChildren().add(linechart);
        linechart.setCreateSymbols(false);
        linechart.getData().add(series);

        chart.getChildren().add(zoomRect);
        linechart.setPrefSize(800,600);
        setUpZooming(zoomRect, linechart);
        linechart.setAnimated(false);
        chart.onKeyPressedProperty().bind(linechart.onKeyPressedProperty());
        linechart.requestFocus();

        contextMenu = new ContextMenu();
        MenuItem privious_view = new MenuItem("Previous View");
        MenuItem initial_view = new MenuItem("Initial View");
//        MenuItem ppmItem = new MenuItem("ppm");
//        MenuItem hzItem = new MenuItem("Hz");
        privious_view.setOnAction(event -> restoreView());
        initial_view.setOnAction(event -> intialView());
        MenuItem saveImg = new MenuItem("Save as an Image");
        saveImg.setOnAction(event -> {
            try
            {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter for text files
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(window);

                if (file != null) {
                    SnapshotParameters sp = new SnapshotParameters();
                    sp.setTransform(javafx.scene.transform.Transform.scale(4, 4));
                    WritableImage image2 = this.getChart().snapshot(sp, null);

                    BufferedImage bufImageARGB2 = SwingFXUtils.fromFXImage(image2, null);
                    BufferedImage bufImageRGB2 = new BufferedImage(bufImageARGB2.getWidth(), bufImageARGB2.getHeight(), BufferedImage.OPAQUE);

                    Graphics2D graphics2 = bufImageRGB2.createGraphics();
                    graphics2.drawImage(bufImageARGB2, 0, 0, null);
                    ImageIO.write(bufImageRGB2,"png", file);
                }

            }
            catch(Exception exception)
            {
                //code
            }
        });

        contextMenu.getItems().addAll(privious_view);
        contextMenu.getItems().addAll(initial_view);
//        contextMenu.getItems().addAll(ppmItem);
//        contextMenu.getItems().addAll(hzItem);
        contextMenu.getItems().add(saveImg);





    }
    private void removeTips() {
        labelsPosition.forEach(o -> chart.getChildren().removeAll(o));
        labelsPosition.clear();
    }
    private void intialView() {
        removeTips();
        xAxis.setLowerBound(ini_view_xL);
        xAxis.setUpperBound(ini_view_xU);
        yAxis.setLowerBound(ini_view_yL);
        yAxis.setUpperBound(ini_view_yU);
        xAxislowerBound = (int) xAxis.getLowerBound();
        xAxisUpperBound = (int) xAxis.getUpperBound();
//        Controller.j3D.zoomX(xAxislowerBound,xAxisUpperBound);
    }

    private void restoreView() {
        removeTips();
        xAxis.setLowerBound(pre_view_xL);
        xAxis.setUpperBound(pre_view_xU);
        yAxis.setLowerBound(pre_view_yL);
        yAxis.setUpperBound(pre_view_yU);
        xAxislowerBound = (int) xAxis.getLowerBound();
        xAxisUpperBound = (int) xAxis.getUpperBound();
//        Controller.j3D.zoomX(xAxislowerBound,xAxisUpperBound);
    }

    public void plot(double[] xArray, double[] data, int selected) {
        removeTips();
        xAxis.setLowerBound(Arrays.stream(xArray).min().getAsDouble());
        xAxis.setUpperBound(Arrays.stream(xArray).max().getAsDouble());
//        xAxis.setTickUnit(1);
        minX = Arrays.stream(data).min().getAsDouble() - Math.abs(Arrays.stream(data).min().getAsDouble());
        maxY = Arrays.stream(data).max().getAsDouble() + Math.abs(Arrays.stream(data).max().getAsDouble());
        yAxis.setLowerBound(minX);
        yAxis.setUpperBound(maxY);
        ini_view_xL = xAxis.getLowerBound();
        ini_view_xU = xAxis.getUpperBound();
        ini_view_yL = yAxis.getLowerBound();
        ini_view_yU = yAxis.getUpperBound();
        linechart.getData().remove(0, linechart.getData().size());
        XYChart.Series series = new XYChart.Series();
        for (int i = 0; i < xArray.length; i = i + 1) {
            series.getData().add(new XYChart.Data(xArray[i], data[i]));
        }
        linechart.getData().add(series);
        series.setName("signal:  " + (selected+1) + "   ");
    }
    public void plotHoldOn(double[] xArray, double[] data, int selected) {

        XYChart.Series series = new XYChart.Series();
        for (int i = 0; i < xArray.length; i = i + 1) {
            series.getData().add(new XYChart.Data(xArray[i], data[i]));
        }
        linechart.getData().add(series);
        series.setName("signal:  " + (selected+1));
        if (minX> Arrays.stream(data).min().getAsDouble()) {
            minX = Arrays.stream(data).min().getAsDouble() - Math.abs(Arrays.stream(data).min().getAsDouble());
        }
        if (maxY< Arrays.stream(data).max().getAsDouble()){
            maxY = Arrays.stream(data).max().getAsDouble() + Math.abs(Arrays.stream(data).max().getAsDouble());
        }

        yAxis.setLowerBound(minX);
        yAxis.setUpperBound(maxY);

    }


    private void setUpZooming(final Rectangle rect, final Node zoomingNode) {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();

        zoomingNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                    mouseAnchor.set(new Point2D(event.getX(), event.getY()));
                    rect.setWidth(0);
                    rect.setHeight(0);
                }
                 if (dataTips && event.isShiftDown() && (event.getButton() == MouseButton.PRIMARY) ) {
                        Label labelPosition = new Label();
                        Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                        double x = xAxis.sceneToLocal(mouseSceneCoords).getX() / xAxis.getScale() + xAxis.getLowerBound();
                        double y = yAxis.sceneToLocal(mouseSceneCoords).getY() / yAxis.getScale() + yAxis.getUpperBound();
                        labelPosition.setText(String.format("x: %.2f", x) + "\n" + String.format("y: %.2f", y));
                        labelPosition.setVisible(true);
                        labelPosition.setTranslateX(chart.sceneToLocal(mouseSceneCoords).getX()-chart.getWidth()/2);
                         labelPosition.setTranslateY(chart.sceneToLocal(mouseSceneCoords).getY()-chart.getHeight()/2);
                     labelPosition.setBackground(new Background(new BackgroundFill(Color.rgb(175,238,238, 0.3), new CornerRadii(0.1), new Insets(0))));
                     labelsPosition.add(labelPosition);
                        chart.getChildren().addAll(labelPosition);
                    }
                if (dataTips && !event.isShiftDown() && (event.getButton() == MouseButton.PRIMARY) ) {
                    removeTips();
                    Label labelPosition = new Label();
                    Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                    double x = xAxis.sceneToLocal(mouseSceneCoords).getX() / xAxis.getScale() + xAxis.getLowerBound();
                    double y = yAxis.sceneToLocal(mouseSceneCoords).getY() / yAxis.getScale() + yAxis.getUpperBound();
                    labelPosition.setText(String.format("x: %.2f", x) + "\n" + String.format("y: %.2f", y));
                    labelPosition.setVisible(true);
                    labelPosition.setTranslateX(chart.sceneToLocal(mouseSceneCoords).getX()-chart.getWidth()/2);
                    labelPosition.setTranslateY(chart.sceneToLocal(mouseSceneCoords).getY()-chart.getHeight()/2);
                    labelPosition.setBackground(new Background(new BackgroundFill(Color.rgb(175,238,238, 0.3), new CornerRadii(0.1), new Insets(0))));
                    labelsPosition.add(labelPosition);
                    chart.getChildren().addAll(labelPosition);
                }
                }

        });
        zoomingNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    double x = event.getX();
                    double y = event.getY();
                    rect.setX(Math.min(x, mouseAnchor.get().getX()));
                    rect.setY(Math.min(y, mouseAnchor.get().getY()));
                    rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
                    rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
                }
            }
        });
        zoomingNode.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                linechart.requestFocus();
                if ((event.getButton() == MouseButton.PRIMARY) && !event.isShiftDown()) {
                    if (rect.getWidth() > 5 && rect.getHeight() > 5) {
                        removeTips();
                        doZoom(rect, (LineChart<Number, Number>) zoomingNode);
                        zoomingNode.requestFocus();
                        removeTips();
                    }
                }
            }

        });
        zoomingNode.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                switch (event.getCode()) {
                    case RIGHT:
                        removeTips();
                        doShift((LineChart<Number, Number>) zoomingNode, 'r');
                        event.consume();
                        break;
                    case LEFT:
                        removeTips();
                        doShift((LineChart<Number, Number>) zoomingNode, 'l');
                        event.consume();
                        break;
                }
            }
        });
        zoomingNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

    }
    private void doShift(LineChart<Number, Number> chart, char orin) {
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();

        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        if (orin == 'r') {
            xAxis.setLowerBound(xAxis.getLowerBound() + 5);
            xAxis.setUpperBound(xAxis.getUpperBound() + 5);
            xAxislowerBound = (int) xAxis.getLowerBound();
            xAxisUpperBound = (int) xAxis.getUpperBound();
//            Controller.j3D.zoomX(xAxislowerBound,xAxisUpperBound);
        } else {
            xAxis.setLowerBound(xAxis.getLowerBound() - 5);
            xAxis.setUpperBound(xAxis.getUpperBound() - 5);
            xAxislowerBound = (int) xAxis.getLowerBound();
            xAxisUpperBound = (int) xAxis.getUpperBound();
//            Controller.j3D.zoomX(xAxislowerBound,xAxisUpperBound);
        }
    }
    private void doZoom(Rectangle zoomRect, LineChart<Number, Number> chart) {
        Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
        Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(), zoomRect.getY() + zoomRect.getHeight());
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        Point2D yAxisInScene = yAxis.localToScene(0, 0);
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        double xOffset = xAxisInScene.getX() - zoomRect.localToScene(0,0).getX();
        xOffset = zoomTopLeft.getX() - xOffset;
        double yOffset =  yAxisInScene.getY() - zoomRect.localToScene(0,0).getY();
        yOffset = zoomTopLeft.getY() - yOffset;
        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        Point2D x = zoomRect.localToScene(0,0);
        Point2D y = zoomRect.localToParent(0,0);
        Point2D z = chart.localToScene(0,0);
        pre_view_xL = xAxis.getLowerBound();
        pre_view_xU = xAxis.getUpperBound();
        pre_view_yL = yAxis.getLowerBound();
        pre_view_yU = yAxis.getUpperBound();

        xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);
        yAxis.setUpperBound(yAxis.getUpperBound() + yOffset / yAxisScale);
        yAxis.setLowerBound(yAxis.getUpperBound() + zoomRect.getHeight() / yAxisScale);
        xAxislowerBound = (int) xAxis.getLowerBound();
        xAxisUpperBound = (int) xAxis.getUpperBound();
//        Controller.j3D.zoomX(xAxislowerBound,xAxisUpperBound);
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);


    }
    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public void setDataTips(boolean dataTips) {
        if (dataTips) this.dataTips = true;
        else this.dataTips = false;
        removeTips();
    }

    public StackPane getChart() {
        return chart;
    }
}
