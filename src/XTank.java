import java.util.Scanner;

public class XTank {
	public static void main(String [] args) {
		Scanner s = new Scanner(System.in);
    	System.out.println("Enter name:");
    	String name = s.nextLine();
    	Client client = null;
		try {
			client = new Client("127.0.0.1", name);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	XTankUI ui = new XTankUI(client);
    	client.setUI(ui);
    	ClientRun cr = new ClientRun(client);
    	UIRun ur = new UIRun(ui);
    	Thread clientThread = new Thread(cr);
    	Thread uiThread = new Thread(ur);
    	uiThread.start();
    	clientThread.start();
    	
    	s.close();
	}
}
