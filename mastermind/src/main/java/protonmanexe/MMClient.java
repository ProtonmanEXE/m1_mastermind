// Dev.: ProtonmanEXE
// Dev. Notes: 
// this is the Client for Mastermind game

package protonmanexe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MMClient {

    public static void main(String[] args) throws UnknownHostException, IOException {

        // variable declaration
        PrintWriter out;
        BufferedReader in;
        String serverMsg = "";
        String line;
        
        // initialising Mastermind game client
        System.out.println("Starting game...");
        Socket socket = new Socket("localhost", 12345);
        System.out.println("Mastermind has started");

        // keying in in-game commands
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        Scanner scan = new Scanner(System.in);
        line = scan.nextLine();

        while (!"-quit".equals(line)) {
            out.println(line);
            out.flush();
            while (serverMsg != null) {
                serverMsg = in.readLine();
                if ("".equals(serverMsg)) break;
                else System.out.println(serverMsg);
            }
            line = scan.nextLine();
        }

        System.out.println("Leaving Mastermind, goodbye");
        socket.close();
        scan.close();
    }
    
}
