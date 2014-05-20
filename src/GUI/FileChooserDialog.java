package GUI;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import startup.Start;

public class FileChooserDialog extends JDialog {

	private JPanel panel;

	public FileChooserDialog() {

		initUI();
	}

	public final void initUI() {

		JFileChooser fileopen = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("c files", "c");
		fileopen.addChoosableFileFilter(filter);

		int ret = fileopen.showDialog(panel, "Open file");

		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fileopen.getSelectedFile();
			Start.file = file;
		}
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("FileChooserDialog");
		setSize(400, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
}
