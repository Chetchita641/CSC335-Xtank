import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameModel implements Serializable {
	public String lastChange;
	
	private List<Glyph> glyphs;
	private List<Tank> tanks;
	
	/**
	 * Creates a new GameModel
	 */
	public GameModel() {
		glyphs = new ArrayList<Glyph>();
		tanks = new ArrayList<Tank>();
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
