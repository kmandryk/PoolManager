package DataHandle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import startup.Start;
import API.Player;
import API.TourneyNight;
import GUI.MessageDialog;

public class ReadExcel {

	Set<Player> playerSet;
	XSSFWorkbook workbook;

	public Set<Player> read() {
		FileInputStream file;
		try {
			file = new FileInputStream(Start.file);
			// Create Workbook instance holding reference to .xlsx file
			workbook = new XSSFWorkbook(file);
			readWeekly();
			readHandicap();
			file.close();
			Start.fileWasRead = true;
		} catch (FileNotFoundException e) {
			new MessageDialog("File not found. Please select an existing file");
			Start.file = null;
			Start.fileWasRead = false;
		} catch (IOException e) {
			new MessageDialog("Error reading file.");
			Start.file = null;
			Start.fileWasRead = false;
		} catch (POIXMLException ife) {
			ife.printStackTrace();
			new MessageDialog(
					"Invalid file. Please ensure the file you selected was of xls format");
			Start.file = null;
			Start.fileWasRead = false;
		}
		return playerSet;
	}

	private void readWeekly() {
		try {

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			playerSet = new HashSet<Player>();

			int rowStart = 2;
			int rowEnd = Math.min(100, sheet.getLastRowNum());

			for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
				XSSFRow row = sheet.getRow(rowNum);
				if (row != null) {
					int lastColumn = Math.max(row.getLastCellNum(), 3);

					Player player = new Player();
					int week = 0;
					for (int cn = 2; cn < lastColumn; cn++) {

						XSSFCell cell = row.getCell(cn,
								Row.RETURN_BLANK_AS_NULL);

						// // Iterate through each rows one by one
						// Iterator<Row> rowIterator = sheet.iterator();
						// while (rowIterator.hasNext()) {
						// XSSFRow row = (XSSFRow) rowIterator.next();
						// For each row, iterate through all the columns
						// Iterator<Cell> cellIterator = row.cellIterator();
						// if (row.getCell(0) != null) {
						// Player player = new Player();

						// while (cellIterator.hasNext()) {
						// XSSFCell cell = (XSSFCell) cellIterator.next();

						if (cell == null) {
							week += 1;
							TourneyNight tn = new TourneyNight(0);
							player.addToWeekly(week, tn);
						} else {
							// Check the cell type and format accordingly
							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_NUMERIC:
								if (cell.getColumnIndex() > 2) {
									week += 1;
									TourneyNight tn = new TourneyNight(
											(int) cell.getNumericCellValue());
									player.addToWeekly(week, tn);
									if (cell.getCellStyle().getFont()
											.getBoldweight() == Font.BOLDWEIGHT_BOLD) {
										tn.setWinner(true);
									}
								}
								break;
							case Cell.CELL_TYPE_STRING:
								if (cell.getColumnIndex() == 2
										&& cell.getRowIndex() != 1) {
									player.setName(cell.getStringCellValue());
								}
								break;

							}
						}
					}
					if (!player.getName().matches(Player.DEFAULT)) {
						playerSet.add(player);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readHandicap() {
		try {
			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(1);

			// List<Player> playerList = new ArrayList<Player>();
			// Iterate through each rows one by one
			// Iterator<Row> rowIterator = sheet.iterator();
			// skip first row
			// Row row = rowIterator.next();

			System.out.println("PLAYER DATA\n\n");

			int rowStart = 2;
			int rowEnd = Math.min(100, sheet.getLastRowNum());

			// while (rowIterator.hasNext()) {
			// row = rowIterator.next();
			for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
				XSSFRow row = sheet.getRow(rowNum);

				int lastColumn = Math.max(row.getLastCellNum(), 3);

				Player player = null;
				XSSFCell cell = row.getCell(2, Row.RETURN_BLANK_AS_NULL);
				if (cell == null) {
					break;
				}
				for (Player p : playerSet) {
					if (cell.getStringCellValue().matches(p.getName())) {
						player = p;
						break;
					}
				}

				double handicap = 8;

				if (player == null) {
					break;
				}
				player.setHandicap(handicap);

				int week = -1;
				for (int cn = 3; cn < lastColumn; cn++) {

					// For each row, iterate through all the columns
					// Iterator<Cell> cellIterator = row.cellIterator();
					// if (!cellIterator.hasNext()) {
					// throw new Exception(" error parsing excel file");
					// }
					// Cell cell = cellIterator.next();
					// try{
					// // skip rank
					// cell = cellIterator.next();
					// //skip handicap
					// cell = cellIterator.next();
					// } catch(NoSuchElementException nse){
					// }
					// handicaps, by default, will start at eight - however,
					// this is
					// just a placeholder until an actual handicap can be
					// calculated
					week += 1;
					TourneyNight tn = player.getWeekly().get(week);
					if (tn == null) {
						throw new NoSuchElementException(
								"player is missing data");
					}
					// handicap points
					cell = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
					if (cell == null) {
						tn.setBallsPocketed(0);
					} else {
						tn.setBallsPocketed((int) cell.getNumericCellValue());
					}

					// games played
					cell = row.getCell(++cn, Row.RETURN_BLANK_AS_NULL);

					if (cell == null) {
						tn.setGamesPlayed(0);
					} else {
						tn.setGamesPlayed((int) cell.getNumericCellValue());
					}
				}

				playerSet.add(player);
				System.out.println(player + "  HANDICAP: "
						+ player.getHandicap());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
