import java.util.Scanner;

public class XTankServerLauncher {
	
	public static void main(String [] args) {
		Scanner s = new Scanner(System.in);
    	System.out.println("Select game type: ");
    	String gameType = s.nextLine();
		System.out.println("Select maze: 1, 2, 3 ");
		int mazeType = s.nextInt();
		XTankServer server = XTankServer.getInstance();
		server.setMaze(mazeType);
		
		server.start();
	}
}
