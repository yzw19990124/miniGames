package common;
import javax.print.attribute.standard.RequestingUserName;
import java.util.Random;

public class WAM {
    private int rows;
    private int cols;
    private Mole[][] board;
    private int lastRows;
    private int lastCols;

    public enum Mole { MOLE_UP, HOLE; }

    /**
     * Generates a WAM game using a board with input rows and cols.
     * @param rows the number of rows
     * @param cols the number of cols
     */
    public WAM (int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        board = new Mole[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = Mole.HOLE;
            }
        }
    }

    /**
     * will return mole number using rows and cols
     * @return mole number
     */
    public int moleUpNum () {
        Random position = new Random();
        int randRow = position.nextInt(rows);
        int randCol = position.nextInt(cols);
        if (board[randRow][randCol].equals(Mole.HOLE)) {
            this.lastCols = randCol;
            this.lastRows = randRow;
            int moleNum = randCol + cols * randRow;
            board[randRow][randCol] = Mole.MOLE_UP;
            return moleNum;
        }
        return 0;
    }

    /**
     * will return rows from board
     * @return
     */
    public int getLastRows () {return lastRows;}

    /**
     * will return cols from board
     * @return
     */
    public int getLastCols () {return lastCols;}
    public int getRows () {return rows;}
    public int getCols () {return cols;}

    /**
     * will set the mole down with knowing row and cols
     * @param row row number
     * @param col col number
     * @return the down mole's position
     */
    public int setMoleDown (int row, int col) {
        board[row][col] = Mole.HOLE;
        return col + cols * row;
    }

    /**
     * Check if the mole is whack-able
     * @param moleNum the mole number
     * @return true if the mole is whack-able
     */
    public boolean isValid (int moleNum) {
        int row = moleNum / cols;
        int col = moleNum % cols;
        return !board[row][col].equals(Mole.HOLE);
    }
}
