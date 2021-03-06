package org.amirshamaei;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class login implements Initializable {
    @FXML
    ComboBox users;
    @FXML
    TextField username;
    @FXML
    Button update;
    ObservableList<User> usersList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        UsersData.getInstance().addUser(new User("guset"));
        Users.getInstance().loadUsers();
        users.setItems(FXCollections.observableArrayList(Users.getInstance().getUsersList()));
        username.setOnKeyTyped(actionEvent -> {
            if (username.getText().isEmpty()) {
                users.setDisable(false);
            } else {
                users.setDisable(true);
            }

        });
        update.setOnAction(e ->{
            try {
                openWebpage(new URL("https://sourceforge.net/projects/brukereyes/files/"));
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            }
        });
    }
    public static boolean openWebpage(URI uri) {
        java.awt.Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
    @FXML
    public void close() {
        System.exit(0);
    }


    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @FXML
    public void login() throws IOException, InterruptedException, URISyntaxException {
        if (!users.isDisable()) {
            String selectedUser = (String) Users.getInstance().getUsersList().get(users.getSelectionModel().getSelectedIndex());
            CurrentUser.getInstance().loadUser(selectedUser);
        } else {
            Users.getInstance().getUsersList().add(username.getText());
            Users.getInstance().saveUsers();
            String selectedUser = username.getText();
            CurrentUser.setUser(new User(selectedUser));
            CurrentUser.getUser().pathViewr = "C:\\WINDOWS\\system32\\notepad.exe";
        }
        App.stage.hide();
        Scene scene = new Scene(loadFXML("main"), 1000, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setOnCloseRequest(windowEvent -> {
            try {
                CurrentUser.getInstance().saveUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Image icon = new Image(getClass().getResource("icons/icon.png").toURI().toString());
        stage.getIcons().add(icon);
        stage.show();
    }

}
