package client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * The GUI
 *
 * @author Yanzheng Wu
 */
public class WAMGUI extends Application implements Observer<WAMBoard> {

    public MoleClient client;

    private GridPane gridPane;
    private BorderPane borderPane = new BorderPane();

    private WAMBoard gameBoard;
    private int port;
    private String host;
    private int rows;
    private int cols;
    /**
     * Initiating the board and add it to observer
     */
    @Override
    public void init() {
        try {
            List<String> args = getParameters().getRaw();
            String hosts = args.get(0);
            int ports = Integer.parseInt(args.get(1));
            this.port = ports;
            this.host = hosts;

            try{
                this.client = new MoleClient(host, port);
            } catch (IOException ie) {}

            this.rows = client.getRows();
            this.cols = client.getCols();
            this.gameBoard = client.getBoard();
            gameBoard.addObserver(this);


        } catch(NumberFormatException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * The start method with holes
     * @param stage the stage
     */
    @Override
    public void start(Stage stage) {
        gridPane = new GridPane();

        for (int row = 0; row < rows ; row++) {
            for (int col = 0; col < cols; col++) {
                Image holeI = new Image(WAMGUI.class.getResourceAsStream("hole.png"));
                ImageView holeNode = new ImageView(holeI);
                Button butt1 = new Button();
                butt1.setGraphic(holeNode);
                gridPane.add(butt1, col, row);
                int r = row;
                int c = col;
                butt1.setOnAction(e -> {
                    client.sendMove(conversionMoleNum(r, c));
                });
            }
        }
        borderPane.setCenter(gridPane);
        Scene scene = new Scene(borderPane);
        stage.setTitle("Whack A Mole!");
        stage.setScene(scene);
        stage.show();

        client.startListener();

    }

    /**
     * The refresh GUI
     */
    public void refreshGUI(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Image holeI = new Image(WAMGUI.class.getResourceAsStream("hole.png"));
                Image moleI = new Image(WAMGUI.class.getResourceAsStream("mole.jpg"));
                ImageView holeNode = new ImageView(holeI);
                ImageView moleNode = new ImageView(moleI);

                if (gameBoard.getContents(i, j) == 1) {
                    Button butt3 = new Button();
                    butt3.setGraphic(moleNode);
                    gridPane.add(butt3, j, i);
                }
                else if (gameBoard.getContents(i, j) == 0) {
                    Button butt2 = new Button();
                    butt2.setGraphic(holeNode);
                    gridPane.add(butt2, j, i);
                }
            }
        }
    }

    /**
     * convert row and cols to mole number
     * @param row row
     * @param column col
     * @return the mole number
     */
    public int conversionMoleNum (int row, int column) {
        return column + row * cols;
    }

    @Override
    public void update(WAMBoard whackMoleGame) {
        if (Platform.isFxApplicationThread()) {
            this.refreshGUI();
        } else {
            Platform.runLater(() -> this.refreshGUI());
        }

    }

    @Override
    public void stop() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java WAMGame host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }

}
