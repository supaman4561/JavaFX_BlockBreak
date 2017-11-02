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

    public  ClientProcThread(int n, Socket i, InputStreamReader isr,
                                BufferedReader in, PrintWriter out) {
        number = n;
        incoming = i;
        myIsr = isr;
        myIn = in;
        myOut = out;

    }

    @Override
    public void run() {
        try {
            myOut.println("Hello, client No." + number + "!");

            myName = myIn.readLine();

            // watching input to socket
            while(true) {
                String str = myIn.readLine();
                System.out.println("Receive from client No." + number +
                                   "(" + myName + "), Messages: " + str);
                if(str != null) {
                    Server.SendAll(str);
                }
            }
        } catch (IOException e) {

        }
    }
}

<<<<<<< HEAD
class BlockThread extends Thread {
    private int id;
    private int x;
    private int y;
    private int xVec;
    private int yVec;
    public BlockThread(int num,int x, int y) {
=======
class BallMoveThread extends Thread {
    private int id;
    private int x;
    private int y;
    private int xVec = 1;
    private int yVec = 1;
	
    public BallMoveThread(int num,int x, int y) {
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096
	id = num;
	x = 450;
	y = 450;
    }
<<<<<<< HEAD

    @Override
    public void run() {
	     while(true){
         String str = new String("Block");
	       Server.SendAll(str);
	       try{
		         Thread.sleep(16);
	       } catch (InterruptedException e) {
		         e.printStackTrace();
	       }
	     }
=======
    
    @Override
    public void run() {
	while(true){
	    move();
	    String str = new String("Ball," + id + "," + x + "," + y + ",");
	    Server.SendAll(str);
	    try{
		Thread.sleep(16);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    private void move() {
        if(x < 5){
	    xVec = 2;
	}else if(x > 295){
	    xVec = -2;
	}

	if(y < 5){
	    yVec = 3;
	}else if(y > 595){
	    yVec = -3;
	}

	x += xVec;
	y += yVec;
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096
    }
}

public class Server {

    private static int maxConnection = 2;
    private static ArrayList<Socket> incoming;
    private static ArrayList<InputStreamReader> isr;
    private static ArrayList<BufferedReader> in;
    private static ArrayList<PrintWriter> out;
    private static ArrayList<ClientProcThread> myClientProcThread;
<<<<<<< HEAD
    private static ArrayList<BlockThread> myBlockThread;
=======
    private static ArrayList<BallMoveThread> myBallMoveThread;
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096

    public static void SendAll(String str){
        for(int i=0; i<incoming.size(); i++){
            out.get(i).println(str);
            out.get(i).flush();
            System.out.println("Send messages to client No." + i);
        }
    }

    public static void main(String[] args) {

        incoming = new ArrayList<Socket>();
        isr = new ArrayList<InputStreamReader>();
        in = new ArrayList<BufferedReader>();
        out = new ArrayList<PrintWriter>();
        myClientProcThread = new ArrayList<ClientProcThread>();
<<<<<<< HEAD
	myBlockThread = new ArrayList<BlockThread>();

        int n;
	int numBlock=0;
=======
	myBallMoveThread = new ArrayList<BallMoveThread>();

        int n;
	int numBall=0;
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096

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
<<<<<<< HEAD
		    numBlock = myBlockThread.size();
		    myBlockThread.add(new BlockThread(numBlock, 400, 400));
		    myBlockThread.get(numBlock).start();
		}
=======
		    numBall = myBallMoveThread.size();
		    myBallMoveThread.add(new BallMoveThread(numBall, 400, 400));
		    myBallMoveThread.get(numBall).start();
		} 
>>>>>>> 80ebf4971c009a225928c83c3d32542f576ce096
            }
        } catch (Exception e) {
            System.out.println("Error occured when socket was being created: " + e );
        }
    }
}

