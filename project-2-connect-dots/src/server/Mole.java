package server;

import common.WAMGame;

public class Mole implements Runnable {
    private final int TIMELIMIT = 3000;
    private boolean isUp;
    private WAMGame game;

    public Mole () {
        this.isUp = false;
    }
    @Override
    public void run() {
        Runnable example = new Mole();
        Thread exampleThread = new Thread(example);
        try {
            while (game.getState()) {
                isUp = true;
                Thread.sleep(TIMELIMIT);
                isUp = false;
            }
        } catch (InterruptedException ie) {}
    }

}
