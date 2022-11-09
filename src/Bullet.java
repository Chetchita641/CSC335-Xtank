
public class Bullet extends Glyph {
	private final double FRICTION = 1; 
	private final int XLIMIT = 1500;
	private final int YLIMIT = 900;
	
	private double xCord;
	private double yCord;
	private double radians;
	private double deltaTime;
	private double velocity;
	private GameModel game;
	
	
	public Bullet(double x, double y, double r) {
		xCord = x;
		yCord = y;
		radians = r;
		velocity = 150;
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
		
		if (velocity > 0) {
			velocity -= FRICTION*deltaTime;
			velocity = Math.max(velocity, 0);
		}
		else if (velocity < 0) {
			velocity += FRICTION*deltaTime;
			velocity = Math.min(velocity, 0);
		}
	
		// move forward 
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
}
