import java.net.*;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.*;


public class Server extends Application implements Runnable
{
	private ServerSocket server;
	private Socket player1;
	private Socket player2;
	private Socket player3;
	private Socket player4;
	private Socket player5;
	private Socket player6;
	private TextArea log;
	private int sessionNum;
	
	@Override
	public void start(Stage primaryStage)
	{
		log = new TextArea();
		log.setEditable(false);
		log.setMaxSize(450, 200);
		primaryStage.setTitle("Server log");
		Scene logScene = new Scene(new ScrollPane(log), 450, 200);
		primaryStage.setScene(logScene);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(e -> primaryStage.close());
		
		new Thread(this).start();
	}
	
	@Override
	public void run()
	{
		try
		{
			server = new ServerSocket(8000);
			log.appendText(new Date() + ": Server created at port 8000.\n");
			
			sessionNum = 0;
			
			while(true)
			{
				sessionNum++;
				
				player1 = server.accept();
				ObjectOutputStream toP1 = new ObjectOutputStream(player1.getOutputStream());
				ObjectInputStream fromP1 = new ObjectInputStream(player1.getInputStream());
				log.appendText(new Date() + ": Player 1 accepted into Session " + sessionNum + ".\n");
				player2 = server.accept();
				ObjectOutputStream toP2 = new ObjectOutputStream(player2.getOutputStream());
				ObjectInputStream fromP2 = new ObjectInputStream(player2.getInputStream());
				log.appendText(new Date() + ": Player 2 accepted into Session " + sessionNum + ".\n");
				player3 = server.accept();
				ObjectOutputStream toP3 = new ObjectOutputStream(player3.getOutputStream());
				ObjectInputStream fromP3 = new ObjectInputStream(player3.getInputStream());
				log.appendText(new Date() + ": Player 3 accepted into Session " + sessionNum + ".\n");
				player4 = server.accept();
				ObjectOutputStream toP4 = new ObjectOutputStream(player4.getOutputStream());
				ObjectInputStream fromP4 = new ObjectInputStream(player4.getInputStream());
				log.appendText(new Date() + ": Player 4 accepted into Session " + sessionNum + ".\n");
				player5 = server.accept();
				ObjectOutputStream toP5 = new ObjectOutputStream(player5.getOutputStream());
				ObjectInputStream fromP5 = new ObjectInputStream(player5.getInputStream());
				log.appendText(new Date() + ": Player 5 accepted into Session " + sessionNum + ".\n");
				player6 = server.accept();
				ObjectOutputStream toP6 = new ObjectOutputStream(player6.getOutputStream());
				ObjectInputStream fromP6 = new ObjectInputStream(player6.getInputStream());
				log.appendText(new Date() + ": Player 6 accepted into Session " + sessionNum + ".\n");
			
				log.appendText(new Date() + ": Starting Session " + sessionNum + ".\n");
				
				Thread handle = new Thread(new HandleASession(player1, toP1, fromP1, player2, toP2, fromP2, player3, toP3, fromP3, player4, toP4, fromP4, player5, toP5, fromP5, player6, toP6, fromP6));
				handle.start();
			}	
		}
		catch(IOException ex)
		{
			System.err.println(ex);
		}
	}
	
	public static void main(String [] args)
	{
		launch(args);
	}
}
