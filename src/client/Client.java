package client;

import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket clientSocket;
	private DataInputStream socketInputStream;
	private DataOutputStream socketOutputStream;
	private final PropertyChangeSupport propertyChangeSupport
		= new PropertyChangeSupport(this);

	public Client() {
		try {
			// On PC go to Command Prompt; look for IPv4 after ipconfig
			// On Mac go to Terminal; look for inet after ifconfig |grep inet
			// Ignore 127.0.0.1
			clientSocket = new Socket("localhost", 10501); 
			socketInputStream = new DataInputStream(clientSocket.getInputStream());
			socketOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		} catch (UnknownHostException e) {
			System.out.println("Unknown Host Exception");
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}

	public void createGuiAndHandler() {
		ClientGUI clientGui = new ClientGUI(propertyChangeSupport);
		ClientHandler handler = new ClientHandler(propertyChangeSupport, socketInputStream, 
				socketOutputStream, clientGui);
		handler.handleMessage(); 
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.createGuiAndHandler();
	}

}
