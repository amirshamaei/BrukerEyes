package org.amirshamaei;

import bruker_plugin_lib.JcampdxData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedHashTreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class User  {
    public ArrayList<LikedPath> likedPaths = new ArrayList<>();
    public String username;
    public ArrayList<String> selectedParameters = new ArrayList();
    public String pathViewr;
    public Boolean dontshoweditor = false;
    public String cd;

    public User(String username) {
        this.username = username;
        selectedParameters.add("ACQ_protocol_name");
        selectedParameters.add("SFO1");
    }

    public File getCD() {
        return new File(cd);
    }

    public void setCd(File cd) {
        try {
            this.cd = cd.toString();
        } catch (Exception e) {
            this.cd = null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<LikedPath> getLikedPaths() {
        return likedPaths;
    }

    @Override
    public String toString() {
        return username;
    }



}
