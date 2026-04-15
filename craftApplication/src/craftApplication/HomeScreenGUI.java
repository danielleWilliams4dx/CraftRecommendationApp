package craftApplication;

//Swing components for building the GUI window
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.File;
import java.io.IOException;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
/*
* - Our current app is console-based and uses Scanner + actionSelect().
* - A real GUI uses windows, panels, labels, buttons, and click events.
* This class extends JFrame, which means this class IS the application window.
*/
public class HomeScreenGUI extends JFrame {
 /*
  * The constructor builds the full GUI:
  * - window settings
  * - main layout
  * - header
  * - welcome/hero section
  * - navigation buttons
  * - footer
  */
 public HomeScreenGUI() {
	 //Adding custom fonts
	 //Referencing https://www.ryisnow.online/2021/04/java-for-beginner-how-to-use-custom-font.html
	 //CHANGE DEFAULT FONTS
	 Font comba = new Font("SansSerif", Font.BOLD, 40);
	 Font forager = new Font("SansSerif", Font.BOLD, 40);
	 Font basicGothicProBold = new Font("SansSerif", Font.BOLD, 40);
	 Font basicGothicProBoldItalic = new Font("SansSerif", Font.BOLD, 40);
	 Font basicGothicProBook = new Font("SansSerif", Font.BOLD, 40);
	 Font basicGothicProBookItalic = new Font("SansSerif", Font.BOLD, 40);
	 Font basicGothicProDemibold = new Font("SansSerif", Font.BOLD, 40);
	 Font basicGothicProDemiboldItalic = new Font("SansSerif", Font.BOLD, 40);
	 
	 try{
		 
		 GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		 
		 //loading Comba Ultra Wide from the fonts folder
		 comba = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Comba-Ultra-Wide.otf")).deriveFont(15f);
		 graphEnv.registerFont(comba);
		 
		 //loading Forager Bold from the fonts folder
		 forager = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Forager-Bold.otf")).deriveFont(15f);
		 graphEnv.registerFont(forager);
		 
		 //loading all Basic Gothic Pro fonts from the fonts folder
		 basicGothicProBold = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Basic-Gothic-Pro-Bold.otf")).deriveFont(15f);
		 basicGothicProBoldItalic = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Basic-Gothic-Pro-Bold-Italic.otf")).deriveFont(15f);
		 basicGothicProBook = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Basic-Gothic-Pro-Book.otf")).deriveFont(15f);
		 basicGothicProBookItalic = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Basic-Gothic-Pro-Book-Italic.otf")).deriveFont(15f);
		 basicGothicProDemibold = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Basic-Gothic-Pro-Demibold.otf")).deriveFont(15f);
		 basicGothicProDemiboldItalic = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Basic-Gothic-Pro-Demibold-Italic.otf")).deriveFont(15f);
		 graphEnv.registerFont(basicGothicProBold);
		 graphEnv.registerFont(basicGothicProBoldItalic);
		 graphEnv.registerFont(basicGothicProBook);
		 graphEnv.registerFont(basicGothicProBookItalic);
		 graphEnv.registerFont(basicGothicProDemibold);
		 graphEnv.registerFont(basicGothicProDemiboldItalic);
		 
	 }catch(IOException | FontFormatException e) {
		 
	 }
	 
     // The title shown in the top bar of the application window
     setTitle("Craft Overflow");
     // Width and height of the application window
     setSize(1000, 650);
     // What happens when the user clicks the X button on the window:
     // EXIT_ON_CLOSE closes the program completely
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     // Centers the window on the user's screen
     setLocationRelativeTo(null);
     // Prevents the user from resizing the window for now
     // This keeps the layout stable while we prototype the design
     setResizable(false);
     /*
      * the main panel is the root container of everything inside the window
      *
      * BorderLayout divides the window into regions:
      * - NORTH   - top
      * - CENTER  -middle
      * - SOUTH   -bottom
      * - header in NORTH
      * - welcome section + main buttons in CENTER
      * - footer in SOUTH
      */
     JPanel mainPanel = new JPanel(new BorderLayout());
     // light background color for the whole screen
     mainPanel.setBackground(new Color(255, 248, 206));
     /*
      * EmptyBorder adds padding around the inside edges 
      */
     mainPanel.setBorder(new EmptyBorder(0, 0 , 0, 0));
     
     //nav section
     JPanel navPanel = new JPanel(new GridLayout(1, 3, 20, 0));
     navPanel.setBackground(new Color(19, 111, 99));
     navPanel.setOpaque(true);
     navPanel.setBorder(new EmptyBorder(20, 20, 0, 20));
     
     JLabel titleInNav = DefaultComponentFactory.getInstance().createLabel("<html>Craft Overflow</html>");
     titleInNav.setFont(comba.deriveFont(20f));
     titleInNav.setOpaque(false);
     titleInNav.setBorder(new EmptyBorder(0, 0, 20, 0));
     titleInNav.setForeground(new Color(255, 255, 255));
     navPanel.add(titleInNav);
     
     JButton homeBtn = new JButton("Home");
     homeBtn.addActionListener(new ActionListener() {
     	public void actionPerformed(ActionEvent e) {
     	}
     });
     homeBtn.setForeground(new Color(19, 111, 99));
     homeBtn.setHorizontalAlignment(SwingConstants.LEADING);
     homeBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
     homeBtn.setBackground(new Color(255, 248, 206));
     homeBtn.setHorizontalAlignment(SwingConstants.LEFT);
     navPanel.add(homeBtn);
     
     JButton savedCraftsBtn = new JButton("Saved Crafts");
     savedCraftsBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
     savedCraftsBtn.setBackground(new Color(255, 248, 206));
     savedCraftsBtn.setHorizontalAlignment(SwingConstants.LEFT);
     navPanel.add(savedCraftsBtn);
     
     JButton inventoryBtn = new JButton("Inventory");
     inventoryBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
     inventoryBtn.setBackground(new Color(255, 248, 206));
     inventoryBtn.setHorizontalAlignment(SwingConstants.LEADING);
     navPanel.add(inventoryBtn);
     
     JButton catalogBtn = new JButton("Catalog");
     catalogBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
     catalogBtn.setBackground(new Color(255, 248, 206));
     catalogBtn.setHorizontalAlignment(SwingConstants.LEADING);
     navPanel.add(catalogBtn);
     
     //container will contain everything beneath the nav
     JPanel container = new JPanel(new BorderLayout());
     container.setBorder(new EmptyBorder(20, 20, 20, 20));
     container.setOpaque(false);
     
     /*
      * The header section
      * - the app title
      * - the slogan/subtitle
      */
     JPanel headerPanel = new JPanel();
     headerPanel.setBackground(new Color(255, 248, 206));
     // Opaque false means the panel will not paint a solid background
     headerPanel.setOpaque(false);
     headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
     GridBagLayout gbl_headerPanel = new GridBagLayout();
     gbl_headerPanel.columnWidths = new int[]{1010, 0};
     gbl_headerPanel.rowHeights = new int[]{46, 0};
     gbl_headerPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
     gbl_headerPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
     headerPanel.setLayout(gbl_headerPanel);
     /*
      * titleGroup stacks the title and subtitle vertically.
      *
      * BoxLayout with Y_AXIS - components are placed
      * from top to bottom.
      */
     /*
      * The hero section is the large colored card near the top-middle of the UI.
      * It gives the user a welcome message and briefly explains what the app does.
      *
      * RoundedPanel lets us draw a panel with rounded corners 
      */
     JPanel heroPanel = new RoundedPanel(30, new Color(19, 111, 99));
     heroPanel.setBackground(new Color(19, 111, 99));
     heroPanel.setLayout(new BorderLayout());
     heroPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
     // Large welcome heading
     JLabel heroTitle = new JLabel("Welcome to Craft Overflow");
     heroTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
     heroTitle.setForeground(Color.WHITE);
     //HTML can be used inside a JLabel when we want multi-line text.
     JLabel heroText = new JLabel(
             "<html>Manage your inventory, explore the catalog, save crafts, " +
             "and get personalized recommendations based on the supplies you already have.</html>"
     );
     heroText.setFont(new Font("SansSerif", Font.PLAIN, 16));
     heroText.setForeground(Color.WHITE);
     JPanel heroTextPanel = new JPanel();
     heroTextPanel.setLayout(new BoxLayout(heroTextPanel, BoxLayout.Y_AXIS));
     heroTextPanel.setOpaque(false);
     heroTextPanel.add(heroTitle);
     heroTextPanel.add(Box.createVerticalStrut(15));
     heroTextPanel.add(heroText);
     heroPanel.add(heroTextPanel, BorderLayout.CENTER);
     /*
      * GridLayout(2, 2, 20, 20) creates:
      * - 2 rows
      * - 2 columns
      * - 20 px horizontal gap
      * - 20 px vertical gap
      */
     JPanel cardPanel = new JPanel(new GridLayout(2, 2, 20, 20));
     cardPanel.setOpaque(false);
     cardPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
     /*
      * Each button is created using a helper method so all buttons
      * share the same structure and style.
      */
     JButton inventoryButton = createMenuButton(
             "Inventory",
             "View and manage your current supplies"
     );
     JButton catalogButton = createMenuButton(
             "Catalog",
             "Browse available craft materials"
     );
     JButton savedCraftsButton = createMenuButton(
             "Saved Crafts",
             "Access your saved crafts"
     );
     JButton recommendationsButton = createMenuButton(
             "Recommendations",
             "Get crafts based on your inventory"
     );
     /*
      * These button actions are placeholders for now.
      *
      * JOptionPane.showMessageDialog(...) opens a small popup window.
      * im using popups temporarily so we can test that clicks work
      */
     inventoryButton.addActionListener(e ->
             JOptionPane.showMessageDialog(this, "Open Inventory screen here.")
     );
     catalogButton.addActionListener(e ->
             JOptionPane.showMessageDialog(this, "Open Catalog screen here.")
     );
     savedCraftsButton.addActionListener(e ->
             JOptionPane.showMessageDialog(this, "Open Saved Crafts screen here.")
     );
     recommendationsButton.addActionListener(e ->
             JOptionPane.showMessageDialog(this, "Open Recommendations screen here.")
     );
     // Add buttons into the 2x2 grid
     cardPanel.add(inventoryButton);
     cardPanel.add(catalogButton);
     cardPanel.add(savedCraftsButton);
     cardPanel.add(recommendationsButton);
     // ---------------- FOOTER SECTION ----------------
     /*
      * The footer sits at the bottom of the window.
      * It includes:
      * - a small descriptive label
      * - an Exit button
      */
     JPanel footerPanel = new JPanel(new BorderLayout());
     footerPanel.setOpaque(false);
     footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
     JLabel footerLabel = new JLabel("Craft Overflow - Java GUI Prototype");
     footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
     footerLabel.setForeground(new Color(130, 130, 130));
     JButton exitButton = new JButton("Exit");
     // Apply shared small-button style
     styleSmallButton(exitButton);
     // Clicking Exit closes the application
     exitButton.addActionListener(e -> System.exit(0));
     footerPanel.add(footerLabel, BorderLayout.WEST);
     footerPanel.add(exitButton, BorderLayout.EAST);
     /*
      * center panel groups the hero section and the button grid together.
      * use another BorderLayout so:
      * - the hero card stays at the top of the center area
      * - the button grid stays below it
      */
     JPanel centerPanel = new JPanel(new BorderLayout());
     centerPanel.setOpaque(false);
     centerPanel.add(headerPanel, BorderLayout.NORTH);
     // Main app title
     JLabel titleLabel = new JLabel("Craft Overflow");
     titleLabel.setVerticalAlignment(SwingConstants.TOP);
     titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
     //titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
     titleLabel.setFont(comba.deriveFont(40f));
     titleLabel.setForeground(new Color(19, 111, 99));
     // slogan under the title
//     JPanel subtitlePanel = new JPanel(new GridBagLayout());
//     subtitlePanel.setOpaque(false);
     JLabel subtitleLabel = new JLabel("Personalized crafting starts here.");
     subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
     subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
     subtitleLabel.setForeground(new Color(0, 15, 8));
//     subtitlePanel.add(subtitleLabel, new GridBagConstraints());
     
//     JPanel titleGroup = new JPanel();
//     titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
//     titleGroup.setOpaque(false);
//     titleGroup.add(titleLabel);
//     // Small vertical 
//     titleGroup.add(Box.createVerticalStrut(8));
     // subtitle
//     titleGroup.add(subtitlePanel);
     // Referenced: https://forums.oracle.com/ords/apexds/post/centering-gui-components-1553
//     headerPanel.add(titleGroup, new GridBagConstraints());
     headerPanel.add(titleLabel, new GridBagConstraints());
     headerPanel.add(subtitleLabel, GridBagConstraints.RELATIVE);
     
     centerPanel.add(heroPanel, BorderLayout.CENTER);
     centerPanel.add(cardPanel, BorderLayout.SOUTH);
     container.add(centerPanel, BorderLayout.NORTH);
     container.add(footerPanel, BorderLayout.SOUTH);
     mainPanel.add(navPanel, BorderLayout.NORTH);
     mainPanel.add(container, BorderLayout.CENTER);
     // Make mainPanel the content area of the JFrame window
     setContentPane(mainPanel);
 }
 /*
  * Helper method for creating the big navigation buttons.
  */
 private JButton createMenuButton(String title, String description) {
     /*
      *  use HTML inside the button label so the text can have:
      * - multiple lines
      * - bold title
      * - smaller description
      * - left alignment
      */
     JButton button = new JButton(
             "<html><div style='text-align: left; background-color: #136F63'>" +
             "<span style='font-size: 18px; font-weight: bold; color: white;'>" + title + "</span><br>" +
             "<span style='font-size: 12px; color: white;'>" + description + "</span>" +
             "</div></html>"
     );
     // Button font fallback / general font
     button.setFont(new Font("SansSerif", Font.PLAIN, 16));
     // White card-like button background
     button.setBackground(new Color(19, 111, 99));
     // Text color
     button.setForeground(Color.WHITE);
     /*
      * CompoundBorder lets combine:
      * 1. a thin outline border
      * 2. inner padding
      */
     button.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(new Color(19, 111, 99), 1),
             new EmptyBorder(20, 20, 20, 20)
     ));
     button.setHorizontalAlignment(SwingConstants.LEFT);
     button.setCursor(new Cursor(Cursor.HAND_CURSOR));
     return button;
 }
 /*
  * Helper method for styling smaller action buttons like Exit.
  */
 private void styleSmallButton(JButton button) {
     button.setFocusPainted(false);
     button.setBackground(new Color(40, 40, 40));
     button.setForeground(Color.WHITE);
     button.setFont(new Font("SansSerif", Font.BOLD, 14));
     button.setCursor(new Cursor(Cursor.HAND_CURSOR));
     button.setBorder(new EmptyBorder(10, 18, 10, 18));
 }
 /*
  * main method
  * SwingUtilities.invokeLater(...) makes sure the GUI is created
  * on Swing's Event Dispatch Thread
  */
 public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> {
         HomeScreenGUI gui = new HomeScreenGUI();
         gui.setVisible(true);
     });
 }
}
/*
* RoundedPanel
*
* This is a custom JPanel subclass used to draw a rounded rectangle background.
*/
class RoundedPanel extends JPanel {
 private final int cornerRadius;
 private final Color backgroundColor;
 public RoundedPanel(int cornerRadius, Color backgroundColor) {
     this.cornerRadius = cornerRadius;
     this.backgroundColor = backgroundColor;
     setOpaque(false);
 }
//paintComponent is called by Swing whenever the panel needs to be drawn or redrawn.
//By overriding this method, we can customize how the panel is rendered.
//In this case, we draw a filled rounded rectangle as the background of the panel.
//The Graphics object 'g' is the canvas we draw on, and we cast it to Graphics2D for better control over rendering.
//We also enable anti-aliasing for smoother edges on the rounded corners.
//The rounded rectangle is drawn to fill the entire panel area, and we use the specified background color.
//Finally, we dispose of the Graphics2D object to free up resources.
     @Override
     protected void paintComponent(Graphics g) {
             super.paintComponent(g);
             Graphics2D g2 = (Graphics2D) g.create();
             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             g2.setColor(backgroundColor);
             g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
             g2.dispose();
     }
}
