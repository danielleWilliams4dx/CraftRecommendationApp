package craftApplication;

import javax.swing.SwingUtilities;

public class DriverGUI {
	public static void main (String[] args) {
		 SwingUtilities.invokeLater(() -> new HomeScreenGUI().setVisible(true));
	}
}
