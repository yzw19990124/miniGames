package client.gui;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import static common.WAMProtocol.*;
import common.WAMProtocol;

/**
 * The Client
 *
 * @author Yanzheng Wu
 */
public class MoleClient {
    /** Turn on if standard output debug messages are desired. */
    private static final boolean DEBUG = false;

    /**
     * Print method that does something only if DEBUG is true
     *
     * @param logMsg the message to log
     */
    private static void dPrint( Object logMsg ) {
        if ( MoleClient.DEBUG ) {
            System.out.println( logMsg );
        }
    }
    /** sentinel loop used to control the main loop */
    private boolean go;
    /**
     * Accessor that takes multithreaded access into account
     *
     * @return whether it ok to continue or not
     */
    private synchronized boolean goodToGo() {
        return this.go;
    }

    /**
     * Multithread-safe mutator
     */
    private synchronized void stop() {
        this.go = false;
    }

    private Socket clientSocket;
    private Scanner in;
    private PrintStream out;
    private WAMBoard board;
    private int rows;
    private int cols;
    private int numPlayers;
    private int playerNum;

    /**
     * The client constructor
     * @param hostname the host name from the arguement
     * @param portnum the port number from the argument
     * @throws IOException
     */
    public MoleClient (String hostname, int portnum) throws IOException {
        this.clientSocket = new Socket(hostname, portnum);
        this.in = new Scanner(clientSocket.getInputStream());
        this.out = new PrintStream(clientSocket.getOutputStream());
        this.go = true;

        String request = this.in.next();
        String arguments = this.in.nextLine();

        String[] args = arguments.split(" ");

        this.rows = Integer.parseInt(args[1]);
        this.cols = Integer.parseInt(args[2]);
        this.numPlayers = Integer.parseInt(args[3]);
        this.playerNum = Integer.parseInt(args[4]);

        this.board = new WAMBoard(rows, cols);

        if (!request.equals(WAMProtocol.WELCOME )) {
            System.out.println("Unable to connect to the server.");
        }
        else {
            MoleClient.dPrint("Connected to server " + this.clientSocket);
        }
    }

    /**
     * Get the number of rows
     * @return the rows
     */
    public int getRows () {return this.rows;}

    /**
     * Get the number of columns
     * @return the columns
     */
    public int getCols () {return this.cols;}

    /**
     * Making a new Thread for the client
     */
    public void startListener() {
        new Thread(() -> {
            try {
                this.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Called when the server sends a message saying that the
     * board has been won by this player. Ends the game.
     */
    public void gameWon() {
        MoleClient.dPrint( '!' + GAME_WON );

        dPrint( "You won!" );
        this.board.gameWon();
        this.stop();
    }

    /**
     * Called when the server sends a message saying that the
     * game has been won by the other player. Ends the game.
     */
    public void gameLost() {
        MoleClient.dPrint( '!' + GAME_LOST );
        dPrint( "You lost!" );
        this.board.gameLost();
        this.stop();
    }

    /**
     * Called when the server sends a message saying that the
     * game is a tie. Ends the game.
     */
    public void gameTied() {
        MoleClient.dPrint( '!' + GAME_TIED );
        dPrint( "You tied!" );
        this.board.gameTied();
        this.stop();
    }

    /**
     * UI wants to send a new mole number to server.
     *
     * @param num the mole number
     */
    public void sendMove(int num) {
        this.send( WHACK + " " + num + " " + playerNum);
        //also need to send to player number
    }

    /**
     * Run method that access the information from the server.
     * @throws IOException
     */
    public void run () throws IOException {
        while (this.goodToGo()) {
            String request = this.in.next();
            String arguments = this.in.nextLine().trim();
            switch (request) {
                case MOLE_UP:
                    board.molePops(Integer.parseInt(arguments));
                    break;
                case MOLE_DOWN:
                    board.moleDown(Integer.parseInt(arguments));
                    break;
                case WHACK:
                    break;
                case SCORE:
                    break;
                case GAME_LOST:
                    gameLost();
                    break;
                case GAME_WON:
                    gameWon();
                    break;
                case GAME_TIED:
                    gameTied();
                    break;
                case ERROR:
                    break;
            }
        }
        this.close();
    }

    public void send (String message) {
        out.println(message);
        out.flush();
    }

    public String read () {
        return in.nextLine();
    }

    /**
     * close the input and output and the socket
     * @throws IOException
     */
    public void close () throws IOException {
        in.close();
        out.close();

        clientSocket.close();
    }

    /**
     * It gets the board with the cols and rows from the server
     * @return the board
     */
    public WAMBoard getBoard() {
        return this.board;
    }

}
