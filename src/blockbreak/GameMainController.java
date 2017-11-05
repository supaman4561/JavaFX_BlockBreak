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
import javafx.scene.layout.*;
import javafx.scene.shape.*;
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

    @FXML
    private Pane root;

    @FXML
    private Rectangle upperpaddle;

    @FXML
    private Rectangle underpaddle;

    /**
     * instanvce(singleton)
     */
    private static final GameMainController INSTANCE;

    /**
     * Scene(singleton)
     */
    private static final Scene SCENE;

    /**
     * for sending to Server
     */
    Circle ball;
    PrintWriter myOut;

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
        ball = new Circle(5.0);
        MesgRecvThread mrt = new MesgRecvThread(BlockBreak.getSocket(), BlockBreak.getUserName());
        mrt.start();
    }
    public void PaddleMoveLeft(String SendNum) {
      if(SendNum.equals("0")) underpaddle.setX(Math.max(underpaddle.getX()-20,150*-1+underpaddle.getWidth()/2));
      if(SendNum.equals("1")) upperpaddle.setX(Math.max(upperpaddle.getX()-20,150*-1+upperpaddle.getWidth()/2));
    }
    public void PaddleMoveRight(String SendNum){
      if(SendNum.equals("0")) underpaddle.setX(Math.min(underpaddle.getX()+20,150-underpaddle.getWidth()/2));
      if(SendNum.equals("1")) upperpaddle.setX(Math.min(upperpaddle.getX()+20,150-upperpaddle.getWidth()/2));
    }

    public class MesgRecvThread extends Thread {

        Socket socket;
        String myName;

        public MesgRecvThread(Socket s, String n) {
            socket = s;
            myName = n;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                myOut = new PrintWriter(socket.getOutputStream(), true);
                myOut.println(myName);
                while(true) {
                    String inputLine = br.readLine();
                    if(inputLine != null) {

                        System.out.println(inputLine);
                        String[] inputTokens = inputLine.split(",",-1);
                        String cmd = inputTokens[0];
                        System.out.println(cmd);


                    System.out.println(inputTokens.length);
                      if(cmd.equals("Paddle")){
                          String cmd2 = inputTokens[1];
                          String cmd3 = inputTokens[2];

                          if(cmd2.equals("LEFT")){
                            PaddleMoveLeft(cmd3);
                        }
                          if(cmd2.equals("RIGHT")){
                            PaddleMoveRight(cmd3);
                        }
                      }
                      if(cmd.equals("Ball")){
                            ball.setCenterX(Integer.parseInt(inputTokens[2]));
			                      ball.setCenterY(Integer.parseInt(inputTokens[3]));
                        }
                    }else{
                        break;
                    }
                }
		socket.close();
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
	myOut.println(event.getCode());
	myOut.flush();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        myName.setText(BlockBreak.getUserName());
	root.getChildren().add(ball);
    }



}
