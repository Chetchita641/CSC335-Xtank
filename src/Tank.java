
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
	private boolean isClient;

	public Tank(int pId, double x, double y) {
		new Tank(pId, x, y, 100, 0, 100);
	}
	
	public Tank(int pId, double x, double y, double radians, double velocity, int health) {
		System.out.println("new tank at " + x + ", " + y);
		this.xCord = x;
		this.yCord = y;
		this.radians = radians;
		this.velocity = velocity;
		this.health = health;
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
	
	/*@Override
	public void draw(XTankUI ui) {
		ui.drawTank(xCord, yCord, radians);
	}*/
	
	@Override
	public void draw(XTankUI ui) {
		ui.drawTank(this);
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
		
	
		this.xCord += Math.cos(radians)*velocity*deltaTime;
		this.yCord -= Math.sin(radians)*velocity*deltaTime;
		//System.out.println("rad " + radians + " velo " + velocity + " d time " + deltaTime);
		//System.out.println("updated tank " + xCord + ", " + yCord);

	}
	
	public Bullet shoot() {
		System.out.println("SHHOOT");
		return new Bullet(xCord, yCord, radians);
	}
	
	public void rotateLeft() {
		radians += Math.PI/32;
	}
	
	public void rotateRight() {
		radians -= Math.PI/32;
	}
	
	public String toString() {
		return String.format("(%s,%f,%f,%f,%f,%d)", playerId, xCord, yCord, radians, velocity, health);
	}

	public String getID() {
		return ""+playerId;
	}
	
	/**
	 * Marks this tank as the cleint's
	 */
	public void setClientTank() {
		isClient = true;
	}
	
	/**
	 * Returns true if this tank is the client's tank
	 * @return
	 */
	public boolean isClientTank() {
		return isClient;
	}
}
