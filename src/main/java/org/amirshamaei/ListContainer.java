package org.amirshamaei;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

public class ListContainer extends HBox {
    private File file;
    Label label = new Label();
    FileType type = FileType.unknown;
    public ListContainer() {
        ImageView img = new ImageView();
        File f = new File("src\\main\\resources\\org\\amirshamaei\\icons\\folder.png");
        Image image = new Image(f.toURI().toString());
        img.setImage(image);
        img.setFitHeight(50);
        img.setFitWidth(50);
        this.getChildren().add(img);
        this.getChildren().add(label);
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    public ListContainer(File file) {
        this.file = file;
        ImageView img = new ImageView();
        Image image = null;
        if(file.isFile()) {
            switch (file.getName()) {
                case "acqp": {
                    try {
                        image = new Image(getClass().getResource("icons/acqp.png").toURI().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    type = FileType.text;
                    break;
                }
                case "method": {
                    try {
                        image = new Image(getClass().getResource("icons/method.png").toURI().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    type = FileType.text;
                    break;
                }
                case "reco": {
                    try {
                        image = new Image(getClass().getResource("icons/reco.png").toURI().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    type = FileType.text;
                    break;
                }
                case "visu_pars": {
                    try {
                        image = new Image(getClass().getResource("icons/visu_pars.png").toURI().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    type = FileType.text;
                    break;
                }
                case "fid": {
                    try {
                        image = new Image(getClass().getResource("icons/fid.png").toURI().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    type = FileType.fid;
                    break;
                }
                case "2dseq": {
                    try {
                        image = new Image(getClass().getResource("icons/2dseq.png").toURI().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    type = FileType.twodseq;
                    break;
                }
                default: {
                    try {
                        image = new Image(getClass().getResource("icons/file.png").toURI().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if(file.isDirectory()){
            try {
                image = new Image(getClass().getResource("icons/folder.png").toURI().toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            type = FileType.directory;
            try {
                if (Arrays.asList(file.list()).contains("fid")) {
                    type = FileType.brukerDirectory;
                    try {
                        image = new Image(getClass().getResource("icons/bruker.png").toURI().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
            }
        }

        img.setImage(image);
        img.setFitHeight(50);
        img.setFitWidth(40);
        this.getChildren().add(img);
        this.getChildren().add(label);
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);
        if (file.getName().isEmpty())
            label.setText(file.getPath());
        else
            label.setText(file.getName());
    }

    public Label getLabel() {
        return label;
    }

    public File getFile() {
        return file;
    }

    public FileType getType() {
        return type;
    }
}
