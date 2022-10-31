import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A client for XTank
 */
public class Client 
{
    private Socket socket;
    //private ObjectInputStream in;
    private Scanner in;
    private PrintWriter out;
    private GameModel game;
    private XTankUI ui;

    public Client(String serverAddress, String name) throws Exception {
        socket = new Socket(serverAddress, 58901);
        //in = new ObjectInputStream(socket.getInputStream());
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
        out.println(name);
        out.flush();
        game = new GameModel();
    }
    
    public void setUI(XTankUI ui) {
    	this.ui = ui;
    }
    
    public void play() throws Exception {
    	
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
            	else if(response.startsWith("move forward")) {
            		processMove(response.substring(21));
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
            socket.close();
        }
    }

	private void processAddTanks(String tanksInfo) {
		System.out.println(tanksInfo);
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
				System.out.println(playerId + " " + xCord + " " +yCord);
				game.addTank(playerId, xCord, yCord);
				i = third+1;
			}
		}
	}
	
	private void processMove(String moveInfo) {
		int playerId = Integer.parseInt(moveInfo);
		game.moveTank(playerId);
	}

	public void move() {
		System.out.println("move in clinet");
		out.println("move forward");
		out.flush();
	}
	
	public void shoot() {
		out.println("shoot");
		out.flush();
	}
}

class ClientRun implements Runnable{
	private Client client;
	
	public ClientRun(Client c) {
		client = c;
	}

	@Override
	public void run() {
		try {
			client.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

