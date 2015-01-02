package model;

public class Player {
	private static int NUMPLAYERS = 0;
	private int playerNumber;
	private boolean roundOver;

	public Player() {
		super();
		NUMPLAYERS++;
		this.playerNumber = NUMPLAYERS;
		this.roundOver = false;
	}
	
	public boolean isRoundOver() {
		return roundOver;
	}

	public void setRoundOver(boolean roundOver) {
		this.roundOver = roundOver;
	}

	public int getPlayerNum(){
		return playerNumber;
	}
}
