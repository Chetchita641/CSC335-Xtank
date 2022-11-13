/**
 * Author: Grace Driskill
 * File name: XTankServer.java
 * Course: CSC 335
 * Assignment: XTank A3
 * Purpose: Server for the XTank game. The server is responsible for 
 * 	keeping track of Players, which are each one client of the server.
 * 	The server sends updates to each client about which rules to use, 
 * 	new tanks, ect. The server receives from the clients the commands
 * 	being execute such as moving forward, shooting, ect. 
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XTankServer {
	private static final String[] mazeFiles = {"arena.txt", "forest.txt", "x.txt"};
	private static final String[] rules = {"classic", "oneshot"};

	private List<Player> players;
	private GameModel game;
	private int mazeType;
	private int ruleType;
	private static XTankServer instance;
	
	/**
	 * Creates a new XTankSever, with no players and empty GameModel
	 */
	private XTankServer() {
		players = new ArrayList<Player>();
    	game = GameModel.getInstance();		
	}
	
	/**
	 * Returns the single instance of XTankSever. Creates a new instance if
	 * one doesn't already exist.
	 * @return XTankServer
	 */
	public static XTankServer getInstance() {
		if(instance==null) {
			instance = new XTankServer();
		}
		return instance;
	}

	
	/**
	 * Starts the server. Begins to accept new clients and add them to the game
	 */
    public void start() {
    	GameModelRun gmrun = new GameModelRun(game);
    	Thread gmThread = new Thread(gmrun);
    	gmThread.start();
    	try (ServerSocket listener = new ServerSocket(58901)){
            System.out.println("XTank Server is Running...");
            ExecutorService pool = Executors.newFixedThreadPool(10);
            while (true) {
                Player p = new Player(listener.accept(), game, players.size());
                players.add(p);
            	pool.execute(p);
            }
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Updates all the players with the change in the GameModel
     */
    public void notifyPlayers() {
    	for(Player p: players) {
    		p.update();
    	}
    }
    
    /**
     * Function for ending and reseting the XTank game. Sends a notification
     * to each player, resets the GameModel and empties the Player list.
     */
	public void gameOver() {
		for(Player p: players) {
    		p.end();
		}
		game.reset();
		players.clear();
	}

	/**
	 * Returns the source file being used for the maze
	 */
	public String getMazeFile() {
		return mazeFiles[mazeType-1];
	}
	
	/**
	 * Returns which rules set is being used (classic or oneshot) 
	 */
	public String getRule() {
		return rules[ruleType-1];
	}

	/**
	 * Sets the maze selection (arena, forest or X)
	 * @param mazeType int 1-3 for one of the three mazes
	 */
	public void setMaze(int mazeType) {
		this.mazeType = mazeType;
		game.setObstacles(mazeFiles[mazeType-1]);
	}

	/**
	 * Sets the rule type (classic or oneshot)
	 * @param ruleType int 1-2 for one of the rules sets
	 */
	public void setRuleType(int ruleType) {
		this.ruleType = ruleType;
		game.setRule(rules[ruleType-1]);
	}
	
	private class GameModelRun implements Runnable {
		/**
		 * GameModelRun is a Runnable to continuously update the GameModel's
		 * state and check if the game is over.
		 */
		private GameModel game;
		private XTankServer server;
		private boolean exit;
		
		/**
		 * Creates a new GameModelRun for the specified GameModel
		 * @param g
		 */
		public GameModelRun(GameModel g) {
			game = g;
			server = XTankServer.getInstance();
			exit = false;
		}

		/**
		 * Updates the game's state and alerts server when the game is 
		 * over
		 */
		@Override
		public void run() {
			while(!exit) {
				if(game.isGameOver()) {
	        		server.gameOver();
	        	}
				game.updateState();
			}
		}
	}
}

 