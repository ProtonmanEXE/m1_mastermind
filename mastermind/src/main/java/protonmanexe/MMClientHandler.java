package protonmanexe;

import static protonmanexe.Constants.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MMClientHandler implements Runnable {

    // variable declaration
    private Socket socket;
    private String input, readLineLogin, readLineSolo;
    private String userName = "guest";
    private PrintWriter out;
    private BufferedReader in;
    private int score = 0;
    private int rd;
    private ArrayList<String> outcome = new ArrayList<String>();
    MMGameCommands command = new MMGameCommands();

    public MMClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            input = in.readLine();
        } catch (IOException ioe) {
            System.out.println("Something went wrong...");
            System.exit(1);
        }

        while (null != input) {
            try {
                switch (input) {
                    case "-login":
                        out.println("Mastermind: Please key in your User ID");
                        out.flush();
                        readLineLogin = in.readLine();
                        if (readLineLogin.isBlank()) {
                            userName = "guest";
                            out.println("Mastermind: You did not key in a User ID, default User ID is " +userName);
                            out.flush();
                        } else if ("guest".equals(readLineLogin)) {
                            out.println("Mastermind: guest is a invalid User ID, this command is invalid");
                            out.flush();
                        } else {
                            if (!"guest".equals(userName)) {
                                command.save(score, userName);
                            }
                            userName = readLineLogin;
                            score = command.login(userName);
                            out.println("Mastermind: Hi " +userName +", welcome, your score is " +score);
                            out.flush();
                        }
                        input = in.readLine();
                        break;

                    case "-solo":
                        out.println("Mastermind: Vs computer, please choose game type 1 or 2 (Type 1 or 2)");
                        out.flush();
                        readLineSolo = in.readLine();
                        if ("1".equals(readLineSolo)) {
                            command.codemaker1();
                            System.out.println("Game type 1 was chosen");
                            rd = 1;
                            out.println("Mastermind: Codebreaker, you may begin Rd " +rd);
                            out.flush();

                            while (!(outcome.equals(WIN) || rd > 10)) {
                                readLineSolo = in.readLine();
                                String [] strArray = readLineSolo.toUpperCase().trim().split(",");
                                boolean check1 = false;

                                // check whether user inputs the correct colours/entries under CODE_PEGS1 
                                for (int i = 0; i < strArray.length; i++) {
                                    if (CODE_PEGS1.indexOf(strArray[i]) == -1) {
                                        check1 = true;
                                        break;
                                    }
                                } 

                                // check whether user inputs exactly four elements
                                if (strArray.length != 4) {
                                    check1 = true;
                                }
                                
                                // if user inputs more than four elements, or inputs wrong colours/entries, then entry is rejected
                                if (check1) {
                                    out.println("Mastermind: Your entry is invalid");
                                    out.flush();
                                    continue;
                                }

                                System.out.println("The guess is: " + Arrays.asList(strArray));
                                outcome = command.solo1(strArray);
                                
                                if (outcome.equals(WIN)) {
                                    out.println("Mastermind: Rd " +rd +" results are " +outcome +" Codebreaker " +userName + " has won!");
                                    out.flush();
                                    score = score + 10;
                                } else if (rd >= 10) {
                                    out.println("Mastermind: Rd " +rd +" results are " +outcome +" Codebreaker " +userName + " has lost");
                                    out.flush();
                                    rd++;
                                } else {
                                    out.println("Mastermind: Rd " +rd +" results are " +outcome);
                                    out.flush();
                                    rd++;
                                }
                            }
                        } else {
                            
                        }
                        input = in.readLine();
                        break;

                    case "-help":
                        System.out.println("helps");
                        out.println("helpc");
                        out.flush();
                        command.codemaker1();
                        input = in.readLine();
                        break;

                    case "-score":
                        if (userName.isBlank()) {
                            out.println("You have not logged in yet, this command is invalid");
                            out.flush();
                        } else {
                            out.println("Hi " +userName +", your score is " +score);
                            out.flush();
                        }
                        input = in.readLine();
                        break;

                    case "-instruction":
                        System.out.println("is");
                        out.println("ic");
                        out.flush();
                        input = in.readLine();
                        break;
                
                    default:
                        System.out.println("Invalid command, please input correct command");
                        out.println("Invalid command, please input correct command");
                        out.flush();
                        input = in.readLine();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
                break;
            } 
        }

        System.out.println("Username is " +userName +" and score is " +score);
        if (!"guest".equals(userName)) {
            command.save(score, userName);
        }
        
        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
