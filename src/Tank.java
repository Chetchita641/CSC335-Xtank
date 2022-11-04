
public class Tank extends Glyph{

	private static final int WIDTH = 50;
	private static final int HEIGHT = 100;
	private int xCord;
	private int yCord;
	private int health;
	private double orientation;
	private int playerId;
	private boolean isActive;
	
	public Tank(int pId, int startX, int startY) {
		xCord = startX;
		yCord = startY;
		orientation = 0;
		health = 100;
		isActive = true;
		playerId = pId;
	}
	
	@Override
	public void draw(XTankUI ui) {
		ui.drawTank(xCord, yCord, orientation);
	}

	@Override
	public void move() {
		yCord-=10;
	}

	@Override
	public boolean intersects(int x, int y) {
		return (x>=xCord&&x<=(xCord+WIDTH))&&(y>=yCord&&y<=(yCord+HEIGHT));
	}
	
	public void shoot() {
		//TODO
	}
	
	public void rotateLeft() {
		orientation += 5;
	}
	
	public void rotateRight() {
		orientation -= 5;
	}
	
	public String toString() {
		return "(" + playerId + "," + xCord + "," + yCord + ")";
	}
}
