package API;

public class Match {
	Player playerOne;
	int winnerScore;
	int loserHandicapPoints;
	int winnerHandicapPoints;
	Player playerTwo;
	int loserScore;
	Player winner;
	Player loser;
	Player byePlayer;
	
	public Player getByePlayer() {
		return byePlayer;
	}
	public void setByePlayer(Player bye) {
		this.byePlayer = bye;
	}
	public Player getWinner() {
		return winner;
	}
	public void setWinner(Player winner) {
		this.winner = winner;
	}
	public Player getLoser() {
		return loser;
	}
	public void setLoser(Player loser) {
		this.loser = loser;
	}
	public Match(){
	}
	public Player getPlayerOne() {
		return playerOne;
	}
	public void setPlayerOne(Player playerOne) {
		this.playerOne = playerOne;
	}
	public int getWinnerScore() {
		return winnerScore;
	}
	public void setWinnerScore(int winnerScore) {
		this.winnerScore = winnerScore;
	}
	public Player getPlayerTwo() {
		return playerTwo;
	}
	public void setPlayerTwo(Player playerTwo) {
		this.playerTwo = playerTwo;
	}
	public int getLoserScore() {
		return loserScore;
	}
	public void setLoserScore(int loserScore) {
		this.loserScore = loserScore;
	}
	public int getLoserHandicapPoints() {
		return loserHandicapPoints;
	}
	public void setLoserHandicapPoints(int loserHandicapPoints) {
		this.loserHandicapPoints = loserHandicapPoints;
	}
	public int getWinnerHandicapPoints() {
		return winnerHandicapPoints;
	}
	public void setWinnerHandicapPoints(int winnerHandicapPoints) {
		this.winnerHandicapPoints = winnerHandicapPoints;
	}
}
