package audio.visualizer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * A colour picker small enough for the narrow side panel. The standard
 * JColorChooser tabs (swatches, HSV, RGB...) are much wider than the panel, so
 * part of them is cut off and cannot be reached. This replaces them with:
 *
 *  - a palette of ready-made colours (click one to use it),
 *  - a "Custom..." button that opens the full colour dialog for any colour,
 *  - a "Reset to Default" button for the default green-to-red colours.
 *
 * It plugs into the existing JColorChooser slot on the Type tab:
 * chooser.setChooserPanels(new AbstractColorChooserPanel[] { new CompactColorPanel(...) }).
 */
public class CompactColorPanel extends AbstractColorChooserPanel {

    private static final Color[] PALETTE = {
        new Color(255, 59, 48), new Color(255, 149, 0), new Color(255, 214, 10),
        new Color(163, 230, 53), new Color(52, 199, 89), new Color(0, 199, 190),
        new Color(50, 215, 255), new Color(0, 122, 255), new Color(88, 86, 214),
        new Color(175, 82, 222), new Color(255, 0, 200), new Color(255, 105, 180),
        new Color(255, 255, 255), new Color(190, 190, 190), new Color(120, 120, 120),
        new Color(255, 127, 80), new Color(152, 255, 200), new Color(200, 162, 255)
    };
    private static final int COLUMNS = 6;

    private final Consumer<Color> onPick;
    private final Runnable onReset;
    private final java.util.List<Swatch> swatches = new java.util.ArrayList<>();
    private Color selected; // null = default green-to-red

    /**
     * @param onPick  called with the colour whenever the user picks one
     * @param onReset called when the user presses "Reset to Default"
     */
    public CompactColorPanel(Consumer<Color> onPick, Runnable onReset) {
        this.onPick = onPick;
        this.onReset = onReset;
    }

    @Override
    protected void buildChooser() {
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel grid = new JPanel(new GridLayout(PALETTE.length / COLUMNS, COLUMNS, 3, 3));
        grid.setOpaque(false);
        for (Color c : PALETTE) {
            Swatch swatch = new Swatch(c);
            swatches.add(swatch);
            grid.add(swatch);
        }

        JButton custom = new JButton("Custom...");
        custom.setToolTipText("Open the full colour dialog to pick any colour");
        custom.addActionListener(e -> chooseCustomColor());

        JButton reset = new JButton("Reset to Default");
        reset.setToolTipText("Go back to the default green-to-red colours");
        reset.addActionListener(e -> {
            setSelected(null);
            onReset.run();
        });

        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 4));
        buttons.setOpaque(false);
        buttons.add(custom);
        buttons.add(reset);

        add(grid, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
    }

    private void chooseCustomColor() {
        Color start = selected != null ? selected : Color.WHITE;
        Color picked = JColorChooser.showDialog(SwingUtilities.getWindowAncestor(this),
                "Custom Visualizer Color", start);
        if (picked != null) {
            pick(picked);
        }
    }

    private void pick(Color color) {
        setSelected(color);
        onPick.accept(color);
    }

    /** Remembers which colour is in use, so its swatch gets a highlight (null = default). */
    private void setSelected(Color color) {
        selected = color;
        for (Swatch swatch : swatches) {
            swatch.repaint();
        }
    }

    // The standard chooser model is not used: colours go straight to onPick.
    @Override
    public void updateChooser() {
    }

    @Override
    public String getDisplayName() {
        return "Colors";
    }

    @Override
    public javax.swing.Icon getSmallDisplayIcon() {
        return null;
    }

    @Override
    public javax.swing.Icon getLargeDisplayIcon() {
        return null;
    }

    /** One clickable colour square. Painted by hand so it looks the same in every look and feel. */
    private class Swatch extends JComponent {

        private final Color color;

        Swatch(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(22, 22));
            setToolTipText(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    pick(Swatch.this.color);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(color);
            g2.fillRoundRect(1, 1, w - 2, h - 2, 6, 6);
            boolean isSelected = color.equals(selected);
            g2.setStroke(new BasicStroke(isSelected ? 2.5f : 1f));
            g2.setColor(isSelected ? Color.BLACK : new Color(0, 0, 0, 90));
            g2.drawRoundRect(1, 1, w - 3, h - 3, 6, 6);
            if (isSelected) {
                g2.setStroke(new BasicStroke(1.2f));
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(3, 3, w - 7, h - 7, 4, 4);
            }
            g2.dispose();
        }
    }
}
