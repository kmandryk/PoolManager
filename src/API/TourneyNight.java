package API;

public class TourneyNight {

	int gamesPlayed = 0;
	int ballsPocketed = 0;
	int basicPoints = 0;
	int handicapPoints = 0;
	int extraPoints = 0;
	int totalPoints;
	boolean isWinner;
	
	public boolean isWinner() {
		return isWinner;
	}
	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}
	public TourneyNight(){
	}
	public TourneyNight( int totalPoints){
		this.totalPoints = totalPoints;
	}
	
	public int getBasicPoints() {
		return basicPoints;
	}
	public void setBasicPoints(int basicPoints) {
		this.basicPoints = basicPoints;
	}
	public int getTotalPoints() {
		if(totalPoints == 0){
			return basicPoints + handicapPoints + extraPoints;
		}
		return totalPoints;
	}
	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}
	public int getGamesPlayed() {
		return gamesPlayed;
	}
	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}
	public int getBallsPocketed() {
		return ballsPocketed;
	}
	public void setBallsPocketed(int ballsPocketed) {
		this.ballsPocketed = ballsPocketed;
	}
	public int getHandicapPoints() {
		return handicapPoints;
	}
	public void setHandicapPoints(int handicapPoints) {
		this.handicapPoints = handicapPoints;
	}
	public int getExtraPoints() {
		return extraPoints;
	}
	public void setExtraPoints(int extraPoints) {
		this.extraPoints = extraPoints;
	}
	
	public static TourneyNight copy(TourneyNight tn){
		TourneyNight ntn = new TourneyNight(tn.getTotalPoints());
		ntn.setBallsPocketed(tn.getBallsPocketed());
		ntn.setBasicPoints(tn.getBasicPoints());
		ntn.setExtraPoints(tn.getExtraPoints());
		ntn.setGamesPlayed(tn.getGamesPlayed());
		ntn.setHandicapPoints(tn.getHandicapPoints());
		ntn.setWinner(tn.isWinner());
		return ntn;
	}
}
