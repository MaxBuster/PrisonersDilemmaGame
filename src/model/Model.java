package model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Model {
	private LinkedList<Player> players;
	private HashMap<Player, Socket> sockets;
	private ArrayList<Pair> pairs; 
	private HashMap<Player,Pair> playersToPairs;
	private int numGames;
	private boolean newGame;
	private boolean allOver;
	private boolean pairedYet;
	private int numRounds;
	private int currentRound;

	public Model() {
		super();
		players = new LinkedList<Player>();
		sockets = new HashMap<Player, Socket>();
		pairs = new ArrayList<Pair>();
		playersToPairs = new HashMap<Player,Pair>();
		numGames = 3;
		newGame = false;
		allOver = false;
		pairedYet = false;
		numRounds = 1;
		currentRound = 1;
	}

	public synchronized void addSocket(Player p, Socket s) {
		sockets.put(p, s);
	}

	public synchronized void removePair(Player p) {
		Pair pair = playersToPairs.get(p);
		Player opponent = pair.getOpponent(p);
		// FIXME What to do with a closed socket
		Socket pSocket = sockets.get(p);
		Socket opponentSocket = sockets.get(opponent);
		sockets.remove(pSocket);
		sockets.remove(opponentSocket);
		try {
			pSocket.close();
			opponentSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		playersToPairs.remove(p);
		playersToPairs.remove(opponent);
		pairs.remove(pair);
		players.remove(p);
		players.remove(opponent);
	}

	public synchronized Player[] getPlayer1s() {
		Player[] player1s = new Player[pairs.size()];
		int i=0;
		for (Pair p : pairs) {
			player1s[i] = p.getPlayer1();
			i++;
		}
		return player1s;
	}

	public synchronized Player[] getPlayer2s() {
		Player[] player2s = new Player[pairs.size()];
		int i=0;
		for (Pair p : pairs) {
			player2s[i] = p.getPlayer2();
			i++;
		}
		return player2s;
	}

	public synchronized boolean isAllOver() {
		return allOver;
	}

	public synchronized void setAllOver() {
		allOver = true;
	}

	public synchronized Pair getPair(Player player) {
		return playersToPairs.get(player);
	}

	public synchronized boolean newGame() {
		return newGame;
	}

	public synchronized int getCurrentRound() {
		return currentRound;
	}

	public synchronized boolean allDone() {
		newGame = false;
		boolean allDone = true;
		for (Player p : players) {
			if (!p.isRoundOver()) {
				allDone = false;
			}
		}
		if (allDone) {
			if (currentRound < numRounds) {
				switchPairs();
			} else {
				setAllOver();
			}
		}
		return allDone;
	}

	public synchronized void switchPairs() {
		currentRound++;
		LinkedList<Player> player1s = new LinkedList<Player>();
		LinkedList<Player> player2s = new LinkedList<Player>();
		ArrayList<Pair> newPairs = new ArrayList<Pair>();
		playersToPairs.clear();
		for (Pair p : pairs) {
			player1s.addLast(p.getPlayer1());
			player2s.addLast(p.getPlayer2());
		}
		System.out.println("Player size is: " + player1s.size());
		for (int j=0; j<player1s.size(); j++) {
			Pair pair = new Pair();
			if (j==(player1s.size()-1)) {
				pair.setPlayer(player1s.get(j));
				pair.setPlayer(player2s.get(0));
			} else {
				pair.setPlayer(player1s.get(j));
				pair.setPlayer(player2s.get(j+1));
			}
			newPairs.add(pair);
		}
		for (Pair p : newPairs) {
			playersToPairs.put(p.getPlayer1(), p);
			playersToPairs.put(p.getPlayer2(), p);
		}
		System.out.println("players to pairs size is: " + playersToPairs.size());
		pairs = newPairs;
		newGame = true;
	}

	public synchronized Player addPlayer() {
		Player player = new Player();
		players.add(player);
		return player;
	}

	public synchronized void initialPair() {
		int playersSize = players.size();
		for (int i=0; i<playersSize/2; i++) {
			Pair pair = new Pair();
			pair.setPlayer(players.get(i));
			pair.setPlayer(players.get(playersSize-i-1));
			pairs.add(pair);
			playersToPairs.put(players.get(i), pair);
			playersToPairs.put(players.get(playersSize-i-1), pair);
		}
		pairedYet = true;
	}

	public synchronized boolean pairedYet() {
		return pairedYet;
	}

	public synchronized void setNumGames(int number) {
		numGames = number;
	}

	public synchronized int getNumGames() {
		return numGames;
	}

	public synchronized void setNumRounds(int number) {
		numRounds = number;
	}

	public synchronized int getNumRounds() {
		return numRounds;
	}
}
