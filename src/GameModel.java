import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameModel implements Serializable {
	private static GameModel gameModelInstance;

	public String lastChange;

	private long lastTime;
	
	private List<Glyph> glyphs;
	private List<Tank> tanks;
	private List<Bullet> bullets;
	private List<Obstacle> obstacles;
	private List<Explosion> explosions;

	Thread thread;
	
	/**
	 * Creates a new GameModel
	 */
	private GameModel() {
		glyphs = new ArrayList<Glyph>();
		tanks = new ArrayList<Tank>();
		bullets = new ArrayList<Bullet>();
		lastTime = System.nanoTime();
		obstacles =  new ArrayList<Obstacle>();
		explosions = new ArrayList<Explosion>();
		
		// test obstacles
		obstacles.add(new Obstacle(5,5,25,25));
		obstacles.add(new Obstacle(150,200,25,25));
		obstacles.add(new Obstacle(25,50,25,25));
	}

	public static GameModel getInstance() {
		if (gameModelInstance == null) {
			gameModelInstance = new GameModel();
		}
		return gameModelInstance;
	}

	public List<Tank> getTanks() {
		return tanks;
	}
	
	public List<Bullet> getBullets(){
		return bullets;
	}
	
	public List<Obstacle> getObstacles(){
		return obstacles;
	}

	public List<Explosion> getExplosions() {
		return explosions;
	}

	public synchronized void updateState() {
		//System.out.println("---UPDATE---");
		long currentTime = System.nanoTime();
		double deltaTime = (double) (currentTime-lastTime)/1000000000;
		for (Glyph g : glyphs) {
			g.update(deltaTime);
		}
		checkTankBulletIntersection();		
		lastTime = currentTime;
	}

	/**
	 * Draws every Gylph in the specified XTankUI
	 * @param ui
	 */
	public void drawAll(XTankUI ui) {
		for(Glyph g: glyphs) {
			g.draw(ui);
		}
	}
	
	/**
	 * Creates a new Tank and adds it at the specified index in the list
	 * of Tanks
	 * @param index
	 */
	public synchronized void addTank(int index) {
		this.addTank(index, 300, 500, 1, 0, 100);

	}
	
	/**
	 * Creates a new Tank and adds it at the specified index and coordinates in 
	 * the list of Tanks
	 * @param index
	 * @param xCord
	 * @param yCord
	 */
	public synchronized void addTank(int index, int xCord, int yCord) {
		Tank t =  new Tank(index, xCord, yCord);
		tanks.add(index, t);
		glyphs.add(t);
		lastChange = "add tanks " + t.toString();
	}
	
	public synchronized void addTank(int playerId, double xCord, double yCord, double rads, double velo, int health) {
		Tank t =  new Tank(playerId, xCord, yCord, rads, velo, health);
		tanks.add(playerId, t);
		glyphs.add(t);
		lastChange = "add tanks " + t.toString();
	}
	 
	/**
	 * Moves the tank at the specified index forward one
	 * @param index 
	 */
	public synchronized void moveTank(int playerId) {
		tanks.get(playerId).move();
		lastChange = "move: player " + playerId;
	}

	public synchronized void rotateLeft(int playerId) {
		tanks.get(playerId).rotateLeft();
		lastChange = "left: player " + playerId;
	}

	public synchronized void rotateRight(int playerId) {
		tanks.get(playerId).rotateRight();
		lastChange = "right: player " + playerId;
	}

	public synchronized void backward(int playerId) {
		tanks.get(playerId).backward();
		lastChange = "back: player " + playerId;
	}
	
	public synchronized void shoot(int playerId) {
		Bullet bullet = tanks.get(playerId).shoot();
		bullets.add(bullet);
		glyphs.add(bullet);
		lastChange = "shoot: player " + playerId;
	}

	public synchronized void explode() {
		Explosion explosion = new Explosion(100, 100, 10);
		explosions.add(explosion);
		glyphs.add(explosion);
		lastChange = "explode: (100, 100)";
	}
	
	/**
	 * Returns a String with information on all the Tanks in this GameModel. 
	 * Has the player ID, x coordinate and y coordinate for each Tank
	 * @return 
	 */
	public String listTanks() {
		String retVal = "";
		for(Tank t: tanks) {
			retVal += t.toString();
		}
		return retVal;
	}
	
	/**
	 * Returns the last change that's happened in this GameModel
	 */
	public String getLastChange() {
		return lastChange;
	}

	/**
	 * Sets which tank is the client's
	 * @param playerId
	 */
	public void setAsClient(int playerId) {
		tanks.get(playerId).setClientTank();
	}
	
	public synchronized void checkTankBulletIntersection() {
		List<Bullet> toRemove = new ArrayList<Bullet>();
		for(Bullet bullet: bullets) {
			for(Tank tank: tanks) {
				if(tank.intersects(bullet.getxCord(), bullet.getyCord())) {
					tank.wasShot(bullet);
					toRemove.add(bullet);
				}
			}
		}
		bullets.removeAll(toRemove);
	}

	public boolean isOpen(double x, double y) {
		for(Obstacle obstacle: obstacles) {
			if(obstacle.intersects(x, y))
				return false;
		}
		return true;
	}

	public synchronized void destroyBullet(Bullet bullet) {
		bullets.remove(bullet);
	}
}
