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
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.beans.property.*;
import java.util.concurrent.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
    private Rectangle MyPaddle;

    @FXML
    private Rectangle EnemyPaddle;

    @FXML
    private Text AnimationText;

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
    private ArrayList<ColoredRect> arrayBlock = new ArrayList<ColoredRect>();

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

    public void MovePaddle(Rectangle MyPaddle,KeyCode key){

        int pX = 0;

        if(key == KeyCode.LEFT){
            pX = (int)Math.max(MyPaddle.getX() - 20,150 * -1 + MyPaddle.getWidth() / 2);
            String SendMesg = new String("Paddle," + id + "," + pX);
            myOut.println(SendMesg);
            Platform.runLater(() -> MyPaddle.setX(Math.max(MyPaddle.getX() - 20,150 * -1 + MyPaddle.getWidth() / 2)));
        }else if(key == KeyCode.RIGHT){
            pX = (int)Math.min(MyPaddle.getX() + 20,150 - MyPaddle.getWidth() / 2);
            String SendMesg = new String("Paddle," + id + "," + pX);
            myOut.println(SendMesg);
            Platform.runLater(() -> MyPaddle.setX(Math.min(MyPaddle.getX() + 20,150 - MyPaddle.getWidth() / 2)));
        }

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
                            if(ballId > arrayBall.size() - 1){
                                arrayBall.add(new Circle(5.0));
                                Platform.runLater(() -> root.getChildren().add(arrayBall.get(ballId)));
                            }

                            Thread thread = new BallMoveThread(arrayBall.get(ballId),
                            Integer.parseInt(inputTokens[2]),
                            Integer.parseInt(inputTokens[3]));
                            thread.start();
                        }else if (cmd.equals("EnemyPaddle")) {
                            Platform.runLater(() -> EnemyPaddle.setX(-1.0*Integer.parseInt(inputTokens[2])));
                        }else if(cmd.equals("Blockset")){

                            ColoredRect target;
                            int x;
                            int y;

                            if(inputTokens[1].equals("end")){
                                if(id % 2 == 1){
                                    Collections.reverse(arrayBlock);
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
                        }else if(cmd.equals("Animation")){
                          AnimationText.visibleProperty().bind(new SimpleBooleanProperty(true));
                          if(!inputTokens[1].equals("0")){
                            show_text(AnimationText,String.valueOf(inputTokens[1]),Integer.parseInt(inputTokens[2]));
                          }else{
                            show_text(AnimationText,"GAMESTART",50);
                          }
                        }else if(cmd.equals("AnimationFinish")){
                          AnimationText.visibleProperty().bind(new SimpleBooleanProperty(false));
                        }else if(cmd.equals("Win")){
                            // Animation
                            AnimationText.visibleProperty().bind(new SimpleBooleanProperty(true));
                            show_text(AnimationText,cmd,50);
                            System.out.println("win");
                        }else if(cmd.equals("Lose")){
                            // Animation
                            AnimationText.visibleProperty().bind(new SimpleBooleanProperty(true));
                            show_text(AnimationText,cmd,50);
                            System.out.println("lose");
                        }else{
                            break;
                        }

                    }
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
                Platform.runLater(() ->  target.setCenterX(x));
                Platform.runLater(() -> target.setCenterY(y));
            } else {
                Platform.runLater(() -> target.setCenterX(root.getWidth() - x) );
                Platform.runLater(() -> target.setCenterY(root.getHeight() - y -10));
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
        MovePaddle(MyPaddle,event.getCode());

    }

    /**
    * Initializes the controller class.
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        myName.setText(BlockBreak.getUserName());

    }

    public static void show_text(Text AnimationText, String str,int size){
        AnimationText.setX(-size/4);
        AnimationText.setY(size/4);
        AnimationText.setFont(Font.font(size));
        AnimationText.setText(str);
    }

}
