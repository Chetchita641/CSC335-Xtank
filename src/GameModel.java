import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GameModel implements Serializable {
	private static GameModel gameModelInstance;

	private String lastChange;
	private String statusMsg;

	private long lastTime;
	
	private List<Glyph> glyphs;
	private List<Tank> tanks;
	private List<Bullet> bullets;
	private List<Obstacle> obstacles;
	private List<Explosion> explosions;
	private List<Integer> currentPlayers;
	Thread thread;
	
	/**
	 * Creates a new GameModel
	 */
	private GameModel() {
		reset();
	}

	public static GameModel getInstance() {
		if (gameModelInstance == null) {
			gameModelInstance = new GameModel();
		}
		return gameModelInstance;
	}
	
	public void reset() {
		glyphs = new ArrayList<Glyph>();
		tanks = new ArrayList<Tank>();
		bullets = new ArrayList<Bullet>();
		lastTime = System.nanoTime();
		obstacles =  new ArrayList<Obstacle>();
		explosions = new ArrayList<Explosion>();
		currentPlayers = new ArrayList<Integer>();
	}

	public Tank getTank(int playerId) {
		return tanks.get(playerId);
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
	
	public boolean isGameOver() {
		return currentPlayers.size()==1&&tanks.size()>1;
	}

	public synchronized void updateState() {
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
	public synchronized void addTank(int index, int type, String name) {
		this.addTank(index, 300, 500, 0, 0, type, name);
	}
	
	public synchronized void addTank(int playerId, double xCord, double yCord, double rads, double velo, int type, String name) {
		if(!currentPlayers.contains(playerId)) {
			Tank t =  new Tank(playerId, type, xCord, yCord, rads, velo, name);
			tanks.add(playerId, t);
			glyphs.add(t);
			lastChange = "add tanks " + t.toString();
			currentPlayers.add(playerId);
			statusMsg = name + " has entered the battlefield!";
		}
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

	public synchronized void explode(int x, int y) {
		Explosion explosion = new Explosion(x, y);
		explosions.add(explosion);
		glyphs.add(explosion);
		lastChange = "explode: " + explosion.toString();
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

	public String getStatusMessage() {
		return statusMsg;
	}

	public void setStatusMessage(String msg) {
		this.statusMsg = msg;
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
					if (!tank.isActive()) {
						explode((int) tank.getXCord(), (int) tank.getYCord());
						statusMsg = tank.getName() + " has been destroyed";
					}
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
	
	public synchronized void removePlayer(int playerId) {
		currentPlayers.remove((Integer)playerId);
	}
	
	public void setObstacles(String fileName) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		    while (scanner.hasNextLine()) {
		        createObstacle(scanner.nextLine());
		    }
	}
	
	private void createObstacle(String line) {
		int[] values = new int[4];
	    Scanner rowScanner = new Scanner(line);
        rowScanner.useDelimiter(",");
        for(int i=0; i<4; i++) {
            values[i] = Integer.parseInt(rowScanner.next());
        }
		obstacles.add(new Obstacle(values[0],values[1],values[2],values[3]));
		rowScanner.close();
	}
}
