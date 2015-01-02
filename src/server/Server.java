package server;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

import model.Model;
import model.ModelHandler;
import model.Pair;
import model.Player;

public class Server {
	private static Model model;
	private ServerSocket socket;
	private ServerGUI gui;
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private ModelHandler modelHandler;

	public Server() throws IOException {
		model = new Model();
		socket = new ServerSocket(10501);
		gui = new ServerGUI(propertyChangeSupport);
		this.modelHandler = new ModelHandler(propertyChangeSupport, model, gui);
	}

	/**
	 * a continuous loop that creates a new thread for each client accepted
	 */
	public void run() {
		try {
			Socket s = null;
			while(true) {
				s = socket.accept();
				System.out.println("Server Connected");
				newThread(s);
			}
		} catch (IOException e){
			System.out.println("Something wrong happened");
		}
	}

	/**
	 * a continuous loop to check for incoming messages, and subsequently respond
	 * @param s
	 */
	public void newThread(final Socket s){
		Thread thread = new Thread(){
			public void run() {
				try {
					DataInputStream in = new DataInputStream(s.getInputStream());
					DataOutputStream out = new DataOutputStream(s.getOutputStream());
					ServerHandler serverHandler = new ServerHandler(model, modelHandler, in, out, gui, s);
					serverHandler.handleMessage();
				} catch (EOFException e) {
					// graceful termination on EOF
				} catch (IOException e) {
					System.out.println("Remote connection reset");
				} 
			}
		};
		thread.start();
	}

	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
