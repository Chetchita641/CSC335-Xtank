/**
 * Author: Grace Driskill
 * File name: Player.java
 * Course: CSC 335
 * Assignment: XTank A3
 * Purpose: Represents a player in XTank on the server side of the game.
 * 	Contains the input and output stream for a single socket, and is 
 * 	responsible for the communication with that client.
 */
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable {
	private Socket socket;
	private Scanner input;
	private PrintWriter output;
	private String name;
	private int type;
	private int playerId;
	private GameModel game;
	private XTankServer server;
	private boolean exit;
	
	/**
	 * Creates a new Player
	 * @param socket the Socket to communicate with the Player's client
	 * @param game GameModel for the game this Player will be in
	 * @param playerId ID number that corresponds to the index the Player
	 * 		is at (first player to join is 0, second to join is 1...)
	 */
	public Player(Socket socket, GameModel game, int playerId) {
		this.socket = socket;
		this.game = game;
		this.playerId = playerId;
		this.server = XTankServer.getInstance();
		exit = false;
	}

	/**
	 * The code to execute when the making this Player's thread
	 * Creates input and output for this client's socket and processes commands
	 * coming from the client
	 */
	@Override
	public void run() {
		try {
			input = new Scanner(socket.getInputStream());
			output = new PrintWriter(socket.getOutputStream());
			output.println("rule: " + server.getRule());

			this.name = input.nextLine();
			this.type = input.nextInt();
			game.addTank(playerId, type, name);

			output.println("hi " + name);
			output.println("set maze " + server.getMazeFile());
			output.println("add tanks " + game.listTanks());
			server.notifyPlayers();
			output.println("your id: " + playerId);
			output.flush();
			processCommands();
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Updates this Player's client with changes in the GameModel
	 */
	public void update() {
		output.println(game.getLastChange());
		output.flush();
	}
	
	/**
	 * Runs a loop to continuously receive and process commands from the client
	 */
	private void processCommands() {
		while(!exit) {
			while (input.hasNextLine()) {
				String command = input.nextLine();
				if(command.equals("move")) {
					game.moveTank(playerId);
					server.notifyPlayers();
				}
				else if (command.equals("left")) {
					game.rotateLeft(playerId);
					server.notifyPlayers();
				}
				else if (command.equals("right")) {
					game.rotateRight(playerId);
					server.notifyPlayers();
				}
				else if(command.equals("shoot")) {
					game.shoot(playerId);
					server.notifyPlayers();
				}
				else if(command.equals("back")) {
					game.backward(playerId);
					server.notifyPlayers();
				}
			}
		}
	}

	/**
	 * Alerts the client that the game is over and ends this Player's 
	 * process commands loop.
	 */
	public void end() {
		output.println("game over");
		output.flush();
		exit = true;
	}
}