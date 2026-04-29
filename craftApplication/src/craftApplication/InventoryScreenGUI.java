package craftApplication; 

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.File;
import java .io.IOException;
import java.util.ArrayList;

public class InventoryScreenGUI extends JFrame {
	
	
	//Colors
	
	private static final Color BG_COLOR = new Color(244, 216, 227);
	private static final Color NAV_COLOR = new Color(126, 78, 96);
//	private static final Color NAV_TAB_CREAM = new Color(255, 248, 240);
	private static final Color ACCENT_COLOR = new Color(100, 30, 65);
	private static final Color TEXT_DARK     = new Color(70, 20, 50);
	private static final Color TEXT_LIGHT = new Color (140, 90, 110);
	private static final Color DIVIDER_COLOR = new Color(190,150,165);
	private static final Color BTN_BORDER = new Color(130, 70, 100); //border around filter, edit, etc. buttons
	
	
	
	
	Font comba = new Font("SansSerif", Font.BOLD, 40);
	Font forager = new Font("SansSerif", Font.BOLD, 24);
	Font basicGothicProBold = new Font("SansSerif", Font.BOLD, 12);
	Font basicGothicProBoldItalic = new Font("SansSerif", Font.ITALIC, 12);
	Font basicGothicProBook = new Font("SansSerif", Font.PLAIN, 12);
	Font basicGothicProBookItalic = new Font("SansSerif", Font.ITALIC, 12);
	Font basicGothicProDemibold = new Font("SansSerif", Font.BOLD, 12);
	Font basicGothicProDemiboldItalic = new Font("SansSerif", Font.ITALIC, 12);
	 
	 
	private final ArrayList<ItemRow> itemRows = new ArrayList<>();
	private final ArrayList<String> activeFilters = new ArrayList<>();
	private JLabel filterActiveLabel;
	private JPanel itemListPanel;
	private JScrollPane itemScroll;
	 
	private static class ItemRow {
		CraftSupply supply;
		JCheckBox checkbox;
		ItemRow(CraftSupply s, JCheckBox c) {
			supply = s;
			checkbox = c;
		}
	}
	
	public InventoryScreenGUI() {
		loadFonts();
		setTitle("Craft OverFlow - Inventory");
		setSize(1000, 650);
		//Closes just the window without shutting down the app
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		//Centers the window on the screen
		setLocationRelativeTo(null);
		//Allows the user to drag the window edges to resize it 
		setResizable(true); 
		
		//BorderLayout divides the space into 5 regions: 
		//NORTH (top strip)
		//SOUTH (bottom)
		//EAST (right)
		//WEST (left)
		//CENTER (fills everything remaining)
		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(BG_COLOR);
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(false);
		wrapper.add(buildNavBar(), BorderLayout.NORTH);
		wrapper.add(buildMainArea(), BorderLayout.CENTER);
		wrapper.setPreferredSize(new Dimension(900, 940));
		JScrollPane sp = new JScrollPane(wrapper);
		sp.setOpaque(false);
		root.add(sp);		
		root.setBorder(new EmptyBorder(0,0,0,0));
		//Mandatory for JFRame 
		setContentPane(root); 
	 }
	 
	//Navigation Bar
	 private JPanel buildNavBar() {
		 //Brand on WEST, tebs fill center 
		 JPanel nav = new JPanel(new BorderLayout());
		 nav.setBackground(NAV_COLOR);
		 nav.setPreferredSize(new Dimension (1000,70));
		 
		 //Brand - fixed width panel, so it never gets squeezed 
		 JPanel brandPanel = new JPanel(new BorderLayout());
		 brandPanel.setOpaque(false);
		 brandPanel.setPreferredSize(new Dimension(250, 70));
		 brandPanel.setBorder(new EmptyBorder(0,20,0,0));
		 JLabel brand = new JLabel("CRAFT OVERFLOW");
		 brand.setFont(comba.deriveFont(Font.BOLD, 13f));
		 brand.setForeground(Color.WHITE);
		 brandPanel.add(brand, BorderLayout.CENTER);
		 nav.add(brandPanel, BorderLayout.WEST);
		 
		 //Tabs = GridLayout(1,4) (rows, cols, hgap, vgap)
		 JPanel tabArea = new JPanel(new GridLayout(1, 4, 0, 0)); 
		 tabArea.setOpaque(false);
		 
		 String[] tabNames = {"Home", "Saved Crafts", "Inventory", "Catalog"};
		 for (String tab : tabNames) {
			 boolean isActive = tab.equals("Inventory");
			 JPanel tabPanel = buildNavTab(tab, isActive);
			 tabArea.add(tabPanel);
		 }
		 
		 nav.add(tabArea, BorderLayout.CENTER);
		 return nav;
	 }
	 
	 //Builds one navigation tab - rounded panel with label inside
	 //Active tab - cream bckgr
	 //Inactive tab - transparent 
	 private JPanel buildNavTab(String label, boolean active) {
		 JPanel tab = new JPanel (new BorderLayout()) {
			 @Override
			 protected void paintComponent(Graphics g) {
				 if (active) {
					 Graphics2D g2 = (Graphics2D) g.create();
					 //Turns antialiasing, smooths the edges of curves
					 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
							 RenderingHints.VALUE_ANTIALIAS_ON); 
					 g2.setColor(BG_COLOR);
					 //rounded top corners only
					 g2.fillRoundRect(0, 15, getWidth(), getHeight() + 20, 30, 30);
					 g2.dispose(); //frees the copy 
				 }
				 super.paintComponent(g);
			 }
		 };
		 //Inactive tabs transparent so the nav bckgr shows through 
		 tab.setOpaque(false);
		 tab.setPreferredSize(new Dimension (160,70));
		 tab.setBorder(new EmptyBorder(20, 20, 0, 20));
		 
		 //Centers text horizontally
		 JLabel lbl = new JLabel(label, SwingConstants.CENTER);
		 //Bold and dark color for active; white nad regular for inactive
		 lbl.setFont(active 
				 ? basicGothicProBold.deriveFont(14f)
				 : basicGothicProBook.deriveFont(14f));
		 lbl.setForeground(active ? ACCENT_COLOR : Color.WHITE); //inactive 
		 tab.add(lbl, BorderLayout.CENTER);
		 
		 //Changes the mouse cursor to a pointing hand on hover 
		 tab.setCursor(new Cursor (Cursor.HAND_CURSOR));
		 tab.addMouseListener(new MouseAdapter() {
			 @Override public void mouseClicked(MouseEvent e) {
				 switchScreens(label); 
			 }
		 });
		 
		 return tab;
	 }
	 
	 //Main Area 
	 private JPanel buildMainArea() {
		 JPanel main = new JPanel(new BorderLayout());
		 main.setBackground(BG_COLOR);
		 main.add(buildTitleSection(), BorderLayout.NORTH);
		 main.add(buildTwoColumnArea(), BorderLayout.CENTER);
		 return main; 
	 }
	 
	 private JPanel buildTitleSection() {
		 JPanel p = new JPanel();
		 p.setLayout(new BoxLayout (p, BoxLayout.Y_AXIS));
		 p.setOpaque(false);
		 //60x top padding, 60x bottom padding 
		 p.setBorder(new EmptyBorder(60, 0, 60, 0));
		 
		 JLabel title = new JLabel("Inventory");
		 title.setFont(forager.deriveFont(52f));
		 title.setForeground(ACCENT_COLOR);
		 //centers the label horizontally inside a BoxLayout
		 //without it boxlayout left-aligns everything by default 
		 title.setAlignmentX(Component.CENTER_ALIGNMENT);
		 
		 JPanel underline = new JPanel();
		 underline.setBackground(ACCENT_COLOR);
		 underline.setMaximumSize(new Dimension (220, 3));
		 underline.setPreferredSize(new Dimension(220,3));
		 underline.setAlignmentX(Component.CENTER_ALIGNMENT);
		 p.add(title);
		 p.add(Box.createVerticalStrut(8));
		 p.add(underline);
		 return p;
	 }
	 
	 // Two columns:
	 //   Left  - item list
	 //   Right - buttons panel
	 private JPanel buildTwoColumnArea() {
		 //no layout manager 
		 JPanel outer = new JPanel(null) {
			 @Override
			 public void doLayout() {
				 //Responsive layout
				 int w = getWidth();
				 int h = getHeight();
				 
				 int itemsX = (int)(w * 0.25); //25% of window width
				 int dividerX = (int)(w * 0.50); // 50% of window width
				 int buttonsX = dividerX + 2; // 2px after divider
				 
				 if (itemScroll != null) {
					 itemScroll.setBounds(itemsX, 0, dividerX - itemsX, h);
				 }
				 
				 Component[] comps = getComponents();
				 for (Component c : comps) {
					 if (c instanceof JPanel) {
						 String name = ((JPanel) c).getName();
						 if ("divider".equals(name)) {
							 c.setBounds(dividerX, 0, 1, h); //1 px wide, full height
						 } else if ("rightWrapper".equals(name)) {
							 c.setBounds(buttonsX, 0, w - buttonsX, h);
						 }
					 }
				 }
			 }
		 };
		 outer.setOpaque(false);
		 
		 //Item List 
		 itemListPanel = new JPanel(); 
		 itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
		 itemListPanel.setOpaque(false);
		 itemListPanel.setBorder(new EmptyBorder(10,10,10,10));
		 populateItemList();
		 //wraps itemListPanel to make it scrollable 
		 itemScroll = new JScrollPane(itemListPanel);
		 itemScroll.setOpaque(false);
		 //returns inner viewport of the scroll pane
		 itemScroll.getViewport().setOpaque(false);
		 //removes the default visible border 
		 itemScroll.setBorder(BorderFactory.createEmptyBorder());
		 //disables horizontal scrolling
		 itemScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		 outer.add(itemScroll);
		 
		 // Divider
		 JPanel divider = new JPanel();
		 divider.setName("divider");
		 divider.setBackground(DIVIDER_COLOR);
		 outer.add(divider);
		 
		 //Right wrapper
		 JPanel rightWrapper = new JPanel(new BorderLayout());
		 rightWrapper.setName("rightWrapper");
		 rightWrapper.setBackground(BG_COLOR);
		 rightWrapper.setOpaque(true);
		 rightWrapper.setBorder(new EmptyBorder(10, 20, 0, 10));
		 rightWrapper.add(buildRightPanel(), BorderLayout.NORTH);
		 outer.add(rightWrapper);
		 
		 return outer;
	 }
	 
	 //Item list
	 private void populateItemList() {
		 itemListPanel.removeAll();
		 itemRows.clear();
		 new Inventory();
		 int count = 0;
		 
		 for (CraftSupply supply : Inventory.items) {
			 if (!activeFilters.isEmpty() && !activeFilters.contains(supply.getType())) continue;
			 //for each item passes the filter, build itw row and add it
			 itemListPanel.add(buildItemRow(supply));
			 //Invisible spacer beneath
			 itemListPanel.add(Box.createVerticalStrut(6));
			 count++;
		 }
		 
		 //if the inventory is empty, add a label "No results."
		 if(count == 0) {
			 JLabel noResults = new JLabel("No results.");
			 noResults.setFont(basicGothicProBold.deriveFont(15f));
			 noResults.setForeground(TEXT_DARK);
			 itemListPanel.add(noResults);
		 }
		 
		 
		 itemListPanel.revalidate();
		 itemListPanel.repaint();
	 }
	 
	 private JPanel buildItemRow (CraftSupply supply) {
		 JPanel row = new JPanel();
		 row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		 row.setOpaque(false);
		 row.setBorder(new EmptyBorder(4, 0, 0, 0));
		 //Stretch as max as possible (row fills full col width)
		 row.setMaximumSize(new Dimension (Integer.MAX_VALUE, 75));
		 
		 JCheckBox cb = new JCheckBox();
		 cb.setOpaque(false);
		 cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
		 
		 JPanel text = new JPanel();
		 text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
		 text.setOpaque(false);
		 text.setBorder(new EmptyBorder(3, 8, 0, 0));
		 
		 JLabel name = new JLabel(supply.getName());
		 name.setFont(basicGothicProBold.deriveFont(15f));
		 name.setForeground(TEXT_DARK);
		 text.add(name);
		 text.setAlignmentY(Component.TOP_ALIGNMENT);
		 
		 //detail lines only appear when the value exists 
		 if(!supply.getColor().isEmpty()) text.add(detailLabel(supply.getColor()));
		 if (!supply.getSize().isEmpty()) text.add(detailLabel(supply.getSize()));
		 if (!supply.getQuantity().isEmpty()) text.add(detailLabel("Qty " + supply.getQuantity()));
		 
		 cb.addItemListener( e -> name.setForeground(cb.isSelected() ? ACCENT_COLOR : TEXT_DARK));
		 cb.setAlignmentY(Component.TOP_ALIGNMENT);
		 
		 row.add(cb, BorderLayout.WEST);
		 row.add(text, BorderLayout.CENTER);
		 itemRows.add(new ItemRow(supply, cb));
		 return row;
	 }
	 
	 private JLabel detailLabel(String t) {
		 JLabel l = new JLabel(t);
		 l.setFont(basicGothicProBook.deriveFont(12f));
		 l.setForeground(TEXT_LIGHT);
		 return l;
	 }
	 
	 //Right panel - Filter, Recommender, Edit, Delete
	 private JPanel buildRightPanel() {
		 JPanel p = new JPanel();
		 p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		 p.setOpaque(false);
		 
		 //Filter row: [Filter button] Active: x, y, z
		 JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		 filterRow.setOpaque(false);
		 filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		 JButton filterBtn = outlineBtn("Filter");
		 filterActiveLabel = new JLabel(getFilterText());
		 filterActiveLabel.setFont(basicGothicProBook.deriveFont(11f));
		 filterActiveLabel.setForeground(TEXT_DARK);
		 filterBtn.addActionListener(e -> openFilterDialog());
		 filterRow.add(filterBtn);
		 filterRow.add(filterActiveLabel);
		 
		 //Recommender
		 JPanel recRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		 recRow.setOpaque(false);
		 recRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		 JButton recBtn = outlineBtn("Recommender");
		 recBtn.addActionListener(e -> openRecommenderDialog());
		 recRow.add(recBtn);
		 
		 //Edit & Delete
		 JPanel edRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		 edRow.setOpaque(false);
		 edRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		 JButton editBtn = outlineBtn("Edit");
		 JButton delBtn = outlineBtn("Delete");
		 editBtn.addActionListener( e -> handleEdit());
		 delBtn.addActionListener(e -> handleDelete());
		 edRow.add(editBtn);
		 edRow.add(delBtn);
		 
		 
		 p.add(filterRow);
		//18px spacers between each other 
		 p.add(Box.createVerticalStrut(18));
		 p.add(recRow);
		 p.add(Box.createVerticalStrut(18));
		 p.add(edRow);
		 return p;
	 }
	 
	 private JButton outlineBtn(String label) {
		 JButton btn = new JButton(label) {
			 //paintComponent draws the button background 
			 //g.create() copies graphics content 
			 @Override protected void paintComponent(Graphics g) {
				 Graphics2D g2 = (Graphics2D) g.create();
				 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				 g2.setColor(getBackground());
				 g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				 //super.paintComponent(g) draws the button label text on top
				 super.paintComponent(g);
				 g2.dispose();
			 }
			 @Override protected void paintBorder(Graphics g) {
				 Graphics2D g2 = (Graphics2D) g.create();
				 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				 g2.setColor(BTN_BORDER);
				 g2.setStroke(new BasicStroke(1.5f));
				 g2.drawRoundRect(1, 1, getWidth() - 2, getHeight()-2, 20, 20);
				 g2.dispose();
			 }
		 };
		 btn.setFont(basicGothicProBook.deriveFont(13f));
		 btn.setForeground(ACCENT_COLOR);
		 btn.setBackground(BG_COLOR);
		 //Tells swing not draw its own default background
		 btn.setContentAreaFilled(false);
		 //Removes the dotted rectanglle that appears aound a button after it's clicked 
		 btn.setFocusPainted(false);
		 btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		 btn.setBorder(new EmptyBorder(8, 18, 8, 18));
		 btn.addMouseListener(new MouseAdapter() {
			 @Override public void mouseEntered(MouseEvent e) {
				 btn.setBackground(new Color(220, 185, 200));
				 btn.repaint();
			 }
			 @Override public void mouseExited(MouseEvent e ) {
				 btn.setBackground(BG_COLOR);
				 btn.repaint();
			 }
		 });
		 return btn;
	 }
	 
	 private void switchScreens(String name) {
		 this.setVisible(false);
		 if(name.equals("Home")) {
			 HomeScreenGUI screen = new HomeScreenGUI();
			 screen.setVisible(true);
		 }else if(name.equals("Inventory")) {
			 InventoryScreenGUI screen = new InventoryScreenGUI();
			 screen.setVisible(true);
		 }else if(name.equals("Catalog")) {
			 CatalogScreenGUI screen = new CatalogScreenGUI();
			 screen.setVisible(true);
		 }else if(name.equals("Saved Crafts")){
//			 SavedCraftsScreenGUI screen = new SavedCraftsScreenGUI();
//			 screen.setVisible(true);
		 }
	 }
	 
	 //Filter Dialog 
	 private void openFilterDialog() {
		 String[] cats = {"Adhesives", "Drawing", "Jewerly", "Painting", "Paper", "Sewing", "Other"};
		 JPanel p = new JPanel();
		 p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		 p.setBorder(new EmptyBorder(10,10,10,10));
		 JLabel h = new JLabel("Select categories:");
		 h.setFont(basicGothicProBold.deriveFont(13f));
		 p.add(h);
		 p.add(Box.createVerticalStrut(10));
		 //need it to read later which boxes are checked
		 ArrayList<JCheckBox> boxes = new ArrayList<>();
		 for (String c : cats) {
			 //CheckBox whose label is a cat. name
			 JCheckBox cb = new JCheckBox(c);
			 cb.setSelected(activeFilters.contains(c));
			 cb.setFont(basicGothicProBook.deriveFont(13f));
			 //saves it to the list
			 boxes.add(cb);
			 p.add(cb);
		 }
		 if (JOptionPane.showConfirmDialog(this, p, "Filter Inventory", 
				 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
			 activeFilters.clear();
			 
			 for (int i = 0; i < cats.length; i++) if (boxes.get(i).isSelected()) activeFilters.add(cats[i]);
			 filterActiveLabel.setText(getFilterText());
			 populateItemList();
		 }
	 }
	 
	 private String getFilterText() {
		 return activeFilters.isEmpty() ? "" : "<html>Active: " + String.join(", ", activeFilters) + "</html>";
	 }
	 
	 //Recommender
	 
	 private void openRecommenderDialog() {
		    Inventory inv = new Inventory();
		    ArrayList<String> visibleNames = inv.getVisibleItemNames(activeFilters);
		 
		    if (getCheckedRows().isEmpty()) {
		        // no items checked
		        int choice = JOptionPane.showConfirmDialog(this,
		                "No items selected. Generate recommendations for your entire inventory?",
		                "Recommender", JOptionPane.YES_NO_OPTION);
		 
		        if (choice == JOptionPane.YES_OPTION) {
		            // pass "this" so the back button can return here
		            RecommenderScreenGUI screen = new RecommenderScreenGUI("E", visibleNames, this);
		            this.setVisible(false); // hide inventory while recommender is open
		            screen.setVisible(true);
		        }
		 
		    } else {
		        // build comma-separated index string from checked rows
		        StringBuilder indices = new StringBuilder();
		        for (int i = 0; i < itemRows.size(); i++) {
		            if (itemRows.get(i).checkbox.isSelected()) {
		                if (indices.length() > 0) indices.append(",");
		                indices.append(i + 1);
		            }
		        }
		        // pass "this" so the back button can return here
		        RecommenderScreenGUI screen = new RecommenderScreenGUI(
		                indices.toString(), visibleNames, this);
		        this.setVisible(false); // hide inventory while recommender is open
		        screen.setVisible(true);
		    }
		}
	 
	 //Edit
	 private void handleEdit() {
		 ArrayList<ItemRow> checked = getCheckedRows();
		 //if none are checked - show a warning 
		 if (checked.isEmpty()) {
			 JOptionPane.showMessageDialog(this, "Please check at least one item to edit.", 
					 "Edit", JOptionPane.WARNING_MESSAGE); return;
		 }
		 //List of items with no-ed. attributes 
		 ArrayList<String> nonEditable = new ArrayList<>();
		 for (ItemRow r : checked) {
			 //loadCatalogEntry() looks up that supply in the materialcatalog
			 CraftSupply e = loadCatalogEntry(r.supply.getName());
			 if (e == null || (!e.needsColor() && !e.needsQuantity() && !e.needsSize()))
				 nonEditable.add(r.supply.getName());
		 }
		 if (!nonEditable.isEmpty()) {

			 //build string of non editable items
			 String s = "";
			 if(nonEditable.size() == 2) {
				 s = nonEditable.get(0) + " and " + nonEditable.get(1);
			 }else {
				 for(int i = 0; i < nonEditable.size(); i++) {
					 if(i > 0 && i == checked.size()-1) s += ", and ";
					 else if(i > 0) s += ", ";
					 s += nonEditable.get(i);
				 }
			 }
			 JOptionPane.showMessageDialog(this, s 
					 + (nonEditable.size() == 1 ? " does not" : " do not")
					 + " have any editable attributes.\nDeselect them and try again.", 
					 "Cannot edit", JOptionPane.WARNING_MESSAGE); return;
		 }
		 Inventory inv = new Inventory();
		 //items successfully saved
		 int updated = 0;
		 for (int page = 0; page < checked.size(); page++) {
			 //current inv. item being edited 
			 CraftSupply orig = checked.get(page).supply;
			 //checking which fields are editable 
			 CraftSupply cat = loadCatalogEntry(orig.getName());
			 JPanel form = new JPanel();
			 form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
			 form.setBorder(new EmptyBorder(10,10,10,10));
			 JLabel h = new JLabel("Editing " + orig.getName() + " (" + (page + 1) 
					 + " of " + checked.size() + ")");
			 h.setFont(basicGothicProBold.deriveFont(14f));
			 form.add(h);
			 form.add(Box.createVerticalStrut(12));
			 
			 //Text field variables for color, quantity and size
			 JTextField cf = null, qf = null, sf = null;
			 if (cat != null && cat.needsColor()) {
				 form.add(fLabel("Color:"));
				 cf = prefilled(orig.getColor(), "Enter color...");
				 form.add(cf);
				 form.add(Box.createVerticalStrut(8));
			 }
			 if (cat != null && cat.needsQuantity()) {
				 form.add(fLabel("Quantity:"));
				 qf = prefilled(orig.getQuantity(), "Enter quantity...");
				 form.add(qf);
				 form.add(Box.createVerticalStrut(8));
			 }
			 if (cat != null && cat.needsSize()) {
				 form.add(fLabel("Size:"));
				 sf = prefilled(orig.getSize(), "Enter size...");
				 form.add(sf);
				 form.add(Box.createVerticalStrut(8));
			 }
			 String[] opts = page < checked.size()-1 
					 ? new String[] {"Next", "Cancel"} : new String[]{"Submit", "Cancel"};
			 //this - parent window; form - the panel with fields inside the dialog;
			 //"Edit Craft Supply" - dialog title
			 //JOptionPane.DEFAULT_OPTION — use custom buttons 
			 //JOptionPane.PLAIN_MESSAGE — no icon
			 //null - no custom icon image
			 //opts - the button labels array
			 //opts[0] - which button is default 
			 //returns the ind of the button clicked 
			 if (JOptionPane.showOptionDialog(this, form, "Edit Craft Supply",
					 JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]) != 0) {
				 JOptionPane.showMessageDialog(this, "Edit cancelled. No changes saved.");
				 return;
			 }
			 if(inv.updateItem(orig, new CraftSupply(orig.getName(), orig.getType(), 
					 cf != null ? cf.getText().trim() : orig.getColor(),
					 qf != null ? qf.getText().trim() : orig.getQuantity(),
					 sf != null ? sf.getText().trim() : orig.getSize()))) updated++;
		 }
		 //Success message 
		 if (updated > 0) {
			 JOptionPane.showMessageDialog(this, updated + " craft " 
					 + (updated == 1 ? "supply was" : "supplies were") + " successfully updated.");
			 populateItemList();
		 }
	 }
	 
	 private void handleDelete() {
		 ArrayList<ItemRow> checked = getCheckedRows();
		 if(checked.isEmpty()) {
			 //get checked rows, warn and exit if none 
			 JOptionPane.showMessageDialog(this, "Please check at least one item to delete.",
					 "Delete", JOptionPane.WARNING_MESSAGE); 
			 return; 
		 }
		 StringBuilder names = new StringBuilder();
		 ArrayList<CraftSupply> toDelete = new ArrayList<>();
		 //check if there are two craft supplies so that there isn't a comma before 'and'
		 if(checked.size() == 2) {
			 names.append(checked.get(0).supply.getName());
			 toDelete.add(checked.get(0).supply);
			 names.append(" and ");
			 names.append(checked.get(1).supply.getName());
			 toDelete.add(checked.get(1).supply);
		 }else {
			 for (int i = 0; i < checked.size(); i++) {
				 if (i> 0 && i == checked.size()-1) names.append(", and ");
				 else if (i > 0) names.append(", ");
				 names.append(checked.get(i).supply.getName());
				 toDelete.add(checked.get(i).supply);
			 }
		 }
		 if (JOptionPane.showConfirmDialog(this, 
				 "Are you sure that you would like to delete your " + names + "?\nThis action cannot be undone.",
				 "Confirm Deletion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
			 Inventory inv = new Inventory();
			 inv.deleteItems(toDelete);
			 int c = toDelete.size();
			 JOptionPane.showMessageDialog(this, c == 1 ? "1 craft supply was deleted" : c + " craft supplies were deleted");
			 populateItemList();
		 }
		 
	 }
	 
	 //Creates a text field that either shows a value or a placeholder hint
	 private JTextField prefilled(String val, String placeholder) {
		 JTextField f = val.isEmpty() ? new JTextField(placeholder, 20) : new JTextField(val, 20);
		 if (val.isEmpty()) {
			 f.setForeground(Color.GRAY);
			 //when user clicks into the text field 
			 f.addFocusListener(new FocusAdapter() {
				 @Override public void focusGained(FocusEvent e) {
					 if (f.getText().equals(placeholder)) {
						 f.setText("");
						 f.setForeground(Color.BLACK);
					 }
				 }
				 //when the user clicks away
				 @Override public void focusLost(FocusEvent e) {
					 if (f.getText().isEmpty()) {
						 f.setText(placeholder);
						 f.setForeground(Color.GRAY);
					 }
				 }
			 });
		 }
		 f.setMaximumSize(new Dimension(300, 30));
		 return f;
	 }
	 
	 private JLabel fLabel(String t) {
		 JLabel l = new JLabel(t);
		 l.setFont(basicGothicProBold.deriveFont(12f));
		 return l;
	 }
	 
	 //returns the full catalog list 
	 private CraftSupply loadCatalogEntry(String name) {
		 for (CraftSupply cs : new CatalogScreen().getItems())
			 if (cs.getName().equalsIgnoreCase(name)) return cs;
		 return null;
	 }
	 
	 
	 //Loops through every tracked row
	 private ArrayList<ItemRow> getCheckedRows() {
		 ArrayList<ItemRow> r = new ArrayList<>();
		 for (ItemRow row : itemRows) if (row.checkbox.isSelected()) r.add(row);
		 return r;
	 }
	 
	 private void loadFonts() {
		 try {
			 GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			 comba = Font.createFont(Font.TRUETYPE_FONT, 
					 new File("fonts/Comba-Ultra-Wide.otf")).deriveFont(12f);
			 ge.registerFont(comba);
			 forager = Font.createFont(Font.TRUETYPE_FONT, 
					 new File("fonts/Forager-Bold.otf")).deriveFont(12f);
			 ge.registerFont(forager);
			 basicGothicProBold = Font.createFont(Font.TRUETYPE_FONT, 
					 new File("fonts/Basic-Gothic-Pro-Bold.otf")).deriveFont(12f);
			 ge.registerFont(basicGothicProBold);
			 basicGothicProBook = Font.createFont(Font.TRUETYPE_FONT, 
					 new File("fonts/Basic-Gothic-Pro-Book.otf")).deriveFont(12f);
			 ge.registerFont(basicGothicProBook);
			 basicGothicProDemibold = Font.createFont(Font.TRUETYPE_FONT, 
					 new File("fonts/Basic-Gothic-Pro-Demibold.otf")).deriveFont(12f);
			 ge.registerFont(basicGothicProDemibold);
			 basicGothicProBoldItalic = Font.createFont(Font.TRUETYPE_FONT, 
					 new File("fonts/Basic-Gothic-Pro-Bold-Italic.otf")).deriveFont(12f);
			 ge.registerFont(basicGothicProBoldItalic);
			 basicGothicProBookItalic = Font.createFont(Font.TRUETYPE_FONT, 
					 new File("fonts/Basic-Gothic-Pro-Book-Italic.otf")).deriveFont(12f);
			 ge.registerFont(basicGothicProBookItalic);
			 basicGothicProDemiboldItalic = Font.createFont(Font.TRUETYPE_FONT, 
					 new File("fonts/Basic-Gothic-Pro-Demibold-Italic.otf")).deriveFont(12f);
			 ge.registerFont(basicGothicProDemiboldItalic);
		 } catch (IOException | FontFormatException e) {
			 //Fallbacks already assigned at declaration - app continues 
		 }
	 }
	 
	 
	 public static void main (String[] args) {
		 SwingUtilities.invokeLater(() -> new InventoryScreenGUI().setVisible(true));
	 }
}

