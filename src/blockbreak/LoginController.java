/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockbreak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author PCUser
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField fieldName;
    
    /**
     * instance(singleton) 
     */
    private static final LoginController INSTANCE;
    
    /**
     * Scene(singleton)
     */
    private static final Scene SCENE;
     
    static {
        FXMLLoader fxmlLoader = new FXMLLoader(BlockBreak.class.getResource("Login.fxml"));
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.getStackTrace();
        }
        Parent parent = fxmlLoader.getRoot();
        Scene s = new Scene(parent);
        s.setFill(Color.TRANSPARENT);
        SCENE = s;
        INSTANCE = fxmlLoader.getController();   
    }
     
    /**
     * return instance(singleton)
     * @return INSTANCE
     */
    public static LoginController getInstance() {
        return INSTANCE;
    }
    
    public void show() {
        BlockBreak.getPresentStage().setScene(SCENE);
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println(fieldName.getText());
        BlockBreak.setUserName(fieldName.getText());
        GameMainController.getInstance().show();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fieldName.setPromptText("input name");
        fieldName.setFocusTraversable(false);
    }    
    
    
}
