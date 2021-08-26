package common;
import server.WhackAMolePlayer;
import java.util.*;

public class WAMGame implements Runnable {

    private WAM game;
    private boolean gameState;
    private WhackAMolePlayer p3;
    private WhackAMolePlayer p2;
    private WhackAMolePlayer p1;

    private int p1Score;
    private int p2Score;
    private int p3Score;

    /**
     * the game constructor
     * @param p1 player one
     * @param p2 player two
     * @param p3 player three
     */
    public WAMGame(WhackAMolePlayer p1, WhackAMolePlayer p2, WhackAMolePlayer p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.game = new WAM(6, 7);
        gameState = true;
    }

    /**
     * will update the states
     * @return the game states
     */
    public boolean getState () {
        return gameState;
    }

    /**
     * will close the game
     */
    public void close () {
        this.close();
    }

    /**
     * will return scores regarding player with his score
     * @return
     */
    public Map<WhackAMolePlayer, Integer> getScores () {
        Map<WhackAMolePlayer, Integer> scoresMap = new HashMap<>();
        scoresMap.put(p1, p1Score);
        scoresMap.put(p2, p2Score);
        scoresMap.put(p3, p3Score);
        return scoresMap;
    }

    @Override
    public void run() {
        boolean go = true;
        while (go) {
            try {
                int upNum = game.moleUpNum();
                //tell players moles are up
                p1.readyToWhack(upNum);
                p2.readyToWhack(upNum);
                p3.readyToWhack(upNum);
                //this game thread will wait three secs for player responses.
                Thread.sleep(3000);
                //each player will whack the mole
                boolean p1Did = p1.getWhacked();
                boolean p2Did = p2.getWhacked();
                boolean p3Did = p3.getWhacked();
                //if player whacked, he earns one point
                if (p1Did) {p1Score++;}
                if (p2Did) {p2Score++;}
                if (p3Did) {p3Score++;}
                //moles will go down if still non-whacked
                int lastCol = game.getLastCols();
                int lastRow = game.getLastRows();
                int downNum = game.setMoleDown(lastRow, lastCol);
                p1.whackMade(downNum);
                p2.whackMade(downNum);
                p3.whackMade(downNum);
                //sends everyone's score
                p1.score(p1Score, p2Score, p3Score);
                p2.score(p1Score, p2Score, p3Score);
                p3.score(p1Score, p2Score, p3Score);

            } catch (InterruptedException ie) {}
        }
    }
}
