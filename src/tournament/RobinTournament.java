package tournament;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import API.Match;
import API.Player;
import API.TourneyNight;

public class RobinTournament {

	private List<Match> gamesList;
	private List<Player> teamA;
	private List<Player> teamB;
	private List<Player> playerList;
	private int sPtr;
	private int thisWeek;

	public RobinTournament(int thisWeek, Set<Player> playerList) {
		sPtr = 0;
		this.thisWeek = thisWeek;
		this.playerList = new ArrayList<Player>();
		this.playerList.addAll(playerList);
		teamA = new ArrayList<Player>();
		teamB = new ArrayList<Player>();
		gamesList = new ArrayList<Match>();
		generateTeams();
		generateGames();
	}

	public List<Match> getGamesList() {
		return gamesList;
	}

	public List<Player> getTeamA() {
		return teamA;
	}

	public List<Player> getTeamB() {
		return teamB;
	}

	public List<Player> getPlayerList() {
		return playerList;
	}

	public int getNoOfGames() {
		return gamesList.size();
	}

	private void generateTeams() {
		Collections.shuffle(playerList);
		int count = playerList.size();
		int iter;
		System.out.println("Team A: ");
		for (iter = 0; iter < (count / 2); iter++) {
			teamA.add(playerList.get(iter));
			System.out.println(playerList.get(iter).getName());
		}
		System.out.println("Team B: ");
		while (iter < count) {
			teamB.add(playerList.get(iter++));
			System.out.println(playerList.get(iter - 1).getName());
		}
	}

	private int generateGames() {
		int count = teamA.size();
		int offset = sPtr;
		for (int iter = 0; iter < count; iter++) {
			Match match = new Match();
			match.setPlayerOne(teamA.get(iter));
			if (teamA.size() != teamB.size()) {
				if (offset > count) {
					offset = 0;
				}
			} else if (offset >= count) {
				offset = 0;
			}
			match.setPlayerTwo(teamB.get(offset++));
			gamesList.add(match);
		}
		return count;
	}

	public void reGenGames() {
		gamesList.clear();
		sPtr++;
		if (teamA.size() != teamB.size()) {
			if (sPtr > (playerList.size() / 2)) {
				sPtr = 0;
			}
		} else if (sPtr >= (playerList.size() / 2)) {
			sPtr = 0;
		}
		generateGames();
	}

	public void finishMatch(Match match) {
		Player winner = match.getWinner();
		winner.setWins(winner.getWins() + 1);
		TourneyNight winnerNight = winner.getWeekly().get(thisWeek);

		Player loser = match.getLoser();
		TourneyNight loserNight = loser.getWeekly().get(thisWeek);

		if (loser.getHandicap() > winner.getHandicap()) {
			match.setWinnerHandicapPoints((int) Math.round(loser.getHandicap()
					- winner.getHandicap()));
			winnerNight.setHandicapPoints(winnerNight.getHandicapPoints()
					+ match.getWinnerHandicapPoints());
		} else if (winner.getHandicap() > loser.getHandicap()) {
			match.setLoserHandicapPoints((int) Math.round(winner.getHandicap()
					- loser.getHandicap()));
			loserNight.setHandicapPoints(loserNight.getHandicapPoints()
					+ match.getLoserHandicapPoints());
		}

		winnerNight.setBallsPocketed(winnerNight.getBallsPocketed()
				+ match.getWinnerScore());
		winnerNight.setGamesPlayed(winnerNight.getGamesPlayed() + 1);
		
		loserNight.setBallsPocketed(loserNight.getBallsPocketed()
				+ match.getLoserScore());
		loserNight.setGamesPlayed(loserNight.getGamesPlayed() + 1);
		loserNight.setBasicPoints(loserNight.getBasicPoints() + match.getLoserScore());

		// Break and Run
		if (match.getWinnerScore() == 11) {
			winnerNight.setBasicPoints(winnerNight.getBasicPoints() + 2);
		}

		winnerNight.setBasicPoints(winnerNight.getBasicPoints() + 10);
		DecimalFormat df = new DecimalFormat("#.###");
		String winnerHandicap = df.format(winner.getHandicap());
		String loserHandicap = df.format(loser.getHandicap());
		System.out.println(winner.getName() + " handicap: " + winnerHandicap
				+ "total handicap points: "
				+ winner.getWeekly().get(thisWeek).getHandicapPoints()
				+ " tonights game points: " + winnerNight.getBasicPoints()
				+ " total points: " + winnerNight.getTotalPoints()
				+ "\nLoser: " + loser.getName() + " handicap: " + loserHandicap
				+ "total handicap points: "
				+ loser.getWeekly().get(thisWeek).getHandicapPoints()
				+ " tonights game points: " + loserNight.getBasicPoints()
				+ " total points: " + loserNight.getTotalPoints());
		gamesList.remove(match);
	}

	private void allocateTeamPoints() {
		int teamATotal = 0, teamBTotal = 0;

		for (Player p : teamA) {
			teamATotal += p.getTotalPoints();
		}
		for (Player p : teamB) {
			teamBTotal += p.getTotalPoints();
		}

		List<Player> winners = (teamATotal > teamBTotal) ? teamA
				: (teamATotal < teamBTotal) ? teamB : null;

		for (Player p : winners) {
			p.setTeamPoints(p.getTeamPoints() + 1);
		}
	}

	public void insertPlayers(List<Player> pList) {
		for (Player p : pList) {
			playerList.add(p);
			if (teamB.size() <= teamA.size()) {
				teamB.add(p);
			} else	//this will make teamA size = teamB size so new game can be created
				teamA.add(p);
				gamesList.clear();
				generateGames();	//adds new game
		}
	}

	private void removePlayers(List<Player> removeList) {
		playerList.removeAll(removeList);
		for (Player player : removeList) {
			if (teamA.contains(player)){
				teamA.remove(player);
			} else if(teamB.contains(player)){
				teamB.remove(player);
			}
		}
	}

	public void rectifyTeams() {
		List<Player> removingPlayers = new ArrayList<Player>();
		for (Player player : playerList) {
			if (player.hasLeft) {
				removingPlayers.add(player);
			}
		}
		removePlayers(removingPlayers);
		rectifyGames();
		if ((teamB.size() - teamA.size()) < 0) {
			while ((teamB.size() - teamA.size()) < 0) {
				teamB.add(teamA.remove(0));
			}
		} else if ((teamA.size() - teamB.size()) > 0) {
			while ((teamA.size() - teamB.size()) > 0) {
				teamA.add(teamB.remove(0));
			}
		}
	}

	private void rectifyGames() {
		List<Match> remGames = new ArrayList<Match>();
		for(Match match : gamesList){
			if(match.getPlayerOne() == null || match.getPlayerTwo() == null){
				remGames.add(match);
			}
		}
		gamesList.removeAll(remGames);
	}
	
	public void finish(){
		allocateTeamPoints();
	}
}
