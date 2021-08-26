package server;

import client.gui.MoleClient;
import common.WAMGame;
import common.WAMProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class WAMServer implements WAMProtocol, Runnable {

    private ServerSocket gameServer;

    /**
     * the server constructor
     * @param port the port number
     * @throws WhackAMoleException
     */
    private WAMServer(int port) throws WhackAMoleException{
        try {
            gameServer = new ServerSocket(port);
        } catch (IOException ioe) {
            throw new WhackAMoleException(ioe);
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Waiting for player one...");

            Socket playerOneSocket = gameServer.accept();
            WhackAMolePlayer player1 = new WhackAMolePlayer(playerOneSocket);
            player1.welcome(WELCOME+" "+6+" "+7+" "+3+" "+1);
            System.out.println("Player 1 has connected!");

            System.out.println("Waiting for player two...");
            Socket playerTwoSocket = gameServer.accept();
            WhackAMolePlayer player2 = new WhackAMolePlayer(playerTwoSocket);
            player2.welcome(WELCOME+" "+6+" "+7+" "+3+" "+2);
            System.out.println("Player 2 has connected!");

            System.out.println("Waiting for player three...");
            Socket playerThreeSocket = gameServer.accept();
            WhackAMolePlayer player3 = new WhackAMolePlayer(playerThreeSocket);
            player3.welcome(WELCOME+" "+6+" "+7+" "+3+" "+3);
            System.out.println("Player 3 has connected!");

            System.out.println("LETS WHACK SOME MOLES!");
            WAMGame game  = new WAMGame(player1, player2, player3);

            new Thread(game).run();
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ie) {}
            game.close();

            System.out.println("Here are your scores!");
            Map<WhackAMolePlayer, Integer> scoreMap = game.getScores();
            System.out.println("Player One: "+scoreMap.get(player1));
            System.out.println("Player Two: "+scoreMap.get(player2));
            System.out.println("Player Three: "+scoreMap.get(player3));
            //Finding the max score
            Map.Entry<WhackAMolePlayer, Integer> maxEntry = null;
            List<Map.Entry<WhackAMolePlayer, Integer>> winner = new ArrayList<>();
            for (Map.Entry<WhackAMolePlayer, Integer> entry : scoreMap.entrySet())
            {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                {
                    maxEntry = entry;
                }
                else if (entry.getValue().compareTo(maxEntry.getValue()) == 0) {
                    winner.add(entry);
                }
            }
            if (winner.size() != 0) {
                for (Map.Entry entry : winner) {
                    if (entry.getKey().equals(player1)) {player1.gameTied();}
                    if (entry.getKey().equals(player2)) {player2.gameTied();}
                    if (entry.getKey().equals(player3)) {player3.gameTied();}
                }
                for (WhackAMolePlayer player : scoreMap.keySet()) {
                    if (!winner.contains(player)) {player.gameLost();}
                }
            }
            else {
                if (maxEntry.getKey().equals(player1)) {
                    player1.gameWon();
                    player2.gameLost();
                    player3.gameLost();
                }
                else if (maxEntry.getKey().equals(player2)) {
                    player1.gameLost();
                    player2.gameWon();
                    player3.gameLost();
                }
                else {
                    player1.gameLost();
                    player2.gameLost();
                    player3.gameWon();
                }
            }

        } catch (IOException e) {
            System.err.println("Something has gone wrong!!!");
            e.printStackTrace();
        } catch (WhackAMoleException we) {
            //squashing
        }
    }



    public static void main(String[] args) throws WhackAMoleException{
        if (args.length != 1) {
            System.out.println("Usage: java ConnectFourServer <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        WAMServer server = new WAMServer(port);
        server.run();
    }
}
