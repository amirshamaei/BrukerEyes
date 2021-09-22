package org.amirshamaei;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.ArrayList;

public class Users {
    public static Users users = new Users();
    ArrayList<String> usersList = new ArrayList();

    public Users() {

    }

    public static void setInstance(Users users) {
        Users.users = users;
    }

    public static Users getInstance() {
        return users;
    }

    public ArrayList getUsersList() {
        return usersList;
    }

    public void loadUsers() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Reader reader = new FileReader("users.json");
            Users.setInstance(gson.fromJson(reader, Users.class));
        } catch (FileNotFoundException e) {
            System.out.println("users.json not found, create a new one");
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
    }

    public void saveUsers() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter("users.json");
        gson.toJson(this,Users.class,writer);
        writer.flush();
    }
}
