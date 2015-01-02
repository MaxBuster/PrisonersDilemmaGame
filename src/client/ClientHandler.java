package client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientHandler implements PropertyChangeListener {
	private PropertyChangeSupport propertyChangeSupport;
	private ClientGUI client;
	private DataInputStream in;
	private DataOutputStream out;

	public ClientHandler(PropertyChangeSupport propertyChangeSupport, 
			DataInputStream dataIn, 
			DataOutputStream dataOut, 
			ClientGUI gui) {
		super();
		this.propertyChangeSupport = propertyChangeSupport;
		this.propertyChangeSupport.addPropertyChangeListener(this);
		this.client = gui;
		this.in = dataIn;
		this.out = dataOut;
	}

	public void handleMessage() {
		while (true) {
			try {
				char c = (char)in.readByte();
				while (c != '!') {
					c = (char)in.readByte();
				}
				int type = in.readByte();
				if (type == 2) {
					int playerNumber = in.readByte();
					client.setPlayerNum(playerNumber);
					System.out.println(playerNumber);
				} else if (type == 3) {
					while (!client.isPopupDone()) {
						sleepThread();
					}
					int opponentNum = in.readByte();
					client.setOpponentNum(opponentNum);
					client.startNewRound(); 
				} else if (type == 4) {
					readAndWritePayoffs();
					client.startPayoffTimer();
				} else if (type == 5) {
					client.startNewRound();
				} else if (type == 6) {
					client.endCurrentGame();
				} else if (type == 7) {
					client.endAllGames();			
				}
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String propertyChange = e.getPropertyName();
		if (propertyChange == "Confess") {
			try {
				out.writeByte('!');
				out.writeByte(0);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				out.writeByte('!');
				out.writeByte(1);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void readAndWritePayoffs() {
		try {
			int myStrat = in.readByte();
			int myPayoff = in.readByte();
			int opponentStrat = in.readByte();
			int opponentPayoff = in.readByte();
			setActionsAndPayoffs(myStrat, myPayoff, 
					opponentStrat, opponentPayoff);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setActionsAndPayoffs(int actionInt, int payoffInt,
			int opponentActionInt, int opponentPayoffInt) {
		String actionString = convertActionIntToString(actionInt);
		String payoffString = convertPayoffIntToString(payoffInt);
		
		String opponentActionString = convertActionIntToString(opponentActionInt);
		String opponentPayoffString = convertPayoffIntToString(opponentPayoffInt);
		
		client.setActionsAndPayoffs(actionString, payoffString, 
				opponentActionString, opponentPayoffString);
	}
	
	public String convertActionIntToString(int actionInt) {
		String actionString = "";
		if (actionInt == 0) {
			actionString = "Confessed";
		} else {
			actionString = "Didn't Confess";
		}
		return actionString;
	}
	
	public String convertPayoffIntToString(int payoffInt) {
		String payoffString = "";
		if (payoffInt == 1) {
			payoffString = payoffInt + " year";
		} else {
			payoffString = payoffInt + " years";
		}
		return payoffString;
	}
	
	public void sleepThread() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}