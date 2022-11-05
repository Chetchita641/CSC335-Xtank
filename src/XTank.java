import java.util.Scanner;

public class XTank {
	public static void main(String [] args) {
		Scanner s = new Scanner(System.in);
    	System.out.println("Enter name:");
    	//String name = s.nextLine();
		String name = "Bob";
    	Client client = null;
		GameModel gameModel = GameModel.getInstance();
		try {
			client = new Client("127.0.0.1", name);
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

		gameModel.addClient(client);
    	
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