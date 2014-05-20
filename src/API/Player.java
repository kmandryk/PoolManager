package API;

import java.util.HashMap;
import java.util.Map;

import tournament.Tournament.Side;

public class Player {

	String name;
	double handicap;
	int totalPoints;
	int gamesPlayed;
	int rank;
	Side side;
	public boolean hasLeft;
	
	//random string so it does not get replicated
	public static final String DEFAULT = "fdf889sgnbfvax234cvbnjhuytqerwmfqeo,regrcde12tsevf";

	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}

	Map<Integer, TourneyNight> weekly;

	public int getGamesPlayed() {
		int week = 0;
		int games = 0;
		while(weekly.get(week) != null){
			games += weekly.get(week).getGamesPlayed();
			week++;
		}
		return games;
	}

	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	/**
	 * default constructor will give the player the name "default" and a
	 * handicap of 7
	 */
	public Player() {
		name = DEFAULT;
		handicap = 7;
		side = Side.ASIDE;
		weekly = new HashMap<Integer, TourneyNight>();
		for(int x = 0; x < 16; x++){
			if(weekly.get(x) == null){
				weekly.put(x, new TourneyNight(0));
			}
		}
	}

	/**
	 * using this constructor will give the player a handicap of 7
	 * 
	 * @param name
	 */
	public Player(String name) {
		this.name = name;
		handicap = 7;
		side = Side.ASIDE;
		weekly = new HashMap<Integer, TourneyNight>();
		for(int x = 0; x < 16; x++){
			if(weekly.get(x) == null){
				weekly.put(x, new TourneyNight(0));
			}
		}
	}

	public Player(String name, int handicap) {
		this.name = name;
		this.handicap = handicap;
		side = Side.ASIDE;
		weekly = new HashMap<Integer, TourneyNight>();
		for(int x = 0; x < 16; x++){
			if(weekly.get(x) == null){
				weekly.put(x, new TourneyNight(0));
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getHandicap() {
		int week = 0;
		int balls = 0;
		while(weekly.get(week) != null){
			balls += weekly.get(week).getBallsPocketed();
			week++;
		}
		try{
		return (double)balls/getGamesPlayed();
		} catch(ArithmeticException ae){
			System.out.println("player does not have any games played...");
			return 8.0;
		}
	}
	
	public void setHandicap(double handicap){
		this.handicap = handicap;
	}

	public int getTotalPoints() {
		int week = 1;
		int weeklyPoints = 0;
		while(weekly.get(week) != null){
			weeklyPoints += weekly.get(week).getTotalPoints();
			week++;
		}
		return weeklyPoints;
	}

	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Map<Integer, TourneyNight> getWeekly() {
		return weekly;
	}

	public void addToWeekly(int week, TourneyNight tourney) {
		weekly.put(week, tourney);
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		String s = getRank() + "," + getTotalPoints() + "," + getName();
		int week = 1;
		while(weekly.get(week) != null){
			s +="," +weekly.get(week).getTotalPoints();
			week++;
		}
		return s;
	}

	public String toHandicapString() {
		String s = getRank() + "," + getHandicap() + "," + getName();
		int week = 0;
		while(weekly.get(week) != null){
			s += ","+ weekly.get(week).getBallsPocketed() + ","
					+ weekly.get(week).getGamesPlayed();
			week++;
		}
		return s;
	}
}
