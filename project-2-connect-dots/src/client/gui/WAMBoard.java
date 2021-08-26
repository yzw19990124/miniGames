package client.gui;

import java.util.LinkedList;
import java.util.List;

/**
 * The Board
 *
 * @author Yanzheng Wu
 */
public class WAMBoard {
    private final int SLEEPTIME = 1000;
    private int rows;
    private int cols;
    private boolean clicked;
    private int duration;
    private static int counter = 0;
    private List<Observer<WAMBoard>> observers;
    private MoleClient client;
    private Status status;

    /** Possible statuses of game */
    public enum Status {
        NOT_OVER, I_WON, I_LOST, TIE, ERROR;

        private String message = null;

        public void setMessage( String msg ) {
            this.message = msg;
        }

        @Override
        public String toString() {
            return super.toString() +
                    this.message == null ? "" : ( '(' + this.message + ')' );
        }
    }

    private int[][] board;

    /**
     * the board class constructor
     * @param rows row
     * @param cols column
     */
    public WAMBoard(int rows, int cols) {
        this.cols = cols;
        this.rows = rows;
        this.observers = new LinkedList<>();
        board = new int[rows][cols];
    }

    public void isClicked () {
        clicked = true;
    }

    public int[][] getBoard () {
        return this.board;
    }

    /**
     * Get the number in the specific slot
     * @param rows row
     * @param cols colomn
     * @return board
     */
    public int getContents(int rows, int cols){
        return board[rows][cols];
    }

    public void close () {alertObserver();}

    /**
     * Add observer to the list.
     * @param observer
     */
    public void addObserver(Observer<WAMBoard> observer) {
        observers.add(observer);
    }

    /**
     * To make a mole pops up and set it to one.
     * @param num the parsing number
     */
    public void molePops (int num) {
        int rows = num / cols;
        int col = num % cols;
        board[rows][col] = 1;
        alertObserver();
    }

    /**
     * To make a mole pops down and set it to zero
     * @param num the parsing number
     */
    public void moleDown (int num) {
        int rows = num / cols;
        int col = num % cols;
        board[rows][col] = 0;
        alertObserver();
    }

    /**
     * Called when the game has been won by this player.
     */
    public void gameWon() {
        this.status = Status.I_WON;
        alertObserver();
    }

    /**
     * Called when the game has been won by the other player.
     */
    public void gameLost() {
        this.status = Status.I_LOST;
        alertObserver();
    }

    /**
     * Called when the game has been tied.
     */
    public void gameTied() {
        this.status = Status.TIE;
        alertObserver();
    }

    public void error(String arguments) {
        this.status = Status.ERROR;
        this.status.setMessage(arguments);
        alertObserver();
    }

    /**
     * Get game status.
     * @return the Status object for the game
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Alert the observer
     */
    private void alertObserver () {
        for (Observer<WAMBoard> obs: this.observers) {
            obs.update(this);
        }
    }


}
