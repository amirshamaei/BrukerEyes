package org.amirshamaei;

import bruker2nii.Bruker2nii;
import bruker2nii.DataType;
import bruker_plugin_lib.Jcampdx;
import bruker_plugin_lib.JcampdxData;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.controlsfx.control.textfield.TextFields;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class MainCtrl implements Initializable {
    @FXML
    MenuItem newdataset;
    @FXML
    MenuItem adddataset;
    @FXML
    MenuItem chngtxtviwr;
    @FXML
    Label leftStatus;
    @FXML
    ScrollPane ScrollPane;
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
    MenuItem convert2nii = new MenuItem("Convert to NIfTI");
    MenuItem delete = new MenuItem("Delete");
    private File folderfile;
    TreeItem<String> mainRoot = new TreeItem<String>("Datasets");
    ObservableList<Map<String, Object>> items =
            FXCollections.<Map<String, Object>>observableArrayList();
    private ContextMenu contxtMenu;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        folderView.setRoot(mainRoot);
        newdataset.setOnAction(this::NewDataset);
        adddataset.setOnAction(this::AddDataset);
        folderView.setOnMouseClicked(this::folderViewClicked);

        addParam.setOnAction(this::AddParam);
        removeParam.setOnAction(this::RemoveParam);
        parameterTextField.setOnMouseClicked(this::getParametersforPrediction);
        renameFiles.setOnAction(this::RenameFiles);

        chngtxtviwr.setOnAction(this::Chngtxtviwr);
        contxtMenu = new ContextMenu();
        contxtMenu.getItems().add(convert2nii);
        contxtMenu.getItems().add(delete);
        folderView.setContextMenu(contxtMenu);
        convert2nii.setOnAction(this::convert2nii);
        delete.setOnAction(e->{
            mainRoot.getChildren().remove(folderView.getSelectionModel().getSelectedItem());
            CurrentUser.getUser().datasets.remove(folderView.getSelectionModel().getSelectedItem());
            CurrentUser.getUser().studyLists.remove(folderView.getSelectionModel().getSelectedItem());
        });
        Nd4j.create(1,1);
    }

    private void convert2nii(ActionEvent actionEvent) {
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
        TreeItem selected = null;
        Path path2file = null;
            selected = (TreeItem) folderView.getSelectionModel().getSelectedItem();
            String path = (String) selected.getValue();
            TreeItem parentfile = selected.getParent();
            if (!path.equals("Datasets") && !parentfile.getValue().toString().equals("Datasets")) {
                do {
                    try {
                        path = String.valueOf(Paths.get(parentfile.getValue().toString(), path));
                        parentfile = parentfile.getParent();
                    } catch (Exception e) {

                    }
                }while (parentfile.getParent() != null);
                path2file = Paths.get(path);
        }else if (folderView.getSelectionModel().getSelectedItem() != null) {
            selected = (TreeItem) folderView.getSelectionModel().getSelectedItem();
            setParamsTable(selected);
            CurrentUser.getUser().lastSelectedItem = selected;
        }
        StringProperty loggerText = new SimpleStringProperty("logger:");
        cntrl.logger.textProperty().bind(loggerText);
        Path finalPath2file = path2file;
        cntrl.convertButton.setOnAction(e -> {
            cntrl.progressBar.setVisible(true);
            if (finalPath2file !=null) {
                String niftiName = cntrl.niftiName.getText();
                if(niftiName.endsWith(".nii")) {
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

                Path path2nii = finalPath2file.getParent().resolve(niftiName);
                Bruker2nii bruker2nii = new Bruker2nii(finalPath2file.toString());



                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean rslt = false;
                        try {
                            rslt = bruker2nii.convert(path2nii.toString(), (DataType) (cntrl.dataType.getSelectionModel().getSelectedItem()));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            loggerText.set(loggerText.get()+"\n"+ "Unuccessful Conversion :( ");
                        }
                        if (rslt) {
                            loggerText.set(loggerText.get()+"\n"+ "Successful Conversion: " + path2nii);
                            cntrl.progressBar.setVisible(false);
                        } else {
                            loggerText.set(loggerText.get()+"\n"+ "Unuccessful Conversion :( ");
                            cntrl.progressBar.setVisible(false);
                        }
                    }
                }).start();


            } else {
            }
        });
    }

    private void Chngtxtviwr(ActionEvent actionEvent) {
        FXMLLoader deDialog = new FXMLLoader(App.class.getResource("DefaultEditor" + ".fxml"));
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
                if(path2viewer == null || ! path2viewer.isFile()) {
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
        System.out.println("test");
        FXMLLoader renameDialog = new FXMLLoader(App.class.getResource("rename" + ".fxml"));
        Parent parent = null;
        try {
            parent = renameDialog.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(parent, 416, 400);
        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
//        stage.showAndWait();
        stage.show();
        RenameCtrlr renamctrlr = (RenameCtrlr) renameDialog.getController();
        renamctrlr.datasets.setItems(mainRoot.getChildren());
        renamctrlr.datasets.getSelectionModel().selectFirst();
        try {
            HashMap<String, JcampdxData> studyList = CurrentUser.getUser().studyLists.get(CurrentUser.getUser().lastSelectedItem.getParent().getValue());
            Set<String> keysets = ((Map<String, Object>) ((JcampdxData) studyList.get(CurrentUser.getUser().lastSelectedItem.getValue())).getParameters()).keySet();
            TextFields.bindAutoCompletion(renamctrlr.parameter,keysets);
        } catch (Exception e) {

        }
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
            if (strings.size()>0) {
                renamctrlr.parameters.getChildren().remove(strings.size()-1);
                strings.remove(strings.size()-1);
            }
        });
//        renamctrlr.parameters.setItems(FXCollections.observableArrayList(CurrentUser.getUser().selectedParameters));
//        renamctrlr.parameters.getSelectionModel().selectFirst();
        StringProperty loggerText = new SimpleStringProperty("logger:");
        renamctrlr.logger.textProperty().bind(loggerText);
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
                    TreeItem selected = (TreeItem) renamctrlr.datasets.getSelectionModel().getSelectedItem();
                    File files;
                    if(renamctrlr.copy.isSelected()){
                        files = new File(CurrentUser.getUser().datasets.get(selected.getValue())+"_copy");
                        try {
                            FileUtils.copyDirectory(new File(CurrentUser.getUser().datasets.get(selected.getValue())), files);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        files = new File(CurrentUser.getUser().datasets.get(selected.getValue()));
                    }


                    if (selected!=null) {
                        HashMap<String, JcampdxData>  studyList = CurrentUser.getUser().studyLists.get(selected.getValue());
                        for(File f : files.listFiles()) {
                            if(f.isDirectory()) {
                                String studyName = "";
                                try {
                                    String absPath = f.getParent();

                                    for (String string:strings
                                    ) {
                                        String tostring;
                                        try {
                                            ArrayList arr = (ArrayList) studyList.get(f.getName()).getParameters().get(string);
                                            tostring = ArrayUtils.toString(arr.get(0));
                                        } catch (Exception e) {
                                            tostring = studyList.get(f.getName()).getParameters().get(string).toString();
                                        }
                                        studyName =  studyName + tostring + "_";
                                    }
                                    studyName = studyName.substring(0,studyName.length()-1);
                                    int i = 1;
                                    if (!f.renameTo(new File(absPath + "/" + studyName))){
                                        while (!f.renameTo(new File(absPath + "/" + studyName + "(" + i +")"))) {
                                            i+=1;
                                        }
                                        loggerText.set(loggerText.get()+"\n"+f.getName() + "  was changed to  " + studyName + "(" + i +")");
                                    } else {
                                        loggerText.set(loggerText.get()+"\n"+f.getName() + "  was changed to  " + studyName);
                                    };

                                } catch (Exception e) {
                                    loggerText.set(loggerText.get()+"\n"+f.getName() + "  could not be changed to  " + studyName);
                                }

                            } else {

                            }
                        }

                        if(renamctrlr.copy.isSelected()){
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    TreeItem<String> root = getNodesForDirectory(new File(CurrentUser.getUser().datasets.get(selected.getValue())+"_copy"),true);
                                    mainRoot.getChildren().add(root);
                                    CurrentUser.getUser().studyLists.put(root.getValue(),getStudyList(new File(CurrentUser.getUser().datasets.get(selected.getValue())+"_copy")));
                                    renamctrlr.progressbar.setVisible(false);
                                }
                            });
                        } else {
                            int selectedIdx = renamctrlr.datasets.getSelectionModel().getSelectedIndex();
                            mainRoot.getChildren().remove(selected);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    TreeItem<String> root = getNodesForDirectory(new File(CurrentUser.getUser().datasets.get(selected.getValue())),true);
                                    mainRoot.getChildren().add(selectedIdx,root);
                                    CurrentUser.getUser().studyLists.put(root.getValue(),getStudyList(new File(CurrentUser.getUser().datasets.get(selected.getValue()))));
                                    renamctrlr.datasets.getSelectionModel().select(selectedIdx);
                                    renamctrlr.progressbar.setVisible(false);
                                }
                            });
                        }

                    }
                }
            });

        });


    }

    private void RemoveParam(ActionEvent actionEvent) {
        CurrentUser.getUser().selectedParameters.remove(tableParams.getSelectionModel().getSelectedIndex());
        setParamsTable(CurrentUser.getUser().lastSelectedItem);
    }

    private void getParametersforPrediction(MouseEvent mouseEvent) {
        try {
            HashMap<String, JcampdxData> studyList = CurrentUser.getUser().studyLists.get(CurrentUser.getUser().lastSelectedItem.getParent().getValue());
            Set<String> keysets = ((Map<String, Object>) ((JcampdxData) studyList.get(CurrentUser.getUser().lastSelectedItem.getValue())).getParameters()).keySet();
            TextFields.bindAutoCompletion(parameterTextField,keysets);
        } catch (Exception e) {

        }
    }


    private void AddParam(ActionEvent actionEvent) {
        try {
            CurrentUser.getUser().selectedParameters.add(parameterTextField.getText());
        } catch (Exception e) {

        }
        setParamsTable(CurrentUser.getUser().lastSelectedItem);
    }

    private void AddDataset(ActionEvent actionEvent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        folderfile = dc.showDialog(null);
        if(folderfile == null || ! folderfile.isDirectory()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not open directory");
            alert.setContentText("The file is invalid.");
            alert.showAndWait();
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    TreeItem<String> root = getNodesForDirectory(folderfile,true);
                    folderView.getRoot().getChildren().add(root);
                    CurrentUser.getUser().studyLists.put(root.getValue(),getStudyList(folderfile));
                    CurrentUser.getUser().datasets.put(root.getValue(),folderfile.getAbsolutePath());
                }
            });
        }
    }

    private void NewDataset(ActionEvent actionEvent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        folderfile = dc.showDialog(null);
        if(folderfile == null || ! folderfile.isDirectory()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not open directory");
            alert.setContentText("The file is invalid.");
            alert.showAndWait();
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    TreeItem<String> root = getNodesForDirectory(folderfile,true);
                    folderView.getRoot().getChildren().clear();
                    folderView.getRoot().getChildren().add(root);
                    CurrentUser.getUser().studyLists.put(root.getValue(),getStudyList(folderfile));
                    CurrentUser.getUser().datasets.put(root.getValue(),folderfile.getAbsolutePath());
                    mainRoot.setExpanded(true);
                }
            });
        }

    }

    private void folderViewClicked(MouseEvent mouseEvent) {
        TreeItem selected = null;
        if (mouseEvent.getClickCount() == 2) {
            selected = (TreeItem) folderView.getSelectionModel().getSelectedItem();
            String path = (String) selected.getValue();
            TreeItem parent = selected.getParent();
            if (!path.equals("Datasets") && !parent.getValue().toString().equals("Datasets")) {
                do {
                    try {
                        path = String.valueOf(Paths.get(parent.getValue().toString(), path));
                        parent = parent.getParent();
                    } catch (Exception e) {

                    }
                }while (parent.getParent() != null);
                Path path2file = Paths.get(path);
                try {
                    openfile(path2file);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not open the file");
                    alert.setContentText("The file is invalid.");
                    alert.showAndWait();
                }
            }
        }else if (folderView.getSelectionModel().getSelectedItem() != null) {
            selected = (TreeItem) folderView.getSelectionModel().getSelectedItem();
            setParamsTable(selected);
            CurrentUser.getUser().lastSelectedItem = selected;
        }

    }

    private void openfile(Path selected) throws IOException {
        switch (selected.getFileName().toString()){
//            case "acqp":
//                System.out.println("acqp");
//                ProcessBuilder pb = new ProcessBuilder("Notepad.exe", selected.toString());
//                pb.start();
//                break;
//            case "method":
//                System.out.println("acqp");
//                break;
//            case "reco":
//                System.out.println("acqp");
//                break;
//            case "visupars":
//                System.out.println("acqp");
//                break;
            case "fid":
                System.out.println("fid");
                break;
            case "2dseq":
                System.out.println("2dseq");
                break;
            default:

                    FXMLLoader deDialog = new FXMLLoader(App.class.getResource("DefaultEditor" + ".fxml"));
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
                                if(path2viewer == null || ! path2viewer.isFile()) {
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
                        ProcessBuilder pb = new ProcessBuilder(defualtEditorCtlr.pathViewer.getText(), selected.toString());
                        try {
                            pb.start();
                            CurrentUser.getUser().dontshoweditor = (defualtEditorCtlr.dontshow.selectedProperty().get());
                            stage.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    ProcessBuilder pb = new ProcessBuilder(CurrentUser.getUser().pathViewr, selected.toString());
                    try {
                        pb.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
//        FXMLLoader imageViwerFXMLLoader = new FXMLLoader(App.class.getResource("imageViwer" + ".fxml"));
//        Scene imageViwerScene = new Scene(imageViwerFXMLLoader.load(), 640, 480);
//        Stage imageViwerStage = new Stage();
//        imageViwerStage.setScene(imageViwerScene);
//        imageViwerStage.show();
//        ImageViwer ctrler = imageViwerFXMLLoader.getController();
//
//
//
//                    Bruker bruker = new Bruker();
//                    bruker.setPath(selected.toAbsolutePath());
//                    DataBruker data = bruker.getData();
//                    float[] realdata = data.getRealData();
//
//                    WritableImage image = new WritableImage(256, 256);
//                    PixelWriter pixelWriter = image.getPixelWriter();
//                    for (int y=0; y<256; y++) {
//                        for (int x=0; x<256; x++) {
//
//                        double dataOfPoint = realdata[x+(256*y)] ;
//                        Color color = Color.hsb(dataOfPoint, 1, 1);
//                            pixelWriter.setColor(x, y, color);
//                        }
//                    }
//                    ImageView imageView = new ImageView(image);
////                    ImageViwer imageViwer = new ImageViwer();
////                    ImageViewer.getChildren().add(imageViwer);
////                    imageViwer.stackPane.getChildren().add(imageView);
//                    FxImageCanvas canvas = new FxImageCanvas();
//                    ctrler.getImagePane().getChildren().add(canvas);
////                    ImageViewer.getChildren().add(canvas);
//                    canvas.widthProperty().bind(ScrollPane.widthProperty());
//                    canvas.heightProperty().bind(ScrollPane.heightProperty());
//                    canvas.setImage(imageView.getImage());
//                    leftStatus.setText("file is loaded");


    }

    private void setParamsTable(TreeItem selected) {
        HashMap<String, JcampdxData>  studyList = null;
        try {
            studyList = CurrentUser.getUser().studyLists.get(selected.getParent().getValue());
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
                    ArrayList arr = (ArrayList) studyList.get(selected.getValue()).getParameters().get(param);
                    tostring = ArrayUtils.toString(arr.get(0));
                } catch (Exception e) {
                    tostring = studyList.get(selected.getValue()).getParameters().get(param).toString();
                }
                item.put("Values" , tostring);
            } catch (Exception e) {

            }
            items.add(item);
        }

        tableParams.getItems().setAll(items);


    }


    public TreeItem<String> getNodesForDirectory(File directory,boolean parent) {
        TreeItem<String> root;
        if (!parent) {
            root = new TreeItem<String>(directory.getName());
        } else {
            root = new TreeItem<String>(directory.getAbsolutePath());
        }
        for(File f : directory.listFiles()) {
            if(f.isDirectory()) {
                root.getChildren().add(getNodesForDirectory(f,false));
            } else {
                root.getChildren().add(new TreeItem<String>(f.getName()));
            }
        }
        return root;
    }
    public HashMap<String, JcampdxData> getStudyList(File directory) {
        HashMap<String,JcampdxData> study_list = new HashMap();
        for(File f : directory.listFiles()) {
            if(f.isDirectory()) {
                try {
                    Map<String, Object> jcampdx_file = new HashMap<>();
                    String acqp_ = f.getAbsolutePath().toString() + "\\acqp";
                    String method_ = f.getAbsolutePath().toString() + "\\method";
                    if(Arrays.asList(f.list()).contains("acqp")) {
                        jcampdx_file = Jcampdx.read_jcampdx_file(acqp_);
                    }
                    if(Arrays.asList(f.list()).contains("method")) {
                        jcampdx_file.putAll(Jcampdx.read_jcampdx_file(method_));
                    }
                    JcampdxData jcampdxData = new JcampdxData(jcampdx_file);
                    study_list.put(f.getName(),jcampdxData);
                    System.out.println(acqp_);
                } catch (Exception e) {

                }
            } else {
                
            }
        }
        return study_list;
    }

}
