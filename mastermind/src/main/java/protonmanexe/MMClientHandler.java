// Dev.: ProtonmanEXE
// Dev. Notes: 
// this is the ClientHandler for Mastermind game

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
    private String input, readLineLogin, readLineSolo, readLineSettings;
    private String userName = DEFAULT_USER;
    private PrintWriter out;
    private BufferedReader in;
    private int score = 0;
    private int rdSet = DEFAULT_RDCOUNTER;
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
            System.out.println("Something went wrong...\n");
            System.exit(1);
        }

        while (null != input) { // when client receives -quit command, null will be sent over to server side
            try {
                switch (input) {
                    case "-login":
                        out.println("Mastermind: Please key in your User ID\n");
                        out.flush();
                        readLineLogin = in.readLine();
                        if (readLineLogin.isBlank()) {
                            userName = DEFAULT_USER;
                            out.println("Mastermind: You did not key in a User ID, default User ID is " +userName +"\n");
                            out.flush();
                        } else if (DEFAULT_USER.equals(readLineLogin)) {
                            out.println("Mastermind: guest is a invalid User ID, this command is invalid\n");
                            out.flush();
                        } else {
                            if (!DEFAULT_USER.equals(userName)) {
                                command.save(score, userName);
                            }
                            userName = readLineLogin;
                            score = command.login(userName);
                            out.printf("Mastermind: Hi %s, welcome, your score is %d\n\n", userName, score);
                            out.flush();
                        }
                        input = in.readLine();
                        break;

                    case "-solo":
                        out.println("Mastermind: Vs computer");
                        out.println("Please choose game type 1 or 2 (Type 1 or 2)\n");
                        out.flush();
                        readLineSolo = in.readLine();
                        if ("1".equals(readLineSolo)) {
                            command.codemaker1();
                            System.out.println("Game type 1 was chosen");
                            rd = 1;
                            out.println("Mastermind: the available colored pegs are: yellow (YW), red (RD), green (GN), blue (BU), black (BW), white (WH)");
                            out.printf("Codebreaker %s, you may begin Rd %d\n\n", userName, rd);
                            out.flush();

                            while (!(outcome.equals(WIN) || rd > rdSet)) {
                                readLineSolo = in.readLine();
                                String [] strArray = readLineSolo.toUpperCase().trim().split(",");
                                boolean check1 = false;

                                // check whether user inputs the correct colours/entries under CODE_PEGS1 
                                for (int i = 0; i < strArray.length; i++) {
                                    if (CODE_PEGS1.indexOf(strArray[i]) == -1) {
                                        check1 = true; // add more error messages here
                                        break;
                                    }
                                } 

                                // check whether user inputs exactly four elements
                                if (strArray.length != 4) { // add more error messages here
                                    check1 = true;
                                }
                                
                                // if user inputs more than four elements, or inputs wrong colours/entries, then entry is rejected
                                if (check1) {
                                    out.println("Mastermind: Your entry is invalid\n");
                                    out.flush();
                                    continue;
                                }

                                System.out.println("The guess is: " + Arrays.asList(strArray));
                                outcome = command.solo1(strArray);
                                
                                if (outcome.equals(WIN)) {
                                    out.println("Mastermind: Rd " +rd +" results are " +outcome +" Codebreaker " +userName + " has won!\n");
                                    out.flush();
                                    score = score + 10;
                                } else if (rd >= rdSet) {
                                    out.println("Mastermind: Rd " +rd +" results are " +outcome +" Codebreaker " +userName + " has lost\n");
                                    out.flush();
                                    rd++;
                                } else {
                                    out.println("Mastermind: Rd " +rd +" results are " +outcome +"\n");
                                    out.flush();
                                    rd++;
                                }
                            }
                        } else {
                            out.println("Invalid command, please input correct command\n");
                            out.flush();
                        }
                        outcome.clear();
                        input = in.readLine();
                        break;

                    case "-help":
                        out.printf("Mastermind: Dear %s, the available in-game commands are as follows:\n", userName);
                        out.println("-help: displays all available in-game commands for player");
                        out.println("-instruction: displays Mastermind game rules");
                        out.println("-login: allows player to log into their own account");
                        out.println("-solo: start Mastermind game");
                        out.println("-score: displays player's score (player must log-in first)");
                        out.println("-quit: ends Mastermind programme");
                        out.println("Reminder: guest user ID cannot save the score\n");
                        out.flush();
                        input = in.readLine();
                        break;

                    case "-score":
                        out.printf("Mastermind: %s, your score is %d\n\n", userName, score);
                        out.flush();
                        input = in.readLine();
                        break;

                    case "-instruction":
                        out.printf("Mastermind: Dear %s, welcome to Mastermind game by ProtonmanEXE\n", userName);
                        out.println("1) The objective of Mastermind is to guess a secret code consisting of a series of 4 colored pegs (code)");
                        out.println("2) Player who is Codemaker, will decide the series of 4 colored pegs (code), there can be more than 1 of the same colour");
                        out.println("3) Player who is Codebreaker, will need to guess Codemaker's series of 4 colored pegs (code)");
                        out.printf("4) Codebreaker will have %d rounds to guess the code\n", rdSet);
                        out.println("5) To make a guess, Codebreaker simply has to type exactly 4 of the colored pegs that the player thinks is the code");
                        out.println("6) Codemaker will respond with up to 4 pegs of the following colour:");
                        out.println("- black peg (BK) to indicate a peg of the right color and in the right position (without indication of which Code Peg it corresponds to)");
                        out.println("- white peg (WH) to indicate a Code Peg of the right color but in the wrong position (without indication of which Code Peg it corresponds to)");
                        out.println("- no peg indicates a wrong color that does not appear in the secret code");
                        out.println("7) Codebreaker continue guessing the code until the rounds finish or the code is correct, in which the Codemaker will place 4 black pegs");
                        out.println("8) If the Codebreaker wins, that player gets 10 points");
                        out.println("9) Upon starting game in solo mode, player will be Codebreaker, who needs to choose either game mode 1 or 2");
                        out.println("Reminder: the available colored pegs are: yellow (YW), red (RD), green (GN), blue (BU), black (BW), white (WH)\n");
                        out.flush();
                        input = in.readLine();
                        break;

                    case "-settings":
                        if ("admin".equals(userName)) {
                            out.printf("Dear %s, please select settings that you would like to adjust:\n", userName);
                            out.println("-setroundcounter: change maximum round counter that Codebreaker can guess\n");
                            out.flush();
                            readLineSettings = in.readLine();
                            switch (readLineSettings) {
                                case "-setroundcounter":
                                    out.println("Please key in desired maximum round(s) (only numbers are allowed)\n");
                                    out.flush();
                                    readLineSettings = in.readLine();
                                    try {
                                        rdSet = Integer.valueOf(readLineSettings);
                                        out.println("Maximum round counter changed successfully to " +rdSet + "\n");
                                        out.flush();
                                    } catch (NumberFormatException e) {
                                        out.println("Invalid number, ypu have to start over\n");
                                        out.flush();
                                    }
                                    break;
                            
                                default:
                                    out.println("Invalid command, please input correct command\n");
                                    out.flush();
                                    break;
                            }
                        } else {
                            out.println("Invalid command, please input correct command\n");
                            out.flush();
                        }
                        input = in.readLine();
                        break;
                
                    default:
                        System.out.printf("Client: User said %s\n", input);
                        out.println("Invalid command, please input correct command\n");
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
        if (!DEFAULT_USER.equals(userName)) {
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
