/**
 * Author: Chris Macholtz
 * File name: Explosion.java
 * Course: CSC 335
 * Assignment: XTank A3
 * Purpose: Explosion object. Spawns a random number of particles with random lifespans
 */
import java.util.Random;

public class Explosion extends Glyph {
    private final int PARTICLE_MIN = 10;
    private final int PARTICLE_MAX = 20;

    private double lifespan = 10;
    private int xCord;
    private int yCord;
    private Particle[] particles;

    public Explosion(int x, int y) {
        this.xCord = x;
        this.yCord = y;
        
        Random rand = new Random();
        int count = rand.nextInt((PARTICLE_MAX-PARTICLE_MIN)+PARTICLE_MIN);
        this.particles = new Particle[count];

        for (int i = 0; i < count; i++) {
            this.particles[i] = new Particle(x, y, rand.nextDouble()*lifespan);
        }
    }
    
    @Override
	public void draw(XTankUI ui) {
		ui.drawExplosion(this);
	}

    @Override
    public boolean intersects(double x, double y) {
        return false;
    }

    /**
     * Updates each of the particles on their trajectories. If lifespan is done, does not update
     * @param deltaTime - Amount of time since last update
     */
    @Override
    public void update(double deltaTime) {
        if (lifespan > 0) {
            for (Particle p : particles) {
                if (p.getLifespan() > 0) {
                    p.update(deltaTime);
                }
            }
            lifespan -= deltaTime;
            
        }
    }

    /**
     * Gets the particles
     * @return array of particles
     */
    public Particle[] getParticles() { return particles; }

    @Override
    public String toString() {
        return String.format("(%d, %d)", xCord, yCord);
    }

	@Override
	public double getXCord() {
		return xCord;
	}

	@Override
	public double getYCord() {
		return yCord;
	}
}
