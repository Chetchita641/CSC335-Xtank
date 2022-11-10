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
    public void move() {}

    @Override
    public boolean intersects(double x, double y) {
        return false;
    }

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

    public Particle[] getParticles() { return particles; }

    @Override
    public String toString() {
        return String.format("(%d, %d)", xCord, yCord);
    }
}
