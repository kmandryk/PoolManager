package GUI;

import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;

public class KeyListener extends KeyAdapter {
	Window window;

	public KeyListener(Window window) {
		this.window = window;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_TAB) {
			if (e.getModifiers() > 0) {
				e.getComponent().transferFocusBackward();
			} else {
				e.getComponent().transferFocus();
			}
			e.consume();
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (window.getFocusOwner() instanceof JButton) {
				((JButton) window.getFocusOwner()).doClick();
			} else if (window instanceof JDialog) {
				if (((JDialog) window).getRootPane().getDefaultButton() != null) {
					((CurrentGameDialog) window).getRootPane()
							.getDefaultButton().doClick();
				}
			}
		}
	}
}
