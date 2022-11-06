
public class Obstacle extends Glyph {
	private int xCord;
	private int yCord;
	private int width;
	private int height;

	public Obstacle(int x, int y, int w, int h) {
		xCord = x;
		yCord = y;
		width = w;
		height = h;
	}
	
	@Override
	public void draw(XTankUI ui) {
		ui.drawObstacle(this);
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
		// TODO Auto-generated method stub
	}
	
	public int getXCord() {
		return xCord;
	}
	
	public int getYCord() {
		return yCord;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
