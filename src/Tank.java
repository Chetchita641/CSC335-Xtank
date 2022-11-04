
public class Tank extends Glyph{

	private static final int WIDTH = 50;
	private static final int HEIGHT = 100;
	private double xCord;
	private double yCord;
	private int health;
	private double radians;
	private int playerId;
	private boolean isActive;
	private double deltaTime;
	
	public Tank(int pId, double startX, double startY) {
		xCord = startX;
		yCord = startY;
		radians = 0;
		health = 100;
		isActive = true;
		playerId = pId;
	}

	public double getXCord() {
		return xCord;
	}

	public double getYCord() {
		return yCord;
	}

	public double getRadians() {
		return radians;
	}
	
	@Override
	public void draw(XTankUI ui) {
		ui.drawTank(xCord, yCord, radians);
	}

	@Override
	public void move() {
		final double FORWARD = 0.2;

		xCord += (int) (Math.cos(radians)*FORWARD*deltaTime);
		yCord -= (int) (Math.sin(radians)*FORWARD*deltaTime);
	}

	@Override
	public boolean intersects(int x, int y) {
		return (x>=xCord&&x<=(xCord+WIDTH))&&(y>=yCord&&y<=(yCord+HEIGHT));
	}

	@Override
	public void increment(double deltaTime) {
		System.out.println("DEBUG: deltaTime: " + deltaTime);
		this.deltaTime = deltaTime;
		move();
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
		return "(" + playerId + "," + (int) xCord + "," + (int) yCord + ")";
	}
}
