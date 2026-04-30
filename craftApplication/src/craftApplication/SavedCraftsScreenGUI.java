package craftApplication;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SavedCraftsScreenGUI extends JFrame {

    private static final Color BG_COLOR     = new Color(207, 223, 242);
    private static final Color NAV_COLOR    = new Color(35,  77,  128);
    private static final Color ACCENT_COLOR = new Color(35,  77,  128);
    private static final Color TEXT_DARK    = new Color(18,  40,  75);
    private static final Color TEXT_LIGHT   = new Color(80,  110, 160);
    private static final Color BTN_BORDER   = new Color(60,  100, 160);
    private static final Color CARD_BG      = Color.WHITE;
    private static final Color CARD_SHADOW  = new Color(180, 200, 225);
    private static final Color MISSING_RED  = new Color(200, 40,  40);
    private static final Color CHECK_GREEN  = new Color(60,  150, 80);
    private static final Color VIEW_BG      = new Color(180, 220, 180);
    private static final Color VIEW_FG      = new Color(40,  100, 40);
    private static final Color EMPTY_CARD   = new Color(225, 235, 245);

    private Font comba              = new Font("SansSerif", Font.BOLD,  13);
    private Font forager            = new Font("Serif",     Font.BOLD,  36);
    private Font basicGothicProBold = new Font("SansSerif", Font.BOLD,  13);
    private Font basicGothicProBook = new Font("SansSerif", Font.PLAIN, 13);

    private ArrayList<Craft>  savedCrafts  = new ArrayList<>();
    private ArrayList<String> invItemNames = new ArrayList<>();
    private JPanel            gridPanel;

    public SavedCraftsScreenGUI() {
        loadFonts();
        setTitle("Craft Overflow — Saved Crafts");
        setSize(DriverGUI.windowSize);
        if (DriverGUI.isMaximized) setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        loadData();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(buildNavBar(),   BorderLayout.NORTH);
        wrapper.add(buildMainArea(), BorderLayout.CENTER);
        wrapper.setPreferredSize(new Dimension(900, 900));
        JScrollPane sp = new JScrollPane(wrapper);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        root.add(sp);
        setContentPane(root);
    }

    private void loadData() {
        savedCrafts.clear();
        invItemNames.clear();
        Inventory inv = new Inventory();
        invItemNames = inv.getJustItemNames();
        java.io.BufferedReader br = null;
        try {
            br = new java.io.BufferedReader(new java.io.FileReader("savedcrafts.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                savedCrafts.add(new Craft(line));
            }
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (br != null) try { br.close(); } catch (java.io.IOException ignored) {}
        }
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
            tabs.add(buildNavTab(t, t.equals("Saved Crafts")));
        }
        nav.add(tabs, BorderLayout.CENTER);
        return nav;
    }

    private JPanel buildNavTab(String label, boolean active) {
        JPanel tab = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                if (active) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BG_COLOR);
                    g2.fillRoundRect(0, 15, getWidth(), getHeight() + 20, 30, 30);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        tab.setOpaque(false);
        tab.setPreferredSize(new Dimension(160, 70));
        tab.setBorder(new EmptyBorder(20, 20, 0, 20));
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(active ? basicGothicProBold.deriveFont(14f) : basicGothicProBook.deriveFont(14f));
        lbl.setForeground(active ? ACCENT_COLOR : Color.WHITE);
        tab.add(lbl, BorderLayout.CENTER);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { switchScreens(label); }
        });
        return tab;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(buildTitleSection(), BorderLayout.NORTH);
        main.add(buildGridArea(),     BorderLayout.CENTER);
        return main;
    }

    private JPanel buildTitleSection() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(60, 0, 60, 0));
        JLabel title = new JLabel("Saved Crafts");
        title.setFont(forager.deriveFont(52f));
        title.setForeground(ACCENT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel underline = new JPanel();
        underline.setBackground(ACCENT_COLOR);
        underline.setMaximumSize(new Dimension(280, 3));
        underline.setPreferredSize(new Dimension(280, 3));
        underline.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(8));
        p.add(underline);
        return p;
    }

    private JPanel buildGridArea() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(10, 40, 40, 40));
        gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setOpaque(false);
        populateGrid();
        outer.add(gridPanel, BorderLayout.NORTH);
        return outer;
    }

    private void populateGrid() {
        gridPanel.removeAll();
        for (int i = 0; i < 6; i++) {
            gridPanel.add(i < savedCrafts.size() ? buildCraftCard(savedCrafts.get(i)) : buildEmptyCard());
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel buildCraftCard(Craft craft) {
        String[] p = craft.getLine().split(",");
        String name  = p.length > 0 ? p[0].trim() : "";
        String level = p.length > 2 ? p[2].trim() : "";
        String type  = p.length > 1 ? p[1].trim() : "";
        String time  = p.length > 3 ? p[3].trim() : "";

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_SHADOW);
                g2.fillRoundRect(4, 4, getWidth() - 2, getHeight() - 2, 24, 24);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 24, 24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(260, 340));

        JPanel img = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(210, 225, 240));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() + 24, 24, 24);
                g2.setColor(new Color(140, 170, 200));
                g2.setFont(basicGothicProBook.deriveFont(11f));
                FontMetrics fm = g2.getFontMetrics();
                String t = "[ craft image ]";
                g2.drawString(t, (getWidth() - 6 - fm.stringWidth(t)) / 2, getHeight() / 2 + fm.getAscent() / 2);
                g2.dispose();
            }
        };
        img.setOpaque(false);
        img.setPreferredSize(new Dimension(254, 180));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(12, 14, 14, 14));

        JLabel nameLbl = new JLabel(name, SwingConstants.CENTER);
        nameLbl.setFont(basicGothicProBold.deriveFont(20f));
        nameLbl.setForeground(TEXT_DARK);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(nameLbl);
        content.add(Box.createVerticalStrut(10));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        badgeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        if (!level.isEmpty()) badgeRow.add(wideBadge(level));
        if (!type.isEmpty())  badgeRow.add(wideBadge(type));
        content.add(badgeRow);
        content.add(Box.createVerticalStrut(10));

        if (!time.isEmpty()) {
            JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            timeRow.setOpaque(false);
            timeRow.setAlignmentX(Component.CENTER_ALIGNMENT);
            timeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
            JLabel clockIcon = new JLabel() {
                @Override public Dimension getPreferredSize() { return new Dimension(18, 18); }
                @Override public Dimension getMinimumSize()   { return new Dimension(18, 18); }
                @Override public Dimension getMaximumSize()   { return new Dimension(18, 18); }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_DARK);
                    g2.setStroke(new BasicStroke(1.8f));
                    g2.drawOval(1, 1, getWidth() - 2, getHeight() - 2);
                    int cx = getWidth() / 2, cy = getHeight() / 2;
                    g2.drawLine(cx, cy, cx, cy - 5);
                    g2.drawLine(cx, cy, cx + 4, cy + 2);
                    g2.dispose();
                }
            };
            clockIcon.setOpaque(false);
            JLabel timeLbl = new JLabel(time);
            timeLbl.setFont(basicGothicProBook.deriveFont(15f));
            timeLbl.setForeground(TEXT_DARK);
            timeRow.add(clockIcon);
            timeRow.add(timeLbl);
            content.add(timeRow);
        }

        card.add(img,     BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        wrapper.add(card);

        wrapper.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showCraftDetail(craft); }
            @Override public void mouseEntered(MouseEvent e) { card.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { card.repaint(); }
        });
        return wrapper;
    }

    private JLabel wideBadge(String text) {
        JLabel b = new JLabel(text, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setOpaque(false);
        b.setFont(basicGothicProBold.deriveFont(16f));
        b.setForeground(ACCENT_COLOR);
        b.setBorder(new EmptyBorder(6, 22, 6, 22));
        return b;
    }

    private JPanel buildEmptyCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(EMPTY_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                g2.setColor(new Color(160, 190, 220));
                float[] dash = {6f, 4f};
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dash, 0f));
                g2.drawRoundRect(1, 1, getWidth() - 7, getHeight() - 7, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(260, 340));
        card.setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("No craft saved");
        lbl.setFont(basicGothicProBook.deriveFont(12f));
        lbl.setForeground(new Color(160, 190, 220));
        card.add(lbl, new GridBagConstraints());
        return card;
    }

    private void showCraftDetail(Craft craft) {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setUndecorated(false);
        dialog.setBackground(BG_COLOR);
        dialog.setSize(420, 560);
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.add(buildDetailCard(craft), BorderLayout.CENTER);

        JButton close = new JButton("Close") {
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
        close.setFont(basicGothicProBook.deriveFont(13f));
        close.setForeground(ACCENT_COLOR);
        close.setBackground(BG_COLOR);
        close.setContentAreaFilled(false);
        close.setFocusPainted(false);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.setBorder(new EmptyBorder(8, 20, 8, 20));
        close.addActionListener(e -> dialog.dispose());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        south.add(close);
        root.add(south, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // Every child in the BoxLayout Y_AXIS content panel has LEFT_ALIGNMENT set.
    // This is the critical rule: if ANY child differs, BoxLayout centers everything.
    private JPanel buildDetailCard(Craft craft) {
        String[] p = craft.getLine().split(",");
        String name  = p.length > 0 ? p[0].trim() : "";
        String level = p.length > 2 ? p[2].trim() : "";
        String time  = p.length > 3 ? p[3].trim() : "";
        String desc  = p.length > 4 ? p[4].trim() : "";

        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        JPanel img = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 215, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 20, 20);
                g2.setColor(new Color(130, 165, 200));
                g2.setFont(basicGothicProBook.deriveFont(11f));
                FontMetrics fm = g2.getFontMetrics();
                String t = "[ craft image ]";
                g2.drawString(t, (getWidth() - fm.stringWidth(t)) / 2, getHeight() / 2 + fm.getAscent() / 2);
                g2.dispose();
            }
        };
        img.setOpaque(false);
        img.setPreferredSize(new Dimension(380, 150));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(12, 16, 12, 16));

        // ── name row ─────────────────────────────────────────────────────────
        JPanel nameRow = new JPanel(new BorderLayout(8, 0));
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

        // ── level badge + time row ────────────────────────────────────────────
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

        // ── Materials ─────────────────────────────────────────────────────────
        JLabel matHdr = new JLabel("Materials");
        matHdr.setFont(basicGothicProBold.deriveFont(15f));
        matHdr.setForeground(TEXT_DARK);
        matHdr.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(matHdr);
        content.add(Box.createVerticalStrut(6));

        for (String mat : craft.getMaterials()) {
            String m = mat.trim();
            boolean owned = invItemNames.contains(m.toLowerCase());
            JPanel mr = materialRow(m, owned);
            mr.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(mr);
            content.add(Box.createVerticalStrut(4));
        }

        content.add(Box.createVerticalStrut(8));

        // ── Description ───────────────────────────────────────────────────────
        if (!desc.isEmpty()) {
            JLabel dh = new JLabel("Description");
            dh.setFont(basicGothicProBold.deriveFont(15f));
            dh.setForeground(TEXT_DARK);
            dh.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(dh);
            content.add(Box.createVerticalStrut(4));

            JLabel dt = new JLabel("<html><body style='width:320px;font-size:12px'>" + desc + "</body></html>");
            dt.setFont(basicGothicProBook.deriveFont(13f));
            dt.setForeground(TEXT_LIGHT);
            dt.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(dt);
            content.add(Box.createVerticalStrut(12));
        }

        // ── View instructions ─────────────────────────────────────────────────
        JButton vb = viewBtn(craft);
        vb.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(vb);

        card.add(img,     BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // ── Material row — BoxLayout X_AXIS with rigid JPanel circles ────────────
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
                g2.setColor(new Color(200, 220, 240));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(100, 140, 190));
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

    private JLabel levelBadge(String level) {
        JLabel b = new JLabel(level) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(140, 175, 215));
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
                "Instructions for: " + craft.getLine().split(",")[0].trim() + "\n\n(Connect to web scraper here)"));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(150, 200, 150)); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(VIEW_BG); btn.repaint(); }
        });
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
        SwingUtilities.invokeLater(() -> new SavedCraftsScreenGUI().setVisible(true));
    }
}