package audio.visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * A bar-style frequency visualizer. Each bar represents one frequency band.
 *
 * The panel does not care where its data comes from. It asks a
 * FrequencyDataSource for a new frame of band magnitudes (each 0.0 to 1.0)
 * about 60 times per second. For now that source is random; later it can be
 * replaced with a class that returns real FFT output.
 */
public class BarVisualizer extends JPanel {

    /** Anything that can produce one frame of band magnitudes, each in 0.0-1.0. */
    public interface FrequencyDataSource {
        double[] nextFrame();
    }

    /** Fake data that loosely behaves like music: more energy in the low bands. */
    public static class RandomFrequencyData implements FrequencyDataSource {
        private final double[] frame;
        private final Random rng = new Random();

        public RandomFrequencyData(int bands) {
            this.frame = new double[bands];
        }

        @Override
        public double[] nextFrame() {
            int n = frame.length;
            for (int i = 0; i < n; i++) {
                double weight = 1.0 - 0.7 * i / Math.max(1, n - 1); // lows louder than highs
                double target = rng.nextDouble() * weight;
                frame[i] += (target - frame[i]) * 0.5;              // move toward target
            }
            return frame.clone();
        }
    }

    private static final int FPS = 60;
    private static final double FALL_SPEED = 0.02;      // how fast bars drop per frame
    private static final double PEAK_FALL_SPEED = 0.005; // how fast the peak caps drop
    private static final int GAP = 3;                    // pixels between bars

    private final FrequencyDataSource source;
    private final double[] bars;
    private final double[] peaks;
    private final Timer timer;

    public BarVisualizer(FrequencyDataSource source, int bands) {
        this.source = source;
        this.bars = new double[bands];
        this.peaks = new double[bands];
        setBackground(new Color(18, 18, 24));
        setPreferredSize(new Dimension(800, 400));

        timer = new Timer(1000 / FPS, e -> {
            updateBars();
            repaint();
        });
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void updateBars() {
        double[] data = source.nextFrame();
        for (int i = 0; i < bars.length; i++) {
            double v = i < data.length ? clamp(data[i]) : 0;

            // Rise instantly, fall gradually so the motion looks smooth.
            bars[i] = v > bars[i] ? v : Math.max(0, bars[i] - FALL_SPEED);

            // Peak cap sticks at the highest recent value, then slowly drops.
            peaks[i] = bars[i] > peaks[i] ? bars[i] : Math.max(0, peaks[i] - PEAK_FALL_SPEED);
        }
    }

    private static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
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
        g2.dispose();
    }

    /** Quick demo: run this class directly to see the bars move. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int bands = 32;
            BarVisualizer panel = new BarVisualizer(new RandomFrequencyData(bands), bands);

            JFrame frame = new JFrame("Audio Visualizer - Bar Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.start();
        });
    }
}
