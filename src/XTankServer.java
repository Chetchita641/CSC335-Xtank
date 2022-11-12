import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
	 * Creates a new XTankSever, with no player and empty GameModel
	 */
	private XTankServer() {
		players = new ArrayList<Player>();
    	game = GameModel.getInstance();

    	game.setObstacles("x.txt");
		
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
    
	public void gameOver() {
		System.out.println("GAME OVER");
		for(Player p: players) {
    		p.end();
		}
		game.reset();
		players.clear();
	}

	public String getMazeFile() {
		return mazeFiles[mazeType-1];
	}
	
	public String getRule() {
		return rules[ruleType-1];
	}

	public void setMaze(int mazeType) {
		this.mazeType = mazeType;
		game.setObstacles(mazeFiles[mazeType-1]);
	}

	public void setRuleType(int ruleType) {
		this.ruleType = ruleType;
		game.setRule(rules[ruleType-1]);
	}
}

class GameModelRun implements Runnable{
	private GameModel game;
	private XTankServer server;
	private boolean exit;
	
	public GameModelRun(GameModel g) {
		game = g;
		server = XTankServer.getInstance();
		exit = false;
	}

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
 