import java.util.Scanner;

public class XTankServerLauncher {
	
	public static void main(String [] args) {
		Scanner s = new Scanner(System.in);
    	System.out.println("Select rule type: (1) classic (2) one shot");
    	int gameType = s.nextInt();
		System.out.println("Select maze: (1) arena, (2) forest, (3) X");
		int mazeType = s.nextInt();
		XTankServer server = XTankServer.getInstance();
		server.setMaze(mazeType);
		server.setRuleType(gameType);
		
		server.start();
	}
}
