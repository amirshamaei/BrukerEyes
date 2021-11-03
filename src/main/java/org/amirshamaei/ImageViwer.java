package org.amirshamaei;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;


import java.net.URL;
import java.util.ResourceBundle;

public class ImageViwer implements Initializable {
    @FXML
    AnchorPane imagePane;
    @FXML
    Button left;
    @FXML
    Button right;
    @FXML
    Label id;
    @FXML
    Label all;
    @FXML
    Slider slider;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public AnchorPane getImagePane() {
        return imagePane;
    }
}
