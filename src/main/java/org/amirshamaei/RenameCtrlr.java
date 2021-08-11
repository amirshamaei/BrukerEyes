package org.amirshamaei;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;


public class RenameCtrlr implements Initializable {
    @FXML
    TextField parameter;
    @FXML
    ChoiceBox datasets;
    @FXML
    HBox parameters;
    @FXML
    Button add;
    @FXML
    Button renameButton;
    @FXML
    Button remove;
    @FXML
    TextArea logger;
    @FXML
    CheckBox copy;
    @FXML
    ProgressIndicator progressbar;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        HashMap<String, JcampdxData> studyList = CurrentUser.getUser().studyLists.get(CurrentUser.getUser().lastSelectedItem.getParent().getValue());
//        Set<String> keysets = ((Map<String, Object>) ((JcampdxData) studyList.get(CurrentUser.getUser().lastSelectedItem.getValue())).getParameters()).keySet();
//        TextFields.bindAutoCompletion(text,keysets);
    }
}
