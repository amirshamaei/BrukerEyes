package org.amirshamaei;

import bruker_plugin_lib.JcampdxData;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class User{
    String username;
    TreeItem lastSelectedItem;
    LinkedHashMap<String, HashMap<String, JcampdxData>> studyLists = new LinkedHashMap<>();
    LinkedHashMap<String, String> datasets = new LinkedHashMap<>();
    ArrayList<String> selectedParameters = new ArrayList();
    String pathViewr;
    Boolean dontshoweditor = false;

    public User(String username) {
        this.username = username;
        selectedParameters.add("ACQ_protocol_name");
        selectedParameters.add("SFO1");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return username;
    }


}
