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
	private Tank tank;
	

	public Player(Socket socket, GameModel game, int playerId) {
		this.socket = socket;
		this.game = game;
		this.playerId = playerId;
		game.addTank(playerId);
	}

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
	
	public void update() {
		/*try {
			output.writeObject(game);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		output.println(game.lastCommand);
		output.flush();
	}

	private void processCommands() {
		System.out.println("going to process commands");
		while (input.hasNextLine()) {
			String command = input.nextLine();
			System.out.println("received command " + command);
			if(command.equals("move forward")) {
				game.moveTank(playerId);
			}
			game.lastCommand = command + ": player " + playerId;
			XTankServer.notifyPlayers();
		}
	}
}