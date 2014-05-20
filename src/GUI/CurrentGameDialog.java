package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.activity.InvalidActivityException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import tournament.Tournament.Side;
import API.Match;
import API.Player;
import API.TournamentTree.Node;

public class CurrentGameDialog extends JDialog {

	JButton submit;
	JTextArea playerOneScore;
	String playerOneName;
	String playerTwoName;
	JTextArea playerTwoScore;
	Node<Match> currentGame;
	Node<Match> siblingGame;
	Match currentMatch;
	Match siblingMatch;
	Player switcher;
	public static final int WIDTH = 300;
	public static final int HEIGHT = 200;

	public CurrentGameDialog(Node<Match> currentGame)
			throws InvalidActivityException {
		this.currentGame = currentGame;
		siblingGame = currentGame.getSibling();
		if (siblingGame != null) {
			siblingMatch = siblingGame.getMatch();
		}
		currentMatch = currentGame.getMatch();
		if (currentMatch.getPlayerOne() == null
				|| currentMatch.getPlayerTwo() == null) {
			if (currentMatch.getPlayerOne() != null) {
				initNonGameUI(currentMatch.getPlayerOne());
			} else if (currentMatch.getPlayerTwo() != null) {
				initNonGameUI(currentMatch.getPlayerTwo());
			}
		} else {
			initGameUI();
		}
	}

	private void initNonGameUI(final Player p) {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		GridLayout gl = new GridLayout(2, 2);
		gl.setHgap(5);
		gl.setVgap(5);

		JPanel panel = new JPanel();
		panel.setLayout(gl);
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT / 2));

		JLabel player = new JLabel();
		player.setHorizontalAlignment(SwingConstants.CENTER);
		if (player.getMouseListeners().length == 0) {
			player.addMouseListener(new GameClickListener(p));
		}
		JButton addPlayer = new JButton("Add a player to the match");
		addPlayer.setPreferredSize(new Dimension(30, 30));
		addPlayer.setHorizontalAlignment(SwingConstants.CENTER);
		addPlayer.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				addPlayerUI();
			}

		});

		panel.add(player);
		add(panel);

		if ((currentGame.hasPlayableChildren())
				|| (currentGame.getSide() == Side.BSIDE && currentGame
						.hasPlayableCousin())) {
			player.setText("<html><p>" + p.getName()
					+ " must wait until other games are played</p></html>");
		} else if (siblingMatch != null) {
			if (((siblingMatch.getPlayerOne() != null) != (siblingMatch

			.getPlayerTwo() != null)) && siblingMatch.getByePlayer() == null) {
				panel.add(addPlayer);
				player.setText(p.getName());
				if (siblingMatch.getPlayerOne() != null) {
					switcher = siblingMatch.getPlayerOne();
				} else if (siblingMatch.getPlayerTwo() != null) {
					switcher = siblingMatch.getPlayerTwo();
				}
				JButton yes = new JButton("<html><p>Move " + switcher.getName()
						+ " to this match");
				yes.setPreferredSize(new Dimension(30, 30));
				yes.setHorizontalAlignment(SwingConstants.CENTER);
				yes.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {
						if (p.equals(currentMatch.getPlayerOne())) {
							currentMatch.setPlayerTwo(switcher);
						} else if (p.equals(currentMatch.getPlayerTwo())) {
							currentMatch.setPlayerOne(switcher);
						}
						if (switcher.equals(siblingMatch.getPlayerOne())) {
							siblingMatch.setPlayerOne(null);
						} else if (switcher.equals(siblingMatch.getPlayerTwo())) {
							siblingMatch.setPlayerTwo(null);
						}
						dispose();
					}
				});

				JButton bye = new JButton("Give bye");
				bye.setPreferredSize(new Dimension(30, 30));
				bye.setHorizontalAlignment(SwingConstants.CENTER);
				bye.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {

						currentMatch.setByePlayer(p);
						dispose();
					}
				});
				panel.add(bye);
				panel.add(yes);

			} else {

				panel.add(addPlayer);
				player.setText("Give " + p.getName() + " a bye?");
				JButton yes = new JButton("Yes");
				yes.setPreferredSize(new Dimension(30, 30));
				yes.setHorizontalAlignment(SwingConstants.CENTER);
				yes.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {

						currentMatch.setByePlayer(p);
						dispose();
					}
				});

				JButton no = new JButton("No");
				no.setPreferredSize(new Dimension(30, 30));
				no.setHorizontalAlignment(SwingConstants.CENTER);
				no.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {

						dispose();
					}
				});

				panel.add(yes);
				panel.add(no);
			}
		} else {

			panel.add(addPlayer);
			player.setText("Give " + p.getName() + " a bye?");
			JButton yes = new JButton("Yes");
			yes.setPreferredSize(new Dimension(30, 30));
			yes.setHorizontalAlignment(SwingConstants.CENTER);
			yes.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {

					currentMatch.setByePlayer(p);
					dispose();
				}
			});

			JButton no = new JButton("No");
			no.setPreferredSize(new Dimension(30, 30));
			no.setHorizontalAlignment(SwingConstants.CENTER);
			no.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {

					dispose();
				}
			});

			panel.add(yes);
			panel.add(no);
		}

		setSize(new Dimension(WIDTH, HEIGHT));
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		setModalityType(ModalityType.APPLICATION_MODAL);

	}

	private void addPlayerUI() {
		getContentPane().removeAll();
		final JTextArea players = new JTextArea();
		players.setLineWrap(true);
		players.setPreferredSize(new Dimension(200, 100));
		players.setBorder(BorderFactory.createLineBorder(new Color(32, 23, 23),
				3));

		JButton addPlayer = new JButton("Add Player");
		add(players);
		add(addPlayer);
		SwingUtilities.updateComponentTreeUI(getContentPane());
		addPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String input = players.getText();
				String[] playerNames = input.split("\n|\\, |\\,");

				if (playerNames.length > 1) {
					new MessageDialog(
							"Cannot insert more than one player at a time.");
					dispose();
					return;
				}
				List<String> newPlayers = new ArrayList<String>();
				Player player = null;
				for (String s : playerNames) {
					boolean exists = false;
					for (Player p : MainView.eligablePlayers) {
						if (p.getName().equalsIgnoreCase(s)) {
							exists = true;
							player = p;
							break;
						}
					}
					if (!exists) {
						newPlayers.add(s);
					} else {
						MainView.playingPlayers.add(player);
					}
				}
				if (!newPlayers.isEmpty()) {
					NewPlayersDialog npd = new NewPlayersDialog(newPlayers);
					npd.setVisible(true);
					npd.setAlwaysOnTop(true);
					return;
				}

				if (currentMatch.getPlayerOne() == null) {
					currentMatch.setPlayerOne(player);
				} else if (currentMatch.getPlayerTwo() == null) {
					currentMatch.setPlayerTwo(player);
				}
				dispose();
			}
		});
	}

	private void initGameUI() throws InvalidActivityException {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		GridLayout gl = new GridLayout(3, 3);
		gl.setHgap(5);
		gl.setVgap(5);
		playerOneName = currentMatch.getPlayerOne().getName();
		playerTwoName = currentMatch.getPlayerTwo().getName();
		JLabel playerOne = new JLabel();
		JLabel playerTwo = new JLabel();
		if (MainView.doubles && currentGame.getSide() == Side.BSIDE) {
			playerOneName += " and " + playerTwoName;
			playerOne.addMouseListener(new GameClickListener(currentMatch
					.getPlayerOne(), currentMatch.getPlayerTwo()));
			if (siblingMatch == null) {
				new MessageDialog(
						"<html><p> No other game to play doubles with!</p><html>");
				dispose();
				return;
			}
			if (siblingMatch.getPlayerOne() != null
					&& siblingMatch.getPlayerTwo() != null) {
				playerTwoName = siblingMatch.getPlayerOne().getName() + " and "
						+ siblingMatch.getPlayerTwo().getName();
				playerTwo.addMouseListener(new GameClickListener(siblingMatch
						.getPlayerOne(), siblingMatch.getPlayerTwo()));
			} else if (siblingMatch.getPlayerOne() != null) {
				playerTwoName = siblingMatch.getPlayerOne().getName();
			} else if (siblingMatch.getPlayerTwo() != null) {
				playerTwoName = siblingMatch.getPlayerTwo().getName();
			}
			if ((siblingGame.hasPlayableChildren())
					|| siblingGame.hasPlayableCousin()) {
				playerOne.setText("<html><p>" + playerOneName
						+ " must wait until " + playerTwoName
						+ " feeding games are played</p></html>");
				add(playerOne);
				setSize(new Dimension(WIDTH, HEIGHT));
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setLocationRelativeTo(null);

				setModalityType(ModalityType.APPLICATION_MODAL);
				return;
			}

		}
		playerOne.setText(playerOneName);
		playerTwo.setText(playerTwoName);
		if (playerOne.getMouseListeners().length == 0) {
			playerOne.addMouseListener(new GameClickListener(currentMatch
					.getPlayerOne()));
		}
		playerOne.setHorizontalAlignment(SwingConstants.CENTER);
		if (playerTwo.getMouseListeners().length == 0) {
			playerTwo.addMouseListener(new GameClickListener(currentMatch
					.getPlayerTwo()));
		}
		playerTwo.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(gl);
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT / 2));
		panel.add(playerOne);
		panel.add(playerTwo);

		add(panel);
		KeyListener focusChanger = new KeyListener(this);
		playerOneScore = new JTextArea();
		playerOneScore.setBorder(BorderFactory.createLineBorder(new Color(32,
				23, 23), 3));
		playerOneScore.addKeyListener(focusChanger);
		playerTwoScore = new JTextArea();
		playerTwoScore.setBorder(BorderFactory.createLineBorder(new Color(32,
				23, 23), 3));
		playerTwoScore.addKeyListener(focusChanger);
		panel.add(playerOneScore);
		panel.add(playerTwoScore);

		add(Box.createRigidArea(new Dimension(0, 10)));
		submit = new JButton("Submit final Score");
		submit.setPreferredSize(new Dimension(30, 30));
		submit.setHorizontalAlignment(SwingConstants.CENTER);
		getRootPane().setDefaultButton(submit);
		submit.addKeyListener(focusChanger);
		submit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				if (playerOneScore.getText().isEmpty()) {
					playerOneScore.setText(playerOneName + "'s score?");
					return;
				}
				if (playerTwoScore.getText().isEmpty()) {
					playerTwoScore.setText(playerTwoName + "'s score?");
					return;
				}
				int pts = 0;
				int pos = 0;
				try {
					pos = Integer.parseInt(playerOneScore.getText());
				} catch (NumberFormatException e) {
					playerOneScore.setText("Please enter a number");
					return;
				}
				try {
					pts = Integer.parseInt(playerTwoScore.getText());
				} catch (NumberFormatException e) {
					playerTwoScore.setText("Please enter a number");
					return;
				}
				Integer.parseInt(playerTwoScore.getText());

				if (pos != 10 && pts != 10) {
					if (pos != 11 && pts != 0) {
						submit.setText("One player must score 10");
						return;
					}
				}
				if (pos == 10 && pts == 10) {
					submit.setText(" Only one player can score 10");
					return;
				}
				if (pos < 0 || pts < 0 || pos > 10 || pts > 10) {
					if (pos != 11 && pts != 0) {
						submit.setText("Scores must be between 0 and 10");
						return;
					}
				}
				if (pos == 10 && pts > 7) {
					submit.setText(playerTwoName
							+ "cannot have a higher score than 7");
					return;
				} else if (pts == 10 && pos > 7) {
					submit.setText(playerOneName
							+ "cannot have a higher score than 7");
					return;
				}
				Player winner = null;
				Player loser = null;
				if (MainView.doubles && currentGame.getSide() == Side.BSIDE) {
					// winner = new Player(currentMatch.getPlayerOne() + " and "
					// + currentMatch.getPlayerTwo());
					// loser = new Player(siblingMatch
					// .getPlayerOne()
					// + " and "
					// + siblingMatch
					// .getPlayerTwo());
					if (pos > pts) {
						winner = currentMatch.getPlayerOne();
						loser = siblingMatch.getPlayerOne();
						if (loser == null) {
							loser = siblingMatch.getPlayerTwo();
						}
						siblingMatch.setWinner(currentMatch.getPlayerTwo());
						siblingMatch.setLoser(siblingMatch.getPlayerTwo());
						currentMatch.setWinnerScore(pos);
						currentMatch.setLoserScore(pts);
						siblingMatch.setWinnerScore(pos);
						siblingMatch.setLoserScore(pts);
					} else if (pts > pos) {
						winner = siblingMatch.getPlayerOne();
						if (winner == null) {
							winner = siblingMatch.getPlayerTwo();
						}
						loser = currentMatch.getPlayerOne();
						siblingMatch.setWinner(siblingMatch.getPlayerTwo());
						siblingMatch.setLoser(currentMatch.getPlayerTwo());
						currentMatch.setWinnerScore(pts);
						currentMatch.setLoserScore(pos);
						siblingMatch.setWinnerScore(pts);
						siblingMatch.setLoserScore(pos);
					}
					currentMatch.setWinner(winner);
					currentMatch.setLoser(loser);

				} else {
					if (pos > pts) {
						winner = currentMatch.getPlayerOne();
						loser = currentMatch.getPlayerTwo();
						currentMatch.setWinner(winner);
						currentMatch.setWinnerScore(pos);
						currentMatch.setLoser(loser);
						currentMatch.setLoserScore(pts);
					} else if (pts > pos) {
						winner = currentMatch.getPlayerTwo();
						loser = currentMatch.getPlayerOne();
						currentMatch.setWinner(winner);
						currentMatch.setWinnerScore(pts);
						currentMatch.setLoser(loser);
						currentMatch.setLoserScore(pos);
					}
				}

				System.out.println("\n***FINISHED GAME***\n\n"
						+ winner.getName() + "  "
						+ currentMatch.getWinnerScore() + loser.getName()
						+ "  " + currentMatch.getLoserScore());
				dispose();
			}
		});
		submit.setAlignmentX(0.5f);
		add(submit);

		setTitle("Current Game: " + currentMatch.getPlayerOne().getName()
				+ " vs " + currentMatch.getPlayerTwo().getName());
		setSize(new Dimension(WIDTH, HEIGHT));
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		setModalityType(ModalityType.APPLICATION_MODAL);
	}

	private class GameClickListener extends ClickListener {
		JMenuItem removePlayerOne;
		JMenuItem removePlayerTwo;
		Player playerOne;
		Player playerTwo;

		protected GameClickListener(Player playerOne) {
			this.playerOne = playerOne;
			removePlayerOne = new JMenuItem("remove " + playerOne.getName());
			removePlayerOne.addMouseListener(this);
		}

		protected GameClickListener(Player playerOne, Player playerTwo) {
			this.playerOne = playerOne;
			removePlayerOne = new JMenuItem("remove " + playerOne.getName());
			removePlayerTwo = new JMenuItem("remove " + playerTwo.getName());
			removePlayerOne.addMouseListener(this);
			removePlayerTwo.addMouseListener(this);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			check(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			check(e);
		}

		private void check(MouseEvent e) {
			if (e.isPopupTrigger()) {
				JPopupMenu popUp = new JPopupMenu();
				popUp.add(removePlayerOne);
				if (removePlayerTwo != null) {
					popUp.add(removePlayerTwo);
				}
				popUp.show(e.getComponent(), e.getX(), e.getY());
			} else if (e.getSource() == removePlayerOne) {
				remove(playerOne);
				dispose();
			} else if (e.getSource() == removePlayerTwo) {
				remove(playerTwo);
				dispose();
			}
		}

		private void remove(Player player) {
			if (player.equals(currentMatch.getPlayerOne())) {
				currentMatch.setPlayerOne(null);
			} else if (player.equals(currentMatch.getPlayerTwo())) {
				currentMatch.setPlayerTwo(null);
			}
			player.hasLeft = true;
			MainView.playingPlayers.remove(player);
		}
	}
}
