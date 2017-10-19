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
    private static Socket socket = null;
    
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
    
    public static Socket getSocket() {
        return socket;
    }
    
    public static Stage getPresentStage() {
        return presentStage;
    }
 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 10027);
        } catch (UnknownHostException e) {
            System.err.println("Not found IPAddress of host." + e);
        } catch (IOException e) {
            System.err.println("The error occured." + e);
        }
        launch(args);
    }
    
}
