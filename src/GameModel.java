import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

public class GameModel implements Serializable {
	private static GameModel gameModelInstance;

	public String lastChange;

	private long lastTime;
	
	private List<Glyph> glyphs;
	private List<Tank> tanks;
	private List<Client> clients;

	Thread thread;
	
	/**
	 * Creates a new GameModel
	 */
	private GameModel() {
		glyphs = new ArrayList<Glyph>();
		tanks = new ArrayList<Tank>();
		clients = new ArrayList<Client>();
		lastTime = System.nanoTime();

	}

	public static GameModel getInstance() {
		if (gameModelInstance == null) {
			gameModelInstance = new GameModel();
		}
		return gameModelInstance;
	}

	public List<Tank> getTanks() {
		return tanks;
	}

	public void addClient(Client client) {
		clients.add(client);
	}

	public void updateState() {
		long currentTime = System.nanoTime();
		double deltaTime = (currentTime-lastTime)/1000000;
		for (Tank tank : tanks) {
			tank.increment(deltaTime);
		}
		lastTime = currentTime;
	}

	/**
	 * Draws every Gylph in the specified XTankUI
	 * @param ui
	 */
	public void drawAll(XTankUI ui) {
		for(Glyph g: glyphs) {
			g.draw(ui);
		}
	}
	
	/**
	 * Creates a new Tank and adds it at the specified index in the list
	 * of Tanks
	 * @param index
	 */
	public void addTank(int index) {
		this.addTank(index, 300, 500);

	}
	
	/**
	 * Creates a new Tank and adds it at the specified index and coordinates in 
	 * the list of Tanks
	 * @param index
	 * @param xCord
	 * @param yCord
	 */
	public void addTank(int index, int xCord, int yCord) {
		Tank t =  new Tank(index, xCord, yCord);
		tanks.add(index, t);
		glyphs.add(t);
		System.out.println("Tank added");
		lastChange = "add tanks " + t.toString();
	}
	 
	/**
	 * Moves the tank at the specified index forward one
	 * @param index 
	 */
	public void moveTank(int index) {
		tanks.get(index).move();
		lastChange = "move: player " + index;
	}

	public void rotateLeft(int index) {
		tanks.get(index).rotateLeft();
		lastChange = "left: player " + index;
	}

	public void rotateRight(int index) {
		tanks.get(index).rotateRight();
		lastChange = "right: player " + index;
	}
	
	public void shoot(int playerId) {
		tanks.get(playerId).shoot();
		lastChange = "shoot: player " + playerId;
	}
	
	/**
	 * Returns a String with information on all the Tanks in this GameModel. 
	 * Has the player ID, x coordinate and y coordinate for each Tank
	 * @return 
	 */
	public String listTanks() {
		String retVal = "";
		for(Tank t: tanks) {
			retVal += t.toString();
		}
		return retVal;
	}
	
	/**
	 * Returns the last change that's happened in this GameModel
	 */
	public String getLastChange() {
		return lastChange;
	}
}
