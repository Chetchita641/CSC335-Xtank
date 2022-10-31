import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameModel implements Serializable {
	public String lastCommand;
	
	private List<Glyph> glyphs;
	private List<Tank> tanks;
	
	public GameModel() {
		glyphs = new ArrayList<Glyph>();
		tanks = new ArrayList<Tank>();
	}
	
	public void drawAll(XTankUI ui) {
		for(Glyph g: glyphs) {
			g.draw(ui);
		}
	}
	
	public void addTank(int index) {
		this.addTank(index, 300, 500);
	}
	
	public void addTank(int index, int xCord, int yCord) {
		Tank t =  new Tank(index, xCord, yCord);
		tanks.add(index, t);
		glyphs.add(t);
	}
	
	public void moveTank(int index) {
		tanks.get(index).move();
	}
	
	public String listTanks() {
		String retVal = "";
		for(Tank t: tanks) {
			retVal += t.toString();
		}
		return retVal;
	}
	
	public String toString() {
		return lastCommand;
	}
}
