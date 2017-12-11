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
// private static double paddleX;
private static int paddleX[] = new int[2];

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
      System.out.println("Receive from client No." + number +
                         "(" + myName + "), Messages: " + str);
      if (str != null) {
        if (input[0].equals("Paddle")) {
          if(input[1].equals("0")){
            paddleX[0] = (int)Double.parseDouble(input[2]) + 120;
          }else if(input[1].equals("1")){
            paddleX[1] = -1 * (int)Double.parseDouble(input[2]) + 120;
          }
          String enemy = new String("EnemyPaddle," + number + "," + input[2]);
          Server.SendAll(enemy,1 - number);
      }else {
          Server.SendAll(str);
          keycode = str;
          String paddle = new String("Paddle," + keycode + "," + number);
          Server.SendAll(paddle,number);
        }
      }

      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
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
private int radius = 5;
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
//  int ballOnPaddleX;
  int beforeX = x - xVec;
  int beforeY = y - yVec;
  int beforeY2 = y + yVec;

/*  if(this.y - radius - yVec <= underPaddleY && underPaddleY <= this.y + radius){
    ballOnPaddleX = this.x + xVec * (underPaddleY - this.y) / yVec;
    if(paddleX -10 <= ballOnPaddleX -5 && ballOnPaddleX <= paddleX + 65){
      yVec *= -1;

    }
  }
  if(this.y + radius + yVec <= upperPaddleY + 5 && upperPaddleY + 5 <= this.y + radius){
    ballOnPaddleX = this.x + xVec * (this.y - upperPaddleY) / yVec;
    if(paddleX -10 <= ballOnPaddleX -5 && ballOnPaddleX <= paddleX + 65){
      yVec *= -1;

    }
  } */

    if(beforeY <= underPaddleY && underPaddleY <= beforeY + radius){
      if(paddleX[0]-10 <= beforeX && beforeX <= paddleX[0] + 75){
        yVec = -3;

      }
    } else
    if(underPaddleY <=  beforeY2 && beforeY2 - radius <= underPaddleY){
      if(paddleX[0]-10 <= beforeX && beforeX <= paddleX[0] + 75){
        yVec = 3;

      }
    } else
    if(upperPaddleY <=  beforeY && beforeY - radius <= upperPaddleY){
      if(paddleX[1]-10 <= beforeX && beforeX <= paddleX[1] + 75){
        yVec = 3;

      }
    } else
    if(beforeY2 <= upperPaddleY && upperPaddleY <= beforeY2 + radius){
      if(paddleX[1]-10 <= beforeX && beforeX <= paddleX[1] + 75){
        yVec = -3;

      }
    }
    /*
    当たり判定のプログラム　パドルの座標は配列に代入してあるので、それを使う。
    四つの場合に分けて書いた。すごく単純だし　たま〜に貫通する。




    */


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
    System.out.println("Send messages to client No." + i);
  }
}

public static void SendAll(String str,int destNum){
  out.get(destNum).println(str);
  out.get(destNum).flush();
  System.out.println("Send messages to client No." + destNum);
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
      System.out.println("Accept client No." + n);

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
