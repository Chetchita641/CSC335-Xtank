import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Player implements Runnable {
	private Socket socket;
	private Scanner input;
	//private ObjectOutputStream output;
	private PrintWriter output;
	private String name;
	private int playerId;
	private GameModel game;
	private XTankServer server;
	
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
		game.addTank(playerId);
		server.notifyPlayers();
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
			//output = new ObjectOutputStream(socket.getOutputStream());
			output = new PrintWriter(socket.getOutputStream());
			this.name = input.nextLine(); 
			//output.writeObject("hi " + name);
			output.println("hi " + name);
			output.println("add tanks " + game.listTanks());
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
	 * Runs a loop to continuously recieve and process commands from the client
	 */
	private void processCommands() {
		while (input.hasNextLine()) {
			String command = input.nextLine();
			System.out.println("received command " + command);
			if(command.equals("move")) {
				game.moveTank(playerId);
			}
			else if (command.equals("left")) {
				game.rotateLeft(playerId);
			}
			else if (command.equals("right")) {
				game.rotateRight(playerId);
			}
			else if(command.equals("shoot")) {
				game.shoot(playerId);
			}
			else if(command.equals("back")) {
				game.backward(playerId);
			}
			server.notifyPlayers();
		}
	}
}