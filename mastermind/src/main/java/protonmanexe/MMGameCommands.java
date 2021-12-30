// Dev.: ProtonmanEXE
// Dev. Notes: 
// this contains all the classes for game commands for Mastermind game

package protonmanexe;

import static protonmanexe.Constants.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class MMGameCommands {

    // variable declaration
    private int score, indexRemove, r, index;
    private String userName, readLine, readUser, readScore, readScore2, writeLine;
    private boolean Check1;
    private ArrayList<String> guess = new ArrayList<String>();
    private ArrayList<String> users = new ArrayList<String>();
    private ArrayList<String> codemaker = new ArrayList<String>();
    private ArrayList<String> codemaker2 = new ArrayList<String>();
    private ArrayList<String> temp = new ArrayList<String>();
    private ArrayList<String> outcome = new ArrayList<String>();
    private ArrayList<Integer> toRemove = new ArrayList<Integer>();

    public int login(String userName) {
        File userFile = new File(DEFAULT_DIR);
        this.userName = userName;
        Check1 = false;

        // check whether user is new, and assign Check1 is true if not new user
        try(BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            while (null != (readLine = br.readLine())) {
                Scanner scan = new Scanner(readLine);
                readUser = scan.next();
                readScore = scan.nextLine();
                readScore2 = readScore.trim();
                if (readUser.equals(userName)) {
                    score = Integer.valueOf(readScore2);
                    Check1 = true;
                }
                scan.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // write new user if user is new
        if (!Check1) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
                score = 0;
                writeLine = userName +" " +score;
                try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
                    if (null == (readLine = br.readLine())) {
                        writer.write(writeLine);
                    } else {
                        writer.newLine();
                        writer.write(writeLine);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } catch (IOException ioe) {
	            ioe.printStackTrace();
                System.exit(1);
            }
        } else {
            // nothing needs to be done if Check1 is true (not new user)
        }

        return score;
    }

    public void save(int score, String userName) {
        File userFile = new File("./db/db.txt");
        this.userName = userName;
        this.score = score;

        // store all User IDs in an array
        try(BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            while (null != (readLine = br.readLine())) {
                users.add(readLine);
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // remove current User ID from array and add back to array with new score
        for (String s : users) {
            Scanner scan = new Scanner(s);
                readUser = scan.next();
                if (readUser.equals(userName)) {indexRemove = users.indexOf(s);}
            scan.close();
        }

        users.remove(indexRemove);
        users.add(userName +" " +score);

        // write to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, false))) {
            for (String s: users) {
                writer.write(s);
                if (users.indexOf(s) == (users.size()-1)) {
                    // nothing needs to be done if s is the last element (last row) of users
                } else writer.newLine(); // this is coded to avoid writing an unnecessary line of empty space
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        users.clear();
    }

    public void codemaker1() {
        codemaker.clear();
        temp = CODE_PEGS1;
        Collections.shuffle(temp);
        for (int i = 0; i < 4; i++) {
            r = (int) (Math.random()*temp.size());
            codemaker.add(temp.get(r));
        }
        // System.out.println(codemaker);
    }

    public ArrayList<String> solo1(String[] strArray) {
        this.guess = new ArrayList<String>(Arrays.asList(strArray));
        outcome.clear(); toRemove.clear(); codemaker2.clear(); // clear all arrays used in this method
        ArrayList<String> codemaker2 = new ArrayList<>(codemaker); // duplicate the original array of codemaker
        
        // placing black key peg
        for (int i = 0; i < guess.size(); i++) {
            if (guess.get(i).equals(codemaker.get(i))) {
                outcome.add("BK");
                toRemove.add(i);
            }
        }

        // remove all elements captured under black key peg (to avoid double count)
        Collections.sort(toRemove, Collections.reverseOrder());
        for (int iterable : toRemove) {
            guess.remove(iterable);
            codemaker2.remove(iterable);
        }

        // placing white key peg
        for (int i = 0; i < guess.size(); i++) {
            index = codemaker2.indexOf(guess.get(i));
            if (index >= 0) {
                outcome.add("WH");
                codemaker2.remove(index);
            }
        }

        return outcome;
    }

}
