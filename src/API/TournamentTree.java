package API;

import java.util.ArrayList;
import java.util.List;

import tournament.Tournament.Side;

public class TournamentTree {
	private Node<Match> root;
	private List<Node<Match>> gameList;
	private Side side;

	public TournamentTree(Side side) {
		root = new Node<Match>();
		// root.setParent(finalGame);
		this.side = side;
		gameList = new ArrayList<Node<Match>>();
	}

	public void generateATree(int children) {
		root.setRound(children);
		root.setSide(side);
		createASideChildren(root, children);
	}

	private void createASideChildren(Node<Match> parent, int children) {
		Node<Match> leftChild = new Node<Match>(parent, children - 1, side);
		Node<Match> rightChild = new Node<Match>(parent, children - 1, side);
		while (true) {
			parent.setLeftChild(leftChild);
			parent.setRightChild(rightChild);
			if (leftChild.getRound() > 1) {
				createASideChildren(leftChild, children - 1);
			}
			if (rightChild.getRound() > 1) {
				createASideChildren(rightChild, children - 1);
			}
			break;
		}
	}

	public void generateBTree(int children) {
		Node<Match> dummy = new Node<Match>();
		dummy.setRightChild(new Node<Match>());
		dummy.setRound(children);
		root.setParent(dummy);
		root.setRound(children);
		root.setSide(side);
		createBSideChildren(root, children);
	}

	private void createBSideChildren(Node<Match> parent, int children) {
		while (children != 0) {
			if (parent.getParent().getRightChild() == null) {
				if (children == 1) {
					break;
				}
				Node<Match> leftChild = new Node<Match>(parent, children, side);
				Node<Match> rightChild = new Node<Match>(parent, children, side);
				parent.setRightChild(rightChild);
				createBSideChildren(rightChild, children);
				parent.setLeftChild(leftChild);
				createBSideChildren(leftChild, children);

			} else if (parent.getParent().getRightChild() != null) {
				Node<Match> leftChild = new Node<Match>(parent, children - 1,
						side);
				parent.setLeftChild(leftChild);
				createBSideChildren(leftChild, children - 1);
			}
			break;
		}
	}

	public void insertPlayers(List<Player> playerList, boolean bottomSideFirst) {
		Player p = playerList.get(0);
		if (bottomSideFirst) {
			if (addPlayersRecursively(p, root.getRightChild())) {
				playerList.remove(p);
			}
		}
		while (!playerList.isEmpty()) {
			p = playerList.get(0);
			boolean inserted;
			if (inserted = addPlayersRecursively(p, root.getLeftChild())) {
				playerList.remove(p);
				if (!playerList.isEmpty()) {
					p = playerList.get(0);
				} else {
					break;
				}
			}
			if (addPlayersRecursively(p, root.getRightChild())) {
				playerList.remove(p);
			} else if (!inserted) {
				return;
			}
		}
	}

	private boolean addPlayersRecursively(Player p, Node<Match> parent) {
		Node<Match> game = parent;
		while (game.getLeftChild() != null) {
			game = game.getLeftChild();
		}
		if (newPlayerCorrection(game, p)) {
			return true;
		}
		game = parent;
		while (game.getRightChild() != null) {
			game = game.getRightChild();
		}
		if (newPlayerCorrection(game, p)) {
			return true;
		}
		if (parent.getLeftChild() != null) {
			if (addPlayersRecursively(p, parent.getLeftChild())) {
				return true;
			}
		}
		if (parent.getRightChild() != null) {
			if (addPlayersRecursively(p, parent.getRightChild())) {
				return true;
			}
		}
		return false;

	}

	private boolean newPlayerCorrection(Node<Match> game, Player p) {
		if (game.getMatch().getByePlayer() != null
				|| game.getMatch().getWinner() != null 
				|| game.getParent().getMatch().getWinner() != null) {	// for expanding brackets case
			return false;
		}
		if ((game.getMatch().getPlayerOne() == null)
				&& (game.getMatch().getPlayerTwo() == null)) {
			game.getMatch().setPlayerOne(p);
			return true;
		}
		if ((game.getSibling().getMatch().getPlayerOne() == null)
				&& (game.getSibling().getMatch().getPlayerTwo() == null)) {
			game.getSibling().getMatch().setPlayerOne(p);
			return true;
		}
		if ((game.getSibling().getMatch().getPlayerOne() != null)
				&& (game.getSibling().getMatch().getPlayerTwo() != null)) {
			if (game.getMatch().getPlayerOne() == null) {
				game.getMatch().setPlayerOne(p);
				return true;
			} else if (game.getMatch().getPlayerTwo() == null) {
				game.getMatch().setPlayerTwo(p);
				return true;
			}
		} else {
			if (game.getMatch().getPlayerOne() == null
					&& game.getSibling().getMatch().getWinner() == null
					&& game.getSibling().getMatch().getByePlayer() == null) {
				if (game.getSibling().getMatch().getPlayerOne() != null) {
					game.getMatch().setPlayerOne(
							game.getSibling().getMatch().getPlayerOne());
					game.getSibling().getMatch().setPlayerOne(p);
				} else if (game.getSibling().getMatch().getPlayerTwo() != null) {
					game.getMatch().setPlayerOne(
							game.getSibling().getMatch().getPlayerTwo());
					game.getSibling().getMatch().setPlayerTwo(p);

				}
				return true;
			} else if (game.getMatch().getPlayerTwo() == null) {
				if (game.getMatch().getPlayerOne() != null
						&& game.getSibling().getMatch().getWinner() == null
						&& game.getSibling().getMatch().getByePlayer() == null) {
					if (game.getSibling().getMatch().getPlayerOne() != null) {
						game.getMatch().setPlayerTwo(
								game.getSibling().getMatch().getPlayerOne());
						game.getSibling().getMatch().setPlayerOne(p);
					} else if (game.getSibling().getMatch().getPlayerTwo() != null) {
						game.getMatch().setPlayerTwo(
								game.getSibling().getMatch().getPlayerTwo());
						game.getSibling().getMatch().setPlayerTwo(p);

					}
				}
				return true;
			}
		}
		return false;
	}

	public void startPlayers(List<Player> playerList) {
		Player p = playerList.get(0);
		while (!playerList.isEmpty()) {
			p = playerList.get(0);
			if (startPlayersRecursively(p, root.getLeftChild())) {
				playerList.remove(p);
				if (!playerList.isEmpty()) {
					p = playerList.get(0);
				} else {
					break;
				}
			} else {
				break;
			}
			if (startPlayersRecursively(p, root.getRightChild())) {
				playerList.remove(p);
			}
		}
	}

	private boolean startPlayersRecursively(Player p, Node<Match> parent) {
		Node<Match> game = parent;
		while (game.getLeftChild() != null) {
			game = game.getLeftChild();
		}
		if (game.getMatch().getPlayerOne() == null) {
			game.getMatch().setPlayerOne(p);
			return true;
		}

		game = parent;
		while (game.getRightChild() != null) {
			game = game.getRightChild();
		}
		if (game.getMatch().getPlayerOne() == null) {
			game.getMatch().setPlayerOne(p);
			return true;
		}
		if (parent.getLeftChild() != null) {
			if (startPlayersRecursively(p, parent.getLeftChild())) {
				return true;
			}
		}
		if (parent.getRightChild() != null) {
			if (startPlayersRecursively(p, parent.getRightChild())) {
				return true;
			}
		}
		return false;

	}

	/**
	 * depth first search of tournament tree
	 * 
	 * @param node
	 * @param round
	 */
	private void findRound(Node<Match> node, int round) {
		Node<Match> left = node.getLeftChild();
		Node<Match> right = node.getRightChild();
		int nodeRound = node.getRound();
		if (nodeRound >= round) {
			if (left != null) {
				findRound(left, round);
			}
			if (right != null) {
				findRound(right, round);
			}

			if (nodeRound == round) {
				gameList.add(node);
			}
		}
	}

	/**
	 * depth first search of tournament tree. Returns an ordered list of the
	 * games
	 * 
	 * @param round
	 */
	public List<Node<Match>> getRound(int round) {
		gameList.clear();
		findRound(getRoot(), round);
		return gameList;
	}

	public String printTree(Node<Match> node) {
		Node<Match> left = node.getLeftChild();
		Node<Match> right = node.getRightChild();
		String tree = "";
		if (left != null) {
			tree += printTree(left);
			if (left.getMatch().getPlayerOne() != null
					&& left.getMatch().getPlayerTwo() != null
					&& left.getMatch().getWinner() == null) {
				tree += "Round= " + left.getRound() + "Side= " + left.getSide()
						+ " PlayerOne= "
						+ left.getMatch().getPlayerOne().getName()
						+ " PlayerTwo= "
						+ left.getMatch().getPlayerTwo().getName() + "\n";
			}
		}
		if (right != null) {
			tree += printTree(right);
			if (right.getMatch().getPlayerOne() != null
					&& right.getMatch().getPlayerTwo() != null
					&& right.getMatch().getWinner() == null) {
				tree += "Round= " + right.getRound() + right.getRound()
						+ "Side= " + right.getSide() + " PlayerOne= "
						+ right.getMatch().getPlayerOne().getName()
						+ " PlayerTwo= "
						+ right.getMatch().getPlayerTwo().getName() + "\n";
			}
		}
		return tree;
	}

	public List<Node<Match>> findAllPlayableGames(Node<Match> node) {
		Node<Match> left = node.getLeftChild();
		Node<Match> right = node.getRightChild();
		List<Node<Match>> playableGames = new ArrayList<Node<Match>>();
		Match match = node.getMatch();
		if (match.getPlayerOne() != null && match.getPlayerTwo() != null) {

			// if the game has already been won
			if (match.getWinner() == null) {
				playableGames.add(node);
			}
		} else if (match.getPlayerOne() != null || match.getPlayerTwo() != null) {
			if (match.getByePlayer() == null) {
				playableGames.add(node);
			}
		}

		if (left != null) {
			playableGames.addAll(findAllPlayableGames(node.getLeftChild()));
		}
		if (right != null) {
			playableGames.addAll(findAllPlayableGames(node.getRightChild()));
		}
		return playableGames;
	}

	public List<Node<Match>> getAllPlayableGames() {
		return findAllPlayableGames(root);
	}

	private void expandASideChildren(Node<Match> parent) {
		Node<Match> leftChild = parent.getLeftChild();
		Node<Match> rightChild = parent.getRightChild();

		if (leftChild == null && rightChild == null) {
			createASideChildren(parent, 2);
			if (parent.getMatch().getWinner() == null
					&& parent.getMatch().getByePlayer() == null) {
				parent.getLeftChild().getMatch()
						.setPlayerOne(parent.getMatch().getPlayerOne());
				parent.getRightChild().getMatch()
						.setPlayerOne(parent.getMatch().getPlayerTwo());
				parent.setMatch(new Match());
			}
		} else {
			expandASideChildren(leftChild);
			expandASideChildren(rightChild);
		}
		parent.setRound(parent.getRound() + 1);
	}

	public void expandASide() {
		expandASideChildren(root);
	}

	private void expandBSideChildren(Node<Match> parent) {
		Node<Match> leftChild = parent.getLeftChild();
		Node<Match> rightChild = parent.getRightChild();

		if (leftChild == null && rightChild == null) {
			createBSideChildren(parent, 2);
			if (parent.getMatch().getWinner() == null
					&& parent.getMatch().getByePlayer() == null) {
				parent.getLeftChild().getLeftChild().getMatch()
						.setPlayerOne(parent.getMatch().getPlayerOne());
				parent.getRightChild().getLeftChild().getMatch()
						.setPlayerOne(parent.getMatch().getPlayerTwo());
				parent.setMatch(new Match());
			}
		} else {
			expandBSideChildren(leftChild);
			if (rightChild != null) {
				expandBSideChildren(rightChild);
			}
		}
		parent.setRound(parent.getRound() + 1);
	}

	public void expandBSide() {
		expandBSideChildren(root);
	}

	public Node<Match> getRoot() {
		return root;
	}

	public void setRoot(Node<Match> root) {
		this.root = root;
	}

	public int size() {
		return (size(getRoot()));
	}

	private int size(Node<Match> node) {
		if (node == null)
			return (0);
		else {
			return (size(node.getLeftChild()) + 1 + size(node.getRightChild()));
		}
	}

	public static class Node<T> {
		private Match match;
		private Node<T> parent;
		private Node<T> leftChild;
		private Node<T> rightChild;
		private Node<T> cousin;
		private Node<T> sibling;
		private boolean wasDoubles;

		public boolean isWasDoubles() {
			return wasDoubles;
		}

		public void setWasDoubles(boolean wasDoubles) {
			this.wasDoubles = wasDoubles;
		}

		public Node<T> getSibling() {
			return sibling;
		}

		public void setSibling(Node<T> sibling) {
			this.sibling = sibling;
		}

		private int round;
		private Side side;

		public Side getSide() {
			return side;
		}

		public void setSide(Side side) {
			this.side = side;
		}

		public Node<T> getCousin() {
			return cousin;
		}

		public void setCousin(Node<T> cousin) {
			this.cousin = cousin;
		}

		public Node() {
			match = new Match();
		}

		public Node(Node<T> parent, int round, Side side) {
			this.parent = parent;
			this.round = round;
			this.side = side;
			match = new Match();
		}

		public boolean hasPlayableChildren() {
			return checkChildren(this);
		}

		private boolean checkChildren(Node<T> node) {
			if (node.leftChild != null) {
				if ((node.leftChild.match.getPlayerOne() != null || node.leftChild.match
						.getPlayerTwo() != null)
						&& (node.leftChild.match.getWinner() == null)
						&& (node.leftChild.match.getByePlayer() == null)) {
					return true;
				} else {
					if (checkChildren(node.leftChild)) {
						return true;
					}
				}
			}
			if (node.rightChild != null) {
				if ((node.rightChild.match.getPlayerOne() != null || node.rightChild.match
						.getPlayerTwo() != null)
						&& ((node.rightChild.match.getWinner() == null) && (node.rightChild.match
								.getByePlayer() == null))) {
					return true;
				} else {
					return checkChildren(node.rightChild);
				}
			}

			return false;

		}

		public boolean hasPlayableCousin() {
			if (cousin != null) {
				if (checkChildren(cousin)
						|| ((cousin.getMatch().getPlayerOne() != null
								&& cousin.getMatch().getPlayerTwo() != null && cousin
								.getMatch().getWinner() == null) && cousin
								.getMatch().getByePlayer() == null)
						|| ((cousin.getSibling().getMatch().getPlayerOne() != null
								&& cousin.getSibling().getMatch()
										.getPlayerTwo() != null && cousin
								.getSibling().getMatch().getWinner() == null) && cousin
								.getSibling().getMatch().getByePlayer() == null)) {
					return true;
				} else
					return false;
			} else
				return false;
		}

		public Match getMatch() {
			return match;
		}

		public void setMatch(Match match) {
			this.match = match;
		}

		public Node<T> getParent() {
			return parent;
		}

		public void setParent(Node<T> parent) {
			this.parent = parent;
		}

		public Node<T> getLeftChild() {
			return leftChild;
		}

		public void setLeftChild(Node<T> leftChild) {
			this.leftChild = leftChild;
		}

		public Node<T> getRightChild() {
			return rightChild;
		}

		public void setRightChild(Node<T> rightChild) {
			this.rightChild = rightChild;
		}

		public void setRound(int round) {
			this.round = round;
		}

		public int getRound() {
			return round;
		}
	}
}
