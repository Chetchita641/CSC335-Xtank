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
            velocity -= FRICTION*deltaTime;
            velocity = Math.max(velocity, 0);
            
            xCord += Math.cos(radians)*velocity*deltaTime;
            yCord -= Math.sin(radians)*velocity*deltaTime;

            lifespan -= deltaTime;
        } 
    }

    public double getLifespan() { return lifespan; }
    public double getXCord() { return xCord; }
    public double getYCord() { return yCord; }
    public double getRadians() { return radians; }
}
