module org.wms {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;

    opens org.wms to javafx.fxml;
    exports org.wms;
}
