package craftApplication;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RecommenderScreenGUI extends JFrame {

    private static final Color BG_COLOR    = new Color(244, 216, 227);
    private static final Color NAV_COLOR   = new Color(126, 78, 96);
    private static final Color ACCENT_COLOR= new Color(100, 30, 65);
    private static final Color TEXT_DARK   = new Color(70, 20, 50);
    private static final Color TEXT_LIGHT  = new Color(140, 90, 110);
    private static final Color BTN_BORDER  = new Color(130, 70, 100);
    private static final Color CARD_BG     = Color.WHITE;
    private static final Color CARD_SHADOW = new Color(197, 197, 197);
    private static final Color MISSING_RED = new Color(200, 40, 40);
    private static final Color CHECK_GREEN = new Color(60, 150, 80);
    private static final Color VIEW_BG     = new Color(180, 220, 180);
    private static final Color VIEW_FG     = new Color(40, 100, 40);

    private Font comba              = new Font("SansSerif", Font.BOLD,  13);
    private Font forager            = new Font("Serif",     Font.BOLD,  36);
    private Font basicGothicProBold = new Font("SansSerif", Font.BOLD,  13);
    private Font basicGothicProBook = new Font("SansSerif", Font.PLAIN, 13);

    private ArrayList<Craft>  recs          = new ArrayList<>();
    private ArrayList<String> availableItems = new ArrayList<>();
    private int               currentIndex  = 0;
    private JPanel            cardArea;
    private JButton           prevBtn, nextBtn;
    private JLabel            counterLabel;
    private JFrame            callerWindow;
    // Stored so setup() can call generateRecs after UI is fully built
    private String            pendingMode;
    private ArrayList<String> pendingVis;

    public RecommenderScreenGUI() {
        loadFonts();
        Inventory inv = new Inventory();
        pendingMode = "E";
        pendingVis  = inv.getVisibleItemNames(new ArrayList<>());
        setup();
    }
    public RecommenderScreenGUI(String mode, ArrayList<String> vis) {
        loadFonts();
        pendingMode = mode;
        pendingVis  = vis;
        setup();
    }
    public RecommenderScreenGUI(String mode, ArrayList<String> vis, JFrame caller) {
        loadFonts();
        this.callerWindow = caller;
        pendingMode = mode;
        pendingVis  = vis;
        setup();
    }

    private void setup() {
        setTitle("Craft Overflow — Recommendations");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_COLOR);
        body.add(buildTopBar(),   BorderLayout.NORTH);
        body.add(buildSlideshow(),BorderLayout.CENTER);
        root.add(buildNavBar(), BorderLayout.NORTH);
        root.add(body,          BorderLayout.CENTER);
        setContentPane(root);
      
        generateRecs(pendingMode, pendingVis);
    }

    private void generateRecs(String mode, ArrayList<String> vis) {
        // Called from end of setup() so cardArea is guaranteed to exist
        
        Inventory inv = new Inventory();
        Recommender rec = new Recommender(mode, inv, vis);
        recs = rec.recs;
        availableItems = rec.getAvailableItemNames();
        currentIndex = 0;
        if (recs == null || recs.isEmpty()) {
        	System.out.println("No recommendation found");
        	recs = new ArrayList<>();
        }
        refreshCard(); 
    }

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(NAV_COLOR);
        nav.setPreferredSize(new Dimension(1000, 70));

        // Brand panel — matches InventoryScreenGUI 
        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        brand.setPreferredSize(new Dimension(250, 70));
        brand.setBorder(new EmptyBorder(0, 20, 0, 0));
        JLabel bl = new JLabel("CRAFT OVERFLOW");
        bl.setFont(comba.deriveFont(Font.BOLD, 13f));
        bl.setForeground(Color.WHITE);
        brand.add(bl, BorderLayout.CENTER);
        nav.add(brand, BorderLayout.WEST);

        // Tabs — GridLayout(1,4) so all tabs share width equally
        JPanel tabs = new JPanel(new GridLayout(1, 4, 0, 0));
        tabs.setOpaque(false);
        for (String t : new String[]{"Home","Saved Crafts","Inventory","Catalog"}) {
            tabs.add(buildNavTab(t));
        }
        nav.add(tabs, BorderLayout.CENTER);
        return nav;
    }

    // Matches InventoryScreenGUI buildNavTab exactly
    private JPanel buildNavTab(String label) {
        JPanel tab = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        tab.setOpaque(false);
        tab.setPreferredSize(new Dimension(160, 70));
        tab.setBorder(new EmptyBorder(20, 20, 0, 20));
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(basicGothicProBook.deriveFont(14f));
        lbl.setForeground(Color.WHITE);
        tab.add(lbl, BorderLayout.CENTER);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab.addMouseListener(new MouseAdapter() {
			 @Override public void mouseClicked(MouseEvent e) {
				 switchScreens(label); 
			 }
		 });
        return tab;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(null) {
            @Override public void doLayout() {
                int w = getWidth();
                for (Component c : getComponents()) {
                    if (c instanceof JButton) c.setBounds(18, 56, 155, 30);
                    else { int pw=560; c.setBounds((w-pw)/2, 40, pw, 82); }
                }
            }
            @Override public Dimension getPreferredSize() { return new Dimension(1000, 130); }
        };
        bar.setOpaque(false);

        JButton back = new JButton("← Back to Inventory") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                super.paintComponent(g); g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BTN_BORDER); g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,12,12); g2.dispose();
            }
        };
        back.setFont(basicGothicProBook.deriveFont(12f));
        back.setForeground(ACCENT_COLOR); back.setBackground(BG_COLOR);
        back.setContentAreaFilled(false); back.setFocusPainted(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setBorder(new EmptyBorder(5,12,5,12));
        back.addActionListener(e -> { if(callerWindow!=null){callerWindow.setVisible(true);callerWindow.toFront();} dispose(); });
        back.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){back.setBackground(new Color(220,185,200));back.repaint();}
            @Override public void mouseExited(MouseEvent e){back.setBackground(BG_COLOR);back.repaint();}
        });
        bar.add(back);

        JPanel tp = new JPanel();
        tp.setLayout(new BoxLayout(tp, BoxLayout.Y_AXIS));
        tp.setOpaque(false);
        JLabel title = new JLabel("Recommendations");
        title.setFont(forager.deriveFont(52f));
        title.setForeground(ACCENT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel line = new JPanel();
        line.setBackground(ACCENT_COLOR);
        line.setMaximumSize(new Dimension(220,3));
        line.setPreferredSize(new Dimension(220,3));
        line.setAlignmentX(Component.CENTER_ALIGNMENT);
        tp.add(title); tp.add(Box.createVerticalStrut(3)); tp.add(line);
        bar.add(tp);
        return bar;
    }

    private JPanel buildSlideshow() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(4, 10, 8, 10));

        // Arrow wrapper panels - give arrows a fixed 60px wide column
        prevBtn = arrowBtn("prev");
        nextBtn = arrowBtn("next");
        prevBtn.addActionListener(e -> { if (currentIndex > 0) { currentIndex--; refreshCard(); }});
        nextBtn.addActionListener(e -> { if (currentIndex < recs.size()-1) { currentIndex++; refreshCard(); }});

        JPanel prevWrapper = new JPanel(new GridBagLayout());
        prevWrapper.setOpaque(false);
        prevWrapper.setPreferredSize(new Dimension(60, 400));
        prevWrapper.add(prevBtn);

        JPanel nextWrapper = new JPanel(new GridBagLayout());
        nextWrapper.setOpaque(false);
        nextWrapper.setPreferredSize(new Dimension(60, 400));
        nextWrapper.add(nextBtn);

        cardArea = new JPanel(new GridBagLayout());
        cardArea.setOpaque(false);

        counterLabel = new JLabel("", SwingConstants.CENTER);
        counterLabel.setFont(basicGothicProBook.deriveFont(12f));
        counterLabel.setForeground(TEXT_LIGHT);
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        south.add(counterLabel);

        outer.add(prevWrapper, BorderLayout.WEST);
        outer.add(cardArea,    BorderLayout.CENTER);
        outer.add(nextWrapper, BorderLayout.EAST);
        outer.add(south,       BorderLayout.SOUTH);
        return outer;
    }

    private void refreshCard() {
        if (cardArea == null) return;
        cardArea.removeAll();
        if (recs==null||recs.isEmpty()) {
            JLabel none = new JLabel("No recommendations found for your current inventory.");
            none.setFont(basicGothicProBook.deriveFont(14f)); none.setForeground(TEXT_LIGHT);
            cardArea.add(none, new GridBagConstraints());
            prevBtn.setEnabled(false); nextBtn.setEnabled(false); counterLabel.setText("");
        } else {
            cardArea.add(buildCard(recs.get(currentIndex), recs.size()>1), new GridBagConstraints());
            prevBtn.setEnabled(currentIndex>0);
            nextBtn.setEnabled(currentIndex<recs.size()-1);
            prevBtn.setForeground(prevBtn.isEnabled()?ACCENT_COLOR:new Color(200,180,190));
            nextBtn.setForeground(nextBtn.isEnabled()?ACCENT_COLOR:new Color(200,180,190));
            prevBtn.repaint(); nextBtn.repaint();
            counterLabel.setText((currentIndex+1)+" of "+recs.size());
        }
        cardArea.revalidate(); cardArea.repaint();
    }

    private JPanel buildCard(Craft craft, boolean showShadow) {
        String[] p = craft.getLine().split(",");
        String name  = p.length>0 ? p[0].trim() : "";
        String level = p.length>2 ? p[2].trim() : "";
        String time  = p.length>3 ? p[3].trim() : "";
        String desc  = p.length>4 ? p[4].trim() : "";

        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(showShadow){ g2.setColor(CARD_SHADOW); g2.fillRoundRect(5,5,getWidth()-1,getHeight()-1,20,20); }
                g2.setColor(CARD_BG); g2.fillRoundRect(0,0,getWidth()-6,getHeight()-6,20,20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(340, 490));

        // Image
        JPanel img = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230,200,215));
                g2.fillRoundRect(0,0,getWidth()-6,getHeight()+20,20,20);
                g2.setColor(new Color(180,140,160));
                g2.setFont(basicGothicProBook.deriveFont(10f));
                FontMetrics fm=g2.getFontMetrics(); String t="[ craft image ]";
                g2.drawString(t,(getWidth()-6-fm.stringWidth(t))/2, getHeight()/2+fm.getAscent()/2);
                g2.dispose();
            }
        };
        img.setOpaque(false);
        img.setPreferredSize(new Dimension(334, 130));

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10,14,10,18));

        // Name n save
        JPanel nameRow = new JPanel(new BorderLayout(6,0));
        nameRow.setOpaque(false);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,28));
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(basicGothicProBold.deriveFont(17f));
        nameLbl.setForeground(TEXT_DARK);
        nameRow.add(nameLbl, BorderLayout.CENTER);
        nameRow.add(saveBtn(craft), BorderLayout.EAST);
        content.add(nameRow);
        content.add(Box.createVerticalStrut(5));

        // Badge row
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        badgeRow.setOpaque(false);
        badgeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,24));
        if(!level.isEmpty()) badgeRow.add(levelBadge(level));
        if(!time.isEmpty()){
            JLabel tl=new JLabel(time);
            tl.setFont(basicGothicProBook.deriveFont(13f)); tl.setForeground(TEXT_LIGHT);
            badgeRow.add(tl);
        }
        content.add(badgeRow);
        content.add(Box.createVerticalStrut(7));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220,200,210));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        content.add(sep);
        content.add(Box.createVerticalStrut(7));

        JLabel matHdr = new JLabel("Materials");
        matHdr.setFont(basicGothicProBold.deriveFont(14f)); matHdr.setForeground(TEXT_DARK);
        content.add(matHdr);
        content.add(Box.createVerticalStrut(4));

        for(String mat : craft.getMaterials()){
            String m=mat.trim();
            boolean owned=availableItems.contains(m.toLowerCase());
            content.add(materialRow(m,owned));
            content.add(Box.createVerticalStrut(2));
        }

        content.add(Box.createVerticalStrut(7));

        if(!desc.isEmpty()){
            JLabel dh=new JLabel("Description");
            dh.setFont(basicGothicProBold.deriveFont(14f)); dh.setForeground(TEXT_DARK);
            content.add(dh);
            content.add(Box.createVerticalStrut(3));
            JLabel dt=new JLabel("<html><body style='width:250px;font-size:12px'>"+desc+"</body></html>");
            dt.setFont(basicGothicProBook.deriveFont(12f)); dt.setForeground(TEXT_LIGHT);
            content.add(dt);
            content.add(Box.createVerticalStrut(9));
        }

        JButton vb=viewBtn(craft);
        vb.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(vb);

        card.add(img, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel materialRow(String material, boolean owned){
        JPanel row=new JPanel(new BorderLayout(5,0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE,22));
        JPanel left=new JPanel(new FlowLayout(FlowLayout.LEFT,4,0));
        left.setOpaque(false);
        JPanel circle=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235,215,225)); g2.fillOval(0,0,getWidth(),getHeight());
                g2.setColor(new Color(160,110,130)); g2.setStroke(new BasicStroke(1f));
                g2.drawOval(0,0,getWidth()-1,getHeight()-1); g2.dispose();
            }
        };
        circle.setOpaque(false); circle.setPreferredSize(new Dimension(16,16));
        JLabel nl=new JLabel(material);
        nl.setFont(basicGothicProBook.deriveFont(13f));
        nl.setForeground(owned?TEXT_DARK:MISSING_RED);
        left.add(circle); left.add(nl);
        JLabel icon=new JLabel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(owned?CHECK_GREEN:MISSING_RED); g2.fillOval(0,0,getWidth(),getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif",Font.BOLD,9));
                FontMetrics fm=g2.getFontMetrics(); String sym=owned?"✓":"✗";
                g2.drawString(sym,(getWidth()-fm.stringWidth(sym))/2,(getHeight()+fm.getAscent())/2-1);
                g2.dispose();
            }
        };
        icon.setOpaque(false); icon.setPreferredSize(new Dimension(16,16));
        row.add(left, BorderLayout.CENTER);
        row.add(icon, BorderLayout.EAST);
        return row;
    }

    private JLabel levelBadge(String level){
        JLabel b=new JLabel(level){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(244,216,227)); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setOpaque(false); b.setFont(basicGothicProBook.deriveFont(13f));
        b.setForeground(ACCENT_COLOR); b.setBorder(new EmptyBorder(2,8,2,8));
        return b;
    }

    private JButton saveBtn(Craft craft){
        JButton btn=new JButton("↓ Save"){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                super.paintComponent(g); g2.dispose();
            }
            @Override protected void paintBorder(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60,150,80)); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,10,10); g2.dispose();
            }
        };
        btn.setFont(basicGothicProBook.deriveFont(12f));
        btn.setForeground(new Color(40,100,40)); btn.setBackground(new Color(220,245,220));
        btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); btn.setBorder(new EmptyBorder(3,9,3,9));
        btn.addActionListener(e->{ craft.save(); JOptionPane.showMessageDialog(this,"\""+craft.getLine().split(",")[0].trim()+"\" saved!"); });
        return btn;
    }

    private JButton viewBtn(Craft craft){
        JButton btn=new JButton("View instructions  ▼"){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                super.paintComponent(g); g2.dispose();
            }
            @Override protected void paintBorder(Graphics g){}
        };
        btn.setFont(basicGothicProBook.deriveFont(14f));
        btn.setForeground(VIEW_FG); btn.setBackground(VIEW_BG);
        btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); btn.setBorder(new EmptyBorder(8,20,8,20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
        btn.addActionListener(e->JOptionPane.showMessageDialog(this,"Instructions for: "+craft.getLine().split(",")[0].trim()+"\n\n(Connect to web scraper here)"));
        btn.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){btn.setBackground(new Color(150,200,150));btn.repaint();}
            @Override public void mouseExited(MouseEvent e){btn.setBackground(VIEW_BG);btn.repaint();}
        });
        return btn;
    }

    private JButton arrowBtn(String direction) {
        boolean isLeft = direction.equals("prev");
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? ACCENT_COLOR : new Color(200, 180, 190));
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int size = 10;
                if (isLeft) {
                    // draw left-pointing chevron  <
                    g2.drawLine(cx + size/2, cy - size, cx - size/2, cy);
                    g2.drawLine(cx - size/2, cy, cx + size/2, cy + size);
                } else {
                    // draw right-pointing chevron  >
                    g2.drawLine(cx - size/2, cy - size, cx + size/2, cy);
                    g2.drawLine(cx + size/2, cy, cx - size/2, cy + size);
                }
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(40, 40));
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

    private void loadFonts(){
        try {
            GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
            comba=Font.createFont(Font.TRUETYPE_FONT,new File("fonts/Comba-Ultra-Wide.otf")).deriveFont(12f); ge.registerFont(comba);
            forager=Font.createFont(Font.TRUETYPE_FONT,new File("fonts/Forager-Bold.otf")).deriveFont(12f); ge.registerFont(forager);
            basicGothicProBold=Font.createFont(Font.TRUETYPE_FONT,new File("fonts/Basic-Gothic-Pro-Bold.otf")).deriveFont(12f); ge.registerFont(basicGothicProBold);
            basicGothicProBook=Font.createFont(Font.TRUETYPE_FONT,new File("fonts/Basic-Gothic-Pro-Book.otf")).deriveFont(12f); ge.registerFont(basicGothicProBook);
        } catch(IOException|FontFormatException e){}
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->new RecommenderScreenGUI().setVisible(true));
    }
}