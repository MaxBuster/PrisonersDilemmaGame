package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import server.ServerGUI;

public class ModelHandler implements PropertyChangeListener {
	private PropertyChangeSupport propertyChangeSupport;
	private Model model;
	private ServerGUI serverGUI;
	
	public ModelHandler(PropertyChangeSupport propertyChangeSupport,
			Model model,
			ServerGUI serverGUI) {
		super();
		this.propertyChangeSupport = propertyChangeSupport;
		this.propertyChangeSupport.addPropertyChangeListener(this);
		this.model = model;
		this.serverGUI = serverGUI;
	}
	
	public boolean checkMoreGames(Game game) {
		if (model.getNumGames() > game.getRoundNum()) {
			return true;
		} else {
			return false;
		}
	}

	public static int[] calcPayoffs(int strat1, int strat2) {
		// 0=C, 1=N
		if (strat1==0 && strat2==0) { // C, C
			return new int[]{2,2};
		} else if (strat1==0 && strat2==1) { // C, N
			return new int[]{0,5};
		} else if (strat1==1 && strat2==1) { // N, N
			return new int[]{1,1};
		} else { // N, C
			return new int[]{5,0};
		} 
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String propertyValue = e.getPropertyName();
		if (propertyValue == "initialPairing") {
			model.initialPair();
			Player[] player1s = model.getPlayer1s();
			Player[] player2s = model.getPlayer2s();
			serverGUI.addPlayers(player1s, player2s);
		} else if (propertyValue == "numRounds") {
			int numRounds = (int) e.getNewValue();
			model.setNumRounds(numRounds);
		} else if (propertyValue == "numGames") {
			int numGames = (int) e.getNewValue();
			model.setNumGames(numGames);
		} else if (propertyValue == "removePair") {
			Player player = (Player) e.getNewValue();
			model.removePair(player);
			Player[] player1s = model.getPlayer1s();
			Player[] player2s = model.getPlayer2s();
			serverGUI.addPlayers(player1s, player2s);
		}		
	}

}
