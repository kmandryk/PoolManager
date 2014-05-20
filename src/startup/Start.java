package startup;

import java.io.File;

import javax.swing.JFrame;

import GUI.MainView;

public class Start {
	public static File file;
	public static boolean fileWasRead = false;
	public static void main(String[] args) {
		
//		Console console = System.console();
//		int thisWeek = -1;
//		String s = "Enter tournament week number\n";
//		String input = console.readLine(s);
//		while(thisWeek == -1){
//			try{
//			thisWeek = Integer.valueOf(input);
//		} catch (NumberFormatException nfe){
//			input = console.readLine("Please enter a valid number\n");
//		}}
		int thisWeek = 3;
//		file = new File("/home/kaegan/development/documents/Summer2014.xlsx");
		MainView view = new MainView(thisWeek);
		
		view.setTitle("PoolManager");
		view.setSize(300, 200);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setVisible(true);

	}
}
