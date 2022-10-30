
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class XTankUI
{
	// The location and direction of the "tank"
	private int x = 300;
	private int y = 500;
	private int directionX = 0;
	private int directionY = -10;

	private Canvas canvas;
	private Display display;
	
	private Client client;
	
	public XTankUI(Client c){
		client = c;
	}
	
	public void start(){
		display = new Display();
		Shell shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());

		canvas = new Canvas(shell, SWT.NO_BACKGROUND);

		canvas.addPaintListener(event -> {
			event.gc.fillRectangle(canvas.getBounds());
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
			event.gc.fillRectangle(x, y, 50, 100);
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			event.gc.fillOval(x, y+25, 50, 50);
			event.gc.setLineWidth(4);
			event.gc.drawLine(x+25, y+25, x+25, y-15);
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
					x += directionX;
					y += directionY;
					client.move();
					canvas.redraw();
				}
				else if(e.keyCode==SWT.ARROW_LEFT) {
					//TODO rotate left
				}
				else if(e.keyCode==SWT.ARROW_RIGHT) {
					//TODO rotate right
				}
				else if(e.keyCode==SWT.SPACE) {
					client.shoot();
				}
			}
			public void keyReleased(KeyEvent e) {}
		});

	
		shell.open();
		while (!shell.isDisposed()) 
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();		
	}
}

class UIRun implements Runnable{
	private XTankUI ui;
	
	public UIRun(XTankUI ui) {
		this.ui = ui;
	}

	@Override
	public void run() {
		ui.start();
	}
}


