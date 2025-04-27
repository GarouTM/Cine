module org.example.final_cine_mrk2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;

    opens Main to javafx.graphics, javafx.fxml;
    opens controller to javafx.fxml;
    opens modelo to javafx.base;
    opens org.example.final_cine_mrk2 to javafx.fxml;
    exports org.example.final_cine_mrk2;
}