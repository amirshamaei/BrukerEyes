module org.amirshamaei {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires JBruker;
    requires bruker2nii;
    requires java.sql;
    opens org.amirshamaei to javafx.fxml;
    exports org.amirshamaei;
}
