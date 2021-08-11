package org.amirshamaei;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ImageViwer implements Initializable {
    @FXML
    StackPane imagePane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public StackPane getImagePane() {
        return imagePane;
    }
}
