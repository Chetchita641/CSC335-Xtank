import java.util.Scanner;

public class XTank {
	private static XTankUI ui;
	private static Client client;
	private static GameModel gameModel;
	
	public static void main(String [] args) {
		restart();
		
		/*String name = "bob";
		int type = 3;

    	Client client = null;
		GameModel gameModel = GameModel.getInstance();
		//gameModel.setObstacles("maze1.txt");
		try {
			client = new Client("127.0.0.1", name, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	XTankUI ui = new XTankUI();
    	ui.setClient(client);
    	client.setUI(ui);
    	ClientRun cr = new ClientRun(client);
    	UIRun ur = new UIRun(ui);
    	Thread clientThread = new Thread(cr);
    	Thread uiThread = new Thread(ur);
    	uiThread.start();
    	clientThread.start();
    	s.close();*/
	}
	
	public static void gameOver() {
		ui.gameOver();
	}
	
	public static void restart() {
		Scanner s = new Scanner(System.in);

		/*
    	System.out.println("Enter name:");
    	String name = s.nextLine();
		System.out.println("Choose your type of tank: (1) Light, (2) Medium, (3) Heavy: ");
		int type = s.nextInt();
		*/

		String name = "bob";
		int type = 3;

    	client = null;
		gameModel = GameModel.getInstance();
		gameModel.reset();
		//gameModel.setObstacles("maze1.txt");
		try {
			client = new Client("127.0.0.1", name, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	ui = new XTankUI();
    	ui.setClient(client);
    	client.setUI(ui);
    	ClientRun cr = new ClientRun(client);
    	UIRun ur = new UIRun(ui);
    	Thread clientThread = new Thread(cr);
    	Thread uiThread = new Thread(ur);
    	uiThread.start();
    	clientThread.start();
    	s.close();
	}
}

class ClientRun implements Runnable{
	private Client client;
	
	public ClientRun(Client c) {
		client = c;
	}

	@Override
	public void run() {
		try {
			client.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
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