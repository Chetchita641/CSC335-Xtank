
public class Bullet extends Glyph {
	private static final double LIGHT_VELOCITY = 200;
	private static final double MEDIUM_VELOCITY = 150;
	private static final double HEAVY_VELOCITY = 100;

	private static final double LIGHT_LIFESPAN = 5;
	private static final double MEDIUM_LIFESPAN = 8;
	private static final double HEAVY_LIFESPAN = 10;

	private static final int LIGHT_DAMAGE = 20;
	private static final int MEDIUM_DAMAGE = 30;
	private static final int HEAVY_DAMAGE = 40;

	private final double FRICTION = 1; 
	private final int XLIMIT = 1500;
	private final int YLIMIT = 900;
	
	private double xCord;
	private double yCord;
	private double radians;
	private int type;
	private int damage;
	private double lifespan;
	private double deltaTime;
	private double velocity;
	private GameModel game;
	
	
	public Bullet(double x, double y, double r, int type) {
		xCord = x;
		yCord = y;
		radians = r;
		this.type = type;
		switch (type) {
			case 1:
				velocity = LIGHT_VELOCITY;
				lifespan = LIGHT_LIFESPAN;
				damage = LIGHT_DAMAGE;
				break;
			case 2:
				velocity = MEDIUM_VELOCITY;
				lifespan = MEDIUM_LIFESPAN;
				damage = MEDIUM_DAMAGE;
				break;
			case 3:
				velocity = HEAVY_VELOCITY;
				lifespan = HEAVY_LIFESPAN;
				damage = HEAVY_DAMAGE;
				break;
			default:
				System.out.println("Unknown tank type. Exiting.");
				System.exit(404);
		}

		game = GameModel.getInstance();
	}

	@Override
	public void draw(XTankUI ui) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean intersects(double x, double y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(double deltaTime) {
		this.deltaTime = deltaTime;
		lifespan -= deltaTime;

		if (velocity > 0) {
			velocity -= FRICTION*deltaTime;
			velocity = Math.max(velocity, 0);
		}
	
		// move forward 
		double newXCord = xCord + Math.cos(radians)*velocity*deltaTime;
		double newYCord = yCord - Math.sin(radians)*velocity*deltaTime;
		
		// wrap around
		newXCord = (newXCord%XLIMIT+XLIMIT)%XLIMIT;
		newYCord = (newYCord%YLIMIT+YLIMIT)%YLIMIT;
		
		// check for obstacles
		if(game.isOpen(newXCord, newYCord) && lifespan > 0) {
			xCord = newXCord;
			yCord = newYCord;
		}
		else {
			game.destroyBullet(this);
		}
	}

	public double getxCord() {
		return xCord;
	}

	public double getyCord() {
		return yCord;
	}

	public double getRadians() {
		return radians;
	}

	public int getType() {
		return type;
	}

	public int getDamage() {
		return damage;
	}
}
