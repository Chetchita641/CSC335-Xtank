/**
 * Author: Grace Driskill
 * File name: Obstacle.java
 * Course: CSC 335
 * Assignment: XTank A3
 * Purpose: Starts the client side of XTank. Prompts the user to 
 * 	enter their name and select a type of tank that vary in power,
 * 	speed and health. Finally, creates and starts all the needed
 * 	components for XTank
 */
import java.util.Scanner;

public class XTank {
	private static XTankUI ui;
	private static Client client;
	private static GameModel gameModel;
	
	private static String name;
	private static int type;
	
	public static void main(String [] args) {
		Scanner s = new Scanner(System.in);
		System.out.println("Enter name:");
    	name = s.nextLine();
		System.out.println("Choose your type of tank: (1) Light, (2) Medium, (3) Heavy: ");
		type = s.nextInt();
		s.close();
		
		restart();
	}
	
	/**
	 * Function to end the XTank game
	 */
	public static void gameOver() {
		ui.gameOver();
	}
	
	/**
	 * Resets the XTank game. Creates new, empty GameModel, UI and 
	 * Client to use, and creates a new connection to the server.
	 */
	public static void restart() {
    	client = null;
		gameModel = GameModel.getInstance();
		gameModel.reset();
		try {
			client = new Client("127.0.0.1", name, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	ui = new XTankUI();
    	ui.setClient(client);
    	ClientRun cr = new ClientRun(client);
    	UIRun ur = new UIRun(ui);
    	Thread clientThread = new Thread(cr);
    	Thread uiThread = new Thread(ur);
    	uiThread.start();
    	clientThread.start();
	}
}

class ClientRun implements Runnable{
	/**
	 * Runnable object for the Client's thread
	 */
	
	private Client client;
	
	public ClientRun(Client c) {
		client = c;
	}

	/**
	 * Calls play for the client
	 */
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
	/**
	 * Runnable object for the UI's thread
	 */
	
	private XTankUI ui;
	
	public UIRun(XTankUI ui) {
		this.ui = ui;
	}

	/**
	 * Calls the UI to start
	 */
	@Override
	public void run() {
		ui.start();
	}
}