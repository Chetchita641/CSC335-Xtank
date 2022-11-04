
public class Tank extends Glyph{

	private static final int WIDTH = 50;
	private static final int HEIGHT = 100;
	private int xCord;
	private int yCord;
	private int health;
	private double radians;
	private int playerId;
	private boolean isActive;
	
	public Tank(int pId, int startX, int startY) {
		xCord = startX;
		yCord = startY;
		radians = 0;
		health = 100;
		isActive = true;
		playerId = pId;
	}
	
	@Override
	public void draw(XTankUI ui) {
		ui.drawTank(xCord, yCord, radians);
	}

	@Override
	public void move() {
		final double FORWARD = 10;

		xCord += Math.cos(radians)*FORWARD;
		yCord -= Math.sin(radians)*FORWARD;
	}

	@Override
	public boolean intersects(int x, int y) {
		return (x>=xCord&&x<=(xCord+WIDTH))&&(y>=yCord&&y<=(yCord+HEIGHT));
	}
	
	public void shoot() {
		//TODO
	}
	
	public void rotateLeft() {
		radians += Math.PI/32;
	}
	
	public void rotateRight() {
		radians -= Math.PI/32;
	}
	
	public String toString() {
		return "(" + playerId + "," + xCord + "," + yCord + ")";
	}
}
