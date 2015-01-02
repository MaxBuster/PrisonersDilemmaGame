package model;

public class Game {
	private int roundNum;
	private Player player1;
	private Player player2;
	private int strategy1 = 3;
	private int strategy2 = 3;
	private int payoff1 = -1;
	private int payoff2 = -1;
	
	public Game(int roundNum, Player player1, Player player2) {
		super();
		this.roundNum = roundNum;
		this.player1 = player1;
		this.player2 = player2;
	}
	
	public void setStrategy(Player player, int strategy) {
		if (player == player1) {
			strategy1 = strategy;
		} else {
			strategy2 = strategy;
		}
	}
	
	public synchronized int getStrategy(Player player) {
		if (player == player1) {
			return strategy1;
		} else {
			return strategy2;
		}
	}
	
	public void setPayoff(Player player, int payoff) {
		if (player == player1) {
			payoff1 = payoff;
		} else {
			payoff2 = payoff;
		}
	}
	
	public synchronized int getPayoff(Player player) {
		if (player == player1) {
			return payoff1;
		} else {
			return payoff2;
		}
	}	
	
	public boolean gameComplete() {
		if (strategy1 != 3 && strategy2 != 3) {
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized int getRoundNum() {
		return roundNum;
	}
}
