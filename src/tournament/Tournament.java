package tournament;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import API.Match;
import API.Player;
import API.TournamentTree;
import API.TournamentTree.Node;
import API.TourneyNight;
import GUI.MainView;

public class Tournament {
	Node<Match> finalGame;
	TournamentTree aSide;
	List<Node<Match>> aSideRound;
	TournamentTree bSide;
	List<Node<Match>> bSideRound;
	List<Node<Match>> transferableBSideRound;
	List<Match> currentGames;
	Set<Player> playerSet;
	boolean finished;
	public static int thisWeek;
	int rounds;

	public enum Side {
		ASIDE, BSIDE, FINAL
	}

	public Tournament(int thisWeek, Set<Player> playerList) {
		finalGame = new Node<Match>();
		finalGame.setSide(Side.FINAL);
		aSide = new TournamentTree(Side.ASIDE);
		bSide = new TournamentTree(Side.BSIDE);
		finalGame.setLeftChild(aSide.getRoot());
		finalGame.setRightChild(bSide.getRoot());
		currentGames = new ArrayList<Match>();
		Tournament.thisWeek = thisWeek;
		this.playerSet = playerList;
		int numberOfPlayers = playerList.size();
		rounds = findExponentBase2(numberOfPlayers);
		aSide.generateATree(rounds);
		bSide.generateBTree(rounds);
		for (int x = 1; x <= rounds; x++) {
			aSideRound = aSide.getRound(x);
			bSideRound = bSide.getRound(x);
			if (x < rounds) {
				setSiblings(x);
			}
			setCousins(x);
		}
		startASide(playerList);
		System.out.println(aSide.printTree(aSide.getRoot()));

		// test();
	}

	public void test() {
		System.out.println("test ASide round 1\n");
		for (int x = 0; x < 4; x++) {
			testASide();
		}
		System.out.println("test BSide round 1\n");
		for (int x = 0; x < 2; x++) {
			testBSide();
		}
		System.out.println(aSide.size());
		aSideRound = aSide.getRound(2);
		System.out.println(bSide.size());
		bSideRound = bSide.getRound(2);
		System.out.println("ROUND 2 \n\n");
		setCousins(2);
		System.out.println("test ASide round 2\n");
		for (int x = 0; x < 2; x++) {
			testASide();
		}
		System.out.println("test BSide round 2\n");
		for (int x = 0; x < 2; x++) {
			testBSide();
		}
		System.out.println(aSide.size());
		aSideRound = aSide.getRound(3);
		System.out.println(bSide.size());
		bSideRound = bSide.getRound(3);
		System.out.println("break");
		System.out.println("ROUND 3 \n\n");
		setCousins(3);
		System.out.println("test ASide round 3\n");
		for (int x = 0; x < 1; x++) {
			testASide();
		}
		System.out.println("test BSide round 3\n");
		for (int x = 0; x < 1; x++) {
			testBSide();
		}
	}

	public void testASide() {
		Node<Match> game = aSideRound.get(0);
		Match dummyMatch = game.getMatch();
		dummyMatch.setLoser(dummyMatch.getPlayerOne());
		dummyMatch.setWinner(dummyMatch.getPlayerTwo());
		dummyMatch.setLoserScore(7);
		finishGame(game);
		System.out.println("Aside\n");
		System.out.println(aSide.printTree(aSide.getRoot()));
		System.out.println("Bside\n");
		System.out.println(bSide.printTree(bSide.getRoot()));
	}

	public void testBSide() {
		Node<Match> game = transferableBSideRound.get(0);
		Match dummyMatch = game.getMatch();
		dummyMatch.setLoser(dummyMatch.getPlayerOne());
		dummyMatch.setWinner(dummyMatch.getPlayerTwo());
		dummyMatch.setLoserScore(7);
		finishGame(game);
		System.out.println("Aside\n");
		System.out.println(aSide.printTree(aSide.getRoot()));
		System.out.println("Bside\n");
		System.out.println(bSide.printTree(bSide.getRoot()));
	}

	public List<Node<Match>> getRound(TournamentTree tree, int round) {
		return tree.getRound(round);
	}

	public void startASide(Set<Player> pSet) {
		List<Player> pList = new ArrayList<Player>();
		pList.addAll(pSet);

		// randomize entries
		Collections.shuffle(pList);

		/*
		 * start players puts players *only* in the player one spot. this
		 * assures even distribution. insert players then checks both positions
		 */
		aSide.startPlayers(pList);
		aSide.insertPlayers(pList, false);
	}

	public void finishGame(Node<Match> game) {
		Match match = game.getMatch();
		if (game.getSide() == Side.BSIDE && MainView.doubles) {
			game.setWasDoubles(true);
			Match BSideParentMatch = null;
			Match parentSibling = null;
			Node<Match> otherGame = game.getSibling();
			Match otherMatch = otherGame.getMatch();
			Player winnerOne = null;
			TourneyNight winnerNightOne = null;
			Player winnerTwo = null;
			TourneyNight winnerNightTwo = null;
			Player loserOne = null;
			TourneyNight loserNightOne = null;
			Player loserTwo = null;
			TourneyNight loserNightTwo = null;
			/*
			 * If the team that one the match is the team in the selected match,
			 * playerOne will be the 'winner'. if they are not, the team from
			 * the other match won.
			 */
			if (match.getWinner().equals(match.getPlayerOne())) {
				BSideParentMatch = game.getParent().getMatch();
				if (game.getParent().getSibling() != null) {
					parentSibling = game.getParent().getSibling().getMatch();
				}
				winnerOne = match.getPlayerOne();
				if (winnerOne == null) {
					winnerOne = new Player("NOBODY");
					winnerOne.addToWeekly(thisWeek, new TourneyNight());
				}
				winnerTwo = match.getPlayerTwo();
				if (winnerTwo == null) {
					winnerTwo = new Player("NOBODY");
					winnerTwo.addToWeekly(thisWeek, new TourneyNight());
				}
				loserOne = otherMatch.getPlayerOne();
				if (loserOne == null) {
					loserOne = new Player();
					loserOne.addToWeekly(thisWeek, new TourneyNight());
				}
				loserTwo = otherMatch.getPlayerTwo();
				if (loserTwo == null) {
					loserTwo = new Player();
					loserTwo.addToWeekly(thisWeek, new TourneyNight());
				}
			} else {
				BSideParentMatch = otherGame.getParent().getMatch();
				if (otherGame.getParent().getSibling() != null) {
					parentSibling = otherGame.getParent().getSibling()
							.getMatch();
				}
				winnerOne = otherMatch.getPlayerOne();
				if (winnerOne == null) {
					winnerOne = new Player();
					winnerOne.addToWeekly(thisWeek, new TourneyNight());
				}
				winnerTwo = otherMatch.getPlayerTwo();
				if (winnerTwo == null) {
					winnerTwo = new Player();
					winnerTwo.addToWeekly(thisWeek, new TourneyNight());
				}
				loserOne = match.getPlayerOne();
				if (loserOne == null) {
					loserOne = new Player();
					loserOne.addToWeekly(thisWeek, new TourneyNight());
				}
				loserTwo = match.getPlayerTwo();
				if (loserTwo == null) {
					loserTwo = new Player();
					loserTwo.addToWeekly(thisWeek, new TourneyNight());
				}
			}
			winnerNightOne = winnerOne.getWeekly().get(thisWeek);
			winnerNightTwo = winnerTwo.getWeekly().get(thisWeek);
			loserNightOne = loserOne.getWeekly().get(thisWeek);
			loserNightTwo = loserTwo.getWeekly().get(thisWeek);

			winnerNightOne.setBallsPocketed(winnerNightOne.getBallsPocketed()
					+ match.getWinnerScore());
			winnerNightOne.setGamesPlayed(winnerNightOne.getGamesPlayed() + 1);
			winnerNightOne.setBasicPoints(winnerNightOne.getBasicPoints() + 2);

			winnerNightTwo.setBallsPocketed(winnerNightTwo.getBallsPocketed()
					+ match.getWinnerScore());
			winnerNightTwo.setGamesPlayed(winnerNightTwo.getGamesPlayed() + 1);
			winnerNightTwo.setBasicPoints(winnerNightTwo.getBasicPoints() + 2);

			loserNightOne.setBallsPocketed(loserNightOne.getBallsPocketed()
					+ match.getLoserScore());
			loserNightOne.setGamesPlayed(loserNightOne.getGamesPlayed() + 1);
			loserNightOne.setBasicPoints(loserNightOne.getBasicPoints() + 1);

			loserNightTwo.setBallsPocketed(loserNightTwo.getBallsPocketed()
					+ match.getLoserScore());
			loserNightTwo.setGamesPlayed(loserNightTwo.getGamesPlayed() + 1);
			loserNightTwo.setBasicPoints(loserNightTwo.getBasicPoints() + 1);

			if (match.getWinnerScore() == 11) {
				winnerNightOne
						.setBasicPoints(winnerNightOne.getBasicPoints() + 1);
				winnerNightTwo
						.setBasicPoints(winnerNightTwo.getBasicPoints() + 1);
			}

			bSideRound.remove(game);
			transferableBSideRound.remove(game);

			Player parent1 = BSideParentMatch.getPlayerOne();
			Player parent2 = BSideParentMatch.getPlayerTwo();
			if (game.getParent().getSibling() != null) {
				Player sibling1 = parentSibling.getPlayerOne();
				Player sibling2 = parentSibling.getPlayerTwo();
				if (parent1 == null && parent2 == null) {
					if (!winnerOne.getName().matches(Player.DEFAULT)) {
						BSideParentMatch.setPlayerOne(winnerOne);
					}
					if (!winnerTwo.getName().matches(Player.DEFAULT)) {
						BSideParentMatch.setPlayerTwo(winnerTwo);
					}
				} else if (sibling1 == null && sibling2 == null) {
					if (!winnerOne.getName().matches(Player.DEFAULT)) {
						parentSibling.setPlayerOne(winnerOne);
					}
					if (!winnerTwo.getName().matches(Player.DEFAULT)) {
						parentSibling.setPlayerTwo(winnerTwo);
					}
				} else {
					if (parent1 == null) {
						if (sibling1 != null) {
							BSideParentMatch.setPlayerOne(sibling1);
							parentSibling.setPlayerOne(winnerOne);
							parentSibling.setPlayerTwo(winnerTwo);
						} else if (sibling2 != null) {
							BSideParentMatch.setPlayerOne(sibling2);
							parentSibling.setPlayerOne(winnerOne);
							parentSibling.setPlayerTwo(winnerTwo);
						} else {
							// we have a prolm...
						}
					} else if (parent2 == null) {
						if (sibling1 != null) {
							BSideParentMatch.setPlayerTwo(sibling1);
							parentSibling.setPlayerOne(winnerOne);
							parentSibling.setPlayerTwo(winnerTwo);
						} else if (sibling2 != null) {
							BSideParentMatch.setPlayerTwo(sibling2);
							parentSibling.setPlayerOne(winnerOne);
							parentSibling.setPlayerTwo(winnerTwo);
						} else {
							// we have a prolm...
						}
					}
				}
			} else {
				if (!winnerOne.getName().matches(Player.DEFAULT)) {
					BSideParentMatch.setPlayerOne(winnerOne);
				}
				if (!winnerTwo.getName().matches(Player.DEFAULT)) {
					BSideParentMatch.setPlayerTwo(winnerTwo);
				}
			}

			System.out.println("Winner1:" + winnerOne.getName()
					+ " handicap : " + winnerOne.getHandicap()
					+ " handicap points: " + winnerNightOne.getHandicapPoints()
					+ " tonights game points: "
					+ winnerNightOne.getBasicPoints() + " total points: "
					+ winnerNightOne.getTotalPoints() + "\nWinner2:"
					+ winnerTwo.getName() + " handicap : "
					+ winnerTwo.getHandicap() + " handicap points: "
					+ winnerNightTwo.getHandicapPoints()
					+ " tonights game points: "
					+ winnerNightTwo.getBasicPoints() + " total points: "
					+ winnerNightTwo.getTotalPoints() + "\nLoser1: "
					+ loserOne.getName() + " handicap :"
					+ loserOne.getHandicap() + " handicap points "
					+ loserNightOne.getHandicapPoints()
					+ " tonights game points: "
					+ loserNightOne.getBasicPoints() + " total points: "
					+ loserNightOne.getTotalPoints() + "\nLoser2: "
					+ loserTwo.getName() + " handicap :"
					+ loserTwo.getHandicap() + " handicap points "
					+ loserNightTwo.getHandicapPoints()
					+ " tonights game points: "
					+ loserNightTwo.getBasicPoints() + " total points: "
					+ loserNightTwo.getTotalPoints() + "\n");

		} else {
			Player winner = match.getWinner();
			TourneyNight winnerNight = winner.getWeekly().get(thisWeek);

			Player loser = match.getLoser();
			TourneyNight loserNight = loser.getWeekly().get(thisWeek);

			if (loser.getHandicap() > winner.getHandicap()) {
				match.setWinnerHandicapPoints((int) Math.round(loser
						.getHandicap() - winner.getHandicap()));
				winnerNight.setHandicapPoints(winnerNight.getHandicapPoints()
						+ match.getWinnerHandicapPoints());
			} else if (winner.getHandicap() > loser.getHandicap()) {
				match.setLoserHandicapPoints((int) Math.round(winner
						.getHandicap() - loser.getHandicap()));
				loserNight.setHandicapPoints(loserNight.getHandicapPoints()
						+ match.getLoserHandicapPoints());
			}

			winnerNight.setBallsPocketed(winnerNight.getBallsPocketed()
					+ match.getWinnerScore());
			winnerNight.setGamesPlayed(winnerNight.getGamesPlayed() + 1);

			loserNight.setBallsPocketed(loserNight.getBallsPocketed()
					+ match.getLoserScore());
			loserNight.setGamesPlayed(loserNight.getGamesPlayed() + 1);
			loserNight.setBasicPoints(loserNight.getBasicPoints() + 1);

			if (match.getWinnerScore() == 11) {
				winnerNight.setBasicPoints(winnerNight.getBasicPoints() + 1);
			}

			if (game.getSide() == Side.ASIDE) {
				loser.setSide(Side.BSIDE);
				winnerNight.setBasicPoints(winnerNight.getBasicPoints() + 3);
				if (game == aSide.getRoot()) {
					finalGame.getMatch().setPlayerOne(winner);
				} else {
					Match aSideParentMatch = game.getParent().getMatch();
					aSideRound.remove(game);
					if (aSideParentMatch.getPlayerOne() == null) {
						aSideParentMatch.setPlayerOne(winner);
					} else if (aSideParentMatch.getPlayerTwo() == null) {
						aSideParentMatch.setPlayerTwo(winner);
					} else {
						System.out
								.println("Something went wrong when placing A side");
					}
				}
				Match bSideMatch = game.getCousin().getMatch();
				if (bSideMatch.getPlayerOne() == null) {
					bSideMatch.setPlayerOne(loser);
				} else if (bSideMatch.getPlayerTwo() == null) {
					bSideMatch.setPlayerTwo(loser);
				} else {
					Match siblingMatch = game.getCousin().getSibling()
							.getMatch();
					if (siblingMatch.getPlayerOne() == null) {
						siblingMatch.setPlayerOne(loser);
					} else if (siblingMatch.getPlayerTwo() == null) {
						siblingMatch.setPlayerTwo(loser);
					} else {
						System.out
								.println("Something went wrong when placing B side from A side during doubles");
					}

				}
			} else if (game.getSide() == Side.BSIDE) {
				winnerNight.setBasicPoints(winnerNight.getBasicPoints() + 2);
				bSideRound.remove(game);
				transferableBSideRound.remove(game);
				if (game == bSide.getRoot()) {
					finalGame.getMatch().setPlayerTwo(winner);
				} else {
					Match BSideParentMatch = game.getParent().getMatch();
					if (BSideParentMatch.getPlayerOne() == null) {
						BSideParentMatch.setPlayerOne(winner);
					} else if (BSideParentMatch.getPlayerTwo() == null) {
						BSideParentMatch.setPlayerTwo(winner);
					} else {
						System.out
								.println("Something went wrong when placing B side from B side");
					}
				}
			} else if (game.getSide() == Side.FINAL) {
				if (winner.getSide() == Side.BSIDE
						&& loser.getSide() == Side.ASIDE) {
					winnerNight
							.setBasicPoints(winnerNight.getBasicPoints() + 2);
					match.setLoser(null);
					match.setLoserScore(0);
					match.setWinner(null);
					match.setWinnerScore(0);
					loser.setSide(Side.BSIDE);
				} else {

					if (winner.getSide() == Side.ASIDE) {
						winnerNight
								.setBasicPoints(winnerNight.getBasicPoints() + 3);
						winnerNight
								.setExtraPoints(winnerNight.getExtraPoints() + 3);
					} else if (winner.getSide() == Side.BSIDE) {
						winnerNight
								.setBasicPoints(winnerNight.getBasicPoints() + 2);
					}
					finished = true;
					winnerNight.setWinner(true);
				}
			}
			DecimalFormat df = new DecimalFormat("#.###");
			String winnerHandicap = df.format(winner.getHandicap());
			String loserHandicap = df.format(loser.getHandicap());
			System.out.println(winner.getName() + " handicap: "
					+ winnerHandicap + "total handicap points: "
					+ winner.getWeekly().get(thisWeek).getHandicapPoints()
					+ " tonights game points: " + winnerNight.getBasicPoints()
					+ " total points: " + winnerNight.getTotalPoints()
					+ "\nLoser: " + loser.getName() + " handicap: "
					+ loserHandicap + "total handicap points: "
					+ loser.getWeekly().get(thisWeek).getHandicapPoints()
					+ " tonights game points: " + loserNight.getBasicPoints()
					+ " total points: " + loserNight.getTotalPoints());
		}
	}

	private void setCousins(int round) {

		if (round % 2 == 0) {
			Collections.reverse(aSideRound);
		}
		Iterator<Node<Match>> it = aSideRound.iterator();
		Node<Match> aSideNode = null;

		transferableBSideRound = new ArrayList<Node<Match>>();

		if (round > 1) {
			for (Node<Match> bSideNode : bSideRound) {
				if (bSideNode.getParent().getRound() == bSideNode.getRound()) {
					transferableBSideRound.add(bSideNode);
				}
			}
			for (Node<Match> bSideNode : transferableBSideRound) {
				if (it.hasNext()) {
					aSideNode = it.next();
				}
				aSideNode.setCousin(bSideNode);
				bSideNode.setCousin(aSideNode);
			}
		} else if (round == 1) {
			for (Node<Match> bSideNode : bSideRound) {
				if (bSideNode.getParent().getRound() > bSideNode.getRound()) {
					transferableBSideRound.add(bSideNode);
				}
			}
			for (Node<Match> bSideNode : transferableBSideRound) {
				if (it.hasNext()) {
					aSideNode = it.next();
					aSideNode.setCousin(bSideNode);
					bSideNode.setCousin(aSideNode);
				}
				if (it.hasNext()) {
					aSideNode = it.next();
					aSideNode.setCousin(bSideNode);
					bSideNode.setCousin(aSideNode);
				}
			}
		}
	}

	private void setSiblings(int round) {
		Iterator<Node<Match>> aSideIterator = aSideRound.iterator();
		Node<Match> aSideNode = null;

		while (aSideIterator.hasNext()) {
			aSideNode = aSideIterator.next();
			Node<Match> parent = aSideNode.getParent();
			if (parent != null) {
				if (aSideNode.equals(parent.getLeftChild())) {
					aSideNode.setSibling(parent.getRightChild());
				} else if (aSideNode.equals(parent.getRightChild())) {
					aSideNode.setSibling(parent.getLeftChild());
				}
			}
		}
		Iterator<Node<Match>> bSideIterator = bSideRound.iterator();
		Node<Match> bSideNode = null;

		while (bSideIterator.hasNext()) {
			bSideNode = bSideIterator.next();

			Node<Match> parent = bSideNode.getParent();
			if (parent != null) {
				if (parent.getRightChild() != null) {
					if (bSideNode.equals(parent.getRightChild())) {
						bSideNode.setSibling(parent.getLeftChild());
					} else if (bSideNode.equals(parent.getLeftChild())) {
						bSideNode.setSibling(parent.getRightChild());
					}
					// parents right child == null
				} else {
					Node<Match> parentParent = parent.getParent();
					if (parentParent != null) {
						if (parent.equals(parentParent.getLeftChild())) {
							bSideNode.setSibling(parentParent.getRightChild()
									.getLeftChild());
						} else if (parent.equals(parentParent.getRightChild())) {
							bSideNode.setSibling(parentParent.getLeftChild()
									.getLeftChild());
						}
					}
				}
			}
		}
	}

	/**
	 * in the case where aSide is full and the tournament size needs to be
	 * increased, this method should be called to expand the size of the
	 * tournament to accompany more players
	 */
	public void expandBracket() {
		aSide.expandASide();
		bSide.expandBSide();
		rounds++;
		for (int x = 1; x <= rounds; x++) {
			aSideRound = aSide.getRound(x);
			bSideRound = bSide.getRound(x);
			if (x < rounds) {
				setSiblings(x);
			}
			setCousins(x);
		}
	}

	/**
	 * Using a double for the parameter allows the returned value to be 'rounded
	 * up'. This means that if there are ten players, it will round the bracket
	 * size to 16 instead of 8
	 * 
	 * @param value
	 * @return
	 */
	public static int findExponentBase2(double value) {
		int x = 0;
		while (value > 1) {
			value /= 2;
			x++;
		}
		return x;

	}

	public boolean isFinished() {
		return finished;
	}

	public Node<Match> getfinalGame() {
		return this.finalGame;
	}

	public Set<Player> getPlayerSet() {
		return playerSet;
	}

	public TournamentTree getaSide() {
		return aSide;
	}

	public void setaSide(TournamentTree aSide) {
		this.aSide = aSide;
	}

	public TournamentTree getbSide() {
		return bSide;
	}

	public void setbSide(TournamentTree bSide) {
		this.bSide = bSide;
	}

	public int getThisWeek() {
		return thisWeek;
	}

	public void setThisWeek(int thisWeek) {
		Tournament.thisWeek = thisWeek;
	}

	public List<Node<Match>> getaSideRound() {
		return aSideRound;
	}

	public void setaSideRound(List<Node<Match>> aSideRound) {
		this.aSideRound = aSideRound;
	}

	public List<Node<Match>> getbSideRound() {
		return bSideRound;
	}

	public void setbSideRound(List<Node<Match>> bSideRound) {
		this.bSideRound = bSideRound;
	}
}
