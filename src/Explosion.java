import java.util.Random;

public class Explosion extends Glyph {
    private final int PARTICLE_MIN = 5;
    private final int PARTICLE_MAX = 10;

    private int xCord;
    private int yCord;
    private double lifetime = 2;
    private Particle[] particles;

    public Explosion(int x, int y, double lifetime) {
        this.xCord = x;
        this.yCord = y;
        this.lifetime = lifetime;
        
        Random rand = new Random();
        int count = rand.nextInt((PARTICLE_MAX-PARTICLE_MIN)+PARTICLE_MIN);
        this.particles = new Particle[count];

        for (int i = 0; i < count; i++) {
            this.particles[i] = new Particle(x, y, rand.nextDouble()*lifetime);
        }
    }

    @Override
    public void draw(XTankUI ui) {
        ui.drawExplosion(this);
    }

    @Override
    public void move() {}

    @Override
    public boolean intersects(int x, int y) {
        return false;
    }

    @Override
    public void update(double deltaTime) {
        if (lifetime > 0) {
            for (Particle p : particles) {
                if (p.getLifetime() > 0) {
                    p.update(deltaTime);
                }
            }
            lifetime -= deltaTime;
        }
    }

    public Particle[] getParticles() { return particles; }
}
