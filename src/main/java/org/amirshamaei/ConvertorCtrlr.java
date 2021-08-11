package org.amirshamaei;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ConvertorCtrlr implements Initializable {


    @FXML
    TextField niftiName;
    @FXML
    ChoiceBox dataType;

    @FXML
    Button convertButton;

    @FXML
    TextArea logger;
    @FXML
    RadioButton compression;
    @FXML
    ProgressIndicator progressBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
