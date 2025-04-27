package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Cargar el FXML del login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            // Configurar la escena
            Scene scene = new Scene(root);

            // Configurar el Stage
            primaryStage.setTitle("House Cinema - Login");
            primaryStage.setScene(scene);
            // Evitar que se pueda redimensionar la ventana de login
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}