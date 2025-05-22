module Final_Cine_Mrk2 {
    // Requiere módulos de JavaFX para la interfaz gráfica
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    // Requiere librerías externas que usas en tu proyecto
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
    requires com.github.librepdf.openpdf;

    // Abre paquetes específicos para reflexión (usado por JavaFX y otras librerías)
    opens Main to javafx.graphics, javafx.fxml; // Paquete donde está Main.java
    opens controller to javafx.fxml;           // Ajusta si tienes controladores en el paquete "controller"
    opens modelo to javafx.base;               // Ajusta si tienes modelos en el paquete "modelo"
    opens FXML to javafx.fxml;                 // Ajusta si tienes archivos FXML en un paquete llamado "FXML"

    // Exporta paquetes necesarios para otros módulos
    exports Main;     // Exporta el paquete Main
    exports controller;
    exports modelo;
}