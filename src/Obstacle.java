/**
 * Author: Grace Driskill
 * File name: Obstacle.java
 * Course: CSC 335
 * Assignment: XTank A3
 * Purpose: Represents an Obstacle in XTank, which make up the game's
 * 	maze. 
 */
public class Obstacle extends Glyph {
	private int xCord;
	private int yCord;
	private int width;
	private int height;

	/**
	 * Creates a new Obstacle with the specified values
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param w	width
	 * @param h	height
	 */
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
	public boolean intersects(double x, double y) {
		return (x>=xCord-15&&x<=(xCord+width+15)&&(y>=yCord-15&&y<=(yCord+height+15)));
	}

	@Override
	public void update(double deltaTime) {
		// does nothing since obstacles do not move
	}
	
	@Override
	public double getXCord() {
		return xCord;
	}
	
	@Override
	public double getYCord() {
		return yCord;
	}

	/**
	 * Returns the obstacle's width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the obstacle's height
	 */
	public int getHeight() {
		return height;
	}
}
