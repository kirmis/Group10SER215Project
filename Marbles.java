import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Marbles implements java.io.Serializable
{
	private char color;
	
	public Marbles ()
	{
		color = 'z';
	}
	
	public Marbles (char x)
	{
		color = x;
	}

	public char getColor() {
		return color;
	}

	public void setColor(char color) {
		this.color = color;
	}
}
