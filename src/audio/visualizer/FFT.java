package audio.visualizer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.math4.transform.FastFourierTransform;
import org.apache.commons.numbers.complex.Complex;

// import apache math commons FFT stuff

/**
 *
 * @author Landon
 */
public class FFT
{
    private final AudioFile audioFile; // WAV file
    private double[] samples; // A particular chunk of samples for FFT to analyze
    private int sampleRate; // Samples per second (ex 48000 hz)
    private Complex[] transformedData; // Output from the FFT! data type Complex = complex numbers,
                                       // provided by Apache Commons Numbers.
    
    // Constructor to specify working file for the FFT object:
    public FFT(AudioFile audioFile) {
        this.audioFile = audioFile;
    }
        
    // Apache's FFT requires # of samples to be a power of 2; going to try 1,024 samples at a time (2^10):
    public void loadData(int firstSample, int chunkSize) throws UnsupportedAudioFileException, IOException {
        
        // ERROR HANDLING: 
        // Prevent invalid sample positions/indexes and chunk sizes:
        if (firstSample < 0 || chunkSize <= 0) {
            throw new IllegalArgumentException("Invalid sample or chunk size.");
        }
        // Ensure chunkSize is a power of 2:
        if ((chunkSize & (chunkSize - 1)) != 0) { // Bitwise operator '&' compares two numbers' binary representations one bit at a time. 
                                                  // All powers of 2 have exactly one 1 in their binary representation.
                                                  // If chunk size is a power of 2, '&' will evaluate to 0 after comparing it with its predecessor.
            throw new IllegalArgumentException("Chunk size must be a power of 2.");
        }
        
        // Get decoded audio data in mono form from AudioFile class:
        double[] allSamples = audioFile.getMonoData(); 
        
        if (firstSample >= allSamples.length) {
            throw new IllegalArgumentException("Initial sample outside of audio data range.");
        }
        
        // Create an array of the size FFT needs. Java should initialize all elements to 0.0.
        samples = new double[chunkSize];
        
        // How many samples remain from our requested starting position?
        int available = Math.min(chunkSize, allSamples.length - firstSample);
        
        // Copy those into the new array we just made:
        System.arraycopy(allSamples, firstSample, samples, 0, available);
        
        sampleRate = audioFile.getSampleRate(); // Save sample rate value - will help translate FFT values -> hertz later.
        transformedData = null; // "Clear" this variable to make room for next chunk of samples.
        
    }
    
    public void saveData() {}
    public void runFFT() {}
    public void getFreq() {}
   
}

