module org.amirshamaei {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires bruker2nii;
    requires java.sql;
    requires java.base;
    requires java.desktop;
    requires javafx.swing;
    requires JBruker;
    requires JTransforms;
    opens org.amirshamaei to javafx.fxml, JBruker, java.base, org.controlsfx.controls;
    opens org.amirshamaei.IDV to javafx.fxml, JBruker, java.base, org.controlsfx.controls;
    exports org.amirshamaei;
    exports org.amirshamaei.IDV;
}
