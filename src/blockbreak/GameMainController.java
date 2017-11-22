/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockbreak;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import java.util.ArrayList;
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
    int id;
    Circle ball;
    ArrayList<Rectangle> myblock = new ArrayList<Rectangle>();
    ArrayList<Rectangle> enemyblock = new ArrayList<Rectangle>();
    
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

	for (int i=0; i<20; i++){
	    myblock.add(new Rectangle());
	    enemyblock.add(new Rectangle());
	}
	
	MesgRecvThread mrt = new MesgRecvThread(BlockBreak.getSocket(), BlockBreak.getUserName());
        mrt.start();

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
			// for debug
                        // System.out.println(inputLine);
                        String[] inputTokens = inputLine.split(",");
                        String cmd = inputTokens[0];
			if(cmd.equals("Hello")){
			    id = Integer.parseInt(inputTokens[1]);
			}else if(cmd.equals("Ball")){
                            if(id == 0){
				ball.setCenterX(Integer.parseInt(inputTokens[2]));
				ball.setCenterY(Integer.parseInt(inputTokens[3]));
			    } else {
				ball.setCenterX(root.getWidth() - Integer.parseInt(inputTokens[2]));
				ball.setCenterY(root.getHeight() - Integer.parseInt(inputTokens[3]));
			    }
                        }else if(cmd.equals("Blockset")){
			    int x = Integer.parseInt(inputTokens[1]);
			    int y = Integer.parseInt(inputTokens[2]);
			    int b = Integer.parseInt(inputTokens[3]);
			    int num = Integer.parseInt(inputTokens[4]);

			    num %= 2;
			    if((b<20&&num==0)||(b>=20&&num==1)){
				if(b>=20){
				    b-=20;

				    if(num==1){
					x = 300-x-50;
					y = 600-y-20;
				    }
				}
				enemyblock.get(b).setX(x);
				enemyblock.get(b).setY(y);
				enemyblock.get(b).setWidth(50);
				enemyblock.get(b).setHeight(20);
				switch(b%5){
				case 0:
				    enemyblock.get(b).setFill(Color.RED);
				    break;
				case 1:
				    enemyblock.get(b).setFill(Color.BLUE);
				    break;
				case 2:
				    enemyblock.get(b).setFill(Color.YELLOW);
				    break;
				case 3:
				    enemyblock.get(b).setFill(Color.GREEN);
				    break;
				case 4:
				    enemyblock.get(b).setFill(Color.ORANGE);
				    break;
				}
				enemyblock.get(b).setStroke(Color.BLACK);
				enemyblock.get(b).setStrokeWidth(1);
			    }else{
				if(b>=20){
				    b-=20;
				}
				if(num==1){
				    x = 300-x-50;
				    y = 600-y-20;
				}
				myblock.get(b).setX(x);
				myblock.get(b).setY(y);
				myblock.get(b).setWidth(50);
				myblock.get(b).setHeight(20);
				switch(b%5){
				case 0:
				    myblock.get(b).setFill(Color.RED);
				    break;
				case 1:
				    myblock.get(b).setFill(Color.BLUE);
				    break;
				case 2:
				    myblock.get(b).setFill(Color.YELLOW);
				    break;
				case 3:
				    myblock.get(b).setFill(Color.GREEN);
				    break;
				case 4:
				    myblock.get(b).setFill(Color.ORANGE);
				    break;
				}
				myblock.get(b).setStroke(Color.BLACK);
				myblock.get(b).setStrokeWidth(1);
                            }
			}else if(cmd.equals("Blockdelete")){
                            int num = Integer.parseInt(inputTokens[1]);
                            int b = Integer.parseInt(inputTokens[2]);
                            num %= 2;
                            if((b<20&&num==0)||(b>=20&&num==1)){
				if(b>=20){
				    b-=20;
				}
				root.getChildren().remove(enemyblock.get(b));
                                //enemyblock.get(b).setFill(Color.WHITE);
                                //enemyblock.get(b).setStroke(Color.WHITE);
			    }else{
                                if(b>=20){
				    b-=20;
                                }
				root.getChildren().remove(myblock.get(b));
				//myblock.get(b).setFill(Color.WHITE);
				//myblock.get(b).setStroke(Color.WHITE);

			    }
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
	root.getChildren().addAll(myblock);
	root.getChildren().addAll(enemyblock);
    }    
    
    
    
}
