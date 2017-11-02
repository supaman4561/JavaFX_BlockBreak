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

import javafx.util.Duration;
import java.util.ArrayList;
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
<<<<<<< HEAD

=======
    
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096
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
<<<<<<< HEAD
    ArrayList<Block> block = new ArrayList<Block>();
=======
    Circle ball;
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096
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
<<<<<<< HEAD
      for (int j=0; j<4; j++) {
        for (int i=0; i<5; i++) {
          block.add(new Block(i,j,(i+j)%4));
        }
      }
//      block = new Rectangle();
=======
        ball = new Circle(5.0);
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096
        MesgRecvThread mrt = new MesgRecvThread(BlockBreak.getSocket(), BlockBreak.getUserName());
        mrt.start();
    }

    public class MesgRecvThread extends Thread {

        Socket socket;
        String myName;

        public MesgRecvThread(Socket s, String n) {
<<<<<<< HEAD

=======
	    
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096
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
                        String[] inputTokens = inputLine.split(",");
                        String cmd = inputTokens[0];
<<<<<<< HEAD
                       if(cmd.equals("Block")){
                            Block.delete(block.get(8));
                            Block.draw(block.get(8));
                      }
=======
                        if(cmd.equals("Ball")){
                            ball.setCenterX(Integer.parseInt(inputTokens[2]));
			    ball.setCenterY(Integer.parseInt(inputTokens[3]));
                        }
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096
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
<<<<<<< HEAD
	      root.getChildren().addAll(block);
    }



=======
	root.getChildren().add(ball);
    }    
    
    
    
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096
}

class Block extends Rectangle {
  private boolean flag=true;

    public Block(double x,double y,int color) {
        super(x*50+25,y*20+20,50,20);
        switch(color){
          case 0 :
          this.setFill(Color.RED);
          break;
          case 1 :
          this.setFill(Color.BLUE);
          break;
          case 2 :
          this.setFill(Color.YELLOW);
          break;
          case 3 :
          this.setFill(Color.GREEN);
          break;
        }
    }

    public static void delete(Block block){
      block.flag = false;
    }

    public static void draw(Block block){
      if(block.flag==false){
        block.setFill(Color.WHITE);
      }
    }
	}
