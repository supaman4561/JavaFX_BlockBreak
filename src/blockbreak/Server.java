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

    int x[] = new int[40];
    int y[] = new int[40];
    int flag[] = new int[40];
    int k = 0;

    public  ClientProcThread(int n, Socket i, InputStreamReader isr,
                                BufferedReader in, PrintWriter out) {
        number = n;
        incoming = i;
        myIsr = isr;
        myIn = in;
        myOut = out;

        for(int j=0; j<28; j++){
          for(int l=0; l<5; l++){
            x[k] = l*50+25;
            y[k] = j*20+20;
            flag[k] = 1;
            k++;
          }
          if(j==3)
            j=23;
        }
    }

    @Override
    public void run() {
      try {
            myOut.println("Hello, client No." + number + "!");

            myName = myIn.readLine();

            //block?generate
            for(int b=0; b<40; b++){
              myOut.println("Blockset," + x[b] + "," + y[b] + "," + b + "," + number);
            }
            //blockdelete
            int b = 30;
            myOut.println("Blockdelete," + number + "," + b);
            flag[b] = 0;

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



public class Server {

    private static int maxConnection = 2;
    private static ArrayList<Socket> incoming;
    private static ArrayList<InputStreamReader> isr;
    private static ArrayList<BufferedReader> in;
    private static ArrayList<PrintWriter> out;
    private static ArrayList<ClientProcThread> myClientProcThread;

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

        int n;

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
		    /*numBlock = myBlockThread.size();
		    myBlockThread.add(new BlockThread(numBlock, 400, 400));
		    myBlockThread.get(numBlock).start();*/

		}
            }
        } catch (Exception e) {
            System.out.println("Error occured when socket was being created: " + e );
        }
    }
}
