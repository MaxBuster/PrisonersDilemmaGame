package server;

import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;

import model.Player; 
import net.miginfocom.swing.MigLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeSupport;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class ServerGUI {
	private PropertyChangeSupport propertyChangeSupport;
	private JFrame frame;
	private JPanel contentPane;

	private JButton initialPairingButton;
	private JButton numGamesButton; 
	private JButton numRoundsButton;
	private JTextField numRoundsField;
	private JTextField numGamesField;
	
	private JButton exportDataButton;
	private JTextField exportDataField;
	private String exportFileName;
	
	private JList players;
	private JList strategies;
	private JList payoffs;
	private JList gameNums;
	
	private LinkedList<Integer> playerList = new LinkedList<>();
	private LinkedList<Integer> strategyList = new LinkedList<>();
	private LinkedList<Integer> payoffList = new LinkedList<>();
	private LinkedList<Integer> gameNumList = new LinkedList<>();
	
	private JList Player2List;
	private JList Player1List;
	private HashMap<Integer, Player> player1Map = new HashMap<>();
	private HashMap<Integer, Player> player2Map = new HashMap<>();
	private JButton btnDeletePair;
	
	private JLabel gameNumberLabel;	
	private JLabel player1Label;
	private JLabel player2Label;

	/**
	 * Create the frame.
	 */
	public ServerGUI(final PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;	

		createFrame();
		createNumRoundsField();
		createNumRoundsButton();
		createNumGamesField();
		createNumGamesButton();
		createInitialPairButton();
		createDataExportField();
		createDataExportButton();
		createGameListLabels();
		createGameLists();
		createPlayerListLabels();
		createPlayerLists();
		createDeletePlayerButton();

		frame.setVisible(true);
	}
	
	public void createFrame() {
		frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		frame.setExtendedState( JFrame.MAXIMIZED_BOTH );

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new MigLayout("", "[0px:n:200px,grow][100px,grow][100px,grow][100px,grow][100px,grow][100px,grow][100px,grow][0px:n:200px,grow]", "[10px:n:20px,grow][23px][23px:n][22px][158px,grow][::100px][10px:n:50px,grow]"));
		frame.setContentPane(contentPane);
	}
	
	public void createNumRoundsField() {
		numRoundsField = new JTextField();
		numRoundsField.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				numRoundsField.setText("");
			}
		});
		numRoundsField.setText("How many rounds?");
		contentPane.add(numRoundsField, "cell 3 1,growx");
		numRoundsField.setColumns(10);
	}
	
	public void createNumRoundsButton() {
		numRoundsButton = new JButton("Submit");
		numRoundsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				String numRounds = numRoundsField.getText();
				try {
					int number = Integer.parseInt(numRounds);
//					model.setNumRounds(number);
					numRoundsButton.setVisible(false);
					numRoundsField.setVisible(false);
					propertyChangeSupport.firePropertyChange("numRounds", null, number);
				} catch (NumberFormatException e) {
					numRoundsField.setText("Not a number");
					System.out.println("Bad number");
				}
			}
		});
		contentPane.add(numRoundsButton, "cell 4 1");
	}
	
	public void createNumGamesField() {
		numGamesField = new JTextField();
		numGamesField.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				numGamesField.setText("");
			}
		});
		numGamesField.setText("How many games?");
		contentPane.add(numGamesField, "cell 1 1,growx,aligny center");
		numGamesField.setColumns(10);
	}
	
	public void createNumGamesButton() {
		numGamesButton = new JButton("Submit");
		numGamesButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				String numGames = numGamesField.getText();
				try {
					int number = Integer.parseInt(numGames);
//					model.setNumGames(number); 
					numGamesButton.setVisible(false);
					numGamesField.setVisible(false);
					propertyChangeSupport.firePropertyChange("numGames",null,number);
				} catch (NumberFormatException e) {
					numGamesField.setText("Not a number");
					System.out.println("Bad number");
				}
			}
		});
		contentPane.add(numGamesButton, "cell 2 1,alignx left,aligny top");
	}
	
	public void createInitialPairButton() {
		initialPairingButton = new JButton("Initial Pairing");
		initialPairingButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				initialPairingButton.setVisible(false);
				propertyChangeSupport.firePropertyChange("initialPairing", null, null);
			}
		});
		contentPane.add(initialPairingButton, "cell 1 2");
	}
	
	public void createDataExportField() {
		exportDataField = new JTextField();
		exportDataField.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				exportDataField.setText("");
			}
		});
		contentPane.add(exportDataField, "cell 5 1,growx");
		exportDataField.setColumns(10);
		exportDataField.setText("File Name");
	}
	
	public void createDataExportButton() {
		exportDataButton = new JButton("Export Data");
		exportDataButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				String name = exportDataField.getText();
				if (name.contains("File Name") || name.equals("")) {
					JOptionPane.showMessageDialog(frame,  "Please add a file name");  
				} else {
					exportFileName = name;
					boolean write = DataExport.exportData(name, playerList, strategyList, payoffList, gameNumList);
					if (!write) {
						exportDataField.setText("File Write Failed");
					} 
				}
			}
		});
		contentPane.add(exportDataButton, "cell 6 1,alignx center");
	}
	
	public void createGameListLabels() {
		gameNumberLabel = new JLabel("Game #");
		contentPane.add(gameNumberLabel, "cell 4 3,alignx center");

		player1Label = new JLabel("Pair 1");
		contentPane.add(player1Label, "cell 5 3,alignx center,aligny center");

		player2Label = new JLabel("Pair 2");
		contentPane.add(player2Label, "cell 6 3,alignx center,aligny center");
	}
	
	public void createGameLists() {
		players = new JList();
		contentPane.add(players, "cell 1 4,grow");

		strategies = new JList();
		contentPane.add(strategies, "cell 2 4,grow");

		payoffs = new JList();
		contentPane.add(payoffs, "cell 3 4,grow");
		
		gameNums = new JList();
		contentPane.add(gameNums, "cell 4 4,grow");
	}
	
	public void createPlayerListLabels() {
		JLabel lblPlayers = new JLabel("Players");
		contentPane.add(lblPlayers, "cell 1 3,alignx center,growy");

		JLabel lblStrategies = new JLabel("Strategies");
		contentPane.add(lblStrategies, "cell 2 3,alignx center,growy");

		JLabel lblScores = new JLabel("Scores");
		contentPane.add(lblScores, "cell 3 3,alignx center,aligny center");
	}
	
	public void createPlayerLists() {
		Player1List = new JList();
		Player1List.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				int selectedIndex = Player1List.getSelectedIndex();
				Player2List.setSelectedIndex(selectedIndex);
			}
		});
		contentPane.add(Player1List, "cell 5 4,grow");

		Player2List = new JList();
		Player2List.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				int selectedIndex = Player2List.getSelectedIndex();
				Player1List.setSelectedIndex(selectedIndex);
			}
		});
		contentPane.add(Player2List, "cell 6 4,grow");
	}
	
	public void createDeletePlayerButton() {
		btnDeletePair = new JButton("Delete Pair");
		btnDeletePair.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Object selectedValue = Player1List.getSelectedValue();
				int selectedInt = (int) selectedValue;
				Player p = player1Map.get(selectedInt);
				propertyChangeSupport.firePropertyChange("removePair", null, p);
			}
		});
		contentPane.add(btnDeletePair, "cell 6 5");
	}

	public synchronized void addPlayers(Player[] player1s, Player[] player2s) {
		Object[] player1Nums = new Object[player1s.length];
		Object[] player2Nums = new Object[player1s.length];

		for (int i=0; i<player1s.length; i++) {
			player1Map.put(player1s[i].getPlayerNum(), player1s[i]);
			player2Map.put(player2s[i].getPlayerNum(), player2s[i]);
			player1Nums[i] = player1s[i].getPlayerNum();
			player2Nums[i] = player2s[i].getPlayerNum();
		}
		Player1List.setListData(player1Nums);
		Player2List.setListData(player2Nums);

		Player1List.repaint();
		Player2List.repaint();
	}
	
	public synchronized void endingConditions() {
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
	}

	public synchronized void addGame(int playerNum, int strategy, int score, int gameNum) {
		System.out.println("Player num is: " + playerNum);
		System.out.println("Strategy is: " + strategy);
		System.out.println("Score: " + score);
		System.out.println("game Nume: " + gameNum);
		playerList.addFirst(playerNum);
		strategyList.addFirst(strategy);
		payoffList.addFirst(score);
		gameNumList.addFirst(gameNum);

		players.setListData(playerList.toArray());
		strategies.setListData(strategyList.toArray());
		payoffs.setListData(payoffList.toArray());
		gameNums.setListData(gameNumList.toArray());

		players.repaint();
		strategies.repaint();
		payoffs.repaint();
		gameNums.repaint();
		
//		DataExport.exportData(exportFileName, playerList, strategyList, payoffList, gameNumList); 
	}
}
