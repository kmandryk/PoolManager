package GUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import API.Player;
import API.TourneyNight;

public class NewPlayersDialog extends JDialog {

	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	private boolean isConfirmed;

	String[] newPlayers;
	List<String> listPlayers;

	public NewPlayersDialog(List<String> newPlayers) {
		this.listPlayers = newPlayers;
		this.newPlayers = new String[newPlayers.size()];
		newPlayers.toArray(this.newPlayers);
		initUI();
	}

	public final void initUI() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		String s = "";
		for (String p : newPlayers) {
			s += p + " - ";
		}

		JLabel players = new JLabel(
				"<html><p>The following players were not recognized. Create new players? \n\n"
						+ s + "</p></html");
		players.setPreferredSize(new Dimension(WIDTH - 10, 150));
		add(players);
		layout.putConstraint(SpringLayout.WEST, players, 10, SpringLayout.WEST,
				this.getContentPane());
		// layout.putConstraint(SpringLayout.NORTH, players, 5,
		// SpringLayout.NORTH, this.getContentPane());

		GridLayout gl = new GridLayout(1, 4);
		gl.setHgap(5);
		gl.setVgap(5);

		JPanel panel = new JPanel();
		panel.setLayout(gl);
		panel.setPreferredSize(new Dimension(WIDTH, 30));
		add(panel);
		layout.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST,
				this.getContentPane());
		layout.putConstraint(SpringLayout.NORTH, panel, 20, SpringLayout.SOUTH,
				players);

		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		JButton no = new JButton("No");
		no.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});

		panel.add(no);

		JButton yes = new JButton("Yes");
		yes.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				for (String s : newPlayers) {
					Player p = new Player(s);
					TourneyNight tn = new TourneyNight();

					// sets the default handicap to 8
					tn.setBallsPocketed(64);
					tn.setGamesPlayed(8);
					p.addToWeekly(0, tn);
					MainView.eligablePlayers.add(p);
					MainView.playingPlayers.add(p);
					listPlayers.clear();
					dispose();
				}
			}
		});

		panel.add(yes);

		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("NEW PLAYERS");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(WIDTH, HEIGHT);
	}
	
	public boolean isConfirmed() {
		return isConfirmed;
	}

	public void setConfirmed(boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
	}

}
