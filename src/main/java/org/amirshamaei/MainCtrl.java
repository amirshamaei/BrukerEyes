package org.amirshamaei;

import bruker2nii.Bruker2nii;
import bruker2nii.DataType;
import bruker_plugin_lib.Jcampdx;
import bruker_plugin_lib.JcampdxData;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.controlsfx.control.textfield.TextFields;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class MainCtrl implements Initializable {
//    @FXML
//    MenuItem aboutUs;
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


    MenuItem convert2nii = new MenuItem("Convert to NIfTI");
    MenuItem delete = new MenuItem("Delete");
    MenuItem renameA = new MenuItem("Rename Automatically");
    MenuItem copy = new MenuItem("Copy");
    MenuItem rename = new MenuItem("Rename");
    MenuItem paste = new MenuItem("Paste");

    private File folderfile;
    TreeItem<String> mainRoot = new TreeItem<String>("Datasets");
    ObservableList<Map<String, Object>> items =
            FXCollections.<Map<String, Object>>observableArrayList();
    private ContextMenu contxtMenu;
    private File cd_copy;
    private File[] listRoot;
    private File cd_temp;
    private JcampdxData studyList;
    private Stage bookmarkstage;
    private FXMLLoader bookmark;
    private ObservableList<LikedPath> likedpathObservable;

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
            if (e.getCode() == KeyCode.ENTER){
                try {
                    CurrentUser.getUser().setCd( new File(pathField.getText()));
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

        listView.setContextMenu(contxtMenu);
        listView.setFixedCellSize(60);
        convert2nii.setOnAction(this::convert2nii);
        delete.setOnAction(e -> {
            cd_temp = new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
            cd_temp.delete();
            updateFolderViewer();
        });
        renameA.setOnAction(this::RenameFiles);
        rename.setOnAction(actionEvent -> {
            cd_temp = new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
            ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().setText("");
            TextField temp_tf = new TextField("");
            ((ListContainer) listView.getSelectionModel().getSelectedItem()).getChildren().add(temp_tf);
            temp_tf.requestFocus();
            temp_tf.setOnKeyReleased(e -> {
                if(e.getCode() == KeyCode.ENTER){
                    ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().setText(temp_tf.getText());
                    cd_temp.renameTo(new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText()));
                    ((ListContainer) listView.getSelectionModel().getSelectedItem()).getChildren().remove(temp_tf);
                }
            });
//            cd_temp.renameTo(new File)
        });
        copy.setOnAction(actionEvent -> {
            cd_copy = new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
        });
        paste.setOnAction(actionEvent -> {
            Platform.runLater( new Runnable() {
                public void run() {
                    if (cd_copy.isDirectory()) {
                        try {
                            FileUtils.copyDirectoryToDirectory(cd_copy,CurrentUser.getUser().getCD());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (cd_copy.isFile()) {
                        try {
                            FileUtils.copyFileToDirectory(cd_copy,CurrentUser.getUser().getCD());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    updateFolderViewer();
                }
            });
        });




        listView.setOnMouseClicked(mouseEvent -> {
            if ( mouseEvent.getClickCount() == 1) {
                if (((ListContainer) listView.getSelectionModel().getSelectedItem()).getType().equals(FileType.brukerDirectory)) {
                    cd_temp = new File(CurrentUser.getUser().getCD().getAbsoluteFile(), ((ListContainer) listView.getSelectionModel().getSelectedItem()).getLabel().getText());
                    setParamsTable(cd_temp);
                }
            } else if (mouseEvent.getClickCount() == 2){
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
        ((BookmarkCtrlr) bookmark.getController()).likedlist.setItems(likedpathObservable );
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
        Nd4j.create(1,1);


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
            bookmarkstage.close();
        });
        bookmarkCtrlr.cacellikedpath.setOnAction(actionEvent1 -> {
            bookmarkstage.close();
        });
        bookmarkCtrlr.likedlist.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1 ) {
                bookmarkCtrlr.deslikedpath.setText(((LikedPath) bookmarkCtrlr.likedlist.getSelectionModel().getSelectedItem()).getDescp());
                bookmarkCtrlr.namelikedpath.setText(((LikedPath) bookmarkCtrlr.likedlist.getSelectionModel().getSelectedItem()).getName());
            } else if (mouseEvent.getClickCount() == 2 ) {
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
                for (File file:listRoot){
                    ListContainer listContainer = new ListContainer(file);
                    listView.getItems().add(listContainer);
                }
                CurrentUser.getUser().setCd(new File(""));
                pathField.setText("root");
            }
    }

    private void updateFolderViewer() {
        listView.getItems().clear();
        try {
            for (File file:CurrentUser.getUser().getCD().listFiles()){
                ListContainer listContainer = new ListContainer(file);
                listView.getItems().add(listContainer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePathField() {
        pathField.setText(CurrentUser.getUser().getCD().getAbsolutePath());
        if (CurrentUser.getUser().getLikedPaths().stream().anyMatch(o -> o.toString().equals(CurrentUser.getUser().getCD().getAbsolutePath()))){
            like.setSelected(true);
        } else {
            like.setSelected(false);
        }
    }

    private void convert2nii(ActionEvent actionEvent) {
        ListContainer selected = null;
        selected = (ListContainer) listView.getSelectionModel().getSelectedItem();
        if (selected.getType().equals(FileType.fid) || selected.getType().equals(FileType.twodseq)  ) {
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
            String path = selected.getFile().getPath();
            StringProperty loggerText = new SimpleStringProperty("logger:");
            cntrl.logger.textProperty().bind(loggerText);
            cntrl.convertButton.setOnAction(e -> {
                cntrl.progressBar.setVisible(true);
                if (path !=null) {
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

                    Path path2nii = Paths.get(path).getParent().resolve(niftiName);
                    Bruker2nii bruker2nii = new Bruker2nii(path);
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
                updateFolderViewer();
            });
        }
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
//        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
//        stage.showAndWait();
        stage.show();


//            ObservableList observableList = FXCollections.observableArrayList(selected);
        RenameCtrlr renamctrlr = (RenameCtrlr) renameDialog.getController();
//            renamctrlr.datasets.setItems(observableList);
//            renamctrlr.datasets.getSelectionModel().selectFirst();

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
//        renamctrlr.parameters.setItems(FXCollections.observableArrayList(CurrentUser.getUser().selectedParameters));
//        renamctrlr.parameters.getSelectionModel().selectFirst();
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
                                file = new File(((ListContainer) (listContainer)).getFile().getPath()+"_copy");
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
                                            if (!file.renameTo(new File(absPath ,studyName))) {
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
            TextFields.bindAutoCompletion(textField,keysets);
        } catch (Exception e) {

        }
    }

    private void AddParam(KeyEvent actionEvent) {
        try {
            CurrentUser.getUser().selectedParameters.add(parameterTextField.getText());
        } catch (Exception e) { }
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
//        switch (selected.getFileName().toString()){
////            case "acqp":
////                System.out.println("acqp");
////                ProcessBuilder pb = new ProcessBuilder("Notepad.exe", selected.toString());
////                pb.start();
////                break;
////            case "method":
////                System.out.println("acqp");
////                break;
////            case "reco":
////                System.out.println("acqp");
////                break;
////            case "visupars":
////                System.out.println("acqp");
////                break;
//            case "fid":
//                System.out.println("fid");
//                break;
//            case "2dseq":
//                System.out.println("2dseq");
//                break;
//            default:

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
                item.put("Values" , tostring);
            } catch (Exception e) {

            }
            items.add(item);
        }

        tableParams.getItems().setAll(items);


    }

    public JcampdxData getStudyList(File f) {
        JcampdxData jcampdxData = null;
        if(f.isDirectory()) {
            try {
                String acqp_ = (new File(f , "acqp")).toString();
                String method_ = (new File(f , "method")).toString();
                File seq_ = new File(f, "pdata" + File.separator + "1");
                String visupars_ = (new File(seq_, "visu_pars")).toString();
                String reco_ = (new File(seq_, "reco")).toString();
                Map<String, Object> jcampdx_file = null;
                if(Arrays.asList(f.list()).contains("acqp")) {
                    jcampdx_file = Jcampdx.read_jcampdx_file(acqp_);
                }
                if(Arrays.asList(f.list()).contains("method")) {
                    jcampdx_file.putAll(Jcampdx.read_jcampdx_file(method_));
                }
                if(Arrays.asList(seq_.list()).contains("reco")) {
                    jcampdx_file.putAll(Jcampdx.read_jcampdx_file(visupars_));
                }
                if(Arrays.asList(seq_.list()).contains("visu_pars")) {
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
}
