package org.amirshamaei;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class BookmarkCtrlr implements Initializable {
    private static BookmarkCtrlr bookmarkCtrlr = new BookmarkCtrlr();
    @FXML
    ListView likedlist;
    @FXML
    TextField namelikedpath;
    @FXML
    TextField deslikedpath;
    @FXML
    Button savelikedpath;
    @FXML
    Button cacellikedpath;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public static BookmarkCtrlr getBookmarkCtrlr() {
        return bookmarkCtrlr;
    }
}
