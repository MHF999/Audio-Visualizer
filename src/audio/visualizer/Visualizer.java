package audio.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Base class for frequency visualizers. It owns everything that is not
 * specific to how the data is drawn: pulling frames from a
 * FrequencyDataSource about 60 times per second, smoothing the band
 * magnitudes (with falling peak caps), and driving the repaint timer.
 *
 * Subclasses only implement {@link #drawFrame(Graphics2D, int, int)}.
 */
public abstract class Visualizer extends JPanel {

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

    protected static final int FPS = 60;
    protected static final double FALL_SPEED = 0.02;       // how fast bars drop per frame
    protected static final double PEAK_FALL_SPEED = 0.005; // how fast the peak caps drop

    private final FrequencyDataSource source;
    private final Timer timer;

    /** Smoothed magnitude of each band, 0.0-1.0. Read by subclasses when drawing. */
    protected final double[] bars;
    /** Peak-cap level of each band, 0.0-1.0. Read by subclasses when drawing. */
    protected final double[] peaks;

    protected Visualizer(FrequencyDataSource source, int bands) {
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
    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawFrame(g2, getWidth(), getHeight());
        g2.dispose();
    }

    /**
     * Draw the current state of {@link #bars} and {@link #peaks}.
     * The Graphics2D is already configured and is disposed by the caller.
     */
    protected abstract void drawFrame(Graphics2D g2, int width, int height);
}
