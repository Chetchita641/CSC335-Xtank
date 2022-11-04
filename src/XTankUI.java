
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

		canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		gc = new GC(canvas);

		canvas.addPaintListener(event -> {
			event.gc.fillRectangle(canvas.getBounds());
			/*event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
			event.gc.fillRectangle(x, y, 50, 100);
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			event.gc.fillOval(x, y+25, 50, 50);
			event.gc.setLineWidth(4);
			event.gc.drawLine(x+25, y+25, x+25, y-15);*/
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
					/*x += directionX;
					y += directionY;*/
					client.move();
					canvas.redraw();
				}
				else if(e.keyCode==SWT.ARROW_LEFT) {
					client.rotateLeft();
					canvas.redraw();
				}
				else if(e.keyCode==SWT.ARROW_RIGHT) {
					client.rotateRight();
					canvas.redraw();
				}
				else if(e.keyCode==SWT.SPACE) {
					client.shoot();
					canvas.redraw();
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
			drawTank(tank.getXCord(), tank.getYCord(), tank.getRadians());
		}
		gameModel.updateState();
		canvas.redraw();
	}
	
	public void drawTank(double x, double y, double radians) {
		Display.getDefault().asyncExec(new Runnable() {
			 public void run() {
				final double WIDTH = 20;
				final double HEIGHT = 40;

				gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

				double ra, rb, rc;
				ra = HEIGHT/2;
				rb = Math.sqrt(Math.pow(WIDTH/2,2)+Math.pow(HEIGHT/2, 2));
				rc = rb;

				//System.out.println("DEBUG: ra: " + ra);
				//System.out.println("DEBUG: rb: " + rb);
				//System.out.println("DEBUG: rc: " + rc);

				int ax, ay, bx, by, cx, cy;

				double aRad = Math.atan2(0, HEIGHT/2)-radians;
				double bRad = Math.atan2(WIDTH/2, -HEIGHT/2)-radians;
				double cRad = Math.atan2(-WIDTH/2, -HEIGHT/2)-radians;

				//System.out.println("DEBUG: aRad: " + aRad);
				//System.out.println("DEBUG: bRad: " + bRad);
				//System.out.println("DEBUG: cRad: " + cRad);

				ax = (int) (ra*Math.cos(aRad)+x);
				ay = (int) (ra*Math.sin(aRad)+y);
				bx = (int) (rb*Math.cos(bRad)+x);
				by = (int) (rb*Math.sin(bRad)+y);
				cx = (int) (rc*Math.cos(cRad)+x);
				cy = (int) (rc*Math.sin(cRad)+y); 
				
				
				//System.out.println("DEBUG: ax: " + ax + ", ay: " + ay);
				//System.out.println("DEBUG: bx: " + bx + ", by: " + by);
				//System.out.println("DEBUG: cx: " + cx + ", cy: " + cy);
				
				gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				gc.fillPolygon(new int[] {ax, ay, bx, by, cx, cy});
		
				/*
				gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
				gc.fillRectangle(x, y, 50, 100);
				gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
				gc.fillOval(x, y+25, 50, 50);
				gc.setLineWidth(4);
				gc.drawLine(x+25, y+25, x+25, y-15);
				*/
			 }
			});
	}
}
