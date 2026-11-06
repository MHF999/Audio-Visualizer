package audio.visualizer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.math4.transform.FastFourierTransform;
import org.apache.commons.numbers.complex.Complex;

/**
 *
 * @author Landon
 */
public class FFT implements Visualizer.FrequencyDataSource
{
    
    private final Player player;
    private final double[] allSamples;
    private double[] samples; // A particular chunk of samples for FFT to analyze
    private final int sampleRate; // Samples per second (ex 48000 hz)
    private Complex[] transformedData; // Output from the FFT! data type Complex = complex numbers,
                                       // provided by Apache Commons Numbers.

    private final int chunkSize = 2048;
    private final int bandCount = 32;
    
    // Constructor to initialize player for the FFT object:
    public FFT(Player player) throws UnsupportedAudioFileException, IOException {
    if (player == null) {
        throw new IllegalArgumentException("Null Player.");
    }

    this.player = player;

    AudioFile audioFile = player.getAudioFile();

    if (audioFile == null) {
        throw new IllegalArgumentException("Player has no AudioFile.");
    }

    this.allSamples = audioFile.getMonoDataCopy();
    this.sampleRate = audioFile.getSampleRate();

    if (allSamples.length == 0) {
        throw new IllegalArgumentException("Audio file contains no samples.");
    }
}
        
    // Request small pieces of the audio file at a time for runFFT to transform.
    public void loadData() {
    long currentFrame = player.getCurrentFrame();

    if (currentFrame < 0 || currentFrame >= allSamples.length) { 
        throw new IllegalStateException("Player position is outside the audio data range.");
    }

    samples = new double[chunkSize];

    int firstSample = Math.toIntExact(currentFrame);

    int available = Math.min(chunkSize, allSamples.length - firstSample);

    System.arraycopy(allSamples, firstSample, samples, 0, available);

    transformedData = null;
}
    
    public void saveData() {
        
    }
    
    // Run the FFT:
    public void runFFT() {

        if (samples == null) {
            throw new IllegalStateException(
                    "Call loadData() before runFFT().");
        }

        FastFourierTransform transform = new FastFourierTransform(FastFourierTransform.Norm.STD);

        transformedData = transform.apply(samples);
    }
    
    // Retrieve the complex array produced by the FFT and convert to format that the visualizer expects:
    public double[] getFreq() {

        if (transformedData == null || samples == null) {
            throw new IllegalStateException(
                    "Call loadData() and runFFT() before getFreq()!!");
        }

        int n = samples.length;
        int binCount = n / 2 + 1;

        double[] magnitudes = new double[binCount];

        for (int i = 0; i < binCount; i++) {

            // DC and Nyquist bins are not doubled; other positive-frequency bins are doubled to account for the omitted negative-frequency half
            boolean isDc = i == 0;
            boolean isNyquist = i == n / 2;

            double scale;

            if (isDc || isNyquist) {
                scale = 1.0;
            } else {
                scale = 2.0;
            }
            
            magnitudes[i] =
                    transformedData[i].abs() * scale / n;
        }

        return magnitudes;
    }
    
    public double getNyquistFrequency() {

        if (sampleRate <= 0) {
            throw new IllegalStateException(
                    "No audio data; could not get Nyquist frequency.");
        }

        return sampleRate / 2.0;
    }

    // Groups the spectrum into frequency bands. Each returned value is in the range 0.0 to 1.0.
    public double[] getBands(int bandCount) {

        if (bandCount <= 0) {
            throw new IllegalArgumentException(
                    "Band count must be positive.");
        }

        double[] magnitudes = getFreq();
        double[] bands = new double[bandCount];

        double nyquist = getNyquistFrequency();
        double binWidth = sampleRate / (double) samples.length;

        for (int band = 0; band < bandCount; band++) {

            double lowFrequency = band * nyquist / bandCount;

            double highFrequency = (band + 1) * nyquist / bandCount;

            int startBin = (int) Math.ceil(lowFrequency / binWidth);

            int endBin;

            if (band == bandCount - 1) {
                // Include the final bin at the Nyquist frequency:
                endBin = magnitudes.length;
            } else {
                // Upper boundary is exclusive:
                endBin = (int) Math.ceil(
                        highFrequency / binWidth);
            }

            // Exclude DC (0 Hz) from the displayed bars:
            startBin = Math.max(1, startBin);
            endBin = Math.min(magnitudes.length, endBin);

            double strongestMagnitude = 0.0;

            for (int bin = startBin; bin < endBin; bin++) {
                strongestMagnitude = Math.max(
                        strongestMagnitude, magnitudes[bin]);
            }

            bands[band] = Math.max(
                    0.0, Math.min(1.0, strongestMagnitude));
        }

        return bands;
    }
    
    @Override
    public double[] nextFrame() {
        loadData();
        runFFT();
        return getBands(bandCount);
    }

}