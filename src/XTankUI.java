import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

		canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(new RowData(xLimit, yLimit));
		canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		gc = new GC(canvas);

		canvas.addPaintListener(event -> {
		});	

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
			} 
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				// update tank location
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
			}
			public void keyReleased(KeyEvent e) {}
		});

		Group statusBarGroup = new Group(shell, SWT.NONE);
		statusBarGroup.setLayoutData(new RowData(xLimit, 50));
		
		Label statusMessage = new Label(statusBarGroup, SWT.NONE);
		Font font = new Font(display, new FontData("Arial", 16, SWT.BOLD));
		statusMessage.setFont(font);
		statusMessage.setText("Welcome");
		statusMessage.setLocation(10, 10);
		statusMessage.pack();

		final int INTERVAL = 10;
		Runnable runnable = new Runnable() {
			public void run() {
				drawAndAnimate();
				statusMessage.setText(getStatusMessage());
				statusMessage.pack();
				display.timerExec(INTERVAL, this);
			}
		};
		display.timerExec(INTERVAL, runnable);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		} 

		display.dispose();		
	}

	public Display getDisplay() {
		return display;
	}

	public void drawAndAnimate() {
		List<Tank> tanks = gameModel.getTanks();
		for (Tank tank : tanks) {
				drawTank(tank);
		}
		List<Bullet> bullets = gameModel.getBullets();
		for (Bullet bullet: bullets) {
			drawBullet(bullet);
		}
		
		for(Obstacle obstacle : gameModel.getObstacles()) {
			obstacle.draw(this);
		}

		for (Explosion explosion : gameModel.getExplosions()) {
			explosion.draw(this);
		}
		gameModel.updateState();
		canvas.redraw();
	}
	
	private void drawBullet(Bullet bullet) {
		double radians = bullet.getRadians();
		double x = bullet.getxCord();
		double y = bullet.getyCord();
		int type = bullet.getType();
		Display.getDefault().asyncExec(new Runnable() {
			 public void run() {
				//gameModel.intersectsTank(bullet);
				int[] cords = calcTriangleCords(x , y, radians, 10, 20, type);
				gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
				gc.fillPolygon(cords);
			 }
			});
	}

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
					int[] cords = calcTriangleCords(x, y, radians, width, height, type);
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

	private int[] calcTriangleCords(double x, double y,
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

	private int[] lightTankCords(double x, double y,
			double radians, double width, double height) {
		
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

	private int[] mediumTankCords(double x, double y,
			double radians, double width, double height) {
		
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

	private int[] heavyTankCords(double x, double y, 
			double radians, double width, double height) {

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
	
	public void drawObstacle(Obstacle obstacle) {
		int x = obstacle.getXCord();
		int y = obstacle.getYCord();
		int width = obstacle.getWidth();
		int height = obstacle.getHeight();
		Display.getDefault().asyncExec(new Runnable() {
			 public void run() {
				gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
				gc.fillRectangle(x, y, width, height);
			 }
		});
	}

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
