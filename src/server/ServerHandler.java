package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import model.Game;
import model.Model;
import model.ModelHandler;
import model.Pair;
import model.Player;

public class ServerHandler {
	private final static Logger LOGGER = Logger.getLogger(ServerHandler.class
		      .getName());
	private static Model model;
	private Player me;
	private Player opponent;
	private Pair pair;
	private Game currentGame;
	private ModelHandler modelHandler;
	private DataInputStream in;
	private DataOutputStream out;
	private ServerGUI gui;
	private int type;

	public ServerHandler(Model thisModel, 
			ModelHandler modelHandler,
			DataInputStream in, 
			DataOutputStream out, 
			ServerGUI gui,
			Socket socket) {
		super();
		model = thisModel;
		me = model.addPlayer();
		model.addSocket(me, socket);
		opponent = null;
		currentGame = null;
		this.modelHandler = modelHandler;
		this.in = in;
		this.out = out;
		this.gui = gui;
		this.type = 1;
		this.pair = null;
	}

	public void handleMessage() {
		initialSetup();
		while (true) {
			waitForMessage();

			currentGame.setStrategy(me,type); 
			while (!currentGame.gameComplete()) {
				sleepThread();
			}
			int opponentStrat = currentGame.getStrategy(opponent);
			getAndSetPayoffs(opponentStrat);
			addGameToServerGUI();

			boolean moreGames = modelHandler.checkMoreGames(currentGame); 
			if (moreGames == true) {
				startNewRound();
			} else {
				handleNoMoreRounds();
			}
		}
	}

	public void initialSetup() {
		writeMessage(out, 2, me.getPlayerNum());
		System.out.println(me.getPlayerNum());
		while (!model.pairedYet()) {
			sleepThread();
		}
		pair = model.getPair(me);
		opponent = pair.getOpponent(me);
		currentGame = pair.getCurrentGame(); 
		writeMessage(out, 3, opponent.getPlayerNum());
	}

	public void waitForMessage() {
		try {
			char c = (char)in.readByte();
			while (c != '!') {
				c = (char)in.readByte();
			}
			type = in.readByte();
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}

	public void getAndSetPayoffs(int opponentStrat) {
		int[] payoffs = ModelHandler.calcPayoffs(type, opponentStrat); // Change this
		currentGame.setPayoff(me, payoffs[0]);
		currentGame.setPayoff(opponent, payoffs[1]);

		writePayoffs(opponentStrat, payoffs);
	}

	public void addGameToServerGUI() {
		gui.addGame(me.getPlayerNum(), currentGame.getStrategy(me), currentGame.getPayoff(me), 
				currentGame.getRoundNum());
	}

	public void writePayoffs(int opponentStrat, int[] payoffs) {
		try {
			writeMessage(out, 4, type);
			out.writeByte(payoffs[0]);
			out.writeByte(opponentStrat);
			out.writeByte(payoffs[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startNewRound() {
		try {
			currentGame = pair.getCurrentGame(); // Change this to get new game
			out.writeByte((int)'!');
			out.writeByte(5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void endRound() {
		try {
			out.writeByte((int)'!');
			out.writeByte(6);
			me.setRoundOver(true);
			model.allDone();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleNoMoreRounds() {
		endRound();

		while (!model.newGame() && !model.isAllOver()) { 
			sleepThread();
		}
		me.setRoundOver(false);
		if (model.newGame()) {
			startNewGame(); 
		} else {
			endGame();
		}
	}

	public void startNewGame() {
		pair = model.getPair(me);
		currentGame = pair.getCurrentGame();
		opponent = pair.getOpponent(me);
		writeMessage(out, 3, opponent.getPlayerNum());
		
		gui.addPlayers(model.getPlayer1s(), model.getPlayer2s()); // FIXME Put this somewhere else
	}

	public void endGame() {
		try {
			out.writeByte((int)'!');
			out.writeByte(7);
			gui.endingConditions();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Player getPlayer() {
		return me;
	}

	public void sleepThread() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void writeMessage(DataOutputStream out, int type, int message) {
		try {
			out.writeByte((int)'!');
			out.writeByte(type);
			out.writeByte(message);
		} catch (IOException e) {
			System.out.println("Error here");
			e.printStackTrace();
//			LOGGER.log(e);
		}
	}
}
