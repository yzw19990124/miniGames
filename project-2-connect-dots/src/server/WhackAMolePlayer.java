package server;
import client.gui.MoleClient;
import common.WAM;
import common.WAMProtocol;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class WhackAMolePlayer implements WAMProtocol, Closeable {
    /**
     * The {@link Socket} used to communicate with the client.
     */
    private Socket sock;

    /**
     * The {@link Scanner} used to read responses from the client.
     */
    private Scanner scanner;

    /**
     * The {@link PrintStream} used to send requests to the client.
     */
    private PrintStream printer;

    private WAM game;

    public WhackAMolePlayer(Socket sock) throws WhackAMoleException {
        this.sock = sock;
        try {
            scanner = new Scanner(sock.getInputStream());
            printer = new PrintStream(sock.getOutputStream());
        } catch (IOException e) {
            throw new WhackAMoleException(e);
        }
    }

    /**
     * will be called if it is welcome
     * @param str the welcome message
     */
    public void welcome(String str){ printer.println(str); }

    /**
     * will be called if game won
     */
    public void gameWon(){printer.println(GAME_WON);}

    /**
     * will be called if game lost
     */
    public void gameLost(){printer.println(GAME_LOST);}

    /**
     * will be called if game tied
     */
    public void gameTied(){ printer.println(GAME_TIED);}

    /**
     * will be called if errors
     * @param message error messages
     */
    public void error(String message){printer.println(ERROR + " " + message );}

    /**
     * will update the scores
     * @param p1Score player one score
     * @param p2Score player two score
     * @param p3Score player three score
     */
    public void score(int p1Score, int p2Score, int p3Score) {
        printer.println(SCORE + " " + p1Score + " " + p2Score + " " + p3Score);
    }

    /**
     * tell client the mole is up
     * @param moleNum the mole number
     */
    public void readyToWhack (int moleNum) {
        printer.println(MOLE_UP+" "+moleNum);
    }

    /**
     * read from client Whack moleNum playerNum
     * @return WHACK moleNumber playerNumber
     */
    public boolean getWhacked () {
        String answer = scanner.nextLine();
        List<String> stringList = new ArrayList<>(Arrays.asList(answer.split(" ")));
        if (game.isValid(Integer.parseInt(stringList.get(1)))) {
            int row = Integer.parseInt(stringList.get(1)) / game.getCols();
            int col = Integer.parseInt(stringList.get(1)) / game.getCols();
            game.setMoleDown(row, col);
            notify();
            return true;
        }
        return false;
    }

    /**
     * tell client a whack has made or mole is down
     * @param moleNum the mole number
     */
    public void whackMade (int moleNum) {
        printer.println(MOLE_DOWN+" "+moleNum);
    }

    @Override
    public void close() {
        try {
            sock.close();
        } catch (IOException e) {
            // squash
        }
    }
}
