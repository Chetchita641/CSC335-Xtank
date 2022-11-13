/**
 * Author: Chris Macholtz
 * File name: Particle.java
 * Course: CSC 335
 * Assignment: XTank A3
 * Purpose: Particle used for explosion. A single point with a trajectory and lifespan.
 */
import java.util.Random;

public class Particle extends Glyph {
    private final double MAX_VELOCITY = 20;
    private final double FRICTION = 0.5;

    private double xCord;
    private double yCord;
    private double radians;
    private double velocity;
    private double lifespan;

    private Random rand = new Random();

    public Particle(double x, double y, double lifespan) {
        this.xCord = x;
        this.yCord = y;
        this.lifespan = lifespan;

        radians = rand.nextDouble()*2*Math.PI;
        velocity = rand.nextDouble()*MAX_VELOCITY;
    }
    
    @Override
	public void draw(XTankUI ui) {
		// Not used
	}

    @Override
    public boolean intersects(double x, double y) {
        // Not used
        return false;
    }

    /**
     * Continues the particle's trajectory. If lifespan is over, does not update
     * @param deltaTime - Amount of time since last update
     */
    @Override
    public void update(double deltaTime) {
        if (lifespan > 0) {
            velocity -= FRICTION*deltaTime;
            velocity = Math.max(velocity, 0);
            
            xCord += Math.cos(radians)*velocity*deltaTime;
            yCord -= Math.sin(radians)*velocity*deltaTime;

            lifespan -= deltaTime;
        } 
    }

    // Getters
    public double getLifespan() { return lifespan; }
    public double getXCord() { return xCord; }
    public double getYCord() { return yCord; }
    public double getRadians() { return radians; }
}
