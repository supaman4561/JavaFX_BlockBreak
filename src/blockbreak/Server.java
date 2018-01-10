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
import java.util.Timer;
import java.util.TimerTask;
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

            String str;
            String keycode = " ";         // used at sending kecode to client
            String command;
            String[] input;

            // watching input to socket
            while(true) {
                str = myIn.readLine();
                // for debug
                // System.out.println("Receive from client No." + number +
                //                    "(" + myName + "), Messages: " + str);
                if(str != null) {
                    input = str.split(",", -1);
                    command = input[0];
                    if(command.equals("Paddle")){
                        if(input[1].equals("0")){
                            paddleX[0] = Integer.parseInt(input[2]) + 120;
                        }else if(input[1].equals("1")){
                            paddleX[1] = -1 * Integer.parseInt(input[2]) + 120;
                        }
                        String enemy = new String("EnemyPaddle," + number + "," + input[2]);
                        Server.SendAll(enemy,1 - number);
                    }else if(command.equals("GameClose")){
                        Server.connectEnd(number);
                        break;
                    }
                }
            }
        } catch (IOException e) {

        }
    }
}

class BallMoveThread extends Thread {
    private ArrayList<Ball> ballArray;
    private int p1LivingBlockNum;
    private int p2LivingBlockNum;
    private ArrayList<Block> blockArray;
    private Timer timer;
    private TimerTask ballAddTask;

    public BallMoveThread(ArrayList<Block> blockArray) {
        ballArray = new ArrayList<Ball>();
        this.blockArray = blockArray;
        p1LivingBlockNum = blockArray.size() / 2;
        p2LivingBlockNum = p1LivingBlockNum;

        ballAddTask = new TimerTask() {
            public void run() {
                ballArray.add(new Ball(150,300));
            }
        };

        timer = new Timer();
        // add ball every thirty seconds
        timer.schedule(ballAddTask, 0L, 5000L);
    }

    @Override
    public void run() {

        Ball target;
        long start;
        long end;

        while(true){
            start = System.currentTimeMillis();
            for(int id=0; id<ballArray.size(); id++){
                target = ballArray.get(id);
                target.move();
                String str = new String("Ball," + id + "," + target.getX() + "," + target.getY() + ",");
                Server.SendAll(str);
                blockCollision(target);
                paddleCollision(target);
            }
            if(isFinished() == true){
                timer.cancel();
                timer = null;
                break;
            }
            end = System.currentTimeMillis();
            try{
                Thread.sleep(16L - (end - start) / 1000000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void paddleCollision(Ball target){
        int paddleX[] = ClientProcThread.getPaddleX();
        final int upperPaddleY = 120;
        final int underPaddleY = 460;
        int ballX = target.getX();
        int ballY = target.getY();
        int xVec = target.getXVec();
        int yVec = target.getYVec();
        int beforeX = ballX - xVec;
        int beforeY = ballY - yVec;


        if(beforeY <= underPaddleY && underPaddleY <= ballY){
            if(paddleX[0] <= beforeX && beforeX <= paddleX[0] + 60){
                target.setY(underPaddleY - yVec);
                target.setYVec(target.getYVec() * -1);
            }
        } else if(beforeY >=  underPaddleY +5 && ballY <= underPaddleY +5){
            if(paddleX[0] <= beforeX && beforeX <= paddleX[0] + 60){
                target.setY(underPaddleY - yVec);
                target.setYVec(target.getYVec() * -1);
            }
        } else if(beforeY >= upperPaddleY +5  && ballY <= upperPaddleY +5){
            if(paddleX[1] <= beforeX && beforeX <= paddleX[1] + 60){
                target.setY(upperPaddleY - yVec);
                target.setYVec(target.getYVec() * -1);
            }
        } else if(beforeY <= upperPaddleY && upperPaddleY <= ballY){
            if(paddleX[1] <= beforeX && beforeX <= paddleX[1] + 60){
                target.setY(upperPaddleY - yVec);
                target.setYVec(target.getYVec() * -1);
            }
        }
    }

    private void blockCollision(Ball bTarget){

        Block target;
        boolean deathFlag;
        int blockX;
        int blockY;
        int bWidth;
        int bHeight;
        int ballX;
        int ballY;
        int radius;

        for(int id=0; id<blockArray.size(); id++){
            target = blockArray.get(id);
            deathFlag = target.isAlive();
            if(deathFlag){
                blockX = target.getX();
                blockY = target.getY();
                bWidth = target.getWidth();
                bHeight = target.getHeight();
                ballX = bTarget.getX();
                ballY = bTarget.getY();
                radius = bTarget.getRadius();

                // collision to block
                // up & down
                if(blockX <= ballX && ballX <= (blockX + bWidth)){

                    if((blockY <= (ballY + radius) && (ballY + radius) <= (blockY + bHeight)) ||
                    (blockY <= (ballY - radius) && (ballY - radius) <= (blockY + bHeight))){

                        bTarget.setYVec(bTarget.getYVec() * -1);
                        deathFlag = false;
                    }


                }

                // left & right
                if(blockY <= ballY && ballY <= (blockY + bHeight)){

                    if((blockX <= (ballX + radius) && (ballX + radius) <= (blockX + bWidth)) ||
                    (blockX <= (ballX - radius) && (ballX - radius) <= (blockX + bWidth))){

                        bTarget.setXVec(bTarget.getXVec() * -1);
                        deathFlag = false;
                    }
                }

                if(deathFlag == false){
                    target.dead(id);
                    // player1
                    if(id < 20){
                        p1LivingBlockNum -= 1;
                        // player2
                    }else{
                        p2LivingBlockNum -= 1;
                    }
                }

            }
        }
    }

    private boolean isFinished(){
        boolean finish = true;

        if(p1LivingBlockNum == 0){
            Server.SendAll("Win",0);
            Server.SendAll("Lose",1);
        }else if(p2LivingBlockNum == 0){
            Server.SendAll("Lose",0);
            Server.SendAll("Win",1);
        }else{
            finish = false;
        }

        return finish;
    }

}

class Ball{
    private int x;
    private int y;
    private int radius;
    private int xVec;
    private int yVec;

    Ball(int x, int y){
        this.x = x;
        this.y = y;
        radius = 5;
        xVec = (int)(Math.random()*6 - 3);
        yVec = (int)(Math.random()*6 - 3);
	if(xVec <= 0) xVec -= 1;
	if(yVec <= 0) yVec -= 1;
    }

    Ball(int x, int y, int radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
        xVec = (int)(Math.random()*6 - 3);
        yVec = (int)(Math.random()*6 - 3);
	if(xVec <= 0) xVec -= 1;
	if(yVec <= 0) yVec -= 1;
    }

    public int getX(){
        return x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return y;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getRadius(){
        return radius;
    }

    public int getXVec(){
        return xVec;
    }

    public int getYVec(){
        return yVec;
    }

    public void setXVec(int xVec){
        this.xVec = xVec;
    }

    public void setYVec(int yVec){
        this.yVec = yVec;
    }

    public void move() {
        if(x < 5 || x > 295){
            xVec *= -1;
        }

        if(y < 5 || y > 595){
            yVec *= -1;
        }

        x += xVec;
        y += yVec;
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

    public void recover(){
        flag = true;
    }
}

public class Server {

    private static int maxConnection = 10;
    private static int cn;
    private static ArrayList<Socket> incoming;
    private static ArrayList<InputStreamReader> isr;
    private static ArrayList<BufferedReader> in;
    private static ArrayList<PrintWriter> out;
    private static ArrayList<ClientProcThread> myClientProcThread;
    private static BallMoveThread myBallMoveThread;
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

    private static void recoverBlock(){
        for(int i=0; i<blockArray.size(); i++){
            blockArray.get(i).recover();
        }
    }

    public static void connectEnd(int n){
        incoming.remove(0);
        isr.remove(0);
        in.remove(0);
        out.remove(0);
        myClientProcThread.remove(0);
        System.out.println("Leave client No." + n);
        cn -= 1;
    }

    public static void main(String[] args) {

        incoming = new ArrayList<Socket>();
        isr = new ArrayList<InputStreamReader>();
        in = new ArrayList<BufferedReader>();
        out = new ArrayList<PrintWriter>();
        myClientProcThread = new ArrayList<ClientProcThread>();
        blockArray = new ArrayList<Block>();

        initBlock();
        cn = 0;

        try {
            System.out.println("The server has launched!");
            ServerSocket server = new ServerSocket(10027);
            while(true) {
                incoming.add(server.accept());
                System.out.println("Accept client No." + cn);

                isr.add(new InputStreamReader(incoming.get(cn).getInputStream()));
                in.add(new BufferedReader(isr.get(cn)));
                out.add(new PrintWriter(incoming.get(cn).getOutputStream(), true));

                myClientProcThread.add(
                new ClientProcThread(cn, incoming.get(cn), isr.get(cn), in.get(cn), out.get(cn)));
                myClientProcThread.get(cn).start();
                cn+=1;

                if(cn == 2){

                    recoverBlock();

                    for(int i=0; i<2; i++){
                        myClientProcThread.get(i).generateBlock(blockArray);
                    }

                    try{

                        for(int num=3; num>=0; num--){
                            for(int size=500; size>0; size-=50){
                                String str = new String("Animation," + num + "," + size + ",");
                                Server.SendAll(str);
                                Thread.sleep(100);
                            }
                        }
                        String str = new String("AnimationFinish,");
                        Server.SendAll(str);

                    }catch(Exception e){

                    }

                    myBallMoveThread = new BallMoveThread(blockArray);
                    myBallMoveThread.start();

                }
            }
        } catch (Exception e) {
            System.out.println("Error occured when socket was being created: " + e );
        }
    }
}
