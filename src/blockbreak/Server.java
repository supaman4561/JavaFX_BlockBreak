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
private static int paddleX[] = {120,120};



public ClientProcThread(int n, Socket i, InputStreamReader isr,
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
    myOut.println("Hello, client No." + "," +number + "," + "!");


    myName = myIn.readLine();

    String keycode = " ";         // クライアントに送信するキーのコード

    // watching input to socket
    while (true) {
      String str = myIn.readLine();
      String[] input = str.split(",", -1);

      if (str != null) {
        if (input[0].equals("Paddle")) {
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
  }
  catch (IOException e) {
  }
}
static int[] getPaddleX(){
  return paddleX;
}

}


class BallMoveThread extends Thread {
private int id;
private int x;
private int y;
private int xVec = 1;
private int yVec = 1;

public BallMoveThread(int num, int x, int y) {
  id = num;
  x = 450;
  y = 450;
}

private void BallCollision(){
  int paddleX[] = ClientProcThread.getPaddleX();
  final int upperPaddleY = 118;
  final int underPaddleY = 499;
  int beforeX = x - xVec;
  int beforeY = y - yVec;

  if(beforeY <= underPaddleY && underPaddleY <= y){
      if(paddleX[0] <= beforeX && beforeX <= paddleX[0] + 60){
        y = underPaddleY - yVec;
        yVec = -3;
      }
    } else if(beforeY >=  underPaddleY && y <= underPaddleY){
      if(paddleX[0] <= beforeX && beforeX <= paddleX[0] + 60){
        y = underPaddleY - yVec;
        yVec = 3;

      }
    } else if(beforeY >= upperPaddleY  && y <= upperPaddleY){
      if(paddleX[1] <= beforeX && beforeX <= paddleX[1] + 60){
        y = upperPaddleY - yVec;
        yVec = 3;
      }
    } else if(beforeY <= upperPaddleY && upperPaddleY <= y){
      if(paddleX[1] <= beforeX && beforeX <= paddleX[1] + 60){
        y = upperPaddleY - yVec;
        yVec = -3;
      }
    }
}

@Override
public void run() {
  while (true) {
    move();
    BallCollision();
    String str = new String("Ball," + id + "," + x + "," + y + ",");
    Server.SendAll(str);
    try {
      Thread.sleep(16);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

private void move() {
  if (x < 5)
    xVec = 2;
  else if (x > 295)
    xVec = -2;

  if (y < 5)
    yVec = 3;
  else if (y > 595)
    yVec = -3;

  x += xVec;
  y += yVec;

}
}


public class Server {
private static int maxConnection = 2;
private static ArrayList <Socket> incoming;
private static ArrayList <InputStreamReader> isr;
private static ArrayList <BufferedReader> in;
private static ArrayList <PrintWriter> out;
private static ArrayList <ClientProcThread> myClientProcThread;
private static ArrayList <BallMoveThread> myBallMoveThread;

public static void SendAll(String str) {
  for (int i = 0; i < incoming.size(); i++) {
    out.get(i).println(str);
    out.get(i).flush();
  }
}

public static void SendAll(String str,int destNum){
  out.get(destNum).println(str);
  out.get(destNum).flush();
}

public static void main(String[] args) {
  incoming = new ArrayList <Socket>();
  isr = new ArrayList <InputStreamReader>();
  in = new ArrayList <BufferedReader>();
  out = new ArrayList <PrintWriter>();
  myClientProcThread = new ArrayList <ClientProcThread>();
  myBallMoveThread = new ArrayList <BallMoveThread>();

  int n;
  int numBall = 0;

  try {
    System.out.println("The server has launched!");
    ServerSocket server = new ServerSocket(10027);
    while (true) {
      n = incoming.size();
      incoming.add(server.accept());


      isr.add(new InputStreamReader(incoming.get(n).getInputStream()));
      in.add(new BufferedReader(isr.get(n)));
      out.add(new PrintWriter(incoming.get(n).getOutputStream(), true));

      myClientProcThread.add(
        new ClientProcThread(n, incoming.get(n), isr.get(n), in.get(n), out.get(n)));
      myClientProcThread.get(n).start();           // start thread

      if (myClientProcThread.size() == 2) {
        numBall = myBallMoveThread.size();
        myBallMoveThread.add(new BallMoveThread(numBall, 400, 400));
        myBallMoveThread.get(numBall).start();
      }
    }
  }
  catch (Exception e) {
    System.out.println("Error occured when socket was being created: " + e);
  }
}
}
