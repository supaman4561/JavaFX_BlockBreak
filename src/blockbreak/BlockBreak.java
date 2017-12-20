/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockbreak;

import java.net.*;
import java.io.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Sudo
 */
public class BlockBreak extends Application {

    private static Stage presentStage;
    private static String userName;

    @Override
    public void start(Stage stage) throws Exception {
        presentStage = stage;

        // Title
        stage.setTitle("BlockBreak");

        // create scene
        Scene scene = new Scene(new Pane());
        stage.setScene(scene);

        // first screan
        LoginController.getInstance().show();

        // display
        stage.show();

    }

    public static void setUserName(String name) {
        userName = name;
    }

    public static String getUserName() {
        return userName;
    }

    public static Stage getPresentStage() {
        return presentStage;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        launch(args);
    }

}
