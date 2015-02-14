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
	
	/**
	 * Note: This keeps the same player references. Players must be overwritten if
	 * doing a deep copy
	 */
	public static Match copyMatch(Match match){
		Match newMatch = new Match();
		newMatch.setByePlayer(match.getByePlayer());
		newMatch.setLoser(match.getLoser());
		newMatch.setLoserHandicapPoints(match.getLoserHandicapPoints());
		newMatch.setLoserScore(match.getLoserScore());
		newMatch.setPlayerOne(match.getPlayerOne());
		newMatch.setPlayerTwo(match.getPlayerTwo());
		newMatch.setWinner(match.getWinner());
		newMatch.setWinnerHandicapPoints(match.getWinnerHandicapPoints());
		newMatch.setWinnerScore(match.getWinnerScore());
		return newMatch;
		
	}
}
