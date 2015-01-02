package model;

public class Pair {
	private int roundNum;
	private Player player1;
	private Player player2;
	private Game currentGame;
	private boolean gamesDone;
	private boolean gameChanged;

	public Pair() {
		super();
		this.roundNum = 0;
		this.player1 = null;
		this.player2 = null;
		this.currentGame = null;
		this.gamesDone = false;
		this.gameChanged = false;
	}
	
	public void setDone() {
		gamesDone = true;
	}

	public synchronized Game getCurrentGame() {
		if (gameChanged == false) {
			roundNum++;
			currentGame = new Game(roundNum,player1,player2);
			gameChanged = true;
		} else {
			gameChanged = false;
		}
		return currentGame;
	}

	public synchronized boolean pairComplete() {
		if (player1 != null && player2 != null) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized void setPlayer(Player player) {
		if (this.player1 == null) {
			this.player1 = player;
		} else {
			this.player2 = player;
		}
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public Player getOpponent(Player player) {
		if (player == player1) {
			return player2;
		} else {
			return player1;
		}
	}

	public synchronized int getGameNumber() {
		return roundNum;
	}
}
