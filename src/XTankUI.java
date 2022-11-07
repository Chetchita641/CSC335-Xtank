
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XTankUI{
	
	private Canvas canvas;
	private Display display;
	private Shell shell;
	private Client client;
	private GC gc;
	private GameModel gameModel = GameModel.getInstance();
	
	public void setClient(Client c) {
		client = c;
	}
	
	public void start(){
		display = new Display();
		this.shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());

		canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
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
				else if(e.keyCode==SWT.BS) {
					gameModel.explode();
				}
			}
			public void keyReleased(KeyEvent e) {}
		});

		final int INTERVAL = 10;
		Runnable runnable = new Runnable() {
			public void run() {
				drawAndAnimate();
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
		//gameModel.updateState();
		canvas.redraw();
	}
	
	private void drawBullet(Bullet bullet) {
		double radians = bullet.getRadians();
		double x = bullet.getxCord();
		double y = bullet.getyCord();
		Display.getDefault().asyncExec(new Runnable() {
			 public void run() {
				int[] cords = calcTriangleCords(x , y, radians, 10, 20);
				gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
				gc.fillPolygon(cords);
			 }
			});
	}

	public void drawTank(Tank tank) {
		double radians = tank.getRadians();
		double x = tank.getXCord();
		double y = tank.getYCord();
		Display.getDefault().asyncExec(new Runnable() {
			 public void run() {
				int[] cords = calcTriangleCords(x, y, radians, 20, 40);
				gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				gc.fillPolygon(cords);
			  }
			});
	}
	
	private int[] calcTriangleCords(double x, double y,
			double radians, double width, double height){
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
}
