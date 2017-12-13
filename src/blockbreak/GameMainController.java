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
import javafx.scene.input.KeyCode;
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
private Rectangle EnemyPaddle;

@FXML
private Rectangle MyPaddle;

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
String myNumber;

static {
  FXMLLoader fxmlLoader = new FXMLLoader(BlockBreak.class.getResource("GameMain.fxml"));
  try {
    fxmlLoader.load();
  }
  catch (IOException e) {
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

public void MovePaddle(Rectangle MyPaddle,KeyCode key){
  if(key == KeyCode.LEFT)  MyPaddle.setX(Math.max(MyPaddle.getX() - 20,150 * -1 + MyPaddle.getWidth() / 2));
  if(key == KeyCode.RIGHT) MyPaddle.setX(Math.min(MyPaddle.getX() + 20,150 - MyPaddle.getWidth() / 2));
  String SendMesg = new String("Paddle," + myNumber + "," + (int)MyPaddle.getX());
  myOut.println(SendMesg);
}

public class MesgRecvThread extends Thread {
Socket socket;
String myName;
float mySpeed = 0;


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
    while (true) {

      String inputLine = br.readLine();
      if (inputLine != null) {
        String[] inputTokens = inputLine.split(",", -1);
        String cmd = inputTokens[0];

        if(cmd.equals("Hello")){
          myNumber = inputTokens[2];
          System.out.println(myNumber);
        }
        if (cmd.equals("Ball")) {
          if(myNumber.equals("0")){
            ball.setCenterX(Integer.parseInt(inputTokens[2]));
            ball.setCenterY(Integer.parseInt(inputTokens[3]));
          }else{
            ball.setCenterX(300 - Integer.parseInt(inputTokens[2]));
            ball.setCenterY(620 - Integer.parseInt(inputTokens[3]));
          }
        }
        if (cmd.equals("EnemyPaddle")) {
          EnemyPaddle.setX(-1.0*Float.valueOf(inputTokens[2]));
        }
      }else{
        break;
      }
    }
    socket.close();
  }
  catch (IOException e) {
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
  MovePaddle(MyPaddle,event.getCode());
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
