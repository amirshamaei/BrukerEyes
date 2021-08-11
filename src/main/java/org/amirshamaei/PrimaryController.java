package org.amirshamaei;

import javafx.fxml.FXML;

import java.io.IOException;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
