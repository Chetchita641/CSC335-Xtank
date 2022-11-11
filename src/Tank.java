
public class Tank extends Glyph{
	private static final int LIGHT_WIDTH = 15;
	private static final int MEDIUM_WIDTH = 20;
	private static final int HEAVY_WIDTH = 30;

	private static final int LIGHT_HEIGHT = 30;
	private static final int MEDIUM_HEIGHT = 40;
	private static final int HEAVY_HEIGHT = 50;

	private static final int LIGHT_HEALTH = 80;
	private static final int MEDIUM_HEALTH = 100;
	private static final int HEAVY_HEALTH = 120;
	
	private static final int LIGHT_ACCEL = 3;
	private static final int MEDIUM_ACCEL = 2;
	private static final int HEAVY_ACCEL = 1;

	private int type;
	private int width;
	private int height;
	private int health;
	private double acceleration;

	private final double FRICTION = 1; 
	private final double MAX_VELOCITY = 100;
	private final int XLIMIT = 1500;
	private final int YLIMIT = 900;

	private double xCord;
	private double yCord;
	
	private double radians;
	private int playerId;
	private boolean isActive;
	private double deltaTime;
	private double velocity;
	private boolean isClient;
	private GameModel game;

	/*
	public Tank(int pId, double x, double y) {
		new Tank(pId, x, y, 0, 0, 100);
	}
	*/
	
	public Tank(int pId, int type, double x, double y, double radians, double velocity, int health) {
		this.xCord = x;
		this.yCord = y;
		this.radians = radians;
		this.velocity = velocity;
		this.type = type;
		switch (type) {
			case 1:
				width = LIGHT_WIDTH;
				height = LIGHT_HEIGHT;
				health = LIGHT_HEALTH;
				acceleration = LIGHT_ACCEL;
				break;
			case 2:
				width = MEDIUM_WIDTH;
				height = MEDIUM_HEIGHT;
				health = MEDIUM_HEALTH;
				acceleration = MEDIUM_ACCEL;
				break;
			case 3:
				width = HEAVY_WIDTH;
				height = HEAVY_HEIGHT;
				health = HEAVY_HEALTH;
				acceleration = HEAVY_ACCEL;
				break;
			default:
				System.out.println("Unknown tank type. Exiting.");
				System.exit(404);
		}

		isActive = true;
		playerId = pId;
		game = GameModel.getInstance();
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getType() {
		return type;
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
		velocity += acceleration;
		velocity = Math.min(velocity, MAX_VELOCITY);
		update(deltaTime);
	}

	public void backward() {
		velocity -= acceleration;
		velocity = Math.max(velocity, -MAX_VELOCITY);
		update(deltaTime);
	}

	@Override
	public boolean intersects(double x, double y) {
		// check if point intersections with a circle centered on the tank
		// with radius of the tank's longest dimension
		int circleRadius = Math.max(height, width)/2;
		double xDistanceSqrd = Math.pow((x - xCord), 2);
		double yDistanceSqrd = Math.pow((y - yCord), 2);
		double radiusSqrd = Math.pow((circleRadius), 2);
		return xDistanceSqrd + yDistanceSqrd <= radiusSqrd;
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
		
		// move
		double newXCord = xCord + Math.cos(radians)*velocity*deltaTime;
		double newYCord = yCord - Math.sin(radians)*velocity*deltaTime;
		
		// wrap around
		newXCord = (newXCord%XLIMIT+XLIMIT)%XLIMIT;
		newYCord = (newYCord%YLIMIT+YLIMIT)%YLIMIT;
		
		
		// check for obstacles
		if(game.isOpen(newXCord, newYCord)) {
			xCord = newXCord;
			yCord = newYCord;
		}
		else {
			velocity = 0;
		}
	}
	
	public Bullet shoot() {
		return new Bullet(xCord+Math.cos(radians)*50, yCord-Math.sin(radians)*50, radians, type);
	}
	
	public void rotateLeft() {
		radians += Math.PI/32;
	}
	
	public void rotateRight() {
		radians -= Math.PI/32;
	}
	
	public String toString() {
		return String.format("(%s,%f,%f,%f,%f,%d)", playerId, xCord, yCord, radians, velocity, type);
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

	public void wasShot(Bullet bullet) {
		System.out.println("a tank was shot");
		health-=bullet.getDamage();
		if(health<=0) {
			isActive = false;
			game.removePlayer(playerId);
		}
	}

	public boolean isActive() {
		return isActive;
	}
}
