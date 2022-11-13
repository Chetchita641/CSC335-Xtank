/**
 * Author: Chris Macholtz
 * File name: XTankUI.java
 * Course: CSC 335
 * Assignment: XTank A3
 * Purpose: UI for XTank, which belongs to the Client. Handles key listeners, status updates, and draws 
 * all assets on the user's window. Regularly interacts and pushes forward the GameModel on a set interval.
 */
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class XTankUI{
	
	private Canvas canvas;
	private Display display;
	private Shell shell;
	private Client client;
	private GC gc;
	private GameModel gameModel = GameModel.getInstance();
	private int xLimit = 1500;
	private int yLimit = 900;
	private boolean gameOverFlag;
	
	public void setClient(Client c) {
		client = c;
	}
	
	public void start(){
		display = new Display();
		this.shell = new Shell(display);
		shell.setSize(xLimit, yLimit+100);
		shell.setText("xtank");

		RowLayout layout = new RowLayout();
		layout.marginLeft = 0;
		shell.setLayout(layout);

		// Game area
		canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(new RowData(xLimit, yLimit));
		canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		gc = new GC(canvas);

		// Key listeners
		gameOverFlag = false;
		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(e.keyCode==SWT.ARROW_UP) {
					client.move();
				}
				else if(e.keyCode==SWT.ARROW_LEFT) {
					client.rotateLeft();
				}
				else if(e.keyCode==SWT.ARROW_RIGHT) {
					client.rotateRight();
				}
				else if(e.keyCode==SWT.ARROW_DOWN) {
					client.backward();
				}
				else if(e.keyCode==SWT.SPACE) {
					client.shoot();
				}
				else if(e.character=='s' && gameOverFlag) {
					XTank.restart();
					display.dispose();
				}
				else if(e.character=='s' && gameOverFlag) {
					XTank.restart();
					display.dispose();
				}
			}
			public void keyReleased(KeyEvent e) {}
		});

		// Status bar
		Group statusBarGroup = new Group(shell, SWT.NONE);
		statusBarGroup.setLayoutData(new RowData(xLimit, 50));
		
		Label statusMessage = new Label(statusBarGroup, SWT.NONE);
		Font font = new Font(display, new FontData("Arial", 16, SWT.BOLD));
		statusMessage.setFont(font);
		statusMessage.setText("Welcome");
		statusMessage.setLocation(10, 10);
		statusMessage.pack();

		// Refresh interval
		final int INTERVAL = 10;
		Runnable runnable = new Runnable() {
			public void run() {
				if(gameOverFlag) {
					gameOverDisplay();
				}
				else {
					drawAndAnimate();
				}
				statusMessage.setText(getStatusMessage());
				statusMessage.pack();
				display.timerExec(INTERVAL, this);
			}
		};
		display.timerExec(INTERVAL, runnable);

		shell.open();
		while (!shell.isDisposed()) {
			try {
			if (!display.readAndDispatch())
				display.sleep();
			}
			catch (Exception e) {}
		} 

		display.dispose();		
	}

	/**
	 * Gets the current display object
	 * @return display object
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * Displays "Game Over" when only one player is left in a cool, retro way
	 */
	public void gameOverDisplay() {
		gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		gc.drawText("GAME OVER", 300, 600);
		gc.drawText("Press S to restart", 350, 650);
	}
	
	/**
	 * Flips the gameOverFlag. Used by GameModel
	 */
	public void gameOver() {
		gameOverFlag = true;
	}
	
	/**
	 * Method used for updating the game's assets
	 */
	public void drawAndAnimate() {
		for(Glyph glyph: gameModel.getGlyphs()) {
			glyph.draw(this);
		}
		gameModel.updateState();
		canvas.redraw();
	}
	
	/**
	 * Draws a bullet on the player's screen. Bullet is drawn in the same shape as the shooter's tank
	 * @param bullet	- Bullet object
	 */
	public void drawBullet(Bullet bullet) {
		double radians = bullet.getRadians();
		double x = bullet.getXCord();
		double y = bullet.getYCord();
		int type = bullet.getType();
		Display.getDefault().asyncExec(new Runnable() {
			 public void run() {
				int[] cords = calcTankCords(x , y, radians, 10, 20, type);
				gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
				gc.fillPolygon(cords);
			 }
			});
	}

	/**
	 * Draws a tank on the player's window. Shape is determined by the tank type.
	 * @param tank	- Tank object
	 */
	public void drawTank(Tank tank) {
		double radians = tank.getRadians();
		double x = tank.getXCord();
		double y = tank.getYCord();
		int width = tank.getWidth();
		int height = tank.getHeight();
		int type = tank.getType();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(tank.isActive()) {
					Color tankColor;
					if(tank.isClientTank()) {
						tankColor = display.getSystemColor(SWT.COLOR_GREEN);
					}
					else {
						tankColor = display.getSystemColor(SWT.COLOR_WHITE);
					}
					int[] cords = calcTankCords(x, y, radians, width, height, type);
					gc.setBackground(tankColor);
					gc.fillPolygon(cords);
					gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
					gc.setForeground(tankColor);
					gc.setFont(new Font(display, new FontData( "Arial", 10, SWT.NONE)));
					gc.drawText(tank.getID(), (int)x, (int)y-40);
				}
			  }
			});
	}

	/**
	 * Calculates and returns coordinates for the Tank polygon, according to the tank's type. 
	 * @param x			- x-coordinate
	 * @param y			- y-coordinate
	 * @param radians	- Radians used for rotation
	 * @param width		- Width of the tank
	 * @param height	- Height of the tank
	 * @param type		- Type of tank
	 * @return Coordinates used for drawing the polygon
	 */
	private int[] calcTankCords(double x, double y,
			double radians, double width, double height, int type) {

		switch (type) {
			case 1:
				return lightTankCords(x, y, radians, width, height);
			case 2:
				return mediumTankCords(x, y, radians, width, height);
			case 3:
				return heavyTankCords(x, y, radians, width, height);
			default:
				System.out.println("Unknown tank type. Exiting");
				System.exit(404);
		}
		return null;
	}

	/**
	 * Calculates the coordinates used to draw a Light tank
	 * @param x			- x-coordinates
	 * @param y			- y-coordinates
	 * @param radians	- Radians used for rotation
	 * @param width		- Width of tank
	 * @param height	- Height of tank
	 * @return Coordinate used for drawing the polygon
	 */
	private int[] lightTankCords(double x, double y,
			double radians, double width, double height) {
		
		// Convert tank's dimensions into polar coordinates to handle rotation
		double ra, rb, rc, rd;
		ra = height/2;
		rb = Math.sqrt(Math.pow(width/2,2)+Math.pow(height/2, 2));
		rc = height/4;
		rd = rb;

		int ax, ay, bx, by, cx, cy, dx, dy;

		double aRad = Math.atan2(0, height/2)-radians;
		double bRad = Math.atan2(width/2, -height/2)-radians;
		double cRad = Math.atan2(0, -height/4)-radians;
		double dRad = Math.atan2(-width/2, -height/2)-radians;

		ax = (int) (ra*Math.cos(aRad)+x);
		ay = (int) (ra*Math.sin(aRad)+y);
		bx = (int) (rb*Math.cos(bRad)+x);
		by = (int) (rb*Math.sin(bRad)+y);
		cx = (int) (rc*Math.cos(cRad)+x);
		cy = (int) (rc*Math.sin(cRad)+y);
		dx = (int) (rd*Math.cos(dRad)+x);
		dy = (int) (rd*Math.sin(dRad)+y);

		int[] cords = {ax, ay, bx, by, cx, cy, dx, dy};

		return cords;
	}

	/**
	 * Calculates the coordinates used to draw a Medium tank
	 * @param x			- x-coordinates
	 * @param y			- y-coordinates
	 * @param radians	- Radians used for rotation
	 * @param width		- Width of tank
	 * @param height	- Height of tank
	 * @return Coordinate used for drawing the polygon
	 */
	private int[] mediumTankCords(double x, double y,
			double radians, double width, double height) {
		
		// Convert tank's dimensions into polar coordinates to handle rotation
		double ra, rb, rc;
		ra = height/2;
		rb = Math.sqrt(Math.pow(width/2,2)+Math.pow(height/2, 2));
		rc = rb;

		int ax, ay, bx, by, cx, cy;

		double aRad = Math.atan2(0, height/2)-radians;
		double bRad = Math.atan2(width/2, -height/2)-radians;
		double cRad = Math.atan2(-width/2, -height/2)-radians;

		ax = (int) (ra*Math.cos(aRad)+x);
		ay = (int) (ra*Math.sin(aRad)+y);
		bx = (int) (rb*Math.cos(bRad)+x);
		by = (int) (rb*Math.sin(bRad)+y);
		cx = (int) (rc*Math.cos(cRad)+x);
		cy = (int) (rc*Math.sin(cRad)+y); 
		
		int[] cords = {ax, ay, bx, by, cx, cy};
	
		return cords;

	}

	/**
	 * Calculates the coordinates used to draw a Heavy tank
	 * @param x			- x-coordinates
	 * @param y			- y-coordinates
	 * @param radians	- Radians used for rotation
	 * @param width		- Width of tank
	 * @param height	- Height of tank
	 * @return Coordinate used for drawing the polygon
	 */
	private int[] heavyTankCords(double x, double y, 
			double radians, double width, double height) {

		// Convert tank's dimensions into polar coordinates to handle rotation
		double ra, rb, rc, rd, re, rf;
		ra = height/2;
		rb = Math.sqrt(Math.pow(width/2, 2)+Math.pow(height/4, 2));
		rc = Math.sqrt(Math.pow(width/2, 2)+Math.pow(height/2, 2));
		rd = height/4;
		re = rc;
		rf = rb;

		int ax, ay, bx, by, cx, cy, dx, dy, ex, ey, fx, fy;

		double aRad = Math.atan2(0, height/2)-radians;
		double bRad = Math.atan2(width/2, height/4)-radians;
		double cRad = Math.atan2(width/2, -height/2)-radians;
		double dRad = Math.atan2(0, -height/4)-radians;
		double eRad = Math.atan2(-width/2, -height/2)-radians;
		double fRad = Math.atan2(-width/2, height/4)-radians;

		ax = (int) (ra*Math.cos(aRad)+x);
		ay = (int) (ra*Math.sin(aRad)+y);
		bx = (int) (rb*Math.cos(bRad)+x);
		by = (int) (rb*Math.sin(bRad)+y);
		cx = (int) (rc*Math.cos(cRad)+x);
		cy = (int) (rc*Math.sin(cRad)+y);
		dx = (int) (rd*Math.cos(dRad)+x);
		dy = (int) (rd*Math.sin(dRad)+y);
		ex = (int) (re*Math.cos(eRad)+x);
		ey = (int) (re*Math.sin(eRad)+y);
		fx = (int) (rf*Math.cos(fRad)+x);
		fy = (int) (rf*Math.sin(fRad)+y);

		int[] cords = {ax, ay, bx, by, cx, cy, dx, dy, ex, ey, fx, fy};

		return cords;
	}

	/**
	 * Draws an obstacle on the player's window
	 * @param obstacle	- Obstacle object
	 */
	public void drawObstacle(Obstacle obstacle) {
		int x = (int) obstacle.getXCord();
		int y = (int) obstacle.getYCord();
		int width = obstacle.getWidth();
		int height = obstacle.getHeight();
		Display.getDefault().asyncExec(new Runnable() {
			 public void run() {
				gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
				gc.fillRectangle(x, y, width, height);
			 }
		});
	}

	/**
	 * Draws an explosion on the player's window
	 * @param explosion	- Explosion object
	 */
	public void drawExplosion(Explosion explosion) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
				for (Particle p : explosion.getParticles()) {
					int x = (int) p.getXCord();
					int y = (int) p.getYCord();

					gc.drawRectangle(x, y, 2, 2);
				}

			}
		});

	}

	/**
	 * Gets the status message from the GameModel to show to the player. Also includes their name, tank type, health, and rule of the game
	 * @return A status message
	 */
	private String getStatusMessage() {
		Tank tank = gameModel.getTank(client.getPlayerId());
		String nameMsg = String.format("Name: %s    ", client.getName());
		String typeMsg = String.format("Type: %s    ", tank.getTypeString());
		String healthMsg = String.format("Health: %d      ", tank.getHealth());
		String ruleMsg = String.format("Rule: %s       ", gameModel.getRule());
		String statusMsg = gameModel.getStatusMessage();
		
		return nameMsg + typeMsg + healthMsg + ruleMsg + statusMsg;
	}
}
