/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockbreak;

import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.beans.property.*;

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
    private static Socket mainSocket = null;
    private static int id;
    private ArrayList<Circle> arrayBall = new ArrayList<Circle>();
    private int ballMax = 1;
    private ArrayList<ColoredRect> arrayBlock = new ArrayList<ColoredRect>();
    private Rectangle EnemyPaddle = new Rectangle(120,130,60,5);
    private Rectangle MyPaddle = new Rectangle(120,487,60,5);

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

        for (int i=0; i<ballMax; i++){
            arrayBall.add(new Circle(5.0));
            arrayBall.get(i).visibleProperty().bind(new SimpleBooleanProperty(true));
        }

        try {
            mainSocket = new Socket("localhost", 10027);
        } catch (UnknownHostException e) {
            System.err.println("Not found IPAddress of host." + e);
        } catch (IOException e) {
            System.err.println("The error occured." + e);
        }

        MesgRecvThread mrt = new MesgRecvThread(mainSocket , BlockBreak.getUserName());
        mrt.start();

    }

    public float MoveLeft(Rectangle Paddle, float mySpeed) {
	mySpeed -= 1.42;
	return mySpeed;
    }

    public float MoveRight(Rectangle Paddle, float mySpeed) {
	mySpeed += 1.42;
	return mySpeed;
    }

    public float MovePaddle(Rectangle Paddle, float mySpeed) {
	if (mySpeed == 0.0) mySpeed += 0.0;
	if (mySpeed < 0.0) {
	    Paddle.setX(Math.max(Paddle.getX() + mySpeed, 150 * -1 + Paddle.getWidth() / 2));
	    mySpeed *= 0.90;
	}
	if (mySpeed > 0.0) {
	    Paddle.setX(Math.min(Paddle.getX() + mySpeed, 150 - Paddle.getWidth() / 2));
	    mySpeed *= 0.90;
	}
	return mySpeed;
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
                while(true) {
                    String inputLine = br.readLine();
                    if(inputLine != null) {
			             // for debug
                        // System.out.println(inputLine);
                        String[] inputTokens = inputLine.split(",");
                        String cmd = inputTokens[0];

                        if(cmd.equals("Hello")){
                            System.out.println(inputLine);
                            id = Integer.parseInt(inputTokens[1]);
                        }else if(cmd.equals("Ball")){
                            int ballId = Integer.parseInt(inputTokens[1]);

                            Thread thread = new BallMoveThread(arrayBall.get(ballId),
                                                               Integer.parseInt(inputTokens[2]),
                                                               Integer.parseInt(inputTokens[3]));
                            thread.start();
                        }else if(cmd.equals("Paddle")) {
                            if (inputTokens[1].equals("LEFT")) mySpeed = MoveLeft(MyPaddle, mySpeed);
                            if (inputTokens[1].equals("RIGHT")) mySpeed =  MoveRight(MyPaddle, mySpeed);
                            String SendMesg = new String("Paddle," + id + "," + MyPaddle.getX());
                            myOut.println(SendMesg);

                        }else if (cmd.equals("EnemyPaddle")) {
                            EnemyPaddle.setX(-1.0*Float.valueOf(inputTokens[2]));
                        }else if(cmd.equals("Blockset")){

                            ColoredRect target;
                            int x;
                            int y;

                            if(inputTokens[1].equals("end")){
                                if(id % 2 == 1){
                                     Collections.reverse(arrayBlock);
                                }

                                System.out.println(id);
                                for(int i=0; i < arrayBlock.size() ; i++){
                                    System.out.println(arrayBlock.get(i).getY());
                                }
                                Platform.runLater(() -> root.getChildren().addAll(arrayBlock));
                            }else{
                                x = Integer.parseInt(inputTokens[1]);
                                y = Integer.parseInt(inputTokens[2]);

                                // width : 50 , height : 20
                                target = new ColoredRect(x, y, 50, 20);
                                target.setColor(arrayBlock.size());
                                arrayBlock.add(target);

                            }

                        }else if(cmd.equals("BlockDelete")){
                            int blockId = Integer.parseInt(inputTokens[1]);
                            arrayBlock.get(blockId).visibleProperty().bind(new SimpleBooleanProperty(false));
                        }else{
                            break;
                        }

                    }
		            mySpeed = MovePaddle(MyPaddle, mySpeed);
                }
		        socket.close();
            } catch (IOException e) {
                System.err.println("error occured: " + e);
            }
        }
    }

    class BallMoveThread extends Thread{

        private Circle target;
        private int x;
        private int y;

        BallMoveThread(Circle ball, int x, int y){
            this.target = ball;
            this.x = x;
            this.y = y;
        }

        public void run() {
            if(id == 0){
                target.setCenterX(x);
                target.setCenterY(y);
            } else {
                target.setCenterX(root.getWidth() - x);
                target.setCenterY(root.getHeight() - y);
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
	//  System.out.println("keypressed");
	//  System.out.println(event.getCode());
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

	root.getChildren().addAll(arrayBall);
	root.getChildren().add(EnemyPaddle);
	root.getChildren().add(MyPaddle);

    }



}
