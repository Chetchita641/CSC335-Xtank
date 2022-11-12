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
    private Scanner in;
    private PrintWriter out;
    private GameModel game;
    private XTankUI ui;

	private int playerId;
	private String name;
    /**
     * Creates a new Client for XTank
     * @param serverAddress the server to connect to
     * @param name name of the player on this Client
     */
    public Client(String serverAddress, String name, int type) {
        try {
			socket = new Socket(serverAddress, 58901);
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		this.name = name;
        out.println(name);
		out.println(type);
        out.flush();
        game = GameModel.getInstance();
    }
    
    /**
     * Sets the XTankUI to use for this Client
     * @param ui
     */
    public void setUI(XTankUI ui) {
    	this.ui = ui;
    }

	public XTankUI getUI() {
		return ui;
	}
    
    /**
     * Starts the game play for this Client
     * This reads in and processes responses from the server
     */
    public void play(){
        try {
            String response;
            
            do {
            	response = in.nextLine();
            	System.out.println(response);
            	if(response.startsWith("add tanks")) {
            		processAddTanks(response.substring(10));
            	}
            	else if(response.startsWith("move")) {
            		processMove(response.substring(13));
            	}
				else if(response.startsWith("left")) {
					processLeft(response.substring(13));
				}
				else if(response.startsWith("right")) {
					processRight(response.substring(14));
				}
				else if(response.startsWith("back")) {
					processBack(response.substring(13));
				}
				else if(response.startsWith("shoot")) {
					processShoot(response.substring(14));
				}
				else if(response.startsWith("your id")) {
					processID(response.substring(9));
				}
				else if(response.startsWith("set maze")) {
					processMaze(response.substring(9));
				}
				else if(response.startsWith("game over")) {
					XTank.gameOver();
				}
				else if(response.startsWith("rule")) {
					processRule(response.substring(6));
				}
            } while (in.hasNext());
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

	public void rotateLeft() {
		out.println("left");
		out.flush();
	}

	public void rotateRight() {
		out.println("right");
		out.flush();
	}

	public void backward() {
		out.println("back");
		out.flush();
	}

	/**
	 * Sends command to server to shoot from this Client's tank
	 */
	public void shoot() {
		out.println("shoot");
		out.flush();
	}

	public int getPlayerId() {
		return playerId;
	}

	public String getName() {
		return name;
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
				int third = tanksInfo.indexOf(",", second+1);
				int fourth = tanksInfo.indexOf(",", third+1);
				int fifth = tanksInfo.indexOf(",", fourth+1);
				int sixth = tanksInfo.indexOf(",", fifth+1);
				int seventh = tanksInfo.indexOf(",", sixth+1);
				int eighth = tanksInfo.indexOf(")", seventh+1);
				int playerId = Integer.parseInt(tanksInfo.substring(i, first));
				int type = Integer.parseInt(tanksInfo.substring(first+1, second));
				double xCord = Double.parseDouble(tanksInfo.substring(second+1, third));
				double yCord = Double.parseDouble(tanksInfo.substring(third+1, fourth));
				double rads = Double.parseDouble(tanksInfo.substring(fourth+1, fifth));
				double velo = Double.parseDouble(tanksInfo.substring(fifth+1, sixth));
				int health = Integer.parseInt(tanksInfo.substring(sixth+1, seventh));
				String tankName = tanksInfo.substring(seventh+1, eighth);

				game.addTank(playerId, type, xCord, yCord, rads, velo, health, tankName);
				i = eighth+1;
			}
		}
	}
	
	/**
	 * Processes a move command. This will move the one of the tanks in the
	 * GameModel
	 * @param moveInfo information from the server on which tank to move
	 */
	private void processMove(String moveInfo) {
		playerId = Integer.parseInt(moveInfo);
		game.moveTank(playerId);
	}

	private void processLeft(String leftInfo) {
		playerId = Integer.parseInt(leftInfo);
		game.rotateLeft(playerId);
	}
	
	private void processRight(String rightInfo) {
		playerId = Integer.parseInt(rightInfo);
		game.rotateRight(playerId);
	}

	private void processBack(String backInfo) {
		playerId = Integer.parseInt(backInfo);
		game.backward(playerId);
	}
	
	private void processShoot(String shootInfo) {
		playerId = Integer.parseInt(shootInfo);
		game.shoot(playerId);
	}
	
	private void processID(String idInfo) {
		playerId = Integer.parseInt(idInfo);
		game.setAsClient(playerId);
	}
	
	private void processMaze(String mazeInfo) {
		game.setObstacles(mazeInfo);
	}

	private void processRule(String rule) {
		game.setRule(rule);
	}
}
