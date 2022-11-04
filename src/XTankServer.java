import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XTankServer {
	private static List<Player> players;

    public static void main(String[] args) throws Exception {
    	players = new ArrayList<Player>();
    	GameModel game = GameModel.getInstance();
        try (ServerSocket listener = new ServerSocket(58901)) {
            System.out.println("XTank Server is Running...");
            ExecutorService pool = Executors.newFixedThreadPool(10);
            while (true) 
            {
                Player p = new Player(listener.accept(), game, players.size());
                players.add(p);
            	pool.execute(p);
            }
        }
    }
    
    public static void notifyPlayers() {
    	for(Player p: players) {
    		p.update();
    	}
    }
}

 