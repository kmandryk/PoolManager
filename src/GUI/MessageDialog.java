package GUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MessageDialog extends JDialog {

	public static final int WIDTH = 250;
	public static final int HEIGHT = 150;
	private String message;
	private boolean confirmed;

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public MessageDialog(String message) {
		this.message = message;
		basicLayout();
		setVisible(true);
	}

	public MessageDialog(String message, boolean confirmable) {
		this.message = message;
		basicLayout();
		if (confirmable) {
			addConfirmable();
		}
		setVisible(true);
	}

	private void basicLayout() {
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		JLabel label = new JLabel("<html><p>" + message + "</p></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		add(label);
		setSize(new Dimension(WIDTH, HEIGHT));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		setModalityType(ModalityType.APPLICATION_MODAL);
	}

	public void addConfirmable() {
		GridLayout gl = new GridLayout();
		gl.setHgap(5);
		gl.setVgap(5);

		JPanel panel = new JPanel();
		panel.setLayout(gl);
		panel.setPreferredSize(new Dimension(WIDTH, 30));
		add(panel);

		JButton no = new JButton("No");
		no.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});

		panel.add(no);

		JButton yes = new JButton("Yes");
		getRootPane().setDefaultButton(yes);
		yes.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				setConfirmed(true);
				dispose();
			}
		});

		panel.add(yes);
		SwingUtilities.updateComponentTreeUI(this);
	}
}
