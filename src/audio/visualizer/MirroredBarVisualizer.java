package audio.visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Mirrored bar visualizer: the spectrum is drawn on the right half and
 * duplicated, flipped, on the left half, so the display is symmetrical and
 * reads like a mountain range.
 *
 * The total number of bars on screen equals the number of bands (32 bands =
 * 32 bars, 16 per side). Neighbouring bands are paired up (largest value wins)
 * so the whole frequency range still fits on each side. An even band count
 * gives a perfectly symmetrical result.
 */
public class MirroredBarVisualizer extends Visualizer {

    private static final int GAP = 3; // pixels between bars

    /**
     * true  = lowest frequencies in the middle, highest at the edges. Music has
     *         the most energy in the bass, so the tallest "peak" sits in the centre.
     * false = lowest frequencies at the edges, highest in the middle.
     */
    private static final boolean BASS_IN_CENTER = true;

    public MirroredBarVisualizer(FrequencyDataSource source, int bands) {
        super(source, bands);
    }

    @Override
    protected void drawFrame(Graphics2D g2, int w, int h) {
        int half = Math.max(1, bars.length / 2);      // bars per side
        double[] sideBars = fold(bars, half);
        double[] sidePeaks = fold(peaks, half);

        double cx = w / 2.0;
        double slot = (w / 2.0) / half;                // width of one bar plus its gap
        double barWidth = Math.max(1.0, slot - GAP);

        for (int i = 0; i < half; i++) {
            // i counts outward from the centre line.
            int band = BASS_IN_CENTER ? i : half - 1 - i;

            double rightX = cx + i * slot + GAP / 2.0;
            double leftX = cx - (i + 1) * slot + GAP / 2.0;

            drawBar(g2, rightX, barWidth, h, sideBars[band], sidePeaks[band]);
            drawBar(g2, leftX, barWidth, h, sideBars[band], sidePeaks[band]);
        }
    }

    private void drawBar(Graphics2D g2, double x, double barWidth, int h, double value, double peak) {
        double barHeight = value * h;

        // Same colour scheme as StandardBarVisualizer: green (low) to red (high).
        g2.setColor(colorFor(value));
        g2.fill(new Rectangle2D.Double(x, h - barHeight, barWidth, barHeight));

        // Peak cap
        double peakY = h - peak * h;
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(new Line2D.Double(x, peakY, x + barWidth, peakY));
    }

    /** Quick demo: run this class directly to see the bars move. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int bands = 32;
            MirroredBarVisualizer panel = new MirroredBarVisualizer(new RandomFrequencyData(bands), bands);
            JFrame frame = new JFrame("Audio Visualizer - Mirrored Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.start();
        });
    }
}
