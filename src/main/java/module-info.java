module org.amirshamaei {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires JBruker;
    requires bruker2nii;
    requires java.sql;
    requires java.base;
    requires java.desktop;
    opens org.amirshamaei to javafx.fxml, JBruker, java.base;

    exports org.amirshamaei;
}
