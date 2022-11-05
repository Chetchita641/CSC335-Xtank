import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

	public synchronized void updateState() {
		//System.out.println(tanks.size());
		long currentTime = System.nanoTime();
		double deltaTime = (double) (currentTime-lastTime)/1000000000;
		for (Tank tank : tanks) {
			//System.out.println(";)");
			tank.update(deltaTime);
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
	public synchronized void addTank(int index) {
		this.addTank(index, 300, 500, 1, 0, 100);

	}
	
	/**
	 * Creates a new Tank and adds it at the specified index and coordinates in 
	 * the list of Tanks
	 * @param index
	 * @param xCord
	 * @param yCord
	 */
	public synchronized void addTank(int index, int xCord, int yCord) {
		Tank t =  new Tank(index, xCord, yCord);
		tanks.add(index, t);
		glyphs.add(t);
		System.out.println("Tank added");
		lastChange = "add tanks " + t.toString();
	}
	
	public synchronized void addTank(int playerId, double xCord, double yCord, double rads, double velo, int health) {
		Tank t =  new Tank(playerId, xCord, yCord, rads, velo, health);
		tanks.add(playerId, t);
		glyphs.add(t);
		System.out.println("Tank added");
		lastChange = "add tanks " + t.toString();
	}
	 
	/**
	 * Moves the tank at the specified index forward one
	 * @param index 
	 */
	public synchronized void moveTank(int playerId) {
		tanks.get(playerId).move();
		lastChange = "move: player " + playerId;
	}

	public synchronized void rotateLeft(int playerId) {
		tanks.get(playerId).rotateLeft();
		lastChange = "left: player " + playerId;
	}

	public synchronized void rotateRight(int playerId) {
		tanks.get(playerId).rotateRight();
		lastChange = "right: player " + playerId;
	}

	public synchronized void backward(int playerId) {
		tanks.get(playerId).backward();
		lastChange = "back: player " + playerId;
	}
	
	public synchronized void shoot(int playerId) {
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
