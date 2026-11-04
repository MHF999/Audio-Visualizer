package audio.visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * The classic bar-style visualizer. Each bar represents one frequency band,
 * with a white peak cap above it.
 */
public class StandardBarVisualizer extends Visualizer {

    private static final int GAP = 3; // pixels between bars

    public StandardBarVisualizer(FrequencyDataSource source, int bands) {
        super(source, bands);
    }

    @Override
    protected void drawFrame(Graphics2D g2, int w, int h) {
        int n = bars.length;
        double barWidth = (w - GAP * (n + 1)) / (double) n;

        for (int i = 0; i < n; i++) {
            int x = (int) (GAP + i * (barWidth + GAP));
            int barHeight = (int) (bars[i] * h);

            // Hue shifts from green (low bars) toward red (high bars).
            float hue = (float) (0.33 * (1.0 - bars[i]));
            g2.setColor(Color.getHSBColor(hue, 0.85f, 1.0f));
            g2.fillRect(x, h - barHeight, (int) barWidth, barHeight);

            // Peak cap
            int peakY = h - (int) (peaks[i] * h);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(x, peakY, x + (int) barWidth, peakY);
        }
    }
// sdnapoles: I commented this out and included the demo inside the GUI
    /** Quick demo: run this class directly to see the bars move. 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int bands = 32;
            StandardBarVisualizer panel =
                    new StandardBarVisualizer(new RandomFrequencyData(bands), bands);

            JFrame frame = new JFrame("Audio Visualizer - Bar Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.start();
        });
    }*/
}
