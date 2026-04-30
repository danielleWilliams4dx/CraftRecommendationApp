package craftApplication;

import javax.swing.SwingUtilities;
import java.awt.Dimension;

public class DriverGUI {
	public static Dimension windowSize = new Dimension(1000, 650);
    public static boolean isMaximized = false;
    
	public static void main (String[] args) {
		 SwingUtilities.invokeLater(() -> new HomeScreenGUI().setVisible(true));
	}
}
