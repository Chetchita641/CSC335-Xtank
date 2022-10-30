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

    public Client(String serverAddress, String name) throws Exception {
        socket = new Socket(serverAddress, 58901);
        //in = new ObjectInputStream(socket.getInputStream());
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
        out.println(name);
    }
    
    public void play() throws Exception {
        try {
            //Object response = in.readObject();
            String response = in.nextLine();
            System.out.println(response);
            while(in.hasNext()) {
            	System.out.println(in.nextLine());
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            socket.close();
        }
    }

	public void move() {
		out.println("move forward");
	}
	
	public void shoot() {
		out.println("shoot");
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

