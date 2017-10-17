/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockbreak;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.*;
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
    
    PrintWriter out;

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
    
    public GameMainController() {
        MesgRecvThread mrt = new MesgRecvThread(BlockBreak.getSocket(), BlockBreak.getUserName());
        mrt.start();
    }
    
    public class MesgRecvThread extends Thread {
        
        Socket socket;
        String myName;
        PrintWriter out;
        
        public MesgRecvThread(Socket s, String n) {
            socket = s;
            myName = n;
        }
        
        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(myName);
                while(true) {
                    String inputLine = br.readLine();
                    if(inputLine != null) {
                        System.out.println(inputLine);
                        String[] inputTokens = inputLine.split(",");
                        String cmd = inputTokens[0];
                        if(cmd.equals("hoge")){
                            // TODO
                        }
                    }else{
                        break;
                    }
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("error occured: " + e);
            }
        }
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
