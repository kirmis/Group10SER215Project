import java.net.*;
import java.io.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Client extends Application implements Runnable
{
	private Text status = new Text(300, 30, "Waiting for other players to join...");
	private Text playerTurn = new Text(320, 50,"");
	private Socket player;
	private ObjectOutputStream toServer = null;
	private ObjectInputStream fromServer = null;
	private int playerNum;
	private Board board = new Board();
	private Circle buttons[][];
	private boolean yourTurn;
	private boolean canMove;
	private Pane canvas = new Pane();
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{	
		GridPane joinPane = new GridPane();
		joinPane.setAlignment(Pos.CENTER);
		joinPane.setHgap(3);
		joinPane.setVgap(15);
		Label lTitle = new Label("Chinese Checkers");
		lTitle.setFont(new Font("Copperplate", 30));
		lTitle.setTextFill(Color.CRIMSON);
		joinPane.setAlignment(Pos.CENTER);
		TextField tfIPAddress = new TextField();
		TextField tfPortNum = new TextField();
		Button bJoinServer = new Button("Join Server");
		
		joinPane.add(lTitle, 0, 0, 2, 1);
		Label lIPAddress = new Label("IP address:");
		joinPane.add(lIPAddress, 0, 1);
		joinPane.add(tfIPAddress, 1, 1);
		Label lPortNumber = new Label("Port number: ");
		joinPane.add(lPortNumber, 0, 2);
		joinPane.add(tfPortNum, 1, 2);
		joinPane.add(bJoinServer, 1, 3);
		
		tfIPAddress.setAlignment(Pos.BOTTOM_LEFT);
		tfPortNum.setAlignment(Pos.BOTTOM_LEFT);
		joinPane.setHalignment(bJoinServer, HPos.CENTER);
		joinPane.setHalignment(lIPAddress, HPos.RIGHT);
		joinPane.setHalignment(lPortNumber, HPos.RIGHT);
		
		Scene joinScene = new Scene(joinPane, 300, 200);
		Stage joinWindow = new Stage();
		joinWindow.setTitle("Chinese Checkers");
		joinWindow.setScene(joinScene);
		joinWindow.show();
		
		bJoinServer.setOnAction(e ->
		{
			joinWindow.close();
			
			try
			{
				player = new Socket(tfIPAddress.getText(), Integer.parseInt(tfPortNum.getText()));
				toServer = new ObjectOutputStream(player.getOutputStream());
				toServer.flush();
				fromServer = new ObjectInputStream(player.getInputStream());
			}
			catch(IOException ex)
			{
				System.err.println(ex);
			}
			
			buttons = new Circle[13][17];
			
			Pane canvas = displayBoard(primaryStage);
			status.setFill(Color.AQUA);
			status.setFont(new Font("Verdana", 12));
			playerTurn.setFill(Color.ANTIQUEWHITE);
			playerTurn.setFont(new Font("Arial", 14));
			canvas.getChildren().add(status);
			canvas.getChildren().add(playerTurn);
			
			new Thread(this).start();
		});
	}
	
	@Override
	public void run()
	{
		try 
		{
			playerNum = fromServer.readInt();
			
			switch(playerNum)
			{
				case 1:
					status.setText("You are the GREEN player.");
					status.setFill(Color.GREEN);
					break;
				case 2:
					status.setText("You are the RED player.");
					status.setFill(Color.RED);
					break;
				case 3:
					status.setText("You are the YELLOW player.");
					status.setFill(Color.YELLOW);
					break;
				case 4:
					status.setText("You are the BLUE player.");
					status.setFill(Color.BLUE);
					break;
				case 5:
					status.setText("You are the PINK player.");
					status.setFill(Color.PINK);
					break;
				case 6:
					status.setText("You are the WHITE player.");
					status.setFill(Color.WHITE);
					break;	
			}
			
			int gameOver = 0;
			
			while(gameOver < 1)
			{
				int whichPlayer = fromServer.readInt();
				
				if(whichPlayer == playerNum)
				{
					yourTurn = true;
					canMove = true;
					playerTurn.setText("It is YOUR move");
					while(yourTurn == true)
					{
						Thread.sleep(500);
					}
					toServer.writeObject(board);
					toServer.flush();
				}
				else
				{
					playerTurn.setText("It is not your move");
				}
				
				gameOver = fromServer.readInt();	
				board = (Board) fromServer.readObject();
				updateBoard();
			}
			
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}
	
	public Pane displayBoard(Stage primaryStage)
	{
			
		canvas = new Pane();
	    canvas.setStyle("-fx-background-color: black;");
	    canvas.setPrefSize(200,200);
	    
	    for(int i = 0; i < 13; i++)
	    		for(int j = 0; j < 17; j++)
	    			buttons[i][j] = new Circle();
	     
	    for (int x = 0; x < 13; x++) {
	    	for (int y = 0; y < 17 ; y++)
	    	{
	    		if(board.getMarble(x, y).getColor() == 'g')
	    		{
	    			if(y % 2 == 0) {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Green"));
	    				buttons[x][y].relocate(x*40, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('g', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    			
	    			else {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Green"));
	    				buttons[x][y].relocate((x * 40) + 20, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('g', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    		}
	    		if(board.getMarble(x, y).getColor() == 'r')
	    		{
	    			if(y % 2 == 0) {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Red"));
	    				buttons[x][y].relocate(x*40, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('r', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    			
	    			else {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Red"));
	    				buttons[x][y].relocate((x * 40) + 20, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('r', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    		}
	    		if(board.getMarble(x, y).getColor() == 'b')
	    		{
	    			if(y % 2 == 0) {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Blue"));
	    				buttons[x][y].relocate(x*40, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('b', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    			
	    			else {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Blue"));
	    				buttons[x][y].relocate((x * 40) + 20, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('b', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    		}
	    		if(board.getMarble(x, y).getColor() == 'y')
	    		{
	    			if(y % 2 == 0) {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Yellow"));
	    				buttons[x][y].relocate(x*40, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('y', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    			
	    			else {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Yellow"));
	    				buttons[x][y].relocate((x * 40) + 20, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('y', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    		}
	    		if(board.getMarble(x, y).getColor() == 'w')
	    		{
	    			if(y % 2 == 0) {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("White"));
	    				buttons[x][y].relocate(x*40, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('w', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    			
	    			else {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("White"));
	    				buttons[x][y].relocate((x * 40) + 20, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('w', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    		}
	    		if(board.getMarble(x, y).getColor() == 'p')
	    		{
	    			if(y % 2 == 0) {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Pink"));
	    				buttons[x][y].relocate(x*40, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('p', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    			
	    			else {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Pink"));
	    				buttons[x][y].relocate((x * 40) + 20, y*40);
	    				buttons[x][y].setOnMouseClicked(new MoveHandler('p', buttons[x][y], x, y));
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    		}
	    		if(board.getMarble(x, y).getColor() == 'a')
	    		{
	    			if(y % 2 == 0) {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Grey"));
	    				buttons[x][y].relocate(x*40, y*40);
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    			
	    			else {
	    				buttons[x][y] = new Circle(10, Paint.valueOf("Grey"));
	    				buttons[x][y].relocate((x * 40) + 20, y*40);
		    			canvas.getChildren().addAll(buttons[x][y]);
	    			}
	    		}
	    	}
	    }
	    
	    Scene scene = new Scene(canvas, 13*40, 17*40);
		primaryStage.setTitle("Chinese Checkers Client"); // Set title
	    primaryStage.setScene(scene); // Place the scene in the stage
	    primaryStage.show(); // Display the stage
	    
	    return canvas;
	}
	
	public void updateBoard()
	{
		for(int x = 0; x < 13; x++)
		{
			for(int y = 0; y < 17; y++)
			{
				if(board.getMarble(x, y).getColor() == 'g')
				{
					buttons[x][y].setFill(Color.GREEN);
				}
				
				else if(board.getMarble(x, y).getColor() == 'r')
				{
					buttons[x][y].setFill(Color.RED);
				}
				
				else if(board.getMarble(x, y).getColor() == 'y')
				{
					buttons[x][y].setFill(Color.YELLOW);
				}
				
				else if(board.getMarble(x, y).getColor() == 'b')
				{
					buttons[x][y].setFill(Color.BLUE);
				}
				
				else if(board.getMarble(x, y).getColor() == 'p')
				{
					buttons[x][y].setFill(Color.PINK);
				}
				
				else if(board.getMarble(x, y).getColor() == 'w')
				{
					buttons[x][y].setFill(Color.WHITE);
				}
				
				else if(board.getMarble(x, y).getColor() == 'a')
				{
					buttons[x][y].setFill(Color.GREY);
				}
			}
		}
	}
	
	class MoveHandler implements EventHandler<MouseEvent>
	{
		private char color;
		private Circle circle;
		private int x;
		private int y;
		
		MoveHandler(char color, Circle circle, int x, int y)
		{
			this.color = color;
			this.circle = circle;
			this.x = x;
			this.y = y;
		}
		
		@Override
		public void handle(MouseEvent e)
		{
			if(yourTurn = true && canMove == true)
			{
				canMove = false;
				
				if(color == 'g' && playerNum == 1)
				{
					circle.setFill(Paint.valueOf("Orange"));
					
					if(y % 2 == 0)
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x, y-1));
						buttons[x-1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x-1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x, y+1));
						buttons[x-1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x-1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x-1, y));
						
					}
					else
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x, y-1));
						buttons[x+1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x+1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x, y+1));
						buttons[x+1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x+1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.GREEN, 'g', x, y, x-1, y));
					}
				}
				
				if(color == 'r' && playerNum == 2)
				{
					circle.setFill(Paint.valueOf("Orange"));
					
					if(y % 2 == 0)
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x, y-1));
						buttons[x-1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x-1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x, y+1));
						buttons[x-1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x-1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x-1, y));
					}
					else
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x, y-1));
						buttons[x+1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x+1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x, y+1));
						buttons[x+1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x+1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.RED, 'r', x, y, x-1, y));
					}
				}
				
				if(color == 'y' && playerNum == 3)
				{
					circle.setFill(Paint.valueOf("Orange"));
					
					if(y % 2 == 0)
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x, y-1));
						buttons[x-1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x-1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x, y+1));
						buttons[x-1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x-1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x-1, y));
					}
					else
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x, y-1));
						buttons[x+1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x+1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x, y+1));
						buttons[x+1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x+1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.YELLOW, 'y', x, y, x-1, y));
					}
				}
				
				if(color == 'b' && playerNum == 4)
				{
					circle.setFill(Paint.valueOf("Orange"));
					
					if(y % 2 == 0)
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x, y-1));
						buttons[x-1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x-1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x, y+1));
						buttons[x-1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x-1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x-1, y));
					}
					else
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x, y-1));
						buttons[x+1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x+1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x, y+1));
						buttons[x+1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x+1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.BLUE, 'b', x, y, x-1, y));
					}
				}
				
				if(color == 'p' && playerNum == 5)
				{
					circle.setFill(Paint.valueOf("Orange"));
					
					if(y % 2 == 0)
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x, y-1));
						buttons[x-1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x-1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x, y+1));
						buttons[x-1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x-1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x-1, y));
					}
					else
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x, y-1));
						buttons[x+1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x+1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x, y+1));
						buttons[x+1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x+1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.PINK, 'p', x, y, x-1, y));
					}
				}
				
				if(color == 'w' && playerNum == 6)
				{
					circle.setFill(Paint.valueOf("Orange"));
					
					if(y % 2 == 0)
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x, y-1));
						buttons[x-1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x-1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x, y+1));
						buttons[x-1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x-1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x-1, y));
					}
					else
					{
						buttons[x][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x, y-1));
						buttons[x+1][y-1].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x+1, y-1));
						buttons[x][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x, y+1));
						buttons[x+1][y+1].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x+1, y+1));
						buttons[x+1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x+1, y));
						buttons[x-1][y].setOnMouseClicked(new MoveOneSpaceHandler(Color.WHITE, 'w', x, y, x-1, y));
					}
				}
			}
		}
	}
	
	class MoveOneSpaceHandler implements EventHandler<MouseEvent>
	{
		private Color color;
		private char charColor;
		private int startX;
		private int startY;
		private int endX;
		private int endY;
		
		MoveOneSpaceHandler(Color color, char charColor, int startX, int startY, int endX, int endY)
		{
			this.color = color;
			this.charColor = charColor;
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}
		
		@Override
		public void handle(MouseEvent e)
		{
			if(board.checkMove(endX, endY) == true)
			{
				buttons[startX][startY].setFill(Paint.valueOf("Grey"));
				buttons[endX][endY].setFill(color);
				board.move(startX, startY, endX, endY);
			}
			
			buttons[startX][startY-1].setOnMouseClicked(new MoveHandler(charColor, buttons[startX][startY-1], startX, startY-1));
			buttons[startX-1][startY-1].setOnMouseClicked(new MoveHandler(charColor, buttons[startX-1][startY-1], startX-1, startY-1));
			buttons[startX][startY+1].setOnMouseClicked(new MoveHandler(charColor, buttons[startX][startY+1], startX, startY+1));
			buttons[startX-1][startY+1].setOnMouseClicked(new MoveHandler(charColor, buttons[startX-1][startY+1], startX-1, startY+1));
			buttons[startX+1][startY].setOnMouseClicked(new MoveHandler(charColor, buttons[startX+1][startY], startX+1, startY));
			buttons[startX-1][startY].setOnMouseClicked(new MoveHandler(charColor, buttons[startX-1][startY], startX-1, startY));
			buttons[startX+1][startY-1].setOnMouseClicked(new MoveHandler(charColor, buttons[startX+1][startY-1], startX+1, startY-1));
			buttons[startX+1][startY+1].setOnMouseClicked(new MoveHandler(charColor, buttons[startX+1][startY+1], startX+1, startY+1));
			yourTurn = false;
		}
	}
	
	public static void main(String [] args)
	{
		launch(args);
	}
}