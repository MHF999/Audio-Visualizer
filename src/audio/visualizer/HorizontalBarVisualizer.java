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
public class HorizontalBarVisualizer extends StandardBarVisualizer {

    public HorizontalBarVisualizer(FrequencyDataSource source, int bands) {
        super(source, bands);
    }

    @Override
    protected void drawFrame(Graphics2D g2, int w, int h) {
        // Reusing the math and logic from StandardBarVisualizer to redraw it vertically
        Graphics2D g2d = (Graphics2D) g2.create();

     
        g2d.rotate(Math.PI / 2);
        g2d.translate(0, -w);

        // Call super.drawFrame with swapped width and height.
        // The inherited vertical-drawing code will now render horizontally.
        super.drawFrame(g2d, h, w);

        g2d.dispose();
    }

/* === Horizontal Demo === 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int bands = 4;
            HorizontalBarVisualizer panel =
                    new HorizontalBarVisualizer(new RandomFrequencyData(bands), bands);

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
