package API;

import java.io.ObjectInputStream.GetField;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import GUI.MainView;
import tournament.Tournament;
import tournament.Tournament.Side;

public class Player {
	
	public boolean hasLeft;
	private String name;
	private int rank;
	private Side side;
	private int teamPoints;
	private int wins;
	private int totalGamesPlayed;
	private Map<Integer, TourneyNight> weekly;
	
	public int getTeamPoints() {
		return teamPoints;
	}

	public void setTeamPoints(int teamPoints) {
		this.teamPoints = teamPoints;
	}

	//random string so it does not get replicated
	public static final String DEFAULT = "fdf889sgnbfvax234cvbnjhuytqerwmfqeo,regrcde12tsevf";

	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}

	public int getGamesPlayed() {
		int week = 0;
		int games = 0;
		while(weekly.get(week) != null){
			games += weekly.get(week).getGamesPlayed();
			week++;
		}
		return games;
	}

	/**
	 * default constructor will give the player the name "default" and a
	 * handicap of 7
	 */
	public Player() {
		name = DEFAULT;
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
		side = Side.ASIDE;
		weekly = new HashMap<Integer, TourneyNight>();
		for(int x = 0; x < 16; x++){
			if(weekly.get(x) == null){
				weekly.put(x, new TourneyNight(0));
			}
		}
	}

	public Player(String name, double handicap) {
		this.name = name;
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
	
	public String getHandicapString(){
		DecimalFormat df = new DecimalFormat("#.###");
		return df.format(getHandicap());
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
	
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public static Player copyPlayer( Player player){
		Player cPlayer = new Player(player.getName(), player.getHandicap());
		cPlayer.setSide(player.getSide());
		int week = 0;
		while(player.getWeekly().get(week) != null){
			cPlayer.weekly.put(week,TourneyNight.copy(player.getWeekly().get(week)));
			week++;
		}
		if(cPlayer.weekly.get(3) == player.weekly.get(3)){
			System.nanoTime();
		}
		return cPlayer;
	}

	@Override
	public String toString() {
		String s = null;
		if(MainView.type == MainView.tType.DELIM){
			s = getRank() + "," + getTotalPoints() + "," + getName();
		} else { 
			double percent = (double)getWins()/getGamesPlayed();
			s = getRank() + "," + getName() + "," + percent + "," + getWins() + "," + getTotalPoints() + "," + getTeamPoints() + "," + getHandicapString();
		}
		int week = 1;
		while(weekly.get(week) != null){
			s +="," +weekly.get(week).getTotalPoints();
			week++;
		}
		return s;
	}

	public String toHandicapString() {
		String s = getRank() + "," + getHandicapString() + "," + getName();
		int week = 0;
		while(weekly.get(week) != null){
			s += ","+ weekly.get(week).getBallsPocketed() + ","
					+ weekly.get(week).getGamesPlayed();
			week++;
		}
		return s;
	}
}
