package client;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;
import java.io.DataOutputStream;
import java.io.IOException;

import net.miginfocom.swing.MigLayout;

public class ClientGUI {
	private JFrame frame;
	private JPanel contentPane;
	private PropertyChangeSupport propertyChangeSupport;
	private JLabel playerNumberLabel;
	private JLabel opponentNumberLabel;

	private JTable payoffTable;
	private JTextPane explanationText;
	private JButton confessButton;
	private JButton dontConfessButton;

	private JLabel genericLabel;
	//	private JLabel waitForResponseLabel; // Make references to this also change generic label

	private Timer actionTimer;
	private Timer payoffTimer;
	private int actionTime = 45000;
	private JLabel timeLeftLabel;
	private String whatToDoNextRound;

	private JDialog payoffPopup; // Change this so payoffs appear in one of the generic boxes
	private boolean popupDone = true;
	private JLabel payoffLabel; // Change these to Strings that get added in a generic box
	private JLabel opponentPayoffLabel;
	private JLabel actionLabel;
	private JLabel opponentActionLabel;

	/**
	 * Create the frame
	 */
	public ClientGUI(final PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
		this.whatToDoNextRound = null;

		createFrame();
		createTimers();
		createLabels();
		createExplanation();
		createButtons();
		createPayoffPopup();

		frame.setVisible(true);
	}

	public void createFrame() {
		frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		frame.setExtendedState( JFrame.MAXIMIZED_BOTH );

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new MigLayout("", "[50px:n,center][78px,grow][35px][149px][143px][50px:n]", "[10px:n:50px,grow][14px][14px][18px][14px][75px:n,grow][123.00px][27.00][][grow][][10px:n:50px,grow,center]"));
		frame.setContentPane(contentPane);
	}

	public void createTimers() {
		timeLeftLabel = new JLabel("Time left: 45");
		contentPane.add(timeLeftLabel, "cell 1 7,alignx center");
		timeLeftLabel.setVisible(false);

		ActionListener payoffTimerStopper = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				popupDone = true;
				stopPayoffTimer();
			}
		};
		int payoffTime = 15000;
		payoffTimer = new Timer(payoffTime, payoffTimerStopper);

		ActionListener actionTimerStopper = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				actionTime = actionTime - 1000;
				timeLeftLabel.setText("Time left: " + actionTime/1000); 
				if (actionTime == 0) {
					endCurrentRound();
					firePropertyChange("Confess");
				}
			}
		};
		int timeIncrement = 1000;
		actionTimer = new Timer(timeIncrement, actionTimerStopper);
	}

	public void createLabels() {
		opponentNumberLabel = new JLabel("Your Opponent's Number Is: ");
		contentPane.add(opponentNumberLabel, "cell 1 2 3 1,growx,aligny top");
		opponentNumberLabel.setVisible(false);

		playerNumberLabel = new JLabel("Your Number Is:");
		contentPane.add(playerNumberLabel, "cell 1 1,alignx left,aligny top");

		genericLabel = new JLabel("Please wait while you are matched with another player");
		contentPane.add(genericLabel, "cell 1 3 3 1,alignx left,growy");
	}

	public void createExplanation() {
		Object[][] tableData = new Object[][]{
				new String[]{"", "", "Your Opponent", ""},
				new String[]{"", "", "Confess", "Don't Confess"},
				new Object[]{"You", "Confess", "2 years, 2 years", "0 years, 5 years"},
				new Object[]{"", "Don't Confess", "5 years, 0 years", "1 years, 1 years"},			
		};
		String[] colNames = new String[]{"", "", "Confess","Don't Confess"};
		payoffTable = new JTable(tableData,colNames);
		payoffTable.setColumnSelectionAllowed(false);
		payoffTable.setPreferredSize(new Dimension(450,50));
		contentPane.add(payoffTable, "cell 1 6,alignx center,aligny center");
		payoffTable.setVisible(false);

		String explanation = "You will have 45 seconds to read the situation and make a decision \n\n"
				+ "You and your accomplice have been caught robbing a bank. You are being "
				+ "interrogated separately from your partner, who is given the same exact information."
				+ "Your lawyer informs you that they have concrete evidence of your robbery, and can "
				+ "give each of you 1 year in jail for it. \n\nHowever, they also want you to confess to "
				+ "another crime that you committed with your partner. If you confess to it, and your "
				+ "partner does not, you will go free, but your partner will receive 5 years in jail. "
				+ "If you both confess, you will each get 2 years. If neither of you confess, you will "
				+ "each receive the initial 1 year. If you do not confess, but your partner does, he "
				+ "will walk free and you will receive 5 years in jail. \n\n"
				+ "Below is a payoff matrix showing how actions correspond to years in jail";

		explanationText = new JTextPane();
		explanationText.setText(explanation);
		explanationText.setEditable(false);
		explanationText.setVisible(false);
		contentPane.add(explanationText, "cell 1 5,grow");
	}

	public void createButtons() {
		dontConfessButton = new JButton("Don't Confess");
		dontConfessButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				endCurrentRound();
				firePropertyChange("Don't Confess");
			}
		});
		dontConfessButton.setVisible(false);
		contentPane.add(dontConfessButton, "cell 4 7,alignx center,aligny top");

		confessButton = new JButton("Confess");
		confessButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				endCurrentRound();
				firePropertyChange("Confess");
			}
		});
		confessButton.setVisible(false);
		contentPane.add(confessButton, "cell 3 7,alignx center,aligny top");
	}

	public void createPayoffPopup() {
		payoffPopup = new JDialog(frame, "Game Payoffs");
		payoffPopup.getContentPane().setLayout(new BoxLayout(payoffPopup.getContentPane(),BoxLayout.Y_AXIS));
		payoffPopup.setBounds(600, 400, 600, 400);
		payoffPopup.setLocationRelativeTo(contentPane);
		payoffPopup.setVisible(false);

		actionLabel = new JLabel();
		actionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		payoffPopup.getContentPane().add(actionLabel);

		payoffLabel = new JLabel();
		payoffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		payoffPopup.getContentPane().add(payoffLabel);

		opponentActionLabel = new JLabel();
		opponentActionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		payoffPopup.getContentPane().add(opponentActionLabel);

		opponentPayoffLabel = new JLabel();
		opponentPayoffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		payoffPopup.getContentPane().add(opponentPayoffLabel);
	}

	public void setPlayerNum(int playerNum) {
		playerNumberLabel.setText("Your number is: " + playerNum);
	}

	public void setOpponentNum(int opponentNum) {
		opponentNumberLabel.setText("Your opponent's number is: " + opponentNum);
		opponentNumberLabel.setVisible(true);
	}

	public void startNewRound() {
		while (!popupDone) {
			sleepThread();
		}
		genericLabel.setVisible(false);

		confessButton.setVisible(true);
		dontConfessButton.setVisible(true);
		payoffTable.setVisible(true);
		explanationText.setVisible(true);
		timeLeftLabel.setVisible(true);
		actionTimer.start();
	}

	public void firePropertyChange(String propertyValue) {
		propertyChangeSupport.firePropertyChange(propertyValue, null, null);
		confessButton.setVisible(false);
		dontConfessButton.setVisible(false);
		endCurrentRound();
	}

	public void endCurrentRound() {
		timeLeftLabel.setText("Time left: 45");
		actionTime = 45000;
		timeLeftLabel.setVisible(false);
		payoffTable.setVisible(false);
		explanationText.setVisible(false);
		actionTimer.stop();

		genericLabel.setText("Please wait for your opponent to respond");
		genericLabel.setVisible(true);
	}

	public void setActionsAndPayoffs(String action, String payoff, 
			String opponentAction, String opponentPayoff) {
		actionLabel.setText("You: " + action);
		payoffLabel.setText("You will go to jail for: " + payoff);

		opponentActionLabel.setText("Your opponent: " + opponentAction);
		opponentPayoffLabel.setText("Your opponent will go to jail for: " + opponentPayoff);
	}

	public void startPayoffTimer() {
		popupDone = false;
		genericLabel.setVisible(false);
		payoffPopup.setVisible(true);
		payoffTimer.start();
	}

	public void stopPayoffTimer() {
		payoffTimer.stop();
		payoffPopup.setVisible(false);
	}
	
	public boolean isPopupDone() {
		return popupDone;
	}

	public void endCurrentGame() {
		opponentNumberLabel.setVisible(false);
		payoffTable.setVisible(false);
		explanationText.setVisible(false);
		genericLabel.setText("Please wait to be repaired for a new game");
		genericLabel.setVisible(true);
	}

	public void endAllGames() {
		genericLabel.setText("All games are over, thank you for participating");
		genericLabel.setVisible(true);

		playerNumberLabel.setVisible(false);
		opponentNumberLabel.setVisible(false);
		while (!popupDone) {
			sleepThread();
		}
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	
	public void sleepThread() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
