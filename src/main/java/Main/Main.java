package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Actualizamos la ruta para que coincida con la carpeta FXML
            URL fxmlUrl = Main.class.getResource("/FXML/login.fxml");
            if (fxmlUrl == null) {
                throw new IllegalStateException("No se puede encontrar el archivo FXML: /FXML/login.fxml");
            }

            System.out.println("FXML encontrado en: " + fxmlUrl);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("House Cinema - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch(Exception e) {
            System.err.println("Error al cargar la aplicación: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}