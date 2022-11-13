/**
 * Author: Chris Macholtz
 * File name: GameModel.java
 * Course: CSC 335
 * Assignment: XTank A3
 * Purpose: Singleton instance of all assets in the game. 
 * Also handles reporting intersections between objects
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameModel implements Serializable {
	private static GameModel gameModelInstance;

	private String lastChange;
	private String statusMsg;

	private String rule;

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

	/**
	 * Singleton. Returns the one instance of GameModel 
	 * @return GameModel singleton
	 */
	public static GameModel getInstance() {
		if (gameModelInstance == null) {
			gameModelInstance = new GameModel();
		}
		return gameModelInstance;
	}
	
	/**
	 * Resets all relevant assets
	 */
	public void reset() {
		glyphs = new ArrayList<Glyph>();
		tanks = new ArrayList<Tank>();
		bullets = new ArrayList<Bullet>();
		lastTime = System.nanoTime();
		obstacles =  new ArrayList<Obstacle>();
		explosions = new ArrayList<Explosion>();
		currentPlayers = new ArrayList<Integer>();
	}

	/**
	 * Gets a tank from the array
	 * @param playerId - Player's ID
	 * @return Tank associated with player ID
	 */
	public Tank getTank(int playerId) {
		return tanks.get(playerId);
	}

	/**
	 * Gets the array of tanks
	 * @return array of tanks
	 */
	public List<Tank> getTanks() {
		return tanks;
	}
	
	/**
	 * Gets the array of bullets
	 * @return array of bullets
	 */
	public List<Bullet> getBullets(){
		return bullets;
	}
	
	/**
	 * Gets the array of obstacles
	 * @return array of obstacles
	 */
	public List<Obstacle> getObstacles(){
		return obstacles;
	}

	/**
	 * Gets the array of explosions
	 * @return array of explosions
	 */
	public List<Explosion> getExplosions() {
		return explosions;
	}
	
	/**
	 * Checks if any players are left on the field
	 * @return true if there are, false if not
	 */
	public boolean isGameOver() {
		return currentPlayers.size()==1&&tanks.size()>1;
	}

	/**
	 * Gets the rule for the game
	 * @return rule for the game
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * Sets the rule for the game
	 */
	public void setRule(String rule) {
		this.rule = rule;
		lastChange = "rule: " + rule;
	}

	/**
	 * Called every iteration of the Client's UI. Updates all glyphs for movement.
	 * Uses deltatime to normalize against the iteration rate
	 */
	public synchronized void updateState() {
		long currentTime = System.nanoTime();
		double deltaTime = (double) (currentTime-lastTime)/1000000000; // how much time has passed in seconds
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
	 * of Tanks. Used by Player during game start-up
	 * @param playerId 	- Id number of the player
	 * @param type	 	- Type of tank
	 * @param name		- Name of player controlling the tank
	 */
	public synchronized void addTank(int index, int type, String name) {
		int health = Tank.getHealthStat(type);
		this.addTank(index, type, 300, 500, 0, 0, health, name);
	}
	

	/**
	 * Creates a new Tank and adds it at the specified index and coordinates in 
	 * the list of Tanks
	 * @param playerId	- Id number of the player
	 * @param type		- Type of tank
	 * @param xCord		- x-coordinate
	 * @param yCord		- y-coordinate
	 * @param rads		- Radians, use for tank rotation
	 * @param velo		- Velocity of the tank
	 * @param health	- Health of the tank
	 * @param name		- Name of player controlling the tank
	 */
	public synchronized void addTank(int playerId, int type, double xCord, double yCord, 
			double rads, double velo, int health, String name) {
		if(!currentPlayers.contains(playerId)) {
			Tank t =  new Tank(playerId, type, xCord, yCord, rads, velo, health, name);
			tanks.add(playerId, t);
			glyphs.add(t);
			lastChange = "add tanks " + t.toString();
			currentPlayers.add(playerId);
			statusMsg = name + " has entered the battlefield!";
		}
	}
	 
	/**
	 * Moves the tank at the specified index forward one
	 * @param playerId	- Id number of the player 
	 */
	public synchronized void moveTank(int playerId) {
		tanks.get(playerId).move();
		lastChange = "move: player " + playerId;
	}

	/**
	 * Rotates the tank counter-clockwise
	 * @param playerId	- Id number of the player
	 */
	public synchronized void rotateLeft(int playerId) {
		tanks.get(playerId).rotateLeft();
		lastChange = "left: player " + playerId;
	}

	/**
	 * Rotates the tank clockwise
	 * @param playerId	- Id number of the player
	 */
	public synchronized void rotateRight(int playerId) {
		tanks.get(playerId).rotateRight();
		lastChange = "right: player " + playerId;
	}

	/**
	 * Pushes the tank backward
	 * @param playerId	- Id number of the player
	 */
	public synchronized void backward(int playerId) {
		tanks.get(playerId).backward();
		lastChange = "back: player " + playerId;
	}
	
	/**
	 * Causes the player to shoot
	 * @param playerId	- Id number of the player
	 */
	public synchronized void shoot(int playerId) {
		Bullet bullet = tanks.get(playerId).shoot();
		bullets.add(bullet);
		glyphs.add(bullet);
		lastChange = "shoot: player " + playerId;
	}

	/**
	 * Causes an explosion to spawn at a location
	 * @param x	- x-coordinate
	 * @param y	- y-coordinate
	 */
	public synchronized void explode(int x, int y) {
		Explosion explosion = new Explosion(x, y);
		explosions.add(explosion);
		glyphs.add(explosion);
		lastChange = "explode: " + explosion.toString();
	}
	
	/**
	 * Returns a String with information on all the Tanks in this GameModel. 
	 * Has the player ID, x coordinate and y coordinate for each Tank
	 * @return String description of all the tanks on the field
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
	 * @return Last change in the model
	 */
	public String getLastChange() {
		return lastChange;
	}

	/**
	 * Returns a status message for the UI to present
	 * @return Status message
	 */
	public String getStatusMessage() {
		return statusMsg;
	}

	/**
	 * Sets the status message for the UI
	 * @param msg	- Message to be set
	 */
	public void setStatusMessage(String msg) {
		this.statusMsg = msg;
	}

	/**
	 * Sets which tank is the client's
	 * @param playerId	- Id number for the player
	 */
	public void setAsClient(int playerId) {
		tanks.get(playerId).setClientTank();
	}
	
	/**
	 * Checks the coordinates of each of the bullets and compares them to each of the tanks.
	 * If an intersection detected, send info to tank to check damage
	 */
	public synchronized void checkTankBulletIntersection() {
		List<Bullet> toRemove = new ArrayList<Bullet>();
		for(Bullet bullet: bullets) {
			for(Tank tank: tanks) {
				if(tank.intersects(bullet.getxCord(), bullet.getyCord())) {
					tank.wasShot(bullet, rule);
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

	/**
	 * Checks if a given coordinate has an obstacle or not
	 * @param x	- x-coordinate
	 * @param y	- y-coordinate
	 * @return	- true if open, false if not
	 */
	public boolean isOpen(double x, double y) {
		for(Obstacle obstacle: obstacles) {
			if(obstacle.intersects(x, y))
				return false;
		}
		return true;
	}

	/**
	 * Takes bullet out of the array of bullets
	 * @param bullet	- Bullet to be removed
	 */
	public synchronized void destroyBullet(Bullet bullet) {
		bullets.remove(bullet);
	}
	
	/**
	 * Removes player from array of players
	 * @param playerId	- Id of player to be removed
	 */
	public synchronized void removePlayer(int playerId) {
		currentPlayers.remove((Integer)playerId);
		System.out.println("num current players: " + currentPlayers.size());
	}
	
	/**
	 * Reads a text file of obstacles and places them
	 * @param fileName	- Text file of coordinates for obstacles
	 */
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
	
	/**
	 * Places obstacles according to a given line from a text file
	 * @param line	- coordinates, width, and height of the obstacle
	 */
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
