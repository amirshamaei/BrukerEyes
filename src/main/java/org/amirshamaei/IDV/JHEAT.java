package org.amirshamaei.IDV;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class JHEAT {
    private final GridPane frame ;
    private double[][] data;
    private double[] vGrid;
    private double[] hGrid;
    private ArrayList<ImageView> imageViewArrayList;
    private VBox root = new VBox();
    private GridPane rootBox = new GridPane();
    private int roll_width;
    private int roll_height;
    private int box_width;
    private int box_height;
    private double image_width;
    private double image_height;
    private int numbeOfResponses;
    private int numberOfPoints;
    private double max;
    private double min;
    private Text hLabel = new Text("");
    private Text vLabel = new Text("");
    private Group hGrids;
    private Group vGrids;
    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    private Window window;

    public void sethLabel(Text hLabel) {
        this.hLabel = hLabel;
    }

    public void setvLabel(Text vLabel) {
        this.vLabel = vLabel;
    }
    public ContextMenu getRightClickMenu() {
        return rightClickMenu;
    }

    private ContextMenu rightClickMenu = new ContextMenu();
    private Node sideBar;
    private boolean reStackPlot = false;
    private boolean reStackDisPlot = false;

    public JHEAT(double image_width, double image_height) {
        this.image_width = image_width;
        this.image_height = image_height;
        frame = new GridPane();
        frame.setHgap(5);
        frame.setVgap(5);
        frame.setPadding(new Insets(20,20,20,20));
        mouseTools();
    }

    private void mouseTools() {
        MenuItem saveImg = new MenuItem("Save as an Image");
        rightClickMenu = new ContextMenu();
        rightClickMenu.getItems().add(saveImg);
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
                    WritableImage image3 = this.getFrame().snapshot(sp, null);

                    BufferedImage bufImageARGB3 = SwingFXUtils.fromFXImage(image3, null);
                    BufferedImage bufImageRGB3 = new BufferedImage(bufImageARGB3.getWidth(), bufImageARGB3.getHeight(), BufferedImage.OPAQUE);

                    Graphics2D graphics3 = bufImageRGB3.createGraphics();
                    graphics3.drawImage(bufImageARGB3, 0, 0, null);
                    ImageIO.write(bufImageRGB3,"png", file);
                }

            }
            catch(Exception exception)
            {
                //code
            }
        });
        root.setOnContextMenuRequested(event -> rightClickMenu.show(root, event.getScreenX(), event.getScreenY()));
    }

    public void plot (double[][] data) {
        if(reStackPlot){
            this.getRoot().getChildren().removeAll(imageViewArrayList);
            frame.getChildren().removeAll(vGrids);
            frame.getChildren().removeAll(hGrids);
            frame.getChildren().removeAll(sideBar);

        }
        this.data = data;
        this.numbeOfResponses = data.length;
        this.numberOfPoints = data[0].length;
        this.roll_height = 1;
        this.roll_width = numberOfPoints;
        this.max = Arrays.stream(data).flatMapToDouble(Arrays::stream).max().getAsDouble();
        this.min = Arrays.stream(data).flatMapToDouble(Arrays::stream).min().getAsDouble();
        imageViewArrayList = new ArrayList();
        for(int i=0;i<numbeOfResponses;i++){
            Image colorScale = createRollImages(i);
            ImageView imageView = new ImageView(colorScale);
            imageView.setFitWidth(image_width);
            if ((image_height / numbeOfResponses)>1) {
                imageView.setFitHeight((image_height / numbeOfResponses));

            } else {

            }
            imageView.setId(String.valueOf(i));
            imageViewArrayList.add(imageView);
        }
        root.getChildren().addAll(imageViewArrayList);


        vLabel.setRotate(-90);
        if(!reStackPlot){
            frame.add(this.getRoot(),2,0,1,1);
            frame.add(hLabel,2,2,1,1);
            frame.add(vLabel,0,0,1,1);
            reStackPlot = false;
        }

        frame.setHalignment(hLabel, HPos.CENTER);
        sideBar = this.getColorBar();
        frame.add(sideBar,3,0);
        reStackPlot = true;
    }
    public void setGrid(double[] vGrid, double[] hGrid) {
        vGrids = createVGrid(vGrid);
        hGrids = createHGrid(hGrid);
        frame.setValignment(vGrids, VPos.CENTER);
        frame.setHalignment(hGrids, HPos.CENTER);
        frame.add(vGrids, 1,0,1,1);
        frame.add(hGrids, 2,1,1,1);
    }
    private Group createHGrid(double[] hGrid) {
        Group grp = new Group();
        if(hGrid.length<20) { 
        for(double value : hGrid) {
            Text text = new Text(String.format("%.1f", value));
            text.setRotate(90);
            double transX = value*(((image_width)/hGrid.length));
            text.setTranslateX(transX);
            grp.getChildren().add(text);
        } 
        } else {
            for(int i=0; i<hGrid.length;i+=50) {
                double value = hGrid[i];
                Text text = new Text(String.format("%.1f", value));
                text.setRotate(90);
                double transX = value*(((image_width)/(hGrid[hGrid.length-1] - hGrid[0])));
                text.setTranslateX(transX);
                grp.getChildren().add(text);
            }
        } 
        return grp;
    }

    private Group createVGrid(double[] vGrid) {
        Group grp = new Group();
        if (vGrid.length<50) {
            for(double value : vGrid) {
                Text text = new Text(String.format("%.1f", value));

                double transY = 0;
                if (image_height/vGrid.length > 1) {
                    transY = value*((image_height/vGrid.length));
                } else {
                    transY = value*(1);
                }
                text.setTranslateY(transY);
                grp.getChildren().add(text);
            }
        } else {
            for(int value=0; value<vGrid.length;value+=(int)(vGrid.length/10)) {
                Text text = new Text(String.format("%o", value));

                double transY = 0;
                if (image_height/vGrid.length > 1) {
                    transY = value*((image_height/vGrid.length));
                } else {
                    transY = value*(1);
                }
                text.setTranslateY(transY);
                grp.getChildren().add(text);
            }
        }
        return grp;
    }
    public void plotDiscreteData (double[][] data) {
        if(reStackDisPlot){
            this.getRootBox().getChildren().removeAll(imageViewArrayList);
            frame.getChildren().removeAll(sideBar);

        }
        this.data = data;
        this.numbeOfResponses = data.length;
        this.numberOfPoints = data[0].length;
        this.box_height = (int) (image_height / numbeOfResponses);
        this.box_width = (int) (image_width / numberOfPoints);
        this.max = Arrays.stream(data).flatMapToDouble(Arrays::stream).max().getAsDouble();
        this.min = Arrays.stream(data).flatMapToDouble(Arrays::stream).min().getAsDouble();
        imageViewArrayList = new ArrayList();
        for(int i=0;i<numbeOfResponses;i++){
            for (int j=0; j<numberOfPoints; j++) {
                Image colorScale = createBoxImages(i, j);
                ImageView imageView = new ImageView(colorScale);
//                imageView.setFitWidth(image_width);
                imageView.setId(String.valueOf(i * (numbeOfResponses+1) + j + 1));
                imageViewArrayList.add(imageView);
                rootBox.add(imageView,i,j);
            }
        }


        hLabel = new Text("h label");
        vLabel = new Text("v label");
        vLabel.setRotate(-90);
        frame.setHalignment(hLabel, HPos.CENTER);
        if(!reStackDisPlot){
            frame.add(rootBox,2,0,1,1);
            frame.add(hLabel,2,2,1,1);
            frame.add(vLabel,0,0,1,1);
            reStackPlot = false;
        }
        sideBar = this.getColorBar();

        frame.add(sideBar,3,0);
        reStackDisPlot = true;
    }
    private Image createBoxImages(int i, int j) {
        WritableImage image = new WritableImage(box_width, box_height);
        PixelWriter pixelWriter = image.getPixelWriter();

            double dataOfPoint = data[i][j] ;
            double value = (dataOfPoint-min)/(max-min);
            Color color = getColorForValue(value);
        for (int x=0; x<box_width; x++) {
            for (int y=0; y<box_height; y++) {
                pixelWriter.setColor(x, y, color);
            }
        }
        return image;
    }
    public void setDiscreteGrid(String[] vGrid, String[] hGrid) {
        Group vGrids = createDisVGrid(vGrid);
        Group hGrids = createDisHGrid(hGrid);
        frame.setValignment(vGrids, VPos.CENTER);
//        frame.setHalignment(hGrids, HPos.CENTER);
        frame.add(vGrids, 1,0,1,1);
        frame.add(hGrids, 2,1,1,1);
    }

    private Group createDisHGrid(String[] hGrid) {
        Group grp = new Group();

        for(int i = 0; i<hGrid.length; i++) {
            Text text = new Text(hGrid[i]);
            double transX = i*box_width;
            text.setTranslateX(transX);
            grp.getChildren().add(text);
        }
        return grp;
    }

    private Group createDisVGrid(String[] vGrid) {
        Group grp = new Group();
        for(int i = 0; i<vGrid.length; i++) {
            Text text = new Text(vGrid[i]);
            double transY = i*(box_height);
            text.setTranslateY(transY);
            grp.getChildren().add(text);

        }
        return grp;
    }

    private Image createRollImages(int i) {
        WritableImage image = new WritableImage(roll_width, roll_height);
        PixelWriter pixelWriter = image.getPixelWriter();
        for (int x=0; x<roll_width; x++) {
            double dataOfPoint = data[i][x] ;
            double value = (dataOfPoint-min)/(max-min);
            Color color = getColorForValue(value);
            for (int y=0; y<roll_height; y++) {
                pixelWriter.setColor(x, y, color);
            }
        }
        return image;
    }
    private Color getColorForValue(double value) {
        double hueValue = 255 * (1 - Mapper.getInstance().mapValue(value));
        if(Mapper.getInstance().isLogScale()) {
            hueValue = 255 * (1 - Math.log10(9 * Mapper.getInstance().mapValue(value) + 1));
        }
        return Color.hsb(hueValue, 1, 1);
    }
    public Node getColorBar() {
        return getColorBar(30,400);
    }
    public Node getColorBar(int colorbar_width, int colorbar_height) {
        WritableImage image = new WritableImage(colorbar_width, colorbar_height );
        PixelWriter pixelWriter = image.getPixelWriter();
        for (int y=0; y<colorbar_height; y++) {
            double value = 1-(((double) y)/colorbar_height);
            Color color = getColorForValue(value);
            for (int x=0; x<colorbar_width; x++) {
                pixelWriter.setColor(x, y, color);
            }
        }
        ImageView imageView = new ImageView(image);
        HBox colorBarFrame = new HBox();
        colorBarFrame.setAlignment(Pos.CENTER);
        colorBarFrame.getChildren().add(imageView);
        Group vGrid = colorBarGrid(colorbar_height);
        colorBarFrame.getChildren().add(vGrid);
        return colorBarFrame;
    }

    private Group colorBarGrid(double colorbar_height) {
        Group grp = new Group();
        double[] values = new double[]{min, max};
        for(double value : values) {
            Text text = new Text("  " + String.format("%5.1f", value));
            double transY =  (1- ((value-min)/(max-min))) * colorbar_height;
            text.setTranslateY(transY);
            grp.getChildren().add(text);
        }
        return grp;
    }

    public void setDisText(String string) {
         for (int i = 0 ; i < numbeOfResponses ; i++) {
            for (int j = 0; j < numberOfPoints; j++) {
                Text text = new Text(string + (i * numbeOfResponses + j));
                getRootBox().add(text, i, j);
                text.setWrappingWidth(box_width);
                text.setTextAlignment(TextAlignment.CENTER);

            }
        }
    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public ArrayList<ImageView> getImageViewArrayList() {
        return imageViewArrayList;
    }

    public void setImageViewArrayList(ArrayList<ImageView> imageViewArrayList) {
        this.imageViewArrayList = imageViewArrayList;
    }

    public VBox getRoot() {
        return root;
    }

    public int getRoll_width() {
        return roll_width;
    }

    public void setRoll_width(int roll_width) {
        this.roll_width = roll_width;
    }

    public int getRoll_height() {
        return roll_height;
    }

    public void setRoll_height(int roll_height) {
        this.roll_height = roll_height;
    }

    public double getImage_width() {
        return image_width;
    }

    public void setImage_width(double image_width) {
        this.image_width = image_width;
    }

    public double getImage_height() {
        return image_height;
    }

    public void setImage_height(double image_height) {
        this.image_height = image_height;
    }

    public double getMax() {
        return max;
    }

    public Node getSideBar() {
        return sideBar;
    }

    public double getMin() {
        return min;
    }

    public GridPane getFrame() {
        return frame;
    }

    public GridPane getRootBox() {
        return rootBox;
    }
}
