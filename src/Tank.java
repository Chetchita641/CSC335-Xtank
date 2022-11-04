
public class Tank extends Glyph{
	private final double ACCELERATION = 2;
	private final double FRICTION = 1; 
	private final double MAX_VELOCITY = 100;

	private static final int WIDTH = 50;
	private static final int HEIGHT = 100;
	private double xCord;
	private double yCord;
	private int health;
	private double radians;
	private int playerId;
	private boolean isActive;
	private double deltaTime;
	private double velocity;
	
	public Tank(int pId, double startX, double startY) {
		xCord = startX;
		yCord = startY;
		radians = 0;
		health = 100;
		isActive = true;
		playerId = pId;
		velocity = 0;
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
		velocity += ACCELERATION;
		velocity = Math.min(velocity, MAX_VELOCITY);
		update(deltaTime);
	}

	public void backward() {
		velocity -= ACCELERATION;
		velocity = Math.max(velocity, -MAX_VELOCITY);
		update(deltaTime);
	}

	@Override
	public boolean intersects(int x, int y) {
		return (x>=xCord&&x<=(xCord+WIDTH))&&(y>=yCord&&y<=(yCord+HEIGHT));
	}

	@Override
	public void update(double deltaTime) {
		this.deltaTime = deltaTime;
		if (velocity > 0) {
			velocity -= FRICTION*deltaTime;
			velocity = Math.max(velocity, 0);
		}
		else if (velocity < 0) {
			velocity += FRICTION*deltaTime;
			velocity = Math.min(velocity, 0);
		}
	
		xCord += Math.cos(radians)*velocity*deltaTime;
		yCord -= Math.sin(radians)*velocity*deltaTime;
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
