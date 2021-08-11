package org.amirshamaei;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class UsersData {
    private static UsersData instance = new UsersData();
    private static String filename;

    private ObservableList<User> users;


    public static UsersData getInstance() {
        return instance;
    }

    private UsersData() {
        filename = "usersdata.txt";
        try {
            this.loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void loadUsers() throws IOException {

        users = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(path);
        } catch (IOException e) {

        }

        String input;

        try {
            while ((input = br.readLine()) != null) {
                User todoItem = new User(input);
                users.add(todoItem);
            }

        } finally {
            if(br != null) {
                br.close();
            }
        }
    }

    public void storeUsers() throws IOException {

        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {
            Iterator<User> iter = users.iterator();
            while(iter.hasNext()) {
                User item = iter.next();
                bw.write(String.format("%s",
                        item.getUsername()));
                bw.newLine();
            }

        } finally {
            if(bw != null) {
                bw.close();
            }
        }
    }
}
