import java.io.Serializable;

public class GameModel implements Serializable {
	public String lastCommand;
	
	public String toString() {
		return lastCommand;
	}
}
