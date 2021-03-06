package sovsen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * @sovsen on 22-Sep-18.
 */
public class Server extends Application
        implements TicTacToeConstants {
    private int sessionNo = 1; // Number a session

    // Declares a boolean datatype
    private boolean isRunning = true;

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {

        // Creates an instance of a TextArea object
        TextArea taLog = new TextArea();

        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
        primaryStage.setTitle("Server"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(3001);

                Platform.runLater(() -> taLog.appendText(new Date() +
                        ": Server started at socket 3001\n"));

                // Ready to create a session for every two players
                while (isRunning) {

                    // Disabling the loop if sessionNo is 3
                    if (sessionNo == 3) {
                        isRunning = false;
                    }

                    // Checks if the sessionNo is less or equals 2
                    if (sessionNo <= 2) {

                        Platform.runLater(() -> taLog.appendText(new Date() +
                                ": Wait for players to join session " + sessionNo + '\n'));


                        // Connect to player 1
                        Socket player1 = serverSocket.accept();

                        Platform.runLater(() -> {
                            taLog.appendText(new Date() + ": Player 1 joined session "
                                    + sessionNo + '\n');
                            taLog.appendText("Player 1's IP address" +
                                    player1.getInetAddress().getHostAddress() + '\n');
                        });

                        // Notify that the player is Player 1
                        new DataOutputStream(
                                player1.getOutputStream()).writeInt(PLAYER_1);

                        // Connect to player 2
                        Socket player2 = serverSocket.accept();

                        Platform.runLater(() -> {
                            taLog.appendText(new Date() +
                                    ": Player 2 joined session " + sessionNo + '\n');
                            taLog.appendText("Player 2's IP address" +
                                    player2.getInetAddress().getHostAddress() + '\n');
                        });

                        // Notify that the player is Player 2
                        new DataOutputStream(
                                player2.getOutputStream()).writeInt(PLAYER_2);

                        // Display this session and increment session number
                        Platform.runLater(() ->
                                taLog.appendText(new Date() +
                                        ": Start a thread for session " + sessionNo + '\n'));


                        // Launch a new thread for this session of two players
                        new Thread(new HandleASession(player1, player2)).start();

                        // Adds 1 to sessionNo
                        sessionNo++;
                    } else {
                        Platform.runLater(() -> taLog.appendText(new Date() +
                                ": Player attempted to join. No sessions available. " + sessionNo + '\n'));

                    }


                }

                if (sessionNo >= 3) {
                    Platform.runLater(() -> {
                        try {
                            // Connect to player
                            Socket player = serverSocket.accept();

                            // Sends a too many players error number to the client
                            new DataOutputStream(
                                    player.getOutputStream()).writeInt(ERROR_TOO_MANY_PLAYERS);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                }


            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();


    }
}
