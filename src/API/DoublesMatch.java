package API;

public class DoublesMatch {
	Match matchOne;
	Match matchTwo;
	
	Player winnerOne;
	Player winnerTwo;
	int winnerScore;
	int loserScore;
	Player loserOne;
	Player loserTwo;
	
	public Match getMatchOne() {
		return matchOne;
	}

	public void setMatchOne(Match matchOne) {
		this.matchOne = matchOne;
	}

	public Match getMatchTwo() {
		return matchTwo;
	}

	public void setMatchTwo(Match matchTwo) {
		this.matchTwo = matchTwo;
	}

	public Player getWinnerOne() {
		return this.winnerOne;
	}

	public void setWinnerOne(Player winnerOne) {
		this.winnerOne = winnerOne;
	}

	public Player getWinnerTwo() {
		return winnerTwo;
	}

	public void setWinnerTwo(Player winnerTwo) {
		this.winnerTwo = winnerTwo;
	}

	public int getWinnerScore() {
		return winnerScore;
	}

	public void setWinnerScore(int winnerScore) {
		this.winnerScore = winnerScore;
	}

	public int getLoserScore() {
		return loserScore;
	}

	public void setLoserScore(int loserScore) {
		this.loserScore = loserScore;
	}

	public Player getLoserOne() {
		return loserOne;
	}

	public void setLoserOne(Player loserOne) {
		this.loserOne = loserOne;
	}

	public Player getLoserTwo() {
		return loserTwo;
	}

	public void setLoserTwo(Player loserTwo) {
		this.loserTwo = loserTwo;
	}

	public DoublesMatch(Match matchOne, Match matchTwo){
		this.matchOne = matchOne;
		this.matchTwo = matchTwo;
	}
	
}
