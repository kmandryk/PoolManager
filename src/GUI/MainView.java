package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activity.InvalidActivityException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import startup.Start;
import tournament.Tournament;
import tournament.Tournament.Side;
import API.Match;
import API.Player;
import API.TournamentTree.Node;
import API.TourneyNight;
import DataHandle.ReadExcel;
import DataHandle.WriteExcel;

public class MainView extends JFrame implements ActionListener {

	JFrame frame;
	JPanel panel;
	JPanel endPanel;
	JButton startTourney;
	JButton importTourney;
	JLabel label;
	JTextArea players;
	public static Set<Player> eligablePlayers;
	public static Set<Player> playingPlayers;
	private int thisWeek;
	Tournament tournament;
	Map<String, Node<Match>> buttonToGame;
	public static boolean doubles;
	String bSideMatchConjunctive;

	private KeyListener focusChanger;

	final Color white = new Color(255, 255, 255);
	final Dimension buttonDimension = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
	final Dimension dialogDimension = new Dimension(500, 300);

	private static int WIDTH = 1200;
	private static int HEIGHT = 800;
	private static int BUTTON_WIDTH = 200;
	private static int BUTTON_HEIGHT = 40;

	public enum Source {
		BUTTON
	}

	public MainView(int thisWeek) {
		this.thisWeek = thisWeek;
		playingPlayers = new HashSet<Player>();
		buttonToGame = new HashMap<String, Node<Match>>();
		panel = new JPanel();
		label = new JLabel();
		getContentPane().add(panel);
		focusChanger = new KeyListener(this);
		setButtons();
		panel.add(startTourney);
		panel.add(importTourney);
		panel.add(label);
		setLocationRelativeTo(null);
	}

	private void setButtons() {
		startTourney = new JButton("Start Tournament");
		startTourney.addActionListener(this);
		startTourney.addKeyListener(focusChanger);
		startTourney.setVisible(true);

		importTourney = new JButton("Import Tournament");
		importTourney.addActionListener(this);
		importTourney.addKeyListener(focusChanger);
		importTourney.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == startTourney) {
			if(Start.file == null || !Start.fileWasRead){
				new MessageDialog("You must select an xls file for the tournament.");
			} else{
			tourneyInitView();
			}
		} else if (event.getSource() == importTourney) {
			FileChooserDialog fcd = new FileChooserDialog();
			if(Start.file == null){
				new MessageDialog("You must select an xls file for the tournament.");
			} else{
			ReadExcel read = new ReadExcel();
			MainView.eligablePlayers = read.read();
			}
		}

	}

	public void tourneyInitView() {

		getContentPane().removeAll();
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		add(Box.createRigidArea(new Dimension(0, 50)));
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setLayout(new GridLayout(1, 2, 5, 5));
		players = new JTextArea();
		players.addKeyListener(focusChanger);
		players.setLineWrap(true);
		players.setPreferredSize(new Dimension(200, 100));
		players.setBorder(BorderFactory.createLineBorder(new Color(32, 23, 23),
				3));
		JScrollPane scroll = new JScrollPane(players);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scroll);
		JLabel comment = new JLabel(
				"<html><p>Please enter the names of the players "
						+ "in the tournament,"
						+ " seperated by a new line or comma</p></html>");
		comment.setPreferredSize(new Dimension(100, 30));
		panel.add(comment);

		add(panel);
		add(Box.createRigidArea(new Dimension(0, 50)));

		JButton start = new JButton("Start Tourney");
		start.addKeyListener(focusChanger);
		start.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				String input = players.getText();
				String[] playerNames = input.split("\n|\\, |\\,");
				List<String> newPlayers = new ArrayList<String>();
				Player player = null;
				for (String s : playerNames) {
					boolean exists = false;
					for (Player p : eligablePlayers) {
						if (p.getName().equalsIgnoreCase(s)) {
							exists = true;
							player = p;
							break;
						}
					}
					if (!exists) {
						newPlayers.add(s);
					} else {
						playingPlayers.add(player);
					}
				}
				if (!newPlayers.isEmpty()) {
					NewPlayersDialog npd = new NewPlayersDialog(newPlayers);
					npd.setVisible(true);
					npd.setAlwaysOnTop(true);
					return;
				}
				if (tournament == null) {
					tournament = new Tournament(thisWeek, playingPlayers);
				}
				tourneyView();
			}
		});

		start.setAlignmentX(0.5f);
		add(start);

		setTitle("Tournament Setup");
		setSize(100, 200);
		pack();
	}

	public void tourneyView() {
		bSideMatchConjunctive = doubles ? " and " : " vs ";
		getContentPane().removeAll();
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		int rounds = Tournament.findExponentBase2(playingPlayers.size());

		List<Node<Match>> aSidePlayableGames = tournament.getaSide()
				.getAllPlayableGames();
		List<Node<Match>> bSidePlayableGames = tournament.getbSide()
				.getAllPlayableGames();

		if (aSidePlayableGames.isEmpty() && bSidePlayableGames.isEmpty()) {
			Node<Match> finalGame = tournament.getfinalGame();
			if (!tournament.isFinished()) {
				JLabel finalLabel = new JLabel("FINAL GAME");
				finalLabel.setFont(new Font(null, Font.BOLD, 30));
				add(finalLabel);
				layout.putConstraint(SpringLayout.WEST, finalLabel, 10,
						SpringLayout.WEST, this.getContentPane());
				layout.putConstraint(SpringLayout.NORTH, finalLabel, 15,
						SpringLayout.NORTH, this.getContentPane());
				String buttonName = finalGame.getMatch().getPlayerOne()
						.getName()
						+ " vs "
						+ finalGame.getMatch().getPlayerTwo().getName();
				buttonToGame.put(buttonName, finalGame);

				JButton button = new JButton(buttonName);
				button.addKeyListener(focusChanger);
				button.addMouseListener(new ButtonClickListener(finalGame));
				button.addActionListener(new ButtonListener());
				add(button);

				layout.putConstraint(SpringLayout.WEST, button, 10,
						SpringLayout.WEST, this.getContentPane());
				layout.putConstraint(SpringLayout.NORTH, button, 15,
						SpringLayout.SOUTH, finalLabel);
			} else {
				JLabel winnerLabel = new JLabel("Congratulations "
						+ finalGame.getMatch().getWinner().getName() + "!");
				winnerLabel.setFont(new Font(null, Font.BOLD, 30));
				add(winnerLabel);
				layout.putConstraint(SpringLayout.WEST, winnerLabel, 10,
						SpringLayout.WEST, this.getContentPane());
				layout.putConstraint(SpringLayout.NORTH, winnerLabel, 15,
						SpringLayout.NORTH, this.getContentPane());

				JButton close = new JButton("Close");
				close.addKeyListener(focusChanger);
				close.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) {
						playingPlayers.addAll(eligablePlayers);
						WriteExcel write = new WriteExcel();
						write.writeWeekly(playingPlayers);
						dispose();
					}
				});

				add(close);
				layout.putConstraint(SpringLayout.WEST, close, 30,
						SpringLayout.WEST, this.getContentPane());
				layout.putConstraint(SpringLayout.NORTH, close, 30,
						SpringLayout.SOUTH, winnerLabel);

			}

		} else {
			int ASideSize = (int) Math.pow(2, rounds) + 1;
			JPanel mainPanel = new JPanel();
			// mainPanel.setPreferredSize(new Dimension(1600, 800));
			mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			mainPanel.setLayout(layout);
			mainPanel.setPreferredSize(new Dimension(BUTTON_WIDTH
					* (rounds + 8), BUTTON_HEIGHT * (2 * ASideSize)));
			setLayout(new BorderLayout());
			JScrollPane scroll = new JScrollPane(mainPanel,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scroll.getVerticalScrollBar().setUnitIncrement(15);
			add(scroll, BorderLayout.CENTER);
			JLabel aSideLabel = new JLabel("A Side Games");
			aSideLabel.setFont(new Font(null, Font.BOLD, 20));
			mainPanel.add(aSideLabel);
			layout.putConstraint(SpringLayout.WEST, aSideLabel, 10,
					SpringLayout.WEST, this.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, aSideLabel, 15,
					SpringLayout.NORTH, this.getContentPane());

			JPanel aPanel = new JPanel();
			aPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			aPanel.setLayout(new GridLayout(1, rounds, 5, 0));
			layout.putConstraint(SpringLayout.WEST, aPanel, 5,
					SpringLayout.WEST, this.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, aPanel, 20,
					SpringLayout.NORTH, aSideLabel);
			mainPanel.add(aPanel);
			int spacing = 0;
			for (int x = 1; x <= rounds; x++) {
				List<Node<Match>> aSideRoundGames = tournament.getaSide()
						.getRound(x);
				JPanel panel = new JPanel();
				panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				panel.setLayout(new GridLayout(ASideSize, 1, 5, 5));
				aPanel.add(panel);
				if (x > 1) {
					spacing += Math.pow(2, (x - 2));
				}
				for (int y = 0; y < spacing; y++) {
					panel.add(new JLabel(""));
				}
				Iterator<Node<Match>> aSideIterator = aSideRoundGames
						.iterator();
				Node<Match> game = null;
				while (aSideIterator.hasNext()) {
					game = aSideIterator.next();
					String buttonName = "";
					Match match = game.getMatch();
					if (match.getPlayerOne() != null
							|| match.getPlayerTwo() != null) {
						if (match.getPlayerOne() != null) {
							if (match.getPlayerTwo() != null) {
								if (match.getWinner() != null) {
									buttonName = "<u>"
											+ match.getWinner().getName()
													.toUpperCase() + "</u> ("
											+ match.getWinnerScore()
											+ ") beats "
											+ match.getLoser().getName() + "("
											+ match.getLoserScore() + ")";
								} else {
									buttonName = match.getPlayerOne().getName()
											+ " vs "
											+ match.getPlayerTwo().getName();
								}
							} else {
								buttonName = match.getPlayerOne().getName();
							}
						} else if (match.getPlayerTwo() != null) {
							buttonName = match.getPlayerTwo().getName();
						}
						if (match.getByePlayer() == null) {
							buttonName = "<html><p>Round " + game.getRound()
									+ " : " + buttonName + "</p></html>";
							if (match.getWinner() == null) {
								buttonToGame.put(buttonName, game);
								JButton button = new JButton(buttonName);
								button.setPreferredSize(buttonDimension);
								button.addMouseListener(new ButtonClickListener(
										game));
								button.addKeyListener(focusChanger);
								button.addActionListener(new ButtonListener());
								panel.add(button);
							} else {
								panel.add(new JLabel(buttonName));
							}
						} else {
							JLabel label = new JLabel(buttonName
									+ " was given a bye");
							panel.add(label);
						}
					} else {
						JLabel label = new JLabel("Round " + game.getRound()
								+ " : " + "No Players");
						panel.add(label);
					}
					if (aSideIterator.hasNext()) {
						for (int y = 0; y < (spacing * 2 + 1); y++) {
							JLabel label = new JLabel("");
							label.setBackground(white);
							panel.add(label);
						}
					}
				}
			}
			JLabel bSideLabel = new JLabel("B Side Games");
			bSideLabel.setFont(new Font(null, Font.BOLD, 20));
			mainPanel.add(bSideLabel);
			layout.putConstraint(SpringLayout.WEST, bSideLabel, 10,
					SpringLayout.WEST, this.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, bSideLabel, 5,
					SpringLayout.SOUTH, aPanel);

			JPanel bPanel = new JPanel();
			bPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			bPanel.setLayout(new GridLayout(1, rounds, 5, 5));
			layout.putConstraint(SpringLayout.WEST, bPanel, 5,
					SpringLayout.WEST, this.getContentPane());
			layout.putConstraint(SpringLayout.NORTH, bPanel, 20,
					SpringLayout.SOUTH, bSideLabel);
			mainPanel.add(bPanel);

			spacing = 0;
			for (int x = 1; x <= rounds; x++) {
				List<Node<Match>> bSideRoundGames = tournament.getbSide()
						.getRound(x);
				Iterator<Node<Match>> bSideIterator = bSideRoundGames
						.iterator();

				Node<Match> game = null;

				JPanel panelOne = new JPanel();
				panelOne.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				panelOne.setLayout(new GridLayout(
						(playingPlayers.size() * 3 + 1), 1, 5, 5));
				bPanel.add(panelOne);

				for (int y = 0; y < spacing; y++) {
					JLabel label = new JLabel("");
					label.setBackground(white);
					panelOne.add(label);
				}

				List<Node<Match>> parentGames = new ArrayList<Node<Match>>();
				while (bSideIterator.hasNext()) {
					game = bSideIterator.next();

					if (bSideRoundGames.contains(game.getLeftChild())) {
						parentGames.add(game);

					} else {

						addToBSideGrid(game, spacing, panelOne);
						for (int y = 0; y < (spacing * 2 + 1); y++) {
							JLabel label = new JLabel("");
							label.setBackground(white);
							panelOne.add(label);
						}
					}
				}

				spacing += Math.pow(2, (x - 2));
				if (x > 1) {

					JPanel panelTwo = new JPanel();
					panelTwo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,
							5));
					panelTwo.setLayout(new GridLayout(
							(playingPlayers.size() * 3 + 1), 1, 5, 5));
					bPanel.add(panelTwo);

					if (x == rounds) {
						// last round the spacing should be the same as
						// the previous round
						spacing = (spacing - 1) / 2;
					}
					for (int y = 0; y < spacing; y++) {
						JLabel label = new JLabel("");
						label.setBackground(white);
						panelTwo.add(label);
					}

					// second column of round
					for (Node<Match> parentGame : parentGames) {
						addToBSideGrid(parentGame, spacing, panelTwo);
						for (int y = 0; y < (spacing * 2 + 1); y++) {
							JLabel label = new JLabel("");
							label.setBackground(white);
							panelTwo.add(label);
						}
					}
				}
			}
			createEndPanel();
		}
		setTitle("Match Manager");
		setSize(WIDTH, HEIGHT);
	}

	private void addToBSideGrid(Node<Match> game, int spacing, JPanel panel) {
		String buttonName = "";
		Match match = game.getMatch();
		if (match.getPlayerOne() != null || match.getPlayerTwo() != null) {
			if (match.getPlayerOne() != null) {
				if (match.getPlayerTwo() != null) {
					if (match.getWinner() != null) {
						buttonName = "<u>"
								+ match.getWinner().getName().toUpperCase()
								+ "</u> (" + match.getWinnerScore()
								+ ") beats " + match.getLoser().getName() + "("
								+ match.getLoserScore() + ")";
					} else {
						buttonName = match.getPlayerOne().getName()
								+ bSideMatchConjunctive
								+ match.getPlayerTwo().getName();
					}
				} else {
					buttonName = match.getPlayerOne().getName();
				}
			} else if (match.getPlayerTwo() != null) {
				buttonName = match.getPlayerTwo().getName();
			}
			buttonName = "<html><p>Round " + game.getRound() + " : "
					+ buttonName + "</p></html>";
			if (match.getByePlayer() == null) {
				if (match.getWinner() == null) {
					buttonToGame.put(buttonName, game);
					JButton button = new JButton(buttonName);
					button.addMouseListener(new ButtonClickListener(game));
					button.addKeyListener(focusChanger);
					button.setPreferredSize(buttonDimension);
					button.addActionListener(new ButtonListener());
					panel.add(button);
				} else {
					panel.add(new JLabel(buttonName));
				}
			} else {
				JLabel label = new JLabel(buttonName + " was given a bye");
				panel.add(label);
			}
		} else {
			JLabel label = new JLabel("Round " + game.getRound() + " : "
					+ "No Players");
			panel.add(label);
		}
	}

	private void createEndPanel() {
		if (endPanel == null) {
			endPanel = new JPanel();
			endPanel.setLayout(new GridLayout(10, 1, 0, 10));
			endPanel.setBorder(BorderFactory.createLineBorder(new Color(10, 90,
					30), 5));
			add(endPanel, BorderLayout.LINE_END);

			JButton close = new JButton(
					"<html><p>End tournament without saving</p><html>");
			close.setPreferredSize(new Dimension(150, 0));
			close.addKeyListener(focusChanger);
			close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					dispose();
				}
			});
			endPanel.add(close);

			JButton finish = new JButton(
					"<html><p>Finish tournament early</p></html>");

			finish.addKeyListener(focusChanger);
			finish.setPreferredSize(new Dimension(150, 0));
			finish.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					playingPlayers.addAll(eligablePlayers);
					WriteExcel write = new WriteExcel();
					write.writeWeekly(playingPlayers);
					dispose();
				}
			});
			endPanel.add(finish);

			JButton playDoubles = new JButton(
					(doubles) ? "<html><p>Split Doubles</p></html>"
							: "<html><p>Play Doubles</p></html>");
			playDoubles.setPreferredSize(new Dimension(150, 0));
			playDoubles.addKeyListener(focusChanger);
			playDoubles.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					doubles = !doubles;
					if (doubles)
						((JButton) event.getSource())
								.setText("<html><p>Split Doubles</p></html>");
					else {
						((JButton) event.getSource())
								.setText("<html><p>Play Doubles</p></html>");

					}
					createEndPanel();

				}
			});
			endPanel.add(playDoubles);

			JButton insertPlayer = new JButton(
					"<html><p>Insert a player</p></html>");
			insertPlayer.setPreferredSize(new Dimension(150, 0));
			insertPlayer.addKeyListener(focusChanger);
			insertPlayer.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					JDialog d = new JDialog();
					d.setLayout(new BoxLayout(d.getContentPane(),
							BoxLayout.Y_AXIS));
					d.setSize(dialogDimension);
					d.setVisible(true);
					d.add(Box.createRigidArea(new Dimension(0, 50)));
					JPanel panel = new JPanel();
					panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
					panel.setLayout(new GridLayout(1, 2, 5, 5));
					players = new JTextArea();
					players.addKeyListener(focusChanger);
					players.setLineWrap(true);
					players.setPreferredSize(new Dimension(200, 100));
					players.setBorder(BorderFactory.createLineBorder(new Color(
							32, 23, 23), 3));
					JScrollPane scroll = new JScrollPane(players);
					scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					panel.add(scroll);
					JLabel comment = new JLabel(
							"<html><p>Please enter the names of the players "
									+ "to be entered in the tournament,"
									+ " seperated by a new line or comma</p></html>");
					comment.setPreferredSize(new Dimension(100, 30));
					panel.add(comment);

					d.add(panel);
					d.add(Box.createRigidArea(new Dimension(0, 50)));
					JButton submit = new JButton("Submit");
					submit.setAlignmentX(0.5f);
					d.add(submit);
					d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					final JDialog finalDialog = d;
					submit.addKeyListener(focusChanger);

					submit.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent event) {
							String input = players.getText();
							String[] playerNames = input.split("\n|\\, |\\,");

							List<String> newPlayers = new ArrayList<String>();
							List<Player> insertPlayers = new ArrayList<Player>();
							Player player = null;
							for (String s : playerNames) {
								boolean exists = false;
								for (Player p : eligablePlayers) {
									if (p.getName().equalsIgnoreCase(s)) {
										exists = true;
										player = p;
										break;
									}
								}
								if (!exists) {
									newPlayers.add(s);
									insertPlayers.add(new Player(s));
								} else {
									insertPlayers.add(player);
									playingPlayers.add(player);
								}
							}
							if (!newPlayers.isEmpty()) {
								NewPlayersDialog npd = new NewPlayersDialog(
										newPlayers);
								npd.setVisible(true);
								npd.setAlwaysOnTop(true);
								return;
							}

							List<Node<Match>> aSideGameList = tournament
									.getRound(tournament.getaSide(), 1);

							Iterator<Node<Match>> gameListIterator = aSideGameList
									.iterator();
							Node<Match> game = null;

							// randomize player entry
							Collections.shuffle(insertPlayers);
							Iterator<Player> pIterator = insertPlayers
									.iterator();
							boolean playableGames = false;
							List<Node<Match>> insertableGames = new ArrayList<Node<Match>>();
							List<Node<Match>> byeGames = new ArrayList<Node<Match>>();
							while (pIterator.hasNext()) {
								Match match = null;
								if (gameListIterator.hasNext()) {
									gameIterator: while (gameListIterator
											.hasNext()) {
										game = gameListIterator.next();
										match = game.getMatch();
										if (!playableGames) {
											playableGames = (match.getWinner() == null) ? true
													: false;
										}

										if (game.getMatch().getPlayerOne() != null
												&& game.getMatch()
														.getPlayerTwo() != null) {
											continue gameIterator;
										} else if (((game.getMatch()
												.getPlayerOne() == null) != (game
												.getMatch().getPlayerTwo() == null))
												&& (game.getMatch().getWinner() == null && game
														.getMatch()
														.getByePlayer() == null)) {
											insertableGames.add(game);
										} else if (game.getMatch()
												.getByePlayer() != null) {
											if (game.getParent()
													.getMatch()
													.getPlayerOne()
													.equals(game.getMatch()
															.getByePlayer())) {
												game.getParent().getMatch()
														.setPlayerOne(null);
											} else if (game
													.getParent()
													.getMatch()
													.getPlayerTwo()
													.equals(game.getMatch()
															.getByePlayer())) {
												game.getParent().getMatch()
														.setPlayerTwo(null);
											}
											game.getMatch().setByePlayer(null);
											insertableGames.add(game);
											// byeGames.add(game.getCousin());
										}
									}
									if (!playableGames) {
										int available = byeGames.size()
												+ insertableGames.size()
												- insertPlayers.size();
										if (available < 0) {
											if (byeGames.size()
													+ insertableGames.size() == 0) {
												MessageDialog md = new MessageDialog(
														" No spaces available for new players ");
											} else {
												MessageDialog md = new MessageDialog(
														" Not enough available spaces to enter players, only "
																+ byeGames
																		.size()
																+ insertableGames
																		.size()
																+ " spots available");
											}
											return;
										}
									}
									if (!insertableGames.isEmpty()) {

										boolean doLow = (playingPlayers.size() % 2 == 0) ? true
												: false;
										tournament.getaSide().insertPlayers(
												insertPlayers, doLow);
										pIterator = insertPlayers.iterator();
									}
									if (pIterator.hasNext()) {
										if (playableGames) {
											// if bside cousins have bye
											// players,
											// room for inserted players, else
											// cannot insert players
											tournament.expandBracket();
											this.actionPerformed(null);
											finalDialog.dispose();
											tourneyView();
											return;
										}
									}
									/**
									 * inserting on BSide
									 */
									// if (pIterator.hasNext()) {
									//
									// if (!byeGames.isEmpty()) {
									//
									// String s = null;
									// for (Player pl : insertPlayers) {
									// s += pl.getName() + " - ";
									// }
									// MessageDialog md = new MessageDialog(
									// "Enter the following players on BSide?\n"
									// + s, true);
									// if (md.isConfirmed()) {
									// tournament.getbSide()
									// .startPlayers(
									// insertPlayers);
									// tournament.getbSide()
									// .insertPlayers(
									// insertPlayers,
									// false);
									// pIterator = insertPlayers
									// .iterator();
									// }
									// }
									// }
									if (pIterator.hasNext()) {
										new MessageDialog(
												"Players could not be allocated");
										break;
									}
								}

							}
							finalDialog.dispose();
							tourneyView();
							SwingUtilities
									.updateComponentTreeUI(getContentPane());

						}
					});
				}
			});
			endPanel.add(insertPlayer);
			for (int y = 0; y < 6; y++) {
				endPanel.add(new JLabel(""));
			}
		} else {
			add(endPanel, BorderLayout.LINE_END);
		}
	}

	public class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			Node<Match> game = buttonToGame.get(event.getActionCommand());
			Match match = game.getMatch();
			try {
				CurrentGameDialog cgd = new CurrentGameDialog(game);
				cgd.setVisible(true);
				cgd.setAlwaysOnTop(true);
			} catch (InvalidActivityException iae) {
				// TODO: Error dialog
				System.out.println(iae.getMessage());
				iae.printStackTrace();
			}
			if (match.getByePlayer() == null && match.getWinner() != null) {
				tournament.finishGame(game);
			} else if (match.getByePlayer() != null) {
				if (game == tournament.getaSide().getRoot()) {
					tournament.getfinalGame().getMatch()
							.setPlayerOne(match.getByePlayer());
				} else if (game == tournament.getbSide().getRoot()) {
					tournament.getfinalGame().getMatch()
							.setPlayerTwo(match.getByePlayer());
				} else {

					Match parentMatch = game.getParent().getMatch();
					if (parentMatch.getPlayerOne() == null) {
						parentMatch.setPlayerOne(match.getByePlayer());
					} else if (parentMatch.getPlayerTwo() == null) {
						parentMatch.setPlayerTwo(match.getByePlayer());
					}
				}
			}
			tourneyView();
			SwingUtilities.updateComponentTreeUI(getContentPane());
		}
	}

	private class ButtonClickListener extends ClickListener {
		JMenuItem doRevert;
		Node<Match> game;

		protected ButtonClickListener(Node<Match> game) {
			this.game = game;
			doRevert = new JMenuItem("Revert to previous games");
			doRevert.addMouseListener(this);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			check(e);
		}

		private void check(MouseEvent e) {
			Match match1 = null;
			Match match2 = null;
			Node<Match> childGame = null;
			Player playerOne = game.getMatch().getPlayerOne();
			Player playerTwo = game.getMatch().getPlayerTwo();
			if (e.isPopupTrigger() || e.getSource() == doRevert) {
				if (game.getLeftChild() != null) {
					if (playerOne != null) {
						if (playerOne.equals(game.getLeftChild().getMatch()
								.getPlayerOne())
								|| playerOne.equals(game.getLeftChild()
										.getMatch().getPlayerTwo())) {
							childGame = game.getLeftChild();
							match1 = childGame.getMatch();

						} else if (game.getRightChild() != null) {
							if (playerOne.equals(game.getRightChild()

							.getMatch().getPlayerOne())
									|| playerOne.equals(game.getRightChild()
											.getMatch().getPlayerTwo())) {
								childGame = game.getRightChild();
								match1 = childGame.getMatch();
							}
						}

					}
					if (playerTwo != null) {
						if (playerTwo.equals(game.getLeftChild().getMatch()
								.getPlayerOne())
								|| playerTwo.equals(game.getLeftChild()
										.getMatch().getPlayerTwo())) {
							childGame = game.getLeftChild();
							match2 = childGame.getMatch();

						} else if (game.getRightChild() != null) {
							if (playerTwo.equals(game.getRightChild()
									.getMatch().getPlayerOne())
									|| playerTwo.equals(game.getRightChild()
											.getMatch().getPlayerTwo())) {
								childGame = game.getRightChild();
								match2 = childGame.getMatch();
							}
						}

					}
					Match cousinMatch = null;
					if (game.getSide() == Side.ASIDE) {
						cousinMatch = childGame.getCousin().getMatch();

						if (cousinMatch.getWinner() == null) {
							if (e.isPopupTrigger()) {
								JPopupMenu popUp = new JPopupMenu();
								popUp.add(doRevert);
								popUp.show(e.getComponent(), e.getX(), e.getY());
								return;
							}

							else if (e.getSource() == doRevert) {
								if (match1 != null) {
									if (match1.getByePlayer() == null) {
										if (match1.getLoser().equals(
												cousinMatch.getPlayerOne())) {
											cousinMatch.setPlayerOne(null);
										} else if (match1.getLoser().equals(
												cousinMatch.getPlayerTwo())) {
											cousinMatch.setPlayerTwo(null);
										}
									}
								}
								if (match2 != null) {
									if (match2.getByePlayer() == null) {
										if (match2.getLoser().equals(
												cousinMatch.getPlayerOne())) {
											cousinMatch.setPlayerOne(null);
										} else if (match2.getLoser().equals(
												cousinMatch.getPlayerTwo())) {
											cousinMatch.setPlayerTwo(null);
										}
									}
								}

								if (match1 != null) {
									resetMatch(match1, game.getSide());
								}
								if (match2 != null) {
									resetMatch(match2, game.getSide());
								}
								game.setMatch(new Match());
								tourneyView();
							}
						}
					}

					else if (game.getSide() == Side.BSIDE) {
						if (e.isPopupTrigger()) {
							JPopupMenu popUp = new JPopupMenu();
							popUp.add(doRevert);
							popUp.show(e.getComponent(), e.getX(), e.getY());
							return;
						} else if (e.getSource() == doRevert) {
							if (game.getCousin() != null) {
								cousinMatch = game.getCousin().getMatch();
								if (match1 == null) {
									match1 = cousinMatch;
								} else if (match2 == null) {
									match2 = cousinMatch;
								}

							}

							if (match1 != null) {
								resetMatch(match1, game.getSide());
							}
							if (match2 != null) {
								resetMatch(match2, game.getSide());
							}
							game.setMatch(new Match());
						}
						tourneyView();
						System.out
								.println("end of bside reset; tourney view called");

					} else if (game.getSide() == Side.FINAL) {
						if (e.isPopupTrigger()) {
							JPopupMenu popUp = new JPopupMenu();
							popUp.add(doRevert);
							popUp.show(e.getComponent(), e.getX(), e.getY());
							return;
						} else if (e.getSource() == doRevert) {
							if (match1 != null) {
								resetMatch(match1, game.getLeftChild()
										.getSide());
							}
							game.setMatch(new Match());
							tourneyView();
						}
					}
				}

				SwingUtilities.updateComponentTreeUI(getContentPane());
				return;
			}

		}

		private void resetMatch(Match match, Side side) {
			if (match.getByePlayer() == null) {
				Player winner = null;
				Player loser = null;
				TourneyNight tn = null;
				winner = match.getWinner();
				if (winner != null) {
					tn = winner.getWeekly().get(thisWeek);
					tn.setGamesPlayed(tn.getGamesPlayed() - 1);
					tn.setBallsPocketed(tn.getBallsPocketed()
							- match.getWinnerScore());
					tn.setHandicapPoints(tn.getHandicapPoints()
							- match.getWinnerHandicapPoints());
					if (side == Side.ASIDE) {
						tn.setBasicPoints(tn.getBasicPoints() - 3);
					} else {
						tn.setBasicPoints(tn.getBasicPoints() - 2);
					}
				}
				loser = match.getLoser();
				if (loser != null) {
					tn = loser.getWeekly().get(thisWeek);
					tn.setGamesPlayed(tn.getGamesPlayed() - 1);
					tn.setBallsPocketed(tn.getBallsPocketed()
							- match.getLoserScore());
					tn.setHandicapPoints(tn.getHandicapPoints()
							- match.getLoserHandicapPoints());
					tn.setBasicPoints(tn.getBasicPoints() - 1);
				}
				match.setLoserScore(0);
				match.setWinnerScore(0);
				match.setWinner(null);
				match.setLoser(null);
				match.setLoserHandicapPoints(0);
				match.setWinnerHandicapPoints(0);
				String s = "";

				if (match.getPlayerOne() != null) {
					s += match.getPlayerOne().getName()
							+ " handicap: "
							+ match.getPlayerOne().getHandicap()
							+ " total handicap points: "
							+ match.getPlayerOne().getWeekly().get(thisWeek)
									.getHandicapPoints();
				}
				if (match.getPlayerTwo() != null) {
					s += "  "
							+ match.getPlayerTwo().getName()
							+ " handicap : "
							+ match.getPlayerTwo().getHandicap()
							+ " total handicap points: "
							+ match.getPlayerTwo().getWeekly().get(thisWeek)
									.getHandicapPoints();
				}
				System.out.println("***RESET MATCH***\n\n" + s + "\n");
			} else {
				match.setByePlayer(null);
			}
		}
	}
}
