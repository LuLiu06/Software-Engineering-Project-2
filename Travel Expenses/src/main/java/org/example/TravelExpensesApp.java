package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;

public class TravelExpensesApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/TravelExpenses.fxml"),
                null,
                null,
                null,
                StandardCharsets.UTF_8
        );
        Parent root = loader.load();

        Scene scene = new Scene(root, 500, 450);
        primaryStage.setTitle("Travel Expenses Calculator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
