package org.amirshamaei;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;

public class CurrentUser {
    private static User user;
    public static CurrentUser currentUser = new CurrentUser();

    public CurrentUser() {

    }

    public static void setUser(User user) {
        CurrentUser.user = user;
    }

    public static User getUser() {
        return user;
    }

    public static CurrentUser getInstance() {
        return currentUser;
    }

    public void saveUser() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter(user.username + ".json");
        gson.toJson(this.user,User.class,writer);
        writer.flush();
    }
    public void loadUser(String username) throws FileNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Reader reader = new FileReader(username + ".json");
            CurrentUser.user = gson.fromJson(reader, User.class);
        } catch (FileNotFoundException e) {
            CurrentUser.user = new User(username);
        } catch (JsonSyntaxException e) {
            CurrentUser.user = new User(username);
        } catch (JsonIOException e) {
            CurrentUser.user = new User(username);
        }
    }

}
