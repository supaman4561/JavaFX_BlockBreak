/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockbreak;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author PCUser
 */
public class GameMainController implements Initializable {
    
    @FXML
    private Text myName;
    
    @FXML
    private Text opponentName;
    
    /**
     * instanvce(singleton)
     */
    private static final GameMainController INSTANCE;
    
    /**
     * Scene(singleton)
     */
    private static final Scene SCENE;
    
    static {
        FXMLLoader fxmlLoader = new FXMLLoader(BlockBreak.class.getResource("GameMain.fxml"));
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.getStackTrace();
        }
        Parent parent = fxmlLoader.getRoot();
        parent.requestFocus();
        Scene s = new Scene(parent);
        s.getRoot().requestFocus();
        s.setFill(Color.TRANSPARENT);
        SCENE = s;
        INSTANCE = fxmlLoader.getController();
    }
    
    /**
     * return instance(singleton)
     * @return INSTANCE
     */
    public static GameMainController getInstance() {
        return INSTANCE;
    }
    
    public void show() {
        BlockBreak.getPresentStage().setScene(SCENE);
    }
    
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        System.out.println("keypressed");
        System.out.println(event.getCode());
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        myName.setText(BlockBreak.getUserName());
    }    
    
    
    
}
