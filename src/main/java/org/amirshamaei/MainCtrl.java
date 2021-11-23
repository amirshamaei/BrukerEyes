package org.amirshamaei;

import bruker2nii.Bruker2nii;
import bruker2nii.Bruker2nii1;
import bruker2nii.DataType;
import bruker_plugin_lib.Bruker;
import bruker_plugin_lib.DataBruker;
import bruker_plugin_lib.Jcampdx;
import bruker_plugin_lib.JcampdxData;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.amirshamaei.IDV.Controller;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.controlsfx.control.textfield.TextFields;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.event.ChangeEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class MainCtrl implements Initializable {
    @FXML
    AnchorPane imageview;
    @FXML
    MenuItem newdataset;
    @FXML
    MenuItem adddataset;
    @FXML
    MenuItem chngtxtviwr;
    @FXML
    Label leftStatus;
    @FXML
    AnchorPane anchorPane;
    @FXML
    TreeView folderView;
    @FXML
    TableView tableParams;
    @FXML
    TableColumn paramColumn;
    @FXML
    TableColumn valueColumn;
    @FXML
    Button addParam;
    @FXML
    Button removeParam;
    @FXML
    VBox mainVBox;
    @FXML
    TextField parameterTextField;
    @FXML
    MenuItem renameFiles;
    @FXML
    TextField pathField;
    @FXML
    ListView listView;
    @FXML
    ToggleButton forward;
    @FXML
    Button backward;
    @FXML
    Label userDisplay;
    @FXML
    ToggleButton like;
    @FXML
    AnchorPane likedpathpane;
    FxImageCanvas canvas = new FxImageCanvas();


    MenuItem convert2nii = new MenuItem("Convert to NIfTI");
    MenuItem delete = new MenuItem("Delete");
    MenuItem renameA = new MenuItem("Rename Automatically");
    MenuItem copy = new MenuItem("Copy");
    MenuItem rename = new MenuItem("Rename");
    MenuItem paste = new MenuItem("Paste");
    MenuItem openasniftiMRS = new MenuItem("Open As NIfTi-MRS");
    MenuItem openasmrs = new MenuItem("Open As Spectroscopy file");
    private File folderfile;
    TreeItem<String> mainRoot = new TreeItem<String>("Datasets");
    ObservableList<Map<String, Object>> items =
            FXCollections.<Map<String, Object>>observableArrayList();
    private ContextMenu contxtMenu;
    private LinkedList cd_copy = new LinkedList();
    private File[] listRoot;
    private File cd_temp;
    private JcampdxData studyList;
    private Stage bookmarkstage;
    private FXMLLoader bookmark;
    private ObservableList<LikedPath> likedpathObservable;
    private float[] canvas_realdata;
    private int[] canvas_visucoresize;
    private double[] canvas_visucoreextent;
    private FxImageCanvas CopyCanvas;
    private Bruker bruker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listRoot = File.listRoots();
            }
        });

        userDisplay.setText("Hi " + CurrentUser.getUser().username);
        try {
            if (CurrentUser.getUser().getCD() == null) {
                CurrentUser.getUser().setCd(new File(System.getProperty("user.dir")));
            }
        } catch (Exception e) {
            CurrentUser.getUser().setCd(new File(System.getProperty("user.dir")));
        }
        updatePathField();
        updateFolderViewer();
        backward.setOnAction(this::backwardPath);
        pathField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                try {
                    CurrentUser.getUser().setCd(new File(pathField.getText()));
                    updatePathField();
                    updateFolderViewer();
                } catch (Exception exception) {

                }
            }
        });
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        contxtMenu = new ContextMenu();
        contxtMenu.getItems().add(convert2nii);
        contxtMenu.getItems().add(renameA);
        contxtMenu.getItems().add(delete);
        contxtMenu.getItems().add(rename);
        contxtMenu.getItems().add(copy);
        contxtMenu.getItems().add(paste);
        contxtMenu.getItems().add(openasniftiMRS);
//        contxtMenu.getItems().add(openasmrs);



        listView.setContextMenu(contxtMenu);
        listView.setFixedCellSize(60);
        convert2nii.setOnAction(this::convert2nii);

        openasniftiMRS.setOnAction(e -> {
            try {
                openIDV();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        openasmrs.setOnAction(e -> {
            try {
                openIDV();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        delete.setOnAction(e -> {
            cd_temp = new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
            cd_temp.delete();
            updateFolderViewer();
        });
        renameA.setOnAction(this::RenameFiles);
        rename.setOnAction(actionEvent -> {
            ListContainer selectedItem = (ListContainer) listView.getSelectionModel().getSelectedItem();
            cd_temp = new File(CurrentUser.getUser().getCD().getAbsoluteFile(), selectedItem.getLabel().getText());
            String old_name = (selectedItem).getLabel().getText();
            (selectedItem).getLabel().setText("");
            TextField temp_tf = new TextField("");
            (selectedItem).getChildren().add(temp_tf);
            temp_tf.requestFocus();
            temp_tf.setOnKeyReleased(e -> {
                if (e.getCode() == KeyCode.ENTER && !temp_tf.getText().isEmpty()) {
                    (selectedItem).getLabel().setText(temp_tf.getText());
                    cd_temp.renameTo(new File(CurrentUser.getUser().getCD().getAbsoluteFile(), (selectedItem).getLabel().getText()));
                    (selectedItem).getChildren().remove(temp_tf);
                } else if (e.getCode() == KeyCode.ENTER && temp_tf.getText().isEmpty()) {
                    (selectedItem).getChildren().remove(temp_tf);
                    (selectedItem).getLabel().setText(old_name);
                }
            });
//            listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent mouseEvent) {
//                    (selectedItem).getChildren().remove(temp_tf);
//                    (selectedItem).getLabel().setText(old_name);
//                }
//            });

//            cd_temp.renameTo(new File)
        });
        copy.setOnAction(actionEvent -> {
            listView.getSelectionModel().getSelectedItems().forEach(e ->
                    cd_copy.add(new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) e).getLabel().getText()))
            );
        });
        paste.setOnAction(actionEvent -> {
            Platform.runLater(new Runnable() {
                public void run() {
                    cd_copy.forEach(file -> {
                        if (((File) file).isDirectory()) {
                            try {
                                FileUtils.copyDirectoryToDirectory((File) file, CurrentUser.getUser().getCD());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (((File) file).isFile()) {
                            try {
                                FileUtils.copyFileToDirectory((File) file, CurrentUser.getUser().getCD());
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        updateFolderViewer();
                    });
                }
            });
        });


        listView.setOnKeyReleased(keyBoardEvent -> {
            if (((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.brukerDirectory)) {
                cd_temp = new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
                setParamsTable(cd_temp);
            }
            if (keyBoardEvent.getCode() == KeyCode.ENTER) {
                if (((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.directory)
                        || ((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.brukerDirectory)) {
                    String temp = null;
                    try {
                        temp = CurrentUser.getUser().getCD().getPath();
                    } catch (Exception e) {
                    }
                    CurrentUser.getUser().setCd(new File(temp, ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText()));
                    updateFolderViewer();
                    updatePathField();
                } else if (((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.text)) {
                    try {
//                        CurrentUser.getUser().setCd(new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText()));
                        openfile(((ListContainer) listView.getSelectionModel().getSelectedItem()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        listView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                if (((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.brukerDirectory)) {
                    cd_temp = new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
                    setParamsTable(cd_temp);
                } else if (((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.twodseq)) {
                    if (imageview.getChildren().isEmpty()) {

//                        imageview.setMinHeight(300);
                            Transition animation = new Transition() {
                                {
                                    setCycleDuration(Duration.millis(500));
                                }

                                protected void interpolate(double frac) {
                                    imageview.setMinHeight(300 * frac);
                                }

                            };

                            animation.play();


                        ProgressIndicator progressIndicator = new ProgressIndicator();
                        progressIndicator.translateXProperty().bind(imageview.widthProperty().divide(2.2));
                        progressIndicator.translateYProperty().bind(imageview.heightProperty().divide(2.2));
                        imageview.getChildren().add(progressIndicator);







                        Task<Void> applyTask = new Task<Void>() {

                            @Override
                            protected Void call() throws Exception {

//                                Thread.sleep(10000);

                                File temp = null;
                                try {
                                    temp = new File(CurrentUser.getUser().getCD().getPath(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
                                } catch (Exception e) {
                                }
                                bruker = getBruker(temp);
                                plot2dseqOnCanvas(temp, bruker);

                                Platform.runLater(() -> {
                                    canvas.repaint();
                                });
                                return null;
                            }
                        };
                        applyTask.setOnSucceeded(e -> {
                            imageview.getChildren().remove(progressIndicator);
                            imageview.getChildren().add(canvas);
                        });
                        applyTask.setOnFailed(e -> {
                            imageview.getChildren().remove(progressIndicator);
                            Label label = new Label("Problem in Loading");
                            imageview.getChildren().add(label);
                            canvas.setImage(null);
                        });

                        new Thread(applyTask, "Apply thread").start();


                    }

                }
            } else if (mouseEvent.getClickCount() == 2) {
                if (((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.directory)
                        || ((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.brukerDirectory)) {
                    String temp = null;
                    try {
                        temp = CurrentUser.getUser().getCD().getPath();
                    } catch (Exception e) {
                    }
                    CurrentUser.getUser().setCd(new File(temp, ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText()));
                    updateFolderViewer();
                    updatePathField();
                } else if (((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.text)
                        || ((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.twodseq)) {
                    try {
//                        CurrentUser.getUser().setCd(new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText()));
                        openfile(((ListContainer) listView.getSelectionModel().getSelectedItem()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
//        addParam.setOnAction(this::AddParam);
        removeParam.setOnAction(this::RemoveParam);
        parameterTextField.setOnMouseClicked(e -> getParametersforPrediction(parameterTextField));
        parameterTextField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                AddParam(keyEvent);
            }
        });
        bookmarkstage();
        likedpathObservable = FXCollections.observableArrayList(CurrentUser.getUser().likedPaths);
        ((BookmarkCtrlr) bookmark.getController()).likedlist.setItems(likedpathObservable);
        ((BookmarkCtrlr) bookmark.getController()).likedlist.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        like.setOnAction(actionEvent -> {
            if (like.isSelected()) {
                CurrentUser.getUser().getLikedPaths().add(new LikedPath(pathField.getText()));
                likedpathObservable.add(new LikedPath(pathField.getText()));
                openLikedList(actionEvent);
            } else {
                CurrentUser.getUser().getLikedPaths().removeIf(o -> ((LikedPath) o).getPath().equals(pathField.getText()));
                likedpathObservable.removeIf(o -> ((LikedPath) o).getPath().equals(pathField.getText()));
            }
        });

        forward.setOnAction(this::openLikedList);
        Nd4j.create(1, 1);


//        folderView.setRoot(mainRoot);
//        newdataset.setOnAction(this::NewDataset);
//        adddataset.setOnAction(this::AddDataset);
//        folderView.setOnMouseClicked(this::folderViewClicked);


//        renameFiles.setOnAction(this::RenameFiles);
//
//        chngtxtviwr.setOnAction(this::Chngtxtviwr);
//        contxtMenu = new ContextMenu();
//        contxtMenu.getItems().add(convert2nii);
//        contxtMenu.getItems().add(delete);
//        folderView.setContextMenu(contxtMenu);
//        convert2nii.setOnAction(this::convert2nii);
//        delete.setOnAction(e->{
//            mainRoot.getChildren().remove(folderView.getSelectionModel().getSelectedItem());
//            CurrentUser.getUser().datasets.remove(folderView.getSelectionModel().getSelectedItem());
//            CurrentUser.getUser().studyLists.remove(folderView.getSelectionModel().getSelectedItem());
//        });
//
    }
    private Bruker getBruker(File temp) {
        Bruker bruker = new Bruker();
        bruker.setPath(temp.toPath());
        return bruker;
    }
    private void plot2dseqOnCanvas(File temp, Bruker bruker) {
//        Bruker bruker = new Bruker();
//        bruker.setPath(temp.toPath());
        DataBruker data = bruker.getData();
        canvas_realdata = data.getRealData();
        canvas_visucoresize = bruker.getJcampdx().getVisu_pars().getINDArray("VisuCoreSize").toIntVector();
        canvas_visucoreextent = bruker.getJcampdx().getVisu_pars().getINDArray("VisuCoreExtent").toDoubleVector();

        WritableImage image = new WritableImage(canvas_visucoresize[0], canvas_visucoresize[1]);
        PixelWriter pixelWriter = image.getPixelWriter();
        Float max = Collections.max(Arrays.asList(ArrayUtils.toObject(canvas_realdata)));
        for (int y = 0; y < canvas_visucoresize[0]; y++) {
            for (int x = 0; x < canvas_visucoresize[1]; x++) {
                double dataOfPoint = canvas_realdata[x + (canvas_visucoresize[1] * y)];
                Color color = Color.gray(dataOfPoint / max);
                pixelWriter.setColor(x, y, color);
            }
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(canvas_visucoreextent[1]);
        imageView.setFitHeight(canvas_visucoreextent[0]);
//                        imageView.fitHeightProperty().bind( ctrler.getImagePane().heightProperty());
//                        imageView.fitWidthProperty().bind( ctrler.getImagePane().widthProperty());
        Platform.runLater(()->{
            canvas.heightProperty().bind(imageview.heightProperty());
            canvas.widthProperty().bind(imageview.widthProperty());
            canvas.setImage(imageView.getImage());
            leftStatus.setText("file is loaded");
            canvas.requestFocus();
        });

    }

    private void replot2dseqOnCanvas(int i) {
        WritableImage image = new WritableImage(canvas_visucoresize[0], canvas_visucoresize[1]);
        PixelWriter pixelWriter = image.getPixelWriter();
        Float max = Collections.max(Arrays.asList(ArrayUtils.toObject(canvas_realdata)));
        for (int y = 0; y < canvas_visucoresize[0]; y++) {
            for (int x = 0; x < canvas_visucoresize[1]; x++) {
                double dataOfPoint = canvas_realdata[x + (canvas_visucoresize[1] * y) + i*(canvas_visucoresize[1] * canvas_visucoresize[0])];
                Color color = Color.gray(dataOfPoint / max);
                pixelWriter.setColor(x, y, color);
            }
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(canvas_visucoreextent[1]);
        imageView.setFitHeight(canvas_visucoreextent[0]);
//                        imageView.fitHeightProperty().bind( ctrler.getImagePane().heightProperty());
//                        imageView.fitWidthProperty().bind( ctrler.getImagePane().widthProperty());
        Platform.runLater(()->{
            CopyCanvas.setImage(imageView.getImage());
            CopyCanvas.getCamera().setZoom(2);
            leftStatus.setText("file is loaded");
            CopyCanvas.requestFocus();
            CopyCanvas.repaint();
        });

    }

    private void openLikedList(ActionEvent actionEvent) {
        bookmarkstage.show();
        bookmarkstage.toFront();
        BookmarkCtrlr bookmarkCtrlr = (BookmarkCtrlr) bookmark.getController();

        bookmarkCtrlr.savelikedpath.setOnAction(actionEvent1 -> {
            try {
                ((LikedPath) bookmarkCtrlr.likedlist.getSelectionModel().getSelectedItem()).setName(bookmarkCtrlr.namelikedpath.getText());
            } catch (Exception e) {

            }
            try {
                ((LikedPath) bookmarkCtrlr.likedlist.getSelectionModel().getSelectedItem()).setDescp(bookmarkCtrlr.deslikedpath.getText());
            } catch (Exception e) {

            }
            try {
                CurrentUser.getInstance().saveUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bookmarkstage.close();
        });
        bookmarkCtrlr.cacellikedpath.setOnAction(actionEvent1 -> {
            bookmarkstage.close();
        });
        bookmarkCtrlr.likedlist.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                bookmarkCtrlr.deslikedpath.setText(((LikedPath) bookmarkCtrlr.likedlist.getSelectionModel().getSelectedItem()).getDescp());
                bookmarkCtrlr.namelikedpath.setText(((LikedPath) bookmarkCtrlr.likedlist.getSelectionModel().getSelectedItem()).getName());
            } else if (mouseEvent.getClickCount() == 2) {
                CurrentUser.getUser().setCd(new File((((LikedPath) bookmarkCtrlr.likedlist.getSelectionModel().getSelectedItem()).toString())));
                updatePathField();
                updateFolderViewer();
            }
        });
    }


    private void bookmarkstage() {
        bookmark = new FXMLLoader(App.class.getResource("bookmark" + ".fxml"));
        Parent parent = null;
        try {
            parent = bookmark.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent, 700, 350);
        bookmarkstage = new Stage();
        bookmarkstage.initStyle(StageStyle.UNDECORATED);
        bookmarkstage.setScene(scene);
    }

    private void backwardPath(ActionEvent actionEvent) {
        File parentFile = CurrentUser.getUser().getCD().getParentFile();
        if (parentFile != null) {
            CurrentUser.getUser().setCd(CurrentUser.getUser().getCD().getParentFile());
            updateFolderViewer();
            updatePathField();
        } else {
            listView.getItems().clear();
            for (File file : listRoot) {
                ListContainer listContainer = new ListContainer(file);
                listView.getItems().add(listContainer);
            }
            CurrentUser.getUser().setCd(new File(""));
            pathField.setText("root");
        }
        Transition animation = new Transition() {
            {
                setCycleDuration(Duration.millis(500));
            }

            protected void interpolate(double frac) {
                imageview.setMaxHeight(300 - frac * 300);
            }

        };
        animation.play();

        imageview.getChildren().clear();
        imageview.setMinHeight(0);
    }

    private void updateFolderViewer() {
        listView.getItems().clear();
        try {
            for (File file : CurrentUser.getUser().getCD().listFiles()) {
                ListContainer listContainer = new ListContainer(file);
                listView.getItems().add(listContainer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePathField() {
        pathField.setText(CurrentUser.getUser().getCD().getAbsolutePath());
        if (CurrentUser.getUser().getLikedPaths().stream().anyMatch(o -> o.toString().equals(CurrentUser.getUser().getCD().getAbsolutePath()))) {
            like.setSelected(true);
        } else {
            like.setSelected(false);
        }
        try {
            CurrentUser.getInstance().saveUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convert2nii(ActionEvent actionEvent) {
        ListContainer selected = null;
        selected = (ListContainer) listView.getSelectionModel().getSelectedItem();
        if (selected.getType().equals(FileType.fid) || selected.getType().equals(FileType.twodseq)) {
            FXMLLoader convertorDialog = new FXMLLoader(App.class.getResource("convertor" + ".fxml"));
            Parent parent = null;
            try {
                parent = convertorDialog.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(parent, 416, 400);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            ConvertorCtrlr cntrl = convertorDialog.getController();
            ObservableList types = FXCollections.observableArrayList();

            types.add(DataType.MRSI);
            types.add(DataType.MRS);
            types.add(DataType.IMAGE);
            cntrl.dataType.setItems(types);
            cntrl.dataType.getSelectionModel().select(2);

            ObservableList niftiversions = FXCollections.observableArrayList();

            niftiversions.add("NifTi 1");
            niftiversions.add("NifTi 2");
            cntrl.niftiversion.setItems(niftiversions);
            cntrl.niftiversion.getSelectionModel().select(1);

            String path = selected.getFile().getPath();
            StringProperty loggerText = new SimpleStringProperty("logger:");
            cntrl.logger.textProperty().bind(loggerText);
            cntrl.convertButton.setOnAction(e -> {
                cntrl.progressBar.setVisible(true);
                if (path != null) {
                    String niftiName = cntrl.niftiName.getText();
                    if (niftiName.endsWith(".nii")) {
                        if (cntrl.compression.isSelected()) {
                            niftiName = niftiName + ".gz";
                        }
                    } else if (niftiName.endsWith(".nii.gz")) {
                        if (!cntrl.compression.isSelected()) {
                            niftiName = FilenameUtils.removeExtension(niftiName);
                        }
                    } else {
                        niftiName = niftiName + ".nii";
                        if (cntrl.compression.isSelected()) {
                            niftiName = niftiName + ".gz";
                        }
                    }

                    Path path2nii = Paths.get(path).getParent().resolve(niftiName);
                    if (cntrl.niftiversion.getSelectionModel().getSelectedIndex() == 0) {
                        Bruker2nii1 bruker2nii = new Bruker2nii1(path);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean rslt = false;
                                try {
                                    rslt = bruker2nii.convert(path2nii.toString(), (DataType) (cntrl.dataType.getSelectionModel().getSelectedItem()));
                                    updateFolderViewer();
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                    loggerText.set(loggerText.get() + "\n" + "Unuccessful Conversion :( ");
                                }
                                if (rslt) {
                                    loggerText.set(loggerText.get() + "\n" + "Successful Conversion: " + path2nii);
                                    cntrl.progressBar.setVisible(false);
                                } else {
                                    loggerText.set(loggerText.get() + "\n" + "Unuccessful Conversion :( ");
                                    cntrl.progressBar.setVisible(false);
                                }
                            }
                        }).start();
                    } else {
                        Bruker2nii bruker2nii = new Bruker2nii(path);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean rslt = false;
                                try {
                                    rslt = bruker2nii.convert(path2nii.toString(), (DataType) (cntrl.dataType.getSelectionModel().getSelectedItem()));
                                    updateFolderViewer();
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                    loggerText.set(loggerText.get() + "\n" + "Unuccessful Conversion :( ");
                                }
                                if (rslt) {
                                    loggerText.set(loggerText.get() + "\n" + "Successful Conversion: " + path2nii);
                                    cntrl.progressBar.setVisible(false);
                                } else {
                                    loggerText.set(loggerText.get() + "\n" + "Unuccessful Conversion :( ");
                                    cntrl.progressBar.setVisible(false);
                                }
                            }
                        }).start();
                    }
                } else {
                }

            });
        }
    }

    @FXML
    private void Chngtxtviwr(ActionEvent actionEvent) {
        FXMLLoader deDialog = new FXMLLoader(App.class.getResource("defaultEditor" + ".fxml"));
        Parent parent = null;
        try {
            parent = deDialog.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DefaultEditorCtlr defualtEditorCtlr = (DefaultEditorCtlr) deDialog.getController();
        defualtEditorCtlr.open.setText("Save");
        defualtEditorCtlr.dontshow.setVisible(false);
        Scene scene = new Scene(parent, 524.0, 258);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        defualtEditorCtlr.pathViewer.setText(CurrentUser.getUser().pathViewr);
        defualtEditorCtlr.selector.setOnAction(actionEvent1 -> {
            FileChooser dc = new FileChooser();
            dc.setInitialDirectory(new File(System.getProperty("user.home")));
            File path2viewer = dc.showOpenDialog(null);
            if (path2viewer == null || !path2viewer.isFile()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Could not open directory");
                alert.setContentText("The file is invalid.");
                alert.showAndWait();
            } else {
                defualtEditorCtlr.pathViewer.setText(path2viewer.getAbsolutePath());
                CurrentUser.getUser().pathViewr = path2viewer.getPath();
            }
        });
        defualtEditorCtlr.open.setOnAction(actionEvent2 -> {
            CurrentUser.getUser().dontshoweditor = (defualtEditorCtlr.dontshow.selectedProperty().get());
            stage.close();
        });
    }

    private void RenameFiles(ActionEvent actionEvent) {
        ObservableList selectedItems = null;
        selectedItems = listView.getSelectionModel().getSelectedItems();
        FXMLLoader renameDialog = new FXMLLoader(App.class.getResource("rename" + ".fxml"));
        Parent parent = null;
        try {
            parent = renameDialog.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent, 416, 400);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        RenameCtrlr renamctrlr = (RenameCtrlr) renameDialog.getController();
        getParametersforPrediction(renamctrlr.parameter);
        ArrayList<String> strings = new ArrayList<>();
        renamctrlr.add.setOnAction(actionEvent1 -> {
            if (renamctrlr.parameter.getText() != null) {
                Label text = new Label("{" + renamctrlr.parameter.getText() + "}  ");
                strings.add(renamctrlr.parameter.getText());
                text.setTextFill(Color.color(Math.random(), Math.random(), Math.random()));
                renamctrlr.parameters.getChildren().add(text);
            }
        });
        renamctrlr.remove.setOnAction(actionEvent1 -> {
            if (strings.size() > 0) {
                renamctrlr.parameters.getChildren().remove(strings.size() - 1);
                strings.remove(strings.size() - 1);
            }
        });
        StringProperty loggerText = new SimpleStringProperty("logger:");
        renamctrlr.logger.textProperty().bind(loggerText);
        ObservableList finalSelectedItems = selectedItems;
        renamctrlr.renameButton.setOnAction(actionEvent1 -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    renamctrlr.progressbar.setVisible(true);
                }
            });
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    File file;
                    for (Object listContainer : finalSelectedItems) {
                        if (((ListContainer) listContainer).getType().equals(FileType.brukerDirectory)) {
                            if (renamctrlr.copy.isSelected()) {
                                file = new File(((ListContainer) (listContainer)).getFile().getPath() + "_copy");
                                try {
                                    FileUtils.copyDirectory(((ListContainer) (listContainer)).getFile(), file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                file = ((ListContainer) (listContainer)).getFile();
                            }


                            if (((ListContainer) (listContainer)) != null) {
                                JcampdxData studyList = getStudyList(((ListContainer) (listContainer)).getFile());
                                String studyName = "";
                                try {
                                    String absPath = file.getParent();

                                    for (String string : strings
                                    ) {
                                        String tostring;
                                        try {
                                            ArrayList arr = (ArrayList) studyList.getParameters().get(string);
                                            tostring = ArrayUtils.toString(arr.get(0));
                                        } catch (Exception e) {
                                            tostring = studyList.getParameters().get(string).toString();
                                        }
                                        studyName = studyName + tostring + "_";
                                    }
                                    studyName = studyName.substring(0, studyName.length() - 1);
                                    int i = 1;
                                    if (!file.renameTo(new File(absPath, studyName))) {
                                        while (!file.renameTo(new File(absPath + "/" + studyName + "(" + i + ")"))) {
                                            i += 1;
                                        }
                                        loggerText.set(loggerText.get() + "\n" + file.getName() + "  was changed to  " + studyName + "(" + i + ")");
                                    } else {
                                        loggerText.set(loggerText.get() + "\n" + file.getName() + "  was changed to  " + studyName);
                                    }
                                } catch (Exception e) {
                                    loggerText.set(loggerText.get() + "\n" + file.getName() + "  could not be changed to  " + studyName);
                                }
                            } else {

                            }
                        } else {
                            loggerText.set(loggerText.get() + "\n" + "Apparently the selected directory is not valid Bruker directory");
                        }
                    }
                    updateFolderViewer();
                    renamctrlr.progressbar.setVisible(false);
                }


                ;
            });
        });
    }

    private void RemoveParam(ActionEvent actionEvent) {
        CurrentUser.getUser().selectedParameters.remove(tableParams.getSelectionModel().getSelectedIndex());
        setParamsTable(cd_temp);
    }

    private void getParametersforPrediction(TextField textField) {
        try {
            Set<String> keysets = (studyList.getParameters()).keySet();
            TextFields.bindAutoCompletion(textField, keysets);
        } catch (Exception e) {

        }
    }

    private void AddParam(KeyEvent actionEvent) {
        try {
            CurrentUser.getUser().selectedParameters.add(parameterTextField.getText());
        } catch (Exception e) {
        }
        setParamsTable(cd_temp);
        parameterTextField.clear();
    }

//    private void AddDataset(ActionEvent actionEvent) {
//        DirectoryChooser dc = new DirectoryChooser();
//        dc.setInitialDirectory(new File(System.getProperty("user.home")));
//        folderfile = dc.showDialog(null);
//        if(folderfile == null || ! folderfile.isDirectory()) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText("Could not open directory");
//            alert.setContentText("The file is invalid.");
//            alert.showAndWait();
//        } else {
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    TreeItem<String> root = getNodesForDirectory(folderfile,true);
//                    folderView.getRoot().getChildren().add(root);
////                    CurrentUser.getUser().studyLists.put(root.getValue(),getStudyList(folderfile));
//                    CurrentUser.getUser().datasets.put(root.getValue(),folderfile.getAbsolutePath());
//                }
//            });
//        }
//    }
//
//    private void NewDataset(ActionEvent actionEvent) {
//        DirectoryChooser dc = new DirectoryChooser();
//        dc.setInitialDirectory(new File(System.getProperty("user.home")));
//        folderfile = dc.showDialog(null);
//        if(folderfile == null || ! folderfile.isDirectory()) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText("Could not open directory");
//            alert.setContentText("The file is invalid.");
//            alert.showAndWait();
//        } else {
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    TreeItem<String> root = getNodesForDirectory(folderfile,true);
//                    folderView.getRoot().getChildren().clear();
//                    folderView.getRoot().getChildren().add(root);
////                    CurrentUser.getUser().studyLists.put(root.getValue(),getStudyList(folderfile));
//                    CurrentUser.getUser().datasets.put(root.getValue(),folderfile.getAbsolutePath());
//                    mainRoot.setExpanded(true);
//                }
//            });
//        }
//
//    }

//    private void folderViewClicked(MouseEvent mouseEvent) {
//        TreeItem selected = null;
//        if (mouseEvent.getClickCount() == 2) {
//            selected = (TreeItem) folderView.getSelectionModel().getSelectedItem();
//            String path = (String) selected.getValue();
//            TreeItem parent = selected.getParent();
//            if (!path.equals("Datasets") && !parent.getValue().toString().equals("Datasets")) {
//                do {
//                    try {
//                        path = String.valueOf(Paths.get(parent.getValue().toString(), path));
//                        parent = parent.getParent();
//                    } catch (Exception e) {
//
//                    }
//                }while (parent.getParent() != null);
//                Path path2file = Paths.get(path);
//                try {
////                    openfile(path2file);
//                } catch (Exception e) {
//                    Alert alert = new Alert(Alert.AlertType.ERROR);
//                    alert.setHeaderText("Could not open the file");
//                    alert.setContentText("The file is invalid.");
//                    alert.showAndWait();
//                }
//            }
//        }else if (folderView.getSelectionModel().getSelectedItem() != null) {
//            selected = (TreeItem) folderView.getSelectionModel().getSelectedItem();
////            setParamsTable(selected);
//            CurrentUser.getUser().lastSelectedItem = selected;
//        }
//
//    }

    private void openfile(ListContainer selectedItem) throws IOException {
        switch (selectedItem.type) {
            case text: {
//                System.out.println("acqp");
//                ProcessBuilder pb = new ProcessBuilder("Notepad.exe", selectedItem.getFile().toString());
//                pb.start();
                opentext();
                break;
            }
            case twodseq: {
                openImage();
                break;
            }
//            case "method":
//                System.out.println("acqp");
//                break;
//            case "reco":
//                System.out.println("acqp");
//                break;
//            case "visupars":
//                System.out.println("acqp");
//                break;
        }


    }

    private void openIDV() throws IOException {
        FXMLLoader root = new FXMLLoader(App.class.getResource("idv" + ".fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Interrealated Data Viewer");
        primaryStage.setScene(new Scene(root.load(), 1280, 720));
        primaryStage.show();
        Controller ctrler = root.getController();
        File temp = null;
        try {
            temp = new File(CurrentUser.getUser().getCD().getPath(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
        } catch (Exception e) {
        }
        ctrler.openfiledata(temp);

    }

    private void setParamsTable(File selected) {
        try {
            studyList = getStudyList(selected);
        } catch (Exception e) {

        }
        tableParams.getItems().clear();
        items =
                FXCollections.<Map<String, Object>>observableArrayList();
        paramColumn.setCellValueFactory(new MapValueFactory<>("Parameters"));
        valueColumn.setCellValueFactory(new MapValueFactory<>("Values"));

        for (String param : CurrentUser.getUser().selectedParameters) {
            Map<String, Object> item = new HashMap<>();
            item.put("Parameters", param);
            try {
                String tostring;
                try {
                    ArrayList arr = (ArrayList) studyList.getParameters().get(param);
                    tostring = ArrayUtils.toString(arr.get(0));
                } catch (Exception e) {
                    tostring = studyList.getParameters().get(param).toString();
                }
                item.put("Values", tostring);
            } catch (Exception e) {

            }
            items.add(item);
        }

        tableParams.getItems().setAll(items);


    }

    public JcampdxData getStudyList(File f) {
        JcampdxData jcampdxData = null;
        if (f.isDirectory()) {
            try {
                String acqp_ = (new File(f, "acqp")).toString();
                String method_ = (new File(f, "method")).toString();
                File seq_ = new File(f, "pdata" + File.separator + "1");
                String visupars_ = (new File(seq_, "visu_pars")).toString();
                String reco_ = (new File(seq_, "reco")).toString();
                Map<String, Object> jcampdx_file = null;
                if (Arrays.asList(f.list()).contains("acqp")) {
                    jcampdx_file = Jcampdx.read_jcampdx_file(acqp_);
                }
                if (Arrays.asList(f.list()).contains("method")) {
                    jcampdx_file.putAll(Jcampdx.read_jcampdx_file(method_));
                }
                if (Arrays.asList(seq_.list()).contains("reco")) {
                    jcampdx_file.putAll(Jcampdx.read_jcampdx_file(visupars_));
                }
                if (Arrays.asList(seq_.list()).contains("visu_pars")) {
                    jcampdx_file.putAll(Jcampdx.read_jcampdx_file(reco_));
                }
                jcampdxData = new JcampdxData(jcampdx_file);
            } catch (Exception e) {

            }
        } else {

        }
//        }
        return jcampdxData;
    }

    public TreeItem<String> getNodesForDirectory(File directory, boolean parent) {
        TreeItem<String> root;
        if (!parent) {
            root = new TreeItem<String>(directory.getName());
        } else {
            root = new TreeItem<String>(directory.getAbsolutePath());
        }
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                root.getChildren().add(getNodesForDirectory(f, false));
            } else {
                root.getChildren().add(new TreeItem<String>(f.getName()));
            }
        }
        return root;
    }

    public void aboutUs() {
        FXMLLoader aboutUsDialog = new FXMLLoader(App.class.getResource("aboutUs" + ".fxml"));
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

    public void quit() {
        System.exit(0);
    }


    private void openImage() throws IOException {
        FXMLLoader imageViwerFXMLLoader = new FXMLLoader(App.class.getResource("imageViewer" + ".fxml"));
        Scene imageViwerScene = new Scene(imageViwerFXMLLoader.load(), 960, 720);

        Stage imageViwerStage = new Stage();
        imageViwerStage.setResizable(false);
        ImageViwer ctrler = imageViwerFXMLLoader.getController();

        File temp = null;
        try {
            temp = new File(CurrentUser.getUser().getCD().getPath(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
        } catch (Exception e) {
        }
//        plot2dseqOnCanvas(temp);
//        Bruker bruker = new Bruker();
//        bruker.setPath(temp.toPath());
//        DataBruker data = bruker.getData();
//        float[] realdata = data.getRealData();
//        int[] visucoresize = bruker.getJcampdx().getVisu_pars().getINDArray("VisuCoreSize").toIntVector();
//        double[] visucoreextent = bruker.getJcampdx().getVisu_pars().getINDArray("VisuCoreExtent").toDoubleVector();
//        FxImageCanvas canvas = new FxImageCanvas();
//        if (visucoresize.length > 2) {
//            for (int i = 0; i < visucoresize[2]; i++) {
//                WritableImage image = new WritableImage(visucoresize[0], visucoresize[1]);
//                PixelWriter pixelWriter = image.getPixelWriter();
//                for (int y = 0; y < visucoresize[0]; y++) {
//                    for (int x = 0; x < visucoresize[1]; x++) {
//
//                        double dataOfPoint = realdata[x + (visucoresize[1] * y)];
//                        Color color = Color.hsb(dataOfPoint, 1, 1);
//                        pixelWriter.setColor(x, y, color);
//                    }
//                }
//                ImageView imageView = new ImageView(image);
//                imageView.setFitWidth(visucoreextent[0]);
//                imageView.setFitHeight(visucoreextent[1]);
////                        imageView.fitHeightProperty().bind( ctrler.getImagePane().heightProperty());
////                        imageView.fitWidthProperty().bind( ctrler.getImagePane().widthProperty());
//
//                StackPane stackPane = new StackPane();
//                canvas.heightProperty().bind(stackPane.heightProperty());
//                canvas.widthProperty().bind(stackPane.widthProperty());
//                canvas.setImage(imageView.getImage());
//                leftStatus.setText("file is loaded");
//
//                ctrler.getImagePane().getChildren().add(stackPane);
//            }
//        } else {
//            WritableImage image = new WritableImage(visucoresize[0], visucoresize[1]);
//            PixelWriter pixelWriter = image.getPixelWriter();
//            Float max = Collections.max(Arrays.asList(ArrayUtils.toObject(realdata)));
//            for (int y = 0; y < visucoresize[0]; y++) {
//                for (int x = 0; x < visucoresize[1]; x++) {
//                    double dataOfPoint = realdata[x + (visucoresize[1] * y)];
//                    Color color = Color.gray(dataOfPoint / max);
//                    pixelWriter.setColor(x, y, color);
//                }
//            }
//            ImageView imageView = new ImageView(image);
//            imageView.setFitWidth(visucoreextent[1]);
//            imageView.setFitHeight(visucoreextent[0]);
////                        imageView.fitHeightProperty().bind( ctrler.getImagePane().heightProperty());
////                        imageView.fitWidthProperty().bind( ctrler.getImagePane().widthProperty());
//

//            canvas.setImage(imageView.getImage());

//        }


        CopyCanvas = new FxImageCanvas();
        replot2dseqOnCanvas(0);
        leftStatus.setText("file is loaded");
        ctrler.getImagePane().getChildren().add(CopyCanvas);
        CopyCanvas.heightProperty().bind(ctrler.getImagePane().heightProperty());
        CopyCanvas.widthProperty().bind(ctrler.getImagePane().widthProperty());
//                        imageView.fitHeightProperty().bind( ctrler.getImagePane().heightProperty());
//                        imageView.fitWidthProperty().bind( ctrler.getImagePane().widthProperty());
        imageViwerStage.setTitle(temp.toString());
        imageViwerStage.setScene(imageViwerScene);
        imageViwerStage.show();
        CopyCanvas.repaint();

        try {
            ctrler.all.setText(String.valueOf(canvas_visucoresize[2]));
        } catch (Exception e) {
            ctrler.all.setText("1");
        }

        AtomicInteger i = new AtomicInteger();
        ctrler.id.setText(String.valueOf(i.get()+1));

        ctrler.right.setOnAction(e -> {
            try {
                if (i.get() < canvas_visucoresize[2]-1) {
                    i.incrementAndGet();
                    replot2dseqOnCanvas(i.get());

//
//
//                    CopyCanvas.heightProperty().bind(ctrler.getImagePane().heightProperty());
//                    CopyCanvas.widthProperty().bind(ctrler.getImagePane().widthProperty());
//                    CopyCanvas.repaint();
                    ctrler.id.setText(String.valueOf(i.get()+1));
                }
            } catch (Exception exception) {

            }
        });
        ctrler.left.setOnAction(e -> {
            if (i.get() > 0) {
                i.decrementAndGet();
                replot2dseqOnCanvas(i.get());

//                leftStatus.setText("file is loaded");
//                CopyCanvas.heightProperty().bind(ctrler.getImagePane().heightProperty());
//                CopyCanvas.widthProperty().bind(ctrler.getImagePane().widthProperty());
//                CopyCanvas.repaint();
                ctrler.id.setText(String.valueOf(i.get()+1));
            }
        });
        ctrler.slider.setMin(1);
        try {
            ctrler.slider.setMax(canvas_visucoresize[2]);
        } catch (Exception e) {
            ctrler.slider.setMax(1);
        }
        ctrler.slider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        replot2dseqOnCanvas(t1.intValue()-1);
                        CopyCanvas.setImage(canvas.image);
                        leftStatus.setText("file is loaded");
                        CopyCanvas.heightProperty().bind(ctrler.getImagePane().heightProperty());
                        CopyCanvas.widthProperty().bind(ctrler.getImagePane().widthProperty());
                        CopyCanvas.repaint();
                        i.set(t1.intValue());
                        ctrler.id.setText(String.valueOf(i.get()+1));
                    }
                }
        );

    }

    private void opentext() {
        FXMLLoader deDialog = new FXMLLoader(App.class.getResource("defaultEditor" + ".fxml"));
        Parent parent = null;
        try {
            parent = deDialog.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DefaultEditorCtlr defualtEditorCtlr = (DefaultEditorCtlr) deDialog.getController();

        if (!CurrentUser.getUser().dontshoweditor) {
            Scene scene = new Scene(parent, 524.0, 258);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
            defualtEditorCtlr.pathViewer.setText(CurrentUser.getUser().pathViewr);
            defualtEditorCtlr.selector.setOnAction(actionEvent -> {
                FileChooser dc = new FileChooser();
                dc.setInitialDirectory(new File(System.getProperty("user.home")));
                File path2viewer = dc.showOpenDialog(null);
                if (path2viewer == null || !path2viewer.isFile()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not open directory");
                    alert.setContentText("The file is invalid.");
                    alert.showAndWait();
                } else {
                    defualtEditorCtlr.pathViewer.setText(path2viewer.getAbsolutePath());
                    CurrentUser.getUser().pathViewr = path2viewer.getPath();
                }
            });
            defualtEditorCtlr.open.setOnAction(actionEvent -> {
                String temp = null;
                try {
                    temp = new File(CurrentUser.getUser().getCD().getPath(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText()).toString();
                } catch (Exception e) {
                }
                ProcessBuilder pb = new ProcessBuilder(defualtEditorCtlr.pathViewer.getText(), temp);
                try {
                    pb.start();
                    CurrentUser.getUser().dontshoweditor = (defualtEditorCtlr.dontshow.selectedProperty().get());
                    stage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            String temp = null;
            try {
                temp = new File(CurrentUser.getUser().getCD().getPath(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText()).toString();
            } catch (Exception e) {
            }
            ProcessBuilder pb = new ProcessBuilder(CurrentUser.getUser().pathViewr, temp);
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
