/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockbreak;

/**
 *
 * @author PCUser
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.lang.Math;
/*
 * Server Side
 */
class ClientProcThread extends Thread {
    private int number;
    private Socket incoming;
    private InputStreamReader myIsr;
    private BufferedReader myIn;
    private PrintWriter myOut;
    private String myName;
    private static int paddleX[] = {120,120};

    public ClientProcThread(int n, Socket i, InputStreamReader isr,
			    BufferedReader in, PrintWriter out) {
        number = n;
        incoming = i;
        myIsr = isr;
        myIn = in;
        myOut = out;
        myOut.println("Hello," + number);
    }


    static int[] getPaddleX(){
	     return paddleX;
    }

    public void generateBlock(ArrayList<Block> blockArray){
    	Block target;
        String message;

    	for(int id=0; id<blockArray.size(); id++){
    	    target = blockArray.get(id);
            message = new String("Blockset," + target.getX() + "," + target.getY());
    	    myOut.println(message);
    	}
        message = new String("Blockset,end");
        myOut.println(message);
    }

    @Override
    public void run() {
        try {

            myName = myIn.readLine();

	    String keycode = " ";         // used at sending kecode to client

            // watching input to socket
            while(true) {
                String str = myIn.readLine();
		String[] input = str.split(",", -1);
		// for debug
		// System.out.println("Receive from client No." + number +
                //                    "(" + myName + "), Messages: " + str);
                if(str != null) {
                    if(input[0].equals("Paddle")){
                        if(input[1].equals("0")){
              			         paddleX[0] = Integer.parseInt(input[2]) + 120;
              		      }else if(input[1].equals("1")){
              			         paddleX[1] = -1 * Integer.parseInt(input[2]) + 120;
              		      }
                        String enemy = new String("EnemyPaddle," + number + "," + input[2]);
                		    Server.SendAll(enemy,1 - number);
                    }
                  }
                }
        } catch (IOException e) {

        }
    }
}

class BallMoveThread extends Thread {
    private int id;
    private int ballX;
    private int ballY;
    private int radius = 5;
    private int xVec = (int)(Math.random()*2 + 1);
    private int yVec = (int)(Math.random()*2 + 1);
    private ArrayList<Block> blockArray;

    public BallMoveThread(int num,int x, int y, ArrayList<Block> blockArray) {
    	id = num;
    	ballX = x;
    	ballY = y;
        if(id%2 == 1){
            xVec *= -1;
            yVec *= -1;
        }
    	this.blockArray = blockArray;
    }

    @Override
    public void run() {
	while(true){
	    move();
	    String str = new String("Ball," + id + "," + ballX + "," + ballY + ",");
	    Server.SendAll(str);
      blockCollision();
      paddleCollision();
	    try{
		Thread.sleep(16);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    private void move() {
        if(ballX < 5 || ballX > 295){
	    xVec *= -1;
	}

	if(ballY < 5 || ballY > 595){
	    yVec *= -1;
	}


    	ballX += xVec;
    	ballY += yVec;
    }

    private void paddleCollision(){
      int paddleX[] = ClientProcThread.getPaddleX();
      final int upperPaddleY = 120;
      final int underPaddleY = 460;
      int beforeX = ballX - xVec;
      int beforeY = ballY - yVec;


      if(beforeY <= underPaddleY && underPaddleY <= ballY){
          if(paddleX[0] <= beforeX && beforeX <= paddleX[0] + 60){
            ballY = underPaddleY - yVec;
            yVec = -2;
          }
        } else if(beforeY >=  underPaddleY +5 && ballY <= underPaddleY +5){
          if(paddleX[0] <= beforeX && beforeX <= paddleX[0] + 60){
            ballY = underPaddleY - yVec;
            yVec = 2;

          }
        } else if(beforeY >= upperPaddleY +5  && ballY <= upperPaddleY +5){
          if(paddleX[1] <= beforeX && beforeX <= paddleX[1] + 60){
            ballY = upperPaddleY - yVec;
            yVec = 2;
          }
        } else if(beforeY <= upperPaddleY && upperPaddleY <= ballY){
          if(paddleX[1] <= beforeX && beforeX <= paddleX[1] + 60){
            ballY = upperPaddleY - yVec;
            yVec = -2;
          }
        }
    }

    private void blockCollision(){

	Block target;
        boolean deathFlag;
        int blockX;
        int blockY;
        int bWidth;
        int bHeight;

	for(int id=0; id<blockArray.size(); id++){
            target = blockArray.get(id);
            deathFlag = target.isAlive();
            if(deathFlag){
                blockX = target.getX();
                blockY = target.getY();
                bWidth = target.getWidth();
                bHeight = target.getHeight();

                // collision to block
                // up & down
                if(blockX <= ballX && ballX <= (blockX + bWidth)){

                    if((blockY <= (ballY + radius) && (ballY + radius) <= (blockY + bHeight)) ||
		       (blockY <= (ballY - radius) && (ballY - radius) <= (blockY + bHeight))){

                        yVec *= -1;
                        deathFlag = false;
                    }


                }

                // left & right
                if(blockY <= ballY && ballY <= (blockY + bHeight)){

                    if((blockX <= (ballX + radius) && (ballX + radius) <= (blockX + bWidth)) ||
		       (blockX <= (ballX - radius) && (ballX - radius) <= (blockX + bWidth))){

                        xVec *= -1;
                        deathFlag = false;
                    }
                }

                if(deathFlag == false){
                    target.dead(id);
                }

	    }
	}
    }

}

class Block {

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean flag;

    Block(int x, int y){
	this.x = x;
	this.y = y;
	this.width = 50;
	this.height = 20;
	flag = true;
    }

    Block(int x, int y, int width, int height){
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	flag = true;
    }

    public int getX(){
	return this.x;
    }

    public int getY(){
	return this.y;
    }

    public int getWidth(){
	return width;
    }

    public int getHeight(){
	return height;
    }

    public boolean isAlive(){
	return this.flag;
    }

    public void dead(int id){
	String str = new String("BlockDelete," + id);
	Server.SendAll(str);
	flag = false;
    }
}

public class Server {

    private static int maxConnection = 2;
    private static ArrayList<Socket> incoming;
    private static ArrayList<InputStreamReader> isr;
    private static ArrayList<BufferedReader> in;
    private static ArrayList<PrintWriter> out;
    private static ArrayList<ClientProcThread> myClientProcThread;
    private static ArrayList<BallMoveThread> myBallMoveThread;
    private static ArrayList<Block> blockArray;

    public static void SendAll(String str){
        for(int i=0; i<incoming.size(); i++){
            out.get(i).println(str);
            out.get(i).flush();
	    // for debug
	    // System.out.println("Send messages to client No." + i);
        }
    }

    public static void SendAll(String str,int destNum){
	out.get(destNum).println(str);
	out.get(destNum).flush();
    }

    private static void initBlock(){
	int xNum = 5;
	int yNum = 4;
	int xInterval = 25;
	int yInterval1 = 20;
	int yInterval2 = 500;
	int width = 50;
	int height = 20;

	for(int i=0; i<yNum; i++){
	    for(int j=0; j<xNum; j++){
		blockArray.add(new Block(j*width+xInterval,
					 i*height+yInterval1));
	    }
	}

	for(int i=0; i<yNum; i++){
	    for(int j=0; j<xNum; j++){
		blockArray.add(new Block(j*width+xInterval,
					 i*height+yInterval2));
	    }
	}
    }

    public static void main(String[] args) {

        incoming = new ArrayList<Socket>();
        isr = new ArrayList<InputStreamReader>();
        in = new ArrayList<BufferedReader>();
        out = new ArrayList<PrintWriter>();
        myClientProcThread = new ArrayList<ClientProcThread>();
	myBallMoveThread = new ArrayList<BallMoveThread>();
	blockArray = new ArrayList<Block>();

        int n;
	int numBall=0;

	initBlock();

        try {
            System.out.println("The server has launched!");
            ServerSocket server = new ServerSocket(10027);
            while(true) {
                n = incoming.size();
                incoming.add(server.accept());
                System.out.println("Accept client No." + n);

                isr.add(new InputStreamReader(incoming.get(n).getInputStream()));
                in.add(new BufferedReader(isr.get(n)));
                out.add(new PrintWriter(incoming.get(n).getOutputStream(), true));

                myClientProcThread.add(
				       new ClientProcThread(n, incoming.get(n), isr.get(n), in.get(n), out.get(n)));
               myClientProcThread.get(n).start();

		if(myClientProcThread.size() == 2){



		    for(int i=0; i<2; i++){
			myClientProcThread.get(i).generateBlock(blockArray);
		    }

		    try{
			Thread.sleep(5000);
                    }catch(Exception e){}

                    numBall = myBallMoveThread.size();
                    myBallMoveThread.add(new BallMoveThread(numBall, 150, 300, blockArray));
                    myBallMoveThread.get(numBall).start();

                }
            }
        } catch (Exception e) {
            System.out.println("Error occured when socket was being created: " + e );
        }
    }
}
