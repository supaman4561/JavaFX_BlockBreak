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
                    Server.SendAll(str, myName);
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

    public static void SendAll(String str, String myName){
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
            }
        } catch (Exception e) {
            System.out.println("Error occured when socket was being created: " + e );
        }
    }
}
