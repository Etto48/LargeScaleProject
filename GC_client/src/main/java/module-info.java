module it.unipi.gc_client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens it.unipi.gc_client to javafx.fxml;
    exports it.unipi.gc_client;
}