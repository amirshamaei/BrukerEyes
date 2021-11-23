package org.amirshamaei.IDV;

import com.ericbarnhill.niftijio.NiftiVolume;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.NifTiMRS.JsonExtention;
import org.NifTiMRS.NiftiMRS;
import org.controlsfx.control.RangeSlider;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    public static J3D j3D;
    @FXML
    Tab tab1;
    @FXML
    Tab tab2;
    @FXML
    AnchorPane anchorPane1;
    @FXML
    AnchorPane anchorPane2;
    @FXML
    BorderPane borderPane1;
    @FXML
    StackPane stackview_center_pane;
    @FXML
    ToggleButton logScale;
    @FXML
    ToggleButton datatips;
    @FXML
    TextArea messeageBar;
    @FXML
    RangeSlider rangeSlider;
    boolean holdon = false;
    //    @FXML
//    CheckComboBox dataChoicer;
    @FXML
    ChoiceBox dataChoicerTab2;
    @FXML
    ChoiceBox dataTypeTab2;
    @FXML
    TabPane mainTab;
    @FXML
    GridPane grid_center;
    @FXML
    RadioButton rainbow;
    @FXML
    Slider lineWidth;
    @FXML
    Slider opacity;
    @FXML
    RadioButton yz;
    @FXML
    RadioButton xz;
    @FXML
    CheckBox init;
    @FXML
    CheckBox datacheck;
    @FXML
    CheckBox fitcheck;
    @FXML
    CheckBox rescheck;
    @FXML
    CheckBox re;
    @FXML
    CheckBox im;

    @FXML
    CheckBox mag;
    @FXML
    CheckBox ph;

    @FXML
    ListView signalList;
    //    @FXML
//    CheckComboBox signalSelection;
//    @FXML
//    RangeSlider rangeSlider;
    @FXML
    ToolBar toolbar3d;
    @FXML
    Button opendata;
    @FXML
    Button openfit;
    @FXML
    TextArea json;
    @FXML
    Button aboutus;
    private Color color;
    private ArrayList selectedSignals = new ArrayList();
    private int max_slider;
    private int min_slider;
    private ArrayList<CheckBox> checklist;
    private JHEAT heatmap;
    private ObservableList<Object> signalsList;
    private boolean rescale = false;

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    private Window window;


    Chart chart = new Chart();
    ArrayList<PickResult> pickResults = new ArrayList<>();
    private int item_meta;
    private int item_param;
    private Integer colIndex = 0;
    private Integer rowIndex = 0;
    double[][] data = DataHolder.getInstance().dataFD;
    double[][] datai = DataHolder.getInstance().dataFDi;
    double[] xArray = DataHolder.getInstance().xArrFD;
    double[] yArray = DataHolder.getInstance().yArrFD;
    private boolean freqdomain = false;
    private boolean ppmunit = false;
    private boolean freqdomain2 = false;
    private boolean ppmunit2 = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        webView.getEngine().load("https://github.com/isi-nmr/IDV-Interrelated-2D-NMR-Datasets-Viewer/blob/standalone_dev/README.md");
//        webView.getEngine().setJavaScriptEnabled(true);
////        hi.setOnAction(e -> {
////            webView.getEngine().executeScript("myFunction()");
////        });





//        opendata.setOnAction(e -> {
//            try {
//                openfiledata();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        });
//        openfit.setOnAction(e -> {
//            try {
//                openfilefit();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        });
        checklist = new ArrayList<CheckBox>();
        checklist.add(datacheck);
        checklist.add(fitcheck);
        checklist.add(rescheck);
        checklist.add(re);
        checklist.add(im);
        checklist.add(mag);
        checklist.add(ph);
        int version = getVersion();
        heatmap = new JHEAT(700, 700);
        if (version > 9) {
            heatmap.setImage_height(600);
            heatmap.setImage_width(600);
            chart.getLinechart().setPrefSize(600, 400);
        }

        heatmap.sethLabel(new Text("frequency"));
        ScrollPane scpane = new ScrollPane();
        scpane.setContent(heatmap.getFrame());

        grid_center.add(scpane, 0, 0);

        plotChart(heatmap.getRoot());

        chart.getChart().setOnContextMenuRequested(event -> chart.getContextMenu().show(anchorPane2, event.getScreenX(), event.getScreenY()));
        grid_center.add(chart.getChart(), 1, 0);
        HBox.setHgrow(chart.getChart(), Priority.ALWAYS);
//

        //

        //

        //

        //                        grid_center.setGridLinesVisible(true);

        //                        heatmap.getFrame().setGridLinesVisible(true);

        //

        //

        //


        j3D = new J3D(400, 400, 400);
        j3D.strokewidth = lineWidth.valueProperty();
        j3D.opacity = opacity.valueProperty();
        j3D.setWindow(window);
        heatmap.setWindow(window);
        borderPane1.setCenter(j3D.getScene());
        toolbar3d.getItems().add(j3D.getMouseTip());
        j3D.getScene().heightProperty().bind(anchorPane1.heightProperty());
        j3D.getScene().widthProperty().bind(anchorPane1.widthProperty());
        j3D.getCube().requestFocus();

        tab2.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (((Tab) event.getSource()).isSelected()) {
//                    if (data != null)
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            heatmapplotter();
                        }
                    });

//                } else {
//
                }
            }
        });
        tab1.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (((Tab) event.getSource()).isSelected()) {
//                    if (data != null)
                    plotter();
                } else {
//                        j3D.getPlottedElements().getChildren().removeIf(o -> o instanceof Polyline);
                }
            }
        });

        signalList.setOnMouseClicked(event -> {
            if (signalList.getSelectionModel().isSelected(0)) {
                selectedSignals.clear();
                int max = yArray.length;
                if((yArray.length)>64) {
                    max = 64;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Only 64 signals can be displayed at a time");
                    Thread thread = new Thread(() -> {
                        try {
                            // Wait for 5 secs
                            Thread.sleep(2000);
                            if (alert.isShowing()) {
                                Platform.runLater(() -> alert.close());
                            }
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    });
                    thread.setDaemon(true);
                    thread.start();
                    Optional<ButtonType> result = alert.showAndWait();

                }
                for (int i = 0; i < max; i++) {
                    selectedSignals.add(i);
                }
                plotter();
            } else {
                if (signalList.getSelectionModel().getSelectedIndices().size()>64){
                    selectedSignals = (ArrayList) signalList.getSelectionModel().getSelectedIndices().stream().map(x -> x = (int) x - 1).collect(Collectors.toList());
                    if (selectedSignals.size()>64){
                        selectedSignals.subList(64,selectedSignals.size()).clear();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("Only 64 signals can be displayed at a time");
                        Thread thread = new Thread(() -> {
                            try {
                                // Wait for 5 secs
                                Thread.sleep(2000);
                                if (alert.isShowing()) {
                                    Platform.runLater(() -> alert.close());
                                }
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }
                        });
                        thread.setDaemon(true);
                        thread.start();
                        Optional<ButtonType> result = alert.showAndWait();
                    }
                }
                selectedSignals = (ArrayList) signalList.getSelectionModel().getSelectedIndices().stream().map(x -> x = (int) x - 1).collect(Collectors.toList());
                plotter();
            }

        });


        rangeSlider.setOnMouseReleased(event -> {
            max_slider = (int) rangeSlider.getHighValue();
            min_slider = (int) rangeSlider.getLowValue();
            plotter();
        });

        xz.setOnAction(e -> {
            yz.setSelected(false);
            if (xz.isSelected()) {
                j3D.getRotateX().setAngle(0);
                j3D.getRotateY().setAngle(0);
            } else {
                j3D.getRotateX().setAngle(20);
                j3D.getRotateY().setAngle(-45);
            }
        });
        yz.setOnAction(e -> {
            xz.setSelected(false);
            if (yz.isSelected()) {
                j3D.getRotateX().setAngle(0);
                j3D.getRotateY().setAngle(-89);
            } else {
                j3D.getRotateX().setAngle(20);
                j3D.getRotateY().setAngle(-45);
            }
        });

        datatips.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                chart.setDataTips(datatips.isSelected());
                messeageBar.setText("Hold Shift For Labeling More Points");
            }
        });
        logScale.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Mapper.getInstance().setLogScale(logScale.isSelected());
                heatmap.plot(data);
                heatmap.setGrid(yArray, xArray);
            }
        });
        j3D.setAxis_label_x(new Text(""));
        j3D.setAxis_label_z(new Text("frequency"));
        j3D.setAxis_label_y(new Text("amplitude"));

        Menu FDomain = new Menu("frequency domain");
        MenuItem hertz = new MenuItem("Hz");
        MenuItem ppm = new MenuItem("PPM");
        FDomain.getItems().addAll(hertz, ppm);
        hertz.setOnAction(event -> {
            if (!freqdomain) {
                freqdomain = true;
                ppmunit = false;
                xArray = DataHolder.getInstance().xArrFD;
                yArray = DataHolder.getInstance().yArrFD;
                rescale = true;
                plotter();
                j3D.setAxis_label_x(new Text(""));
                j3D.setAxis_label_z(new Text("frequency"));
                j3D.setAxis_label_y(new Text("amplitude"));
            }
        });

        ppm.setOnAction(event -> {
            if (!ppmunit) {
                ppmunit = true;
                freqdomain = false;
                xArray = DataHolder.getInstance().xArrPPM;
                yArray = DataHolder.getInstance().yArrFD;
                j3D.setAxis_label_x(new Text(""));
                j3D.setAxis_label_z(new Text("ppm"));
                j3D.setAxis_label_y(new Text("amplitude"));
                rescale = true;
                plotter();
            }
        });
        j3D.getRightClickMenu().getItems().add(FDomain);
        MenuItem TDomain = new MenuItem("time domain");
        TDomain.setOnAction(event -> {
            if (freqdomain || ppmunit) {
                freqdomain = false;
                ppmunit = false;
                xArray = DataHolder.getInstance().xArrTD;
                yArray = DataHolder.getInstance().yArrTD;
                rescale = true;
                plotter();
                j3D.setAxis_label_x(new Text(""));
                j3D.setAxis_label_z(new Text("time"));
                j3D.setAxis_label_y(new Text("amplitude"));
            }
        });
        j3D.getRightClickMenu().getItems().add(TDomain);

        rainbow.setOnAction(event -> {
            if (rainbow.isSelected()) {
                color = Color.TRANSPARENT;
                j3D.getPlottedElements().getChildren().removeIf(o -> o instanceof Polyline);
                j3D.plotSeries(xArray, yArray, data, false, color, selectedSignals, min_slider, max_slider);
            } else {
                plotter();
            }
        });

        Menu FDomain2 = new Menu("frequency domain");
        MenuItem hertz2 = new MenuItem("Hz");
        MenuItem ppm2 = new MenuItem("PPM");
        FDomain2.getItems().addAll(hertz2, ppm2);
        hertz2.setOnAction(event -> {
            if (!freqdomain2) {
                freqdomain2 = true;
                ppmunit2 = false;
                xArray = DataHolder.getInstance().xArrFD;
                yArray = DataHolder.getInstance().yArrFD;
                heatmapplotter();
            }
        });
        MenuItem PPMDomain2 = new MenuItem("ppm domain");
        ppm2.setOnAction(event -> {
            if (!ppmunit2) {
                ppmunit2 = true;
                freqdomain2 = false;
                xArray = DataHolder.getInstance().xArrPPM;
                yArray = DataHolder.getInstance().yArrFD;
                heatmapplotter();
            }
        });
        MenuItem TDomain2 = new MenuItem("time domain");
        TDomain2.setOnAction(event -> {
            if (freqdomain2 || ppmunit2) {
                freqdomain2 = false;
                ppmunit2 = false;
                xArray = DataHolder.getInstance().xArrTD;
                yArray = DataHolder.getInstance().yArrTD;
                heatmapplotter();
            }
        });


        heatmap.getRightClickMenu().getItems().add(FDomain2);
        heatmap.getRightClickMenu().getItems().add(TDomain2);


        datacheck.setOnAction(event -> {
            if (data != null) plotter();
        });
        datacheck.setSelected(true);
        fitcheck.setOnAction(event -> {
            if (data != null) plotter();
        });
        rescheck.setOnAction(event -> {
            if (data != null) plotter();
        });
        re.setSelected(true);
        re.setOnAction(event -> {
            if (data != null) plotter();
        });
        im.setOnAction(event -> {
            if (data != null) plotter();
        });
        mag.setOnAction(event -> {
            if (data != null) plotter();
        });
        ph.setOnAction(event -> {
            if (data != null) plotter();
        });
        dataChoicerTab2.setOnAction(event -> heatmapplotter());
        dataChoicerTab2.getSelectionModel().select(0);
        dataTypeTab2.setOnAction(event -> heatmapplotter());
        dataTypeTab2.getSelectionModel().select(0);
//        signalList.setOnMouseExited(event -> {
////
//            Transition animation = new Transition() {
//                {
//                    setCycleDuration(Duration.millis(200));
//                }
//
//                protected void interpolate(double frac) {
//                    signalList.setPrefWidth(signalList.getPrefWidth() - frac * signalList.getPrefWidth());
//                }
//
//            };
//
//            animation.play();
//        });
        signalList.setPrefWidth( 200);
//        signalList.setOnMouseEntered(event -> {
//            Transition animation = new Transition() {
//                {
//                    setCycleDuration(Duration.millis(200));
//                }
//
//                protected void interpolate(double frac) {
//                    signalList.setPrefWidth(frac * 200);
//                }
//
//            };
//
//            animation.play();
//        });
//        aboutus.setOnAction(e -> {
//            aboutUs();
//        });


    }
    public void aboutUs() {
        FXMLLoader aboutUsDialog = new FXMLLoader(Controller.class.getResource("aboutUs" + ".fxml"));
        Parent parent = null;
        try {
            parent = aboutUsDialog.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent, 600, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    private void initplotter() {
        signalsList = FXCollections.observableArrayList();
        signalsList.add("All");
        try {
            for (int i = 1; i <= yArray.length; i++) {
                signalsList.add("Signal " + i);
            }
        } catch (Exception e) {

        }
        signalList.getItems().setAll(signalsList);
        signalList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        try {
            if (yArray.length < 50) {
                for (int i = 0; i < yArray.length; i++) {
                    selectedSignals.add(i);
                }
            } else {
                for (int i = 0; i < 50; i++) {
                    selectedSignals.add(i);
                }
            }
        } catch (Exception e) {

        }
        rangeSlider.setMin(0);
        rangeSlider.setMax(xArray.length);
        rangeSlider.setHighValue(rangeSlider.getMax());
        max_slider = (int) rangeSlider.getHighValue();
        min_slider = (int) rangeSlider.getLowValue();
    }

    private void heatmapplotter() {
        int t1 = dataChoicerTab2.getSelectionModel().getSelectedIndex();
        int t2 = dataTypeTab2.getSelectionModel().getSelectedIndex();
        if (t1 == 0) {
            if (t2 == 0) {
                if (freqdomain2 || ppmunit)
                    data = DataHolder.getInstance().dataFD;
                else
                    data = DataHolder.getInstance().dataTD;
            }
            if (t2 == 1) {
                if (freqdomain2 || ppmunit)
                    data = DataHolder.getInstance().dataFDi;
                else
                    data = DataHolder.getInstance().dataTDi;
            }

            if (t2 == 2) {
                if (freqdomain2 || ppmunit) {
                    data = new double[DataHolder.getInstance().dataFD.length][DataHolder.getInstance().dataFD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFD.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFD[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataFD[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataFDi[i][j], 2));
                        }
                    }
                } else {
                    data = new double[DataHolder.getInstance().dataTD.length][DataHolder.getInstance().dataTD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTD.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTD[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataTD[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataTDi[i][j], 2));
                        }
                    }
                }
            }
            if (t2 == 3) {
                if (freqdomain2 || ppmunit) {
                    data = new double[DataHolder.getInstance().dataFD.length][DataHolder.getInstance().dataFD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFD.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFD[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataFDi[i][j]
                                    , DataHolder.getInstance().dataFD[i][j]);
                        }
                    }
                } else {
                    data = new double[DataHolder.getInstance().dataTD.length][DataHolder.getInstance().dataTD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTD.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTD[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataTDi[i][j]
                                    , DataHolder.getInstance().dataTD[i][j]);
                        }
                    }
                }
            }
        }
        if (t1 == 1) {
            if (t2 == 0) {
                if (freqdomain2 || ppmunit2)
                    data = DataHolder.getInstance().dataFDFit;
                else
                    data = DataHolder.getInstance().dataTDFit;
            }
            if (t2 == 1) {
                if (freqdomain2 || ppmunit2)
                    data = DataHolder.getInstance().dataFDFiti;
                else
                    data = DataHolder.getInstance().dataTDFiti;
            }
            if (t2 == 2) {
                if (freqdomain2 || ppmunit2) {
                    data = new double[DataHolder.getInstance().dataFDFit.length][DataHolder.getInstance().dataFDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDFit.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDFit[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataFDFit[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataFDFiti[i][j], 2));
                        }
                    }
                } else {
                    data = new double[DataHolder.getInstance().dataTDFit.length][DataHolder.getInstance().dataTDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDFit.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDFit[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataTDFit[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataTDFiti[i][j], 2));
                        }
                    }
                }
            }
            if (t2 == 3) {
                if (freqdomain2 || ppmunit2) {
                    data = new double[DataHolder.getInstance().dataFDFit.length][DataHolder.getInstance().dataFDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDFit.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDFit[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataFDFiti[i][j]
                                    , DataHolder.getInstance().dataFDFit[i][j]);
                        }
                    }
                } else {
                    data = new double[DataHolder.getInstance().dataTDFit.length][DataHolder.getInstance().dataTDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDFit.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDFit[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataTDFiti[i][j]
                                    , DataHolder.getInstance().dataTDFit[i][j]);
                        }
                    }
                }
            }
        }
        if (t1 == 2) {
            if (t2 == 0) {
                if (freqdomain2 || ppmunit2)
                    data = DataHolder.getInstance().dataFDRes;
                else
                    data = DataHolder.getInstance().dataTDRes;
            }
            if (t2 == 1) {
                if (freqdomain2 || ppmunit2)
                    data = DataHolder.getInstance().dataFDResi;
                else
                    data = DataHolder.getInstance().dataTDResi;
            }
            if (t2 == 2) {
                if (freqdomain2 || ppmunit2) {
                    data = new double[DataHolder.getInstance().dataFDRes.length][DataHolder.getInstance().dataFDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDRes.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDRes[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataFDRes[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataFDResi[i][j], 2));
                        }
                    }
                } else {
                    data = new double[DataHolder.getInstance().dataTDRes.length][DataHolder.getInstance().dataTDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDRes.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDRes[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataTDRes[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataTDResi[i][j], 2));
                        }
                    }
                }
            }
            if (t2 == 3) {
                if (freqdomain2 || ppmunit2) {
                    data = new double[DataHolder.getInstance().dataFDRes.length][DataHolder.getInstance().dataFDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDRes.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDRes[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataFDi[i][j]
                                    , DataHolder.getInstance().dataFDRes[i][j]);
                        }
                    }
                } else {
                    data = new double[DataHolder.getInstance().dataTDRes.length][DataHolder.getInstance().dataTDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDRes.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDRes[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataTDResi[i][j]
                                    , DataHolder.getInstance().dataTDRes[i][j]);
                        }
                    }
                }
            }
        }
        if (data != null) {
            heatmap.plot(data);
            heatmap.setGrid(yArray, xArray);
        } else {
            messeageBar.setText("Residue is null");
        }
    }


    private double[][][][] createParam() {
        double[][][][] noiseArray = new double[5][5][4][5];
        for (int vy = 0; vy < 5; vy++) {
            for (int vx = 0; vx < 5; vx++) {
                for (int x = 0; x < 5; x = x + 1) {
                    for (int y = 0; y < 4; y = y + 1) {
                        noiseArray[vx][vy][y][x] = (float) (10 * (vx + 1) * (vy + 1)) * (((40 - (y + 100 * vx)) * Math.sin(Math.PI * 0.1 * y) + (80 - x * (y + 100 * vx)) * Math.sin(Math.PI * 0.1 * x)) + 20);
                    }
                }
            }
        }
        return noiseArray;

    }


    public void selectVoxel(JHEAT root_voxel, JHEAT root_meta, double[][][][] data) {
        root_voxel.getRootBox().setOnMouseClicked(event -> {
            Node source = event.getPickResult().getIntersectedNode();
            colIndex = GridPane.getColumnIndex(source);
            rowIndex = GridPane.getRowIndex(source);
            if ((colIndex != null) && (rowIndex != null)) {
                root_meta.plotDiscreteData(data[colIndex][rowIndex]);
            }
        });
    }


    public void plotChart(VBox root) {

        root.setOnMousePressed(event -> {
            if (!event.isSecondaryButtonDown()) {
                if (event.isControlDown()) {
                    PickResult a = event.getPickResult();
                    Node node = a.getIntersectedNode();
                    chart.plotHoldOn(xArray, data[Integer.valueOf(node.getId())], Integer.valueOf(node.getId()));
//                    chart.getLinechart().getXAxis().setTickLength(10);
                } else {
                    PickResult a = event.getPickResult();
                    Node node = a.getIntersectedNode();
                    try {
                        chart.plot(xArray, data[Integer.valueOf(node.getId())], Integer.valueOf(node.getId()));
//                        chart.getLinechart().getXAxis().setTickLength(10);
                    } catch (NumberFormatException e) {

                    }
                    messeageBar.setText("Hold Ctrl For Selecting More Signals");
                }
            }
        });


    }

    public void plotter() {
        rainbow.setSelected(false);
        j3D.getPlottedElements().getChildren().removeIf(o -> o instanceof Polyline);
        int c = 0;
        for (CheckBox check : checklist) {
            if (check.isSelected())
                c++;
        }
        if (c > 2)
            holdon = true;
        else {
            holdon = false;
        }
//        if (re.isSelected() && im.isSelected())
//            holdon = true;
//        else {
//            holdon = false;
//        }

        if (datacheck.isSelected()) {
            if (freqdomain || ppmunit) {
                if (re.isSelected()) {
                    color = Color.RED;
                    data = DataHolder.getInstance().dataFD;
                    if (data != null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
                if (im.isSelected()) {
                    color = Color.GREEN;
                    data = DataHolder.getInstance().dataFDi;
                    if (data != null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
                if (mag.isSelected()) {
                    color = Color.VIOLET;
                    data = new double[DataHolder.getInstance().dataFD.length][DataHolder.getInstance().dataFD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFD.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFD[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataFD[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataFDi[i][j], 2));
                        }
                    }
                    if (data != null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
                if (ph.isSelected()) {
                    color = Color.DARKGREEN;
                    data = new double[DataHolder.getInstance().dataFD.length][DataHolder.getInstance().dataFD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFD.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFD[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataFDi[i][j]
                                    , DataHolder.getInstance().dataFD[i][j]);
                        }
                    }
                    if (data != null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
            } else {
                if (re.isSelected()) {
                    color = Color.RED;
                    data = DataHolder.getInstance().dataTD;
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);}
                }
                if (im.isSelected()) {
                    color = Color.GREEN;
                    data = DataHolder.getInstance().dataTDi;
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);}
                }
                if (mag.isSelected()) {
                    color = Color.VIOLET;
                    data = new double[DataHolder.getInstance().dataTD.length][DataHolder.getInstance().dataTD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTD.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTD[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataTD[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataTDi[i][j], 2));
                        }
                    }
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);}
                }
                if (ph.isSelected()) {
                    color = Color.DARKGREEN;
                    data = new double[DataHolder.getInstance().dataTD.length][DataHolder.getInstance().dataTD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTD.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTD[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataTDi[i][j]
                                    , DataHolder.getInstance().dataTD[i][j]);
                        }
                    }
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);}
                }
            }
        }
        if (fitcheck.isSelected()) {
            if (freqdomain || ppmunit) {
                if (re.isSelected()) {
                    color = Color.BLUE;
                    data = DataHolder.getInstance().dataFDFit;
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);}
                }
                if (im.isSelected()) {
                    color = Color.ORANGE;
                    data = DataHolder.getInstance().dataFDFiti;
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);}
                }
                if (mag.isSelected()) {
                    color = Color.SADDLEBROWN;
                    data = new double[DataHolder.getInstance().dataFDFit.length][DataHolder.getInstance().dataFDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDFit.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDFit[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataFDFit[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataFDFiti[i][j], 2));
                        }
                    }
                    if (data!=null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                    }
                if (ph.isSelected()) {
                    color = Color.DARKSALMON;
                    data = new double[DataHolder.getInstance().dataFDFit.length][DataHolder.getInstance().dataFDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDFit.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDFit[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataFDFiti[i][j]
                                    , DataHolder.getInstance().dataFDFit[i][j]);
                        }
                    }
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
            } else {
                if (re.isSelected()) {
                    color = Color.BLUE;
                    data = DataHolder.getInstance().dataTDFit;
                    if (data!=null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
                if (im.isSelected()) {
                    color = Color.ORANGE;
                    data = DataHolder.getInstance().dataTDFiti;
                    if (data!=null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
                if (mag.isSelected()) {
                    color = Color.SADDLEBROWN;
                    data = new double[DataHolder.getInstance().dataTDFit.length][DataHolder.getInstance().dataTDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDFit.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDFit[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataTDFit[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataTDFiti[i][j], 2));
                        }
                    }
                    if (data!=null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
                if (ph.isSelected()) {
                    color = Color.DARKSALMON;
                    data = new double[DataHolder.getInstance().dataTDFit.length][DataHolder.getInstance().dataTDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDFit.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDFit[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataTDFiti[i][j]
                                    , DataHolder.getInstance().dataTDFit[i][j]);
                        }
                    }
                    if (data!=null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
            }
        }
        if (rescheck.isSelected()) {

            if (freqdomain || ppmunit) {
                if (re.isSelected()) {
                    color = Color.BLACK;
                    data = DataHolder.getInstance().dataFDRes;
                    if (data!=null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
                    }
                }
                if (im.isSelected()) {
                    color = Color.YELLOW;
                    data = DataHolder.getInstance().dataFDResi;
                    if (data!=null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
                    }
                }
                if (mag.isSelected()) {
                    color = Color.BROWN;
                    data = new double[DataHolder.getInstance().dataFDRes.length][DataHolder.getInstance().dataFDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDRes.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDRes[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataFDRes[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataFDResi[i][j], 2));
                        }
                    }
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);}
                }
                if (ph.isSelected()) {
                    color = Color.CADETBLUE;
                    data = new double[DataHolder.getInstance().dataFDRes.length][DataHolder.getInstance().dataFDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDRes.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDRes[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataFDResi[i][j]
                                    , DataHolder.getInstance().dataFDRes[i][j]);
                        }
                    }
                    if (data!=null) {
                        if (rescale) {
                            j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                            rescale = false;
                        }
                        j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                    }
                }
            } else {
                if (re.isSelected()) {
                    color = Color.BLACK;
                    data = DataHolder.getInstance().dataTDRes;
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);}
                }
                if (im.isSelected()) {
                    color = Color.YELLOW;
                    data = DataHolder.getInstance().dataTDResi;
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);}
                }
                if (mag.isSelected()) {
                    color = Color.BROWN;
                    data = new double[DataHolder.getInstance().dataTDRes.length][DataHolder.getInstance().dataTDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDRes.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDRes[i].length; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataTDRes[i][j], 2)
                                    + Math.pow(DataHolder.getInstance().dataTDResi[i][j], 2));
                        }
                    }
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);}
                }
                if (ph.isSelected()) {
                    color = Color.CADETBLUE;
                    data = new double[DataHolder.getInstance().dataTDRes.length][DataHolder.getInstance().dataTDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDRes.length; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDRes[i].length; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataTDResi[i][j]
                                    , DataHolder.getInstance().dataTDRes[i][j]);
                        }
                    }
                    if (data!=null) {
                    if (rescale) {
                        j3D.rescale(xArray, yArray, data, min_slider, max_slider);
                        rescale = false;
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);}
                }
            }
        }
//        dataChoicer.getCheckModel().getCheckedItems().forEach(selected -> {
//            switch ((String) selected) {
//                case "Data":
//                        color = Color.RED;
//                        if (freqdomain || ppmunit) {
//                            data = DataHolder.getInstance().dataFD;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
//                        } else {
//                            data = DataHolder.getInstance().dataTD;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
//                        }
//                    break;
//                case "Fit":
//                        color = Color.BLUE;
//                        if (freqdomain || ppmunit) {
//                            data = DataHolder.getInstance().dataFDFit;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
//                        } else {
//                            data = DataHolder.getInstance().dataTDFit;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
//                        }
//                    break;
//                case "Residue":
//                        color = Color.BLACK;
//                        if (freqdomain || ppmunit) {
//                            data = DataHolder.getInstance().dataFDRes;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
//                        } else {
//                            data = DataHolder.getInstance().dataTDRes;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
//                        }
//                    break;
//            }
//            ;
//        });

    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public double[] getxArray() {
        return xArray;
    }

    public void setxArray(double[] xArray) {
        this.xArray = xArray;
    }

    public double[] getyArray() {
        return yArray;
    }

    public void setyArray(double[] yArray) {
        this.yArray = yArray;
    }

    private static int getVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }
    public void openfiledata(File file) throws IOException {
            new Thread() {
                @Override
                public void run() {
                    NiftiMRS niftiMrsObj = null;
                    try {
                        niftiMrsObj = NiftiMRS.read(file.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    NiftiVolume niftiObj = niftiMrsObj.getNifti();
                    JsonExtention jsonObj = niftiMrsObj.getJson();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            json.setText("Data\n"+ jsonObj.toString());
                        }
                    });
                    // I don't know how big the file is going to be, so I reserve some space.
                    long[] dims = niftiObj.getHeader2().dim;
                    int signalCounter = 0;
                    for (int o = 1; o < 8; o++)
                        if (dims[o] == 0)
                            dims[o] = 1;
                    DataHolder.getInstance().xArrTD = new double[(int) dims[4]];
                    DataHolder.getInstance().xArrFD = new double[(int) dims[4]];
                    for (int i = 0; i < dims[4]; i++) {
                        DataHolder.getInstance().xArrTD[i] = niftiObj.getHeader2().pixdim[4] * 1000 * i;
                        DataHolder.getInstance().xArrFD[i] = (-0.5 / niftiObj.getHeader2().pixdim[4]) + (i / (dims[4] * niftiObj.getHeader2().pixdim[4]));
                    }
                    double[][] signals = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsi = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    for (int i = 0; i < dims[1]; i++) {
                        for (int j = 0; j < dims[2]; j++) {
                            for (int k = 0; k < dims[3]; k++) {
                                for (int l = 0; l < dims[5]; l++) {
                                    for (int m = 0; m < dims[6]; m++) {
                                        for (int n = 0; n < dims[7]; n++) {
                                            for (int p = 0; p < dims[4]; p++) {
                                                signals[signalCounter][p] = niftiObj.getData().get(new int[]{i, j, k, p, l, m, n});
                                                signalsi[signalCounter][p] = niftiObj.getData().get(new int[]{i + 1, j, k, p, l, m, n});
                                            }
                                            signalCounter++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    DataHolder.getInstance().setDataTD(signals);
                    DataHolder.getInstance().setDataTDi(signalsi);
                    DataHolder.getInstance().yArrTD = new double[signalCounter];
                    DataHolder.getInstance().yArrFD = new double[signalCounter];
                    for (int i = 0; i < signalCounter; i++) {
                        DataHolder.getInstance().yArrTD[i] = i;
                        DataHolder.getInstance().yArrFD[i] = i;
                    }
                    Platform.runLater(() -> {
                        xArray = DataHolder.getInstance().xArrTD;
                        yArray = DataHolder.getInstance().yArrTD;
                        initplotter();
//                        plotter();
//                        heatmapplotter();
                    });
                    DoubleFFT_1D fft = new DoubleFFT_1D((int) dims[4]);
                    double[] signalfft = new double[(int) (dims[4] * 2)];
                    double[][] signalsf = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsfi = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    for (int i = 0; i < signalCounter; i++) {
                        for (int s = 0; s < dims[4]; s = s + 1) {
                            signalfft[2 * s] = signals[i][s];
                            signalfft[2 * s + 1] = signalsi[i][s];
                        }
                        fft.complexForward(signalfft);
                        for (int s = 0; s < dims[4]; s++) {
                            signalsf[i][s] = signalfft[(int) (((dims[4]) + (s * 2)) % (2 * dims[4]))];
                            signalsfi[i][s] = signalfft[(int) (((dims[4]) + (s * 2 + 1)) % (2 * dims[4]))];
                        }
                    }
                    DataHolder.getInstance().setDataFD(signalsf);
                    DataHolder.getInstance().setDataFDi(signalsfi);
                    tab1.setDisable(false);
                    tab2.setDisable(false);
                }
            }.start();
    }
    private void openfiledata() throws IOException {
        tab1.setDisable(true);
        tab2.setDisable(true);
        FileChooser dc = new FileChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = dc.showOpenDialog(null);
        if (file == null || !file.isFile()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not open file");
            alert.setContentText("The file is invalid.");
            alert.showAndWait();
        } else {
            new Thread() {
                @Override
                public void run() {
                    NiftiMRS niftiMrsObj = null;
                    try {
                        niftiMrsObj = NiftiMRS.read(file.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    NiftiVolume niftiObj = niftiMrsObj.getNifti();
                    JsonExtention jsonObj = niftiMrsObj.getJson();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            json.setText("Data\n"+ jsonObj.toString());
                        }
                    });
//            endOfFileEncountered = false;
//            // The following is arbitrary.
//            tempData.setStepTime(niftiObj.getHeader2().pixdim[4]*1000);
//            tempData.setNucleus(jsonObj.getResonantNucleus()[0]);
//            tempData.B0 = 0;
//            tempData.setTransmitterFrequency(jsonObj.getSpectrometerFrequency()[0]*10e5);
//            tempData.setEchoes(false);
                    // I don't know how big the file is going to be, so I reserve some space.
                    long[] dims = niftiObj.getHeader2().dim;
                    int signalCounter = 0;
                    for (int o = 1; o < 8; o++)
                        if (dims[o] == 0)
                            dims[o] = 1;
                    DataHolder.getInstance().xArrTD = new double[(int) dims[4]];
                    DataHolder.getInstance().xArrFD = new double[(int) dims[4]];
                    for (int i = 0; i < dims[4]; i++) {
                        DataHolder.getInstance().xArrTD[i] = niftiObj.getHeader2().pixdim[4] * 1000 * i;
                        DataHolder.getInstance().xArrFD[i] = (-0.5 / niftiObj.getHeader2().pixdim[4]) + (i / (dims[4] * niftiObj.getHeader2().pixdim[4]));
                    }
                    double[][] signals = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsi = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    for (int i = 0; i < dims[1]; i++) {
                        for (int j = 0; j < dims[2]; j++) {
                            for (int k = 0; k < dims[3]; k++) {
                                for (int l = 0; l < dims[5]; l++) {
                                    for (int m = 0; m < dims[6]; m++) {
                                        for (int n = 0; n < dims[7]; n++) {
                                            for (int p = 0; p < dims[4]; p++) {
                                                signals[signalCounter][p] = niftiObj.getData().get(new int[]{i, j, k, p, l, m, n});
                                                signalsi[signalCounter][p] = niftiObj.getData().get(new int[]{i + 1, j, k, p, l, m, n});
                                            }
                                            signalCounter++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    DataHolder.getInstance().setDataTD(signals);
                    DataHolder.getInstance().setDataTDi(signalsi);
                    DataHolder.getInstance().yArrTD = new double[signalCounter];
                    DataHolder.getInstance().yArrFD = new double[signalCounter];
                    for (int i = 0; i < signalCounter; i++) {
                        DataHolder.getInstance().yArrTD[i] = i;
                        DataHolder.getInstance().yArrFD[i] = i;
                    }
                    Platform.runLater(() -> {
                        xArray = DataHolder.getInstance().xArrTD;
                        yArray = DataHolder.getInstance().yArrTD;
                        initplotter();
//                        plotter();
//                        heatmapplotter();
                    });
                    DoubleFFT_1D fft = new DoubleFFT_1D((int) dims[4]);
                    double[] signalfft = new double[(int) (dims[4] * 2)];
                    double[][] signalsf = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsfi = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    for (int i = 0; i < signalCounter; i++) {
                        for (int s = 0; s < dims[4]; s = s + 1) {
                            signalfft[2 * s] = signals[i][s];
                            signalfft[2 * s + 1] = signalsi[i][s];
                        }
                        fft.complexForward(signalfft);
                        for (int s = 0; s < dims[4]; s++) {
                            signalsf[i][s] = signalfft[(int) (((dims[4]) + (s * 2)) % (2 * dims[4]))];
                            signalsfi[i][s] = signalfft[(int) (((dims[4]) + (s * 2 + 1)) % (2 * dims[4]))];
                        }
                    }
                    DataHolder.getInstance().setDataFD(signalsf);
                    DataHolder.getInstance().setDataFDi(signalsfi);
                    tab1.setDisable(false);
                    tab2.setDisable(false);
                }
            }.start();
        }
    }

    private void openfilefit() throws IOException {
        FileChooser dc = new FileChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = dc.showOpenDialog(null);
        if (file == null || !file.isFile()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not open file");
            alert.setContentText("The file is invalid.");
            alert.showAndWait();
        } else {
            new Thread() {
                @Override
                public void run() {
                    NiftiMRS niftiMrsObj = null;
                    try {
                        niftiMrsObj = NiftiMRS.read(file.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    NiftiVolume niftiObj = niftiMrsObj.getNifti();
                    JsonExtention jsonObj = niftiMrsObj.getJson();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            json.setText("\nfit\n"+jsonObj.toString());
                        }
                    });
//            endOfFileEncountered = false;
//            // The following is arbitrary.
//            tempData.setStepTime(niftiObj.getHeader2().pixdim[4]*1000);
//            tempData.setNucleus(jsonObj.getResonantNucleus()[0]);
//            tempData.B0 = 0;
//            tempData.setTransmitterFrequency(jsonObj.getSpectrometerFrequency()[0]*10e5);
//            tempData.setEchoes(false);
                    // I don't know how big the file is going to be, so I reserve some space.
                    long[] dims = niftiObj.getHeader2().dim;
                    int signalCounter = 0;
                    for (int o = 1; o < 8; o++)
                        if (dims[o] == 0)
                            dims[o] = 1;
                    DataHolder.getInstance().xArrTD = new double[(int) dims[4]];
                    DataHolder.getInstance().xArrFD = new double[(int) dims[4]];
                    for (int i = 0; i < dims[4]; i++) {
                        DataHolder.getInstance().xArrTD[i] = niftiObj.getHeader2().pixdim[4] * 1000 * i;
                        DataHolder.getInstance().xArrFD[i] = (-0.5 / niftiObj.getHeader2().pixdim[4]) + (i / (dims[4] * niftiObj.getHeader2().pixdim[4]));
                    }
                    double[][] signals = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsi = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsr = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsri = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    for (int i = 0; i < dims[1]; i++) {
                        for (int j = 0; j < dims[2]; j++) {
                            for (int k = 0; k < dims[3]; k++) {
                                for (int l = 0; l < dims[5]; l++) {
                                    for (int m = 0; m < dims[6]; m++) {
                                        for (int n = 0; n < dims[7]; n++) {
                                            for (int p = 0; p < dims[4]; p++) {
                                                signals[signalCounter][p] = niftiObj.getData().get(new int[]{i, j, k, p, l, m, n});
                                                signalsi[signalCounter][p] = niftiObj.getData().get(new int[]{i + 1, j, k, p, l, m, n});
                                                signalsr[signalCounter][p] = DataHolder.getInstance().getDataTD()[signalCounter][p] - signals[signalCounter][p];
                                                signalsri[signalCounter][p] = DataHolder.getInstance().getDataTDi()[signalCounter][p] - signals[signalCounter][p];
                                            }
                                            signalCounter++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    DataHolder.getInstance().setDataTDFit(signals);
                    DataHolder.getInstance().setDataTDFiti(signalsi);
                    DataHolder.getInstance().setDataTDRes(signalsr);
                    DataHolder.getInstance().setDataTDResi(signalsri);
                    DataHolder.getInstance().yArrTD = new double[signalCounter];
                    DataHolder.getInstance().yArrFD = new double[signalCounter];
                    for (int i = 0; i < signalCounter; i++) {
                        DataHolder.getInstance().yArrTD[i] = i;
                        DataHolder.getInstance().yArrFD[i] = i;
                    }
                    Platform.runLater(() -> {
                        xArray = DataHolder.getInstance().xArrTD;
                        yArray = DataHolder.getInstance().yArrTD;
                        initplotter();
//                        plotter();
//                        heatmapplotter();
                    });
                    DoubleFFT_1D fft = new DoubleFFT_1D((int) dims[4]);
                    double[] signalfft = new double[(int) (dims[4] * 2)];
                    double[][] signalsf = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsfi = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsrf = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    double[][] signalsrfi = new double[(int) (dims[1] * dims[2] * dims[3] * dims[5] * dims[6] * dims[7])][(int) dims[4]];
                    for (int i = 0; i < signalCounter; i++) {
                        for (int s = 0; s < dims[4]; s = s + 1) {
                            signalfft[2 * s] = signals[i][s];
                            signalfft[2 * s + 1] = signalsi[i][s];
                        }
                        fft.complexForward(signalfft);
                        for (int s = 0; s < dims[4]; s++) {
                            signalsf[i][s] = signalfft[(int) (((dims[4]) + (s * 2)) % (2 * dims[4]))];
                            signalsfi[i][s] = signalfft[(int) (((dims[4]) + (s * 2 + 1)) % (2 * dims[4]))];

                            signalsrf[i][s] = DataHolder.getInstance().getDataFD()[i][s] - signalsf[i][s];
                            signalsrfi[i][s] = DataHolder.getInstance().getDataFDi()[i][s] - signalsfi[i][s];
                        }
                    }
                    DataHolder.getInstance().setDataFDFit(signalsf);
                    DataHolder.getInstance().setDataFDFiti(signalsfi);
                    DataHolder.getInstance().setDataFDRes(signalsrf);
                    DataHolder.getInstance().setDataFDResi(signalsrfi);
                }
            }.start();
        }
    }
}
