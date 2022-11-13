/**
 * Author: Grace Driskill
 * File name: Glyph.java
 * Course: CSC 335
 * Assignment: XTank A3
 * Purpose: Abstract class that represents all the visible objects 
 * 	in the XTank game
 */
public abstract class Glyph {
	
	public abstract void draw(XTankUI ui);
	
	/**
	 * Returns true if the specified coordinates interest this
	 * Glyph
	 * @param x	x coordinate to check
	 * @param y y coordinate to check
	 * @return true if coordinate intersects, false otherwise
	 */
	public abstract boolean intersects(double x, double y);
	
	/**
	 * Updates this Glyph's position based on an amount of passed time.
	 * This is done based on the Glyph's start position and velocity
	 * @param deltaTime
	 */
	public abstract void update(double deltaTime);
	
	/**
	 * Returns the Glyph's x coordinate
	 */
	public abstract double getXCord();
	
	/**
	 * Returns the Glyph's y coordinate 
	 */
	public abstract double getYCord();
}
