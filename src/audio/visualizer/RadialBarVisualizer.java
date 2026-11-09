package audio.visualizer;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Radial bar visualizer: thin green-to-red bars radiate from an empty centre.
 * The space inside the bars is left clear (just the panel background) so an
 * image can be drawn there later.
 *
 * The layout is left/right symmetrical so it looks even all the way round:
 * the right half shows the spectrum clockwise from the top, and the left half
 * is its mirror image. Like MirroredBarVisualizer, the total number of bars
 * equals the number of bands (32 bands = 32 bars, 16 per side), with
 * neighbouring bands paired up so the whole range fits on each side.
 */
public class RadialBarVisualizer extends Visualizer {

    /** true = lowest frequencies at the top, highest at the bottom. false = the reverse. */
    private static final boolean BASS_AT_TOP = true;

    private static final int MARGIN = 14;         // pixels kept free around the outside
    private static final double BAR_BASE = 0.44;  // where bars start, as a share of the outer radius

    public RadialBarVisualizer(FrequencyDataSource source, int bands) {
        super(source, bands);
    }

    @Override
    protected void drawFrame(Graphics2D g2, int w, int h) {
        int half = Math.max(1, bars.length / 2);   // bars per side
        double[] side = fold(bars, half);

        double cx = w / 2.0;
        double cy = h / 2.0;
        double outerRadius = Math.min(w, h) / 2.0 - MARGIN;
        if (outerRadius <= 0) {
            return;
        }
        double base = outerRadius * BAR_BASE;
        double maxLength = outerRadius - base;

        double slotAngle = Math.PI / half;                 // each side covers 180 degrees
        double barWidth = Math.max(2.0, slotAngle * base * 0.5);

        AffineTransform original = g2.getTransform();
        g2.setStroke(new BasicStroke((float) barWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int j = 0; j < half; j++) {
            int band = BASS_AT_TOP ? j : half - 1 - j;
            double angle = (j + 0.5) * slotAngle;          // clockwise from the top
            double length = side[band] * maxLength;

            // Same colour scheme as the other visualizers: green (low) to red (high).
            g2.setColor(colorFor(side[band]));

            for (int mirror = 0; mirror < 2; mirror++) {
                g2.setTransform(original);
                g2.translate(cx, cy);
                g2.rotate(mirror == 0 ? angle : -angle);   // 0 points straight up
                g2.draw(new Line2D.Double(0, -(base + barWidth / 2), 0, -(base + barWidth / 2 + length)));
            }
        }
        g2.setTransform(original);
    }

    /** Quick demo: run this class directly to see the bars move. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int bands = 32;
            RadialBarVisualizer panel = new RadialBarVisualizer(new RandomFrequencyData(bands), bands);
            JFrame frame = new JFrame("Audio Visualizer - Radial Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.start();
        });
    }
}
