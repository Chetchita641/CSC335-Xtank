
public class Bullet extends Glyph {
	private final double FRICTION = 1; 

	private double xCord;
	private double yCord;
	private double radians;
	private double deltaTime;
	private double velocity;
	
	public Bullet(double x, double y, double r) {
		xCord = x;
		yCord = y;
		radians = r;
		velocity = 150;
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
	public boolean intersects(int x, int y) {
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
	
		this.xCord += Math.cos(radians)*velocity*deltaTime;
		this.yCord -= Math.sin(radians)*velocity*deltaTime;
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
