import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * A client for XTank
 */
public class Client {
    private Socket socket;
    //private ObjectInputStream in;
    private Scanner in;
    private PrintWriter out;
    private GameModel game;
    private XTankUI ui;

    /**
     * Creates a new Client for XTank
     * @param serverAddress the server to connect to
     * @param name name of the player on this Client
     */
    public Client(String serverAddress, String name) {
        try {
			socket = new Socket(serverAddress, 58901);
	        //in = new ObjectInputStream(socket.getInputStream());
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		} 
        out.println(name);
        out.flush();
        game = new GameModel();
    }
    
    /**
     * Sets the XTankUI to use for this Client
     * @param ui
     */
    public void setUI(XTankUI ui) {
    	this.ui = ui;
    }
    
    /**
     * Starts the game play for this Client
     * This reads in and processes responses from the server
     */
    public void play(){
    	
        try {
            //Object response = in.readObject();
            String response = in.nextLine();
            System.out.println(response);
            
            while(in.hasNext()) {
            	response = in.nextLine();
            	System.out.println(response);
            	if(response.startsWith("add tanks")) {
            		processAddTanks(response.substring(10));
            	}
            	else if(response.startsWith("move")) {
            		processMove(response.substring(13));
            	}
            	game.drawAll(ui);
            }
            /*
            while(in.available()>0) {
            	System.out.println(in.readObject());
            }
            */
        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }


	/**
	 * Sends command to server this Client's tank
	 */
	public void move() {
		out.println("move");
		out.flush();
	}
	
	/**
	 * Sends command to server to shoot from this Client's tank
	 */
	public void shoot() {
		out.println("shoot");
		out.flush();
	}
    
    /**
     * Process an "add tanks" command from the server
     * Will add Tanks to the Client's GameModel using the info specified
     * in tanksInfo
     * @param tanksInfo String that contains the IDs and coordinates of
     * 		the tanks to add
     */
	private void processAddTanks(String tanksInfo) {
		int i = 0;
		while(i<tanksInfo.length()) {
			if(tanksInfo.charAt(i)=='(') {
				i++;
			}
			else {
				int first = tanksInfo.indexOf(",", i);
				int second = tanksInfo.indexOf(",", first+1);
				int third = tanksInfo.indexOf(")", second);
				int playerId = Integer.parseInt(tanksInfo.substring(i, first));
				int xCord = Integer.parseInt(tanksInfo.substring(first+1, second));
				int yCord = Integer.parseInt(tanksInfo.substring(second+1, third));
				game.addTank(playerId, xCord, yCord);
				i = third+1;
			}
		}
	}
	
	/**
	 * Processes a move command. This will move the one of the tanks in the
	 * GameModel
	 * @param moveInfo information from the server on which tank to move
	 */
	private void processMove(String moveInfo) {
		int playerId = Integer.parseInt(moveInfo);
		game.moveTank(playerId);
	}
}
