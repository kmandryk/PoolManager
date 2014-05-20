package DataHandle;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import startup.Start;
import API.Player;
import GUI.MainView;

public class WriteExcel {

	public void writeWeekly(Set<Player> playerSet) {

		// Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet("Weekly");

		List<Player> rankedList = new ArrayList<Player>();
		rankedList.addAll(playerSet);
		Collections.sort(rankedList, new Comparator<Player>() {
			@Override
			public int compare(Player a, Player b) {
				return a.getTotalPoints() >= b.getTotalPoints() ? -1
						: a == b ? 0 : 1;
			}
		});
		int rank = 1;
		for (Player p : rankedList) {
			p.setRank(rank);
			rank++;
		}
		playerSet.clear();
		playerSet.addAll(rankedList);
		// This data needs to be written (Object[])
		Map<String, Object[]> data = new TreeMap<String, Object[]>();

		data.put("1", new Object[] { "",
				"University of Victoria Tuesday Tourney hosted by Felicita's" });
		data.put("2", new Object[] { "Rank", "Points", "Player", "W1pts",
				"W2pts", "W3pts", "W4pts", "W5pts", "W6pts", "W7pts", "W8pts",
				"W9pts", "W10pts", "W11pts", "W12pts", "W13pts", "W14pts" });
		for (Player p : playerSet) {
			String s = p.toString();
			data.put(String.valueOf(p.getRank() + 2), s.split(","));

		}
		
		int index = data.size();
		index += 5;
		data.put(
				String.valueOf(index),
				new Object[] { "3 points awarded for a win on A side, 2 for a win on B side, 1 point for a loss" });
		index++;
		data.put(
				String.valueOf(index),
				new Object[] { "3 extra points are awarded for winning the Tourney without a loss" });
		index++;
		data.put(
				String.valueOf(index),
				new Object[] { "Points are awared to the player with the lower handicap of a match in the amount of the difference in handicap" });
		index++;
		data.put(String.valueOf(index),
				new Object[] { "Winners of a Tourney appear in BOLD" });
		index++;

		// Iterate over data and write to sheet
		Set<String> keyset = data.keySet();
		List<String> keyList = new ArrayList<String>();
		keyList.addAll(keyset);
		Collections.sort(keyList, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return Integer.parseInt(a) < Integer.parseInt(b) ? -1
						: a == b ? 0 : 1;
			}
		});
		int rownum = 0;
		for (String key : keyList) {
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			int cellNum = 0;
			Player player = null;
			Cell winnerCell = null;
			for (Object obj : objArr) {
				Double d = null;
				try {
					d = Double.parseDouble((String) obj);
				} catch (NumberFormatException nfe) {
				}
				Cell cell = row.createCell(cellNum++);
				if (d != null) {
					cell.setCellValue(d);
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					if (player != null) {
						XSSFFont font = workbook.createFont();
						XSSFCellStyle style = workbook.createCellStyle();
						if (player.getWeekly().get(cellNum - 3).isWinner()) {

							font.setBold(true);
							font.setBoldweight(Font.BOLDWEIGHT_BOLD);
							if(winnerCell != null){
								style.setFont(font);
								winnerCell.setCellStyle(style);
							}
						} else {
							font.setBold(false);
							font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
						}
						style.setFont(font);
						cell.setCellStyle(style);
					}
				} else if (obj instanceof String) {
					cell.setCellValue((String) obj);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					for (Player p : MainView.playingPlayers) {
						if (p.getName().matches((String) obj)) {
							player = p;
							winnerCell = cell;
							break;
						}
					}
				}
			}
		}
		try {
			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(Start.file);
			workbook.write(out);
			out.close();
			System.out.println("Weekly sheet successfully written to file : "
					+ out.getFD());
		} catch (Exception e) {
			e.printStackTrace();
		}

		writeHandicap(playerSet, workbook);
	}

	public void writeHandicap(Set<Player> playerSet, XSSFWorkbook workbook) {

		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet("Handicap");

		// This data needs to be written (Object[])
		Map<String, Object[]> data = new TreeMap<String, Object[]>();

		data.put("0", new Object[] { "Rank", "HDC", "Player",
				"Previous Semester Points", "Previous Semester Games Played",
				"W1pts", "W1gp", "W2pts", "W2gp", "W3pts", "W3gp", "W4pts",
				"W4gp", "W5pts", "W5gp", "W6pts", "W6gp", "W7pts", "W7gp",
				"W8pts", "W8gp", "W9pts", "W9gp", "W10pts", "W10gp", "W11pts",
				"W11gp", "W12pts", "W12gp", "W13pts", "W13gp", "W14pts",
				"W14gp" });
		for (Player p : playerSet) {
			String s = p.toHandicapString();
			data.put(String.valueOf(p.getRank()), s.split(","));

		}

		
		// Iterate over data and write to sheet
		Set<String> keyset = data.keySet();
		List<String> keyList = new ArrayList<String>();
		keyList.addAll(keyset);
		Collections.sort(keyList, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return Integer.parseInt(a) < Integer.parseInt(b) ? -1
						: a == b ? 0 : 1;
			}
		});

		int rownum = 1;
		for (String key : keyList) {
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			int cellNum = 0;
			boolean doBold = true;
			for (Object obj : objArr) {
				Double d = null;
				try {
					d = Double.parseDouble((String) obj);
				} catch (NumberFormatException nfe) {
				}
				Cell cell = row.createCell(cellNum++);
				if (d != null) {
					cell.setCellValue(d);
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					XSSFFont font = workbook.createFont();
					XSSFCellStyle style = workbook.createCellStyle();
					if (doBold) {
						font.setBold(true);
						font.setBoldweight(Font.BOLDWEIGHT_BOLD);
					} else {
						font.setBold(false);
						font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
					}
					style.setFont(font);
					cell.setCellStyle(style);
					doBold = !doBold;
				} else if (obj instanceof String) {
					cell.setCellValue((String) obj);
					cell.setCellType(Cell.CELL_TYPE_STRING);
				}
			}
		}
		try {
			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(Start.file);
			workbook.write(out);
			out.close();
			System.out.println("Handicap sheet successfully written to file : "
					+ out.getFD());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}