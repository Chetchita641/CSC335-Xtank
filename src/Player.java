import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Player implements Runnable 
{
	Socket socket;
	Scanner input;
	//ObjectOutputStream output;
	PrintWriter output;
	String name;
	int playerId;
	GameModel game;

	public Player(Socket socket, GameModel game, int playerId) 
	{
		this.socket = socket;
		this.game = game;
		this.playerId = playerId;
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
			output.flush();
			processCommands();
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void update() {
		//output.writeObject(game);
		output.println(game.lastCommand);
		output.flush();
	}

	private void processCommands() {
		while (input.hasNextLine()) {
			String command = input.nextLine();
			game.lastCommand = command + ": player " + playerId;
			XTankServer.notifyPlayers();
		}
	}
}