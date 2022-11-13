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
	
	public abstract void move();
	
	public abstract boolean intersects(double x, double y);
	
	public abstract void update(double deltaTime);
}
