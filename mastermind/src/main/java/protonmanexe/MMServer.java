package protonmanexe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MMServer {

    public static void main(String[] args) throws IOException {
        
        // variable declaration
        Socket socket;
        ServerSocket serverSocket;

        // initialising Mastermind game server
        serverSocket = new ServerSocket(12345);
        System.out.println("Game server started at port 12345");     
        
        // starting mutiple threads
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        socket = serverSocket.accept();
        System.out.println("Game client has connected at port 12345"); 

        // executing MMClientHandler
        MMClientHandler handler = new MMClientHandler(socket);
        threadPool.submit(handler);
            
        // ending the threads
        threadPool.shutdown();
        serverSocket.close();
    
    }
    
}
