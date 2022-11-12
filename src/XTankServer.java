import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XTankServer {
	private final String rule = "classic";
	
	private List<Player> players;
	private GameModel game;
	private static XTankServer instance;
	
	/**
	 * Creates a new XTankSever, with no player and empty GameModel
	 */
	private XTankServer() {
		players = new ArrayList<Player>();
    	game = GameModel.getInstance();
    	game.setObstacles("x.txt");
		
		game.setRule(rule);
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

	public String getRule() {
		return rule;
	}
	
	/**
	 * Starts the server. Begins to accept new clients and add them to the game
	 */
    public void start() {
    	GameModelRun gmrun = new GameModelRun(game);
    	Thread gmThread = new Thread(gmrun);
    	gmThread.start();
        try (ServerSocket listener = new ServerSocket(58901)) {
            System.out.println("XTank Server is Running...");
            ExecutorService pool = Executors.newFixedThreadPool(10);
            while (true) 
            {
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
}

class GameModelRun implements Runnable{
	private GameModel game;
	
	public GameModelRun(GameModel g) {
		game = g;
	}

	@Override
	public void run() {
		while(true) {
			game.updateState();
		}
	}
}
 