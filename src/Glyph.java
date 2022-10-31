
public abstract class Glyph {

	public abstract void draw(XTankUI ui);
	
	public abstract void move();
	
	public abstract boolean intersects(int x, int y);
	
}
