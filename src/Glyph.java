
public abstract class Glyph {

	public abstract void draw(XTankUI ui);
	
	public abstract void move();
	
	public abstract boolean intersects(double x, double y);
	
	public abstract void update(double deltaTime);
}
