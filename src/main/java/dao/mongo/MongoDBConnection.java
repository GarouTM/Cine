package dao.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static Process mongoProcess;  // Añadimos una referencia al proceso

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            startMongoDB();
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("Cine");
            System.out.println("Conexión establecida con MongoDB.");
        }
        return database;
    }

    private static void startMongoDB() {
        try {
            mongoProcess = new ProcessBuilder("mongod", "--dbpath", "C:\\data\\db")
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();

            // Esto registrar un shutdown hook para asegurar que MongoDB se cierre al terminar la aplicacion
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                stopMongoDB();
            }));

            Thread.sleep(2000);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error iniciando MongoDB: " + e.getMessage());
        }
    }

    private static void stopMongoDB() {
        if (mongoProcess != null) {
            mongoProcess.destroy(); // Termina el proceso de MongoDB
            try {
                boolean terminado = mongoProcess.waitFor(5, TimeUnit.SECONDS);
                if (!terminado) {
                    mongoProcess.destroyForcibly(); // Fuerza el cierre si no termina normalmente
                }
            } catch (InterruptedException e) {
                mongoProcess.destroyForcibly();
            }
            mongoProcess = null;
            System.out.println("Proceso de MongoDB terminado.");
        }
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("Conexión cerrada con MongoDB.");
        }
        stopMongoDB(); // Asegurarse de que el proceso de MongoDB también se detenga
    }
}