package org.amirshamaei;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class login implements Initializable {
    @FXML
    ComboBox users;
    @FXML
    TextField username;

    ObservableList<User> usersList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        UsersData.getInstance().addUser(new User("guset"));
        users.setItems(UsersData.getInstance().getUsers());
        username.setOnKeyTyped(actionEvent -> {
            if (username.getText().isEmpty()) {
                users.setDisable(false);
            } else {
                users.setDisable(true);
            }

        });
    }
    @FXML
    public void close() {
        System.exit(0);
    }
    @FXML
    public void reglogin() throws IOException {
        User newUser;
        if (!username.getText().isEmpty()) {
            UsersData.getInstance().addUser(new User(username.getText()));
            UsersData.getInstance().storeUsers();
            App.stage.hide();
            Scene scene = new Scene(loadFXML("main"), 1000, 600);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } else {
        }

    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @FXML
    public void login() throws IOException, InterruptedException {
        User selectedUser = UsersData.getInstance().getUsers().get(users.getSelectionModel().getSelectedIndex());
        CurrentUser.setUser(selectedUser);
        App.stage.hide();
        Scene scene = new Scene(loadFXML("main"), 1000, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

}
