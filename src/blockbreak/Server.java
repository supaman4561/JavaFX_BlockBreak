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

    public ClientProcThread(int n, Socket i, InputStreamReader isr,
			     BufferedReader in, PrintWriter out) {
        number = n;
        incoming = i;
        myIsr = isr;
        myIn = in;
        myOut = out;
    }

    public void generateBlock(ArrayList<Block> blockArray){
    	Block target;
    	for(int id=0; id<blockArray.size(); id++){
    	    target = blockArray.get(id);
    	    myOut.println("Blockset," + target.getX()
    			  + "," + target.getY() + "," + id + "," + number);
    	}
    }

    @Override
    public void run() {
        try {
            myOut.println("Hello," + number);

            myName = myIn.readLine();

            // watching input to socket
            while(true) {
                String str = myIn.readLine();
		// for debug
		// System.out.println("Receive from client No." + number +
                //                    "(" + myName + "), Messages: " + str);
                if(str != null) {
                    Server.SendAll(str);
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
    private int xVec = 1;
    private int yVec = 1;
    private ArrayList<Block> blockArray;

    public BallMoveThread(int num,int x, int y, ArrayList<Block> blockArray) {
	id = num;
	ballX = x;
	ballY = y;
	this.blockArray = blockArray;
    }

    @Override
    public void run() {
	while(true){
	    move();
	    String str = new String("Ball," + id + "," + ballX + "," + ballY + ",");
	    Server.SendAll(str);
	    watchCollision();
	    try{
		Thread.sleep(16);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    private void move() {
        if(ballX < 5){
	    xVec = 2;
	}else if(ballX > 295){
	    xVec = -2;
	}

	if(ballY < 5){
	    yVec = 3;
	}else if(ballY > 595){
	    yVec = -3;
	}

	ballX += xVec;
	ballY += yVec;
    }

    private void watchCollision(){

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

		        myClientProcThread.get(n).start(); // start thread

		        if(myClientProcThread.size() == 2){

		            for(int i=0; i<2; i++){
			              myClientProcThread.get(i).generateBlock(blockArray);
		            }

		            try{
			            Thread.sleep(5000);
                    }catch(Exception e){}

                    numBall = myBallMoveThread.size();
                    myBallMoveThread.add(new BallMoveThread(numBall, 400, 400, blockArray));
                    myBallMoveThread.get(numBall).start();
                }
            }
        } catch (Exception e) {
            System.out.println("Error occured when socket was being created: " + e );
        }
    }
}
