package de.gfi.felix.abschlussprojekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MenueController menue = new MenueController();
        menue.showDialog();
    }

    public static void main(String[] args) {
        launch();
    }
}