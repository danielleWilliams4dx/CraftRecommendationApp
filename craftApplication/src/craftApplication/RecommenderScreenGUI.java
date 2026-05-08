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
        setSize(DriverGUI.windowSize);
        if (DriverGUI.isMaximized) setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_COLOR);
        body.add(buildTopBar(),    BorderLayout.NORTH);
        body.add(buildSlideshow(), BorderLayout.CENTER);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(buildNavBar(), BorderLayout.NORTH);
        wrapper.add(body, BorderLayout.CENTER);
        wrapper.setPreferredSize(new Dimension(900, 800));
        JScrollPane sp = new JScrollPane(wrapper);
        sp.setOpaque(false);
        root.add(sp);
       
        setContentPane(root);
        generateRecs(pendingMode, pendingVis);
    }

    private void generateRecs(String mode, ArrayList<String> vis) {
        Inventory inv = new Inventory();
        Recommender rec = new Recommender(true, mode, inv, vis);
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
        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        brand.setPreferredSize(new Dimension(250, 70));
        brand.setBorder(new EmptyBorder(0, 20, 0, 0));
        JLabel bl = new JLabel("CRAFT OVERFLOW");
        bl.setFont(comba.deriveFont(Font.BOLD, 13f));
        bl.setForeground(Color.WHITE);
        brand.add(bl, BorderLayout.CENTER);
        nav.add(brand, BorderLayout.WEST);
        JPanel tabs = new JPanel(new GridLayout(1, 4, 0, 0));
        tabs.setOpaque(false);
        for (String t : new String[]{"Home", "Saved Crafts", "Inventory", "Catalog"}) {
            tabs.add(buildNavTab(t));
        }
        nav.add(tabs, BorderLayout.CENTER);
        return nav;
    }

    private JPanel buildNavTab(String label) {
        JPanel tab = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) { super.paintComponent(g); }
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
            @Override public void mouseClicked(MouseEvent e) { switchScreens(label); }
        });
        return tab;
    }

    // Title bar — same padding as Inventory/Catalog (60 top, 60 bottom)
    // Back button WEST, title CENTER, invisible spacer EAST to keep title truly centered
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Back button
        JButton back = new JButton("← Back to Inventory") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BTN_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                g2.dispose();
            }
        };
        back.setFont(basicGothicProBook.deriveFont(12f));
        back.setForeground(ACCENT_COLOR);
        back.setBackground(BG_COLOR);
        back.setContentAreaFilled(false);
        back.setFocusPainted(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setBorder(new EmptyBorder(5, 12, 5, 12));
        back.addActionListener(e -> {
            if (callerWindow != null) { callerWindow.setVisible(true); callerWindow.toFront(); }
            dispose();
        });
        back.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { back.setBackground(new Color(220, 185, 200)); back.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { back.setBackground(BG_COLOR); back.repaint(); }
        });

        // Wrap back button to align it vertically in the middle of the title area
        JPanel backWrapper = new JPanel(new GridBagLayout());
        backWrapper.setOpaque(false);
        backWrapper.add(back, new GridBagConstraints());

        // Title panel — same structure as InventoryScreenGUI.buildTitleSection()
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(60, 0, 60, 0));

        JLabel title = new JLabel("Recommendations");
        title.setFont(forager.deriveFont(52f));
        title.setForeground(ACCENT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel line = new JPanel();
        line.setBackground(ACCENT_COLOR);
        line.setMaximumSize(new Dimension(300, 3));
        line.setPreferredSize(new Dimension(300, 3));
        line.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(line);

        // Right spacer same width as back button wrapper so title stays centered
        JPanel rightSpacer = new JPanel();
        rightSpacer.setOpaque(false);
        rightSpacer.setPreferredSize(backWrapper.getPreferredSize());

        bar.add(backWrapper,  BorderLayout.WEST);
        bar.add(titlePanel,   BorderLayout.CENTER);
        bar.add(rightSpacer,  BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSlideshow() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(4, 10, 8, 10));

        prevBtn = arrowBtn("prev");
        nextBtn = arrowBtn("next");
        prevBtn.addActionListener(e -> { if (currentIndex > 0)               { currentIndex--; refreshCard(); } });
        nextBtn.addActionListener(e -> { if (currentIndex < recs.size() - 1) { currentIndex++; refreshCard(); } });

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
        if (recs == null || recs.isEmpty()) {
            JLabel none = new JLabel("No recommendations found for your current inventory.");
            none.setFont(basicGothicProBook.deriveFont(14f));
            none.setForeground(TEXT_LIGHT);
            cardArea.add(none, new GridBagConstraints());
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
            counterLabel.setText("");
        } else {
            cardArea.add(buildCard(recs.get(currentIndex), recs.size() > 1), new GridBagConstraints());
            prevBtn.setEnabled(currentIndex > 0);
            nextBtn.setEnabled(currentIndex < recs.size() - 1);
            prevBtn.setForeground(prevBtn.isEnabled() ? ACCENT_COLOR : new Color(200, 180, 190));
            nextBtn.setForeground(nextBtn.isEnabled() ? ACCENT_COLOR : new Color(200, 180, 190));
            prevBtn.repaint();
            nextBtn.repaint();
            counterLabel.setText((currentIndex + 1) + " of " + recs.size());
        }
        cardArea.revalidate();
        cardArea.repaint();
    }

    // Same layout as SavedCraftsScreenGUI.buildDetailCard() — every child LEFT_ALIGNMENT
    private JPanel buildCard(Craft craft, boolean showShadow) {
        String[] p = craft.getLine().split(",");
        String name  = p.length > 0 ? p[0].trim() : "";
        String level = p.length > 2 ? p[2].trim() : "";
        String time  = p.length > 3 ? p[3].trim() : "";
        String desc  = p.length > 4 ? p[4].trim() : "";

        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (showShadow) { g2.setColor(CARD_SHADOW); g2.fillRoundRect(5, 5, getWidth() - 1, getHeight() - 1, 20, 20); }
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(340, 490));

        // image — pink theme
        JPanel img = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 200, 215));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() + 20, 20, 20);
                g2.setColor(new Color(180, 140, 160));
                g2.setFont(basicGothicProBook.deriveFont(10f));
                FontMetrics fm = g2.getFontMetrics();
                String t = "[ craft image ]";
                g2.drawString(t, (getWidth() - 6 - fm.stringWidth(t)) / 2, getHeight() / 2 + fm.getAscent() / 2);
                g2.dispose();
            }
        };
        img.setOpaque(false);
        img.setPreferredSize(new Dimension(334, 130));

        // content — every child LEFT_ALIGNMENT
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 14, 10, 18));

        // name + save
        JPanel nameRow = new JPanel(new BorderLayout(6, 0));
        nameRow.setOpaque(false);
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(basicGothicProBold.deriveFont(19f));
        nameLbl.setForeground(TEXT_DARK);
        nameRow.add(nameLbl, BorderLayout.CENTER);
        nameRow.add(saveBtn(craft), BorderLayout.EAST);
        content.add(nameRow);
        content.add(Box.createVerticalStrut(8));

        // level badge + time — left aligned
        JPanel levelTimeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        levelTimeRow.setOpaque(false);
        levelTimeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        levelTimeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        if (!level.isEmpty()) levelTimeRow.add(levelBadge(level));
        if (!time.isEmpty()) {
            JLabel tl = new JLabel(time);
            tl.setFont(basicGothicProBook.deriveFont(14f));
            tl.setForeground(TEXT_LIGHT);
            levelTimeRow.add(tl);
        }
        content.add(levelTimeRow);
        content.add(Box.createVerticalStrut(10));

        // separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 200, 210));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(sep);
        content.add(Box.createVerticalStrut(7));

        // Materials
        JLabel matHdr = new JLabel("Materials");
        matHdr.setFont(basicGothicProBold.deriveFont(15f));
        matHdr.setForeground(TEXT_DARK);
        matHdr.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(matHdr);
        content.add(Box.createVerticalStrut(4));

        for (String mat : craft.getMaterials()) {
            String m = mat.trim();
            boolean owned = availableItems.contains(m.toLowerCase());
            JPanel mr = materialRow(m, owned);
            mr.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(mr);
            content.add(Box.createVerticalStrut(2));
        }

        content.add(Box.createVerticalStrut(7));

        // Description
        if (!desc.isEmpty()) {
            JLabel dh = new JLabel("Description");
            dh.setFont(basicGothicProBold.deriveFont(15f));
            dh.setForeground(TEXT_DARK);
            dh.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(dh);
            content.add(Box.createVerticalStrut(3));
            JLabel dt = new JLabel("<html><body style='width:270px;font-size:12px'>" + desc + "</body></html>");
            dt.setFont(basicGothicProBook.deriveFont(13f));
            dt.setForeground(TEXT_LIGHT);
            dt.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(dt);
            content.add(Box.createVerticalStrut(9));
        }

        JButton vb = viewBtn(craft);
        vb.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(vb);

        card.add(img,     BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // Fixed circles using BoxLayout X_AXIS with rigid JPanel — pink theme colors
    private JPanel materialRow(String material, boolean owned) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.setOpaque(false);

        JPanel circleBox = new JPanel() {
            @Override public Dimension getPreferredSize() { return new Dimension(18, 18); }
            @Override public Dimension getMinimumSize()   { return new Dimension(18, 18); }
            @Override public Dimension getMaximumSize()   { return new Dimension(18, 18); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 215, 225));  // pink theme
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(160, 110, 130));  // pink theme
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        circleBox.setOpaque(false);
        circleBox.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel nl = new JLabel(material);
        nl.setFont(basicGothicProBook.deriveFont(13f));
        nl.setForeground(owned ? TEXT_DARK : MISSING_RED);
        nl.setBorder(new EmptyBorder(0, 6, 0, 0));
        nl.setAlignmentY(Component.CENTER_ALIGNMENT);

        left.add(circleBox);
        left.add(nl);

        JPanel iconBox = new JPanel() {
            @Override public Dimension getPreferredSize() { return new Dimension(18, 18); }
            @Override public Dimension getMinimumSize()   { return new Dimension(18, 18); }
            @Override public Dimension getMaximumSize()   { return new Dimension(18, 18); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(owned ? CHECK_GREEN : MISSING_RED);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                FontMetrics fm = g2.getFontMetrics();
                String sym = owned ? "✓" : "✗";
                g2.drawString(sym, (getWidth() - fm.stringWidth(sym)) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 1);
                g2.dispose();
            }
        };
        iconBox.setOpaque(false);

        JPanel iconWrapper = new JPanel(new GridBagLayout());
        iconWrapper.setOpaque(false);
        iconWrapper.setPreferredSize(new Dimension(26, 26));
        iconWrapper.setMinimumSize(new Dimension(26, 26));
        iconWrapper.setMaximumSize(new Dimension(26, 26));
        iconWrapper.add(iconBox, new GridBagConstraints());

        row.add(left,        BorderLayout.CENTER);
        row.add(iconWrapper, BorderLayout.EAST);
        return row;
    }

    // White fill with pink outline — matches Figma
    private JLabel levelBadge(String level) {
        JLabel b = new JLabel(level) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(200, 160, 180));  // pink outline
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setOpaque(false);
        b.setFont(basicGothicProBook.deriveFont(13f));
        b.setForeground(TEXT_DARK);
        b.setBorder(new EmptyBorder(4, 14, 4, 14));
        return b;
    }

    private JButton saveBtn(Craft craft) {
        JButton btn = new JButton("↓ Save") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 150, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.dispose();
            }
        };
        btn.setFont(basicGothicProBook.deriveFont(12f));
        btn.setForeground(new Color(40, 100, 40));
        btn.setBackground(new Color(220, 245, 220));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(3, 9, 3, 9));
        btn.addActionListener(e -> {
            craft.save();
            JOptionPane.showMessageDialog(this, "\"" + craft.getLine().split(",")[0].trim() + "\" saved!");
        });
        return btn;
    }

    private JButton viewBtn(Craft craft) {
        JButton btn = new JButton("View instructions  ▼") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setFont(basicGothicProBook.deriveFont(14f));
        btn.setForeground(VIEW_FG);
        btn.setBackground(VIEW_BG);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                craft.showInstructions()));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(150, 200, 150)); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(VIEW_BG); btn.repaint(); }
        });
        return btn;
    }

    private JButton arrowBtn(String direction) {
        boolean isLeft = direction.equals("prev");
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? ACCENT_COLOR : new Color(200, 180, 190));
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2, size = 10;
                if (isLeft) {
                    g2.drawLine(cx + size / 2, cy - size, cx - size / 2, cy);
                    g2.drawLine(cx - size / 2, cy,        cx + size / 2, cy + size);
                } else {
                    g2.drawLine(cx - size / 2, cy - size, cx + size / 2, cy);
                    g2.drawLine(cx + size / 2, cy,        cx - size / 2, cy + size);
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
        DriverGUI.windowSize  = this.getSize();
        DriverGUI.isMaximized = (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
        this.setVisible(false);
        if (name.equals("Home")) {
            HomeScreenGUI screen = new HomeScreenGUI();
            if (DriverGUI.isMaximized) screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
            screen.setVisible(true);
        } else if (name.equals("Inventory")) {
            InventoryScreenGUI screen = new InventoryScreenGUI();
            if (DriverGUI.isMaximized) screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
            screen.setVisible(true);
        } else if (name.equals("Catalog")) {
            CatalogScreenGUI screen = new CatalogScreenGUI();
            if (DriverGUI.isMaximized) screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
            screen.setVisible(true);
        } else if (name.equals("Saved Crafts")) {
            SavedCraftsScreenGUI screen = new SavedCraftsScreenGUI();
            if (DriverGUI.isMaximized) screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
            screen.setVisible(true);
        }
    }

    private void loadFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            comba              = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Comba-Ultra-Wide.otf")).deriveFont(12f); ge.registerFont(comba);
            forager            = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Forager-Bold.otf")).deriveFont(12f);     ge.registerFont(forager);
            basicGothicProBold = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Basic-Gothic-Pro-Bold.otf")).deriveFont(12f); ge.registerFont(basicGothicProBold);
            basicGothicProBook = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Basic-Gothic-Pro-Book.otf")).deriveFont(12f); ge.registerFont(basicGothicProBook);
        } catch (IOException | FontFormatException e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RecommenderScreenGUI().setVisible(true));
    }
}