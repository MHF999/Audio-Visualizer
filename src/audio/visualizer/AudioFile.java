package audio.visualizer;

import java.nio.file.*;
import java.io.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Landon
 */
public class AudioFile
{
    private Path file;
    private String name;
    
    AudioFile(String filepath)
    {
        file = Paths.get(filepath);
        name = file.getFileName().toString(); // Assigns a name (just the file name); 
    }                                         // otherwise file is nameless until renamed.
    
       
    
    public boolean isWAV()
    {
        boolean isWAV = false;
        String filepath = getFile().getAbsolutePath();
        
        int periodIndex = filepath.lastIndexOf(".");
        
        if (filepath.substring(periodIndex+1).equals("wav") ||
                filepath.substring(periodIndex+1).equals("WAV"))
        {
            isWAV = true;
        }
        
        return isWAV;
        
        // Alternative version w/ built-in string methods:
        // String fileName = getFile().getName();
        // return fileName.toLowerCase.(java.util.Locale.ROOT).endsWith(".wav");
        // NOTE: endsWith() will still return a boolean!
    }
    
    public void rename(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Path getPath()
    {
        return file;
    }
    
    public File getFile()
    {
        return file.toFile();
    }
    
    // Method that opens the audio file and creates an input stream for reading 
    // in audio data. Returns audio duration in seconds; throws Illegal State
    // Exception if wrong audio format, file can't be read, or duration can't
    // be determined.
    public double getLength() {
        
        // Try should automatically close input stream after execution.
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(getFile())) {
            
            // Variables for frame rate and frame length; AudioInputStream provides these.
            // Frame rate = number of audio frames processed per second.
            // Frame length = total number of frames in audio file.
            float frameRate = audio.getFormat().getFrameRate(); 
            long frameLength = audio.getFrameLength(); 

            // These values must be positive. Otherwise, duration cannot be
            // accurately calculated.
            if (frameRate <= 0 || frameLength < 0) {
                throw new IllegalStateException("Failed to retrieve audio duration: " + file);
            }
            
            // Divide total number of frames by number of frames per second.
            // Doesn't have to be cast as a double, but preserves fractional values.
            return frameLength / (double) frameRate;
        }
        
        // Handle cases with wrong audio format and general file read issues:
        catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException("Could not read WAV file: " + file, e);
        }
    }
    
    // Method to get sample rate from the audio in samples per second.
    // Return sample rate as an integer.
    // Throws Illegal State Exception if file is not or cannot be read.
    public int getSampleRate() {
        
     // Open audio file and extract its audio format information.
     // Stream should close automatically after execution.
     try (AudioInputStream audio = AudioSystem.getAudioInputStream(getFile())) {
         // Read/return sample rate and convert this to an integer.
            return (int) audio.getFormat().getSampleRate();
        }
        
        // Handle incorrect audio format, file read errors.
     catch (UnsupportedAudioFileException | IOException e) { 
        throw new IllegalStateException("Could not read file: " + file, e);
        }
    }
    
    // Retrieve and decode samples stored as encoded binary data?
    // Returns an array of doubles representing the samples.
    // firstSample = entry point/where to start reading data; should be an index.
    // chunkSize = how many samples to grab for the array. 
    // should we convert stereo (left and right speakers) to mono for simpler/faster processing? if so can write another method.
    // SEE LINK: https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdAudio.html#read(java.lang.String)
    // ^ has method StdAudio.read() which reads an entire file of audio into an array of doubles.
    // Converts audio to a 44,100 Hz sample rate and 16-bit samples.
    // may want to use or riff on depending on what FFT needs, could load entire file and then pass chunks of array by index?
    public double[] getChunk(long firstSample, int chunkSize) {
        // Read and decode requested audio samples?
        return new double[0]; // Temp value to keep stub healthy. 
    }                         // New array shouldn't leak memory since it's empty!
    
}

// NOTES FOR AUDIO DATA DECODING: integer value = lower byte + 256(higher byte); 
// eg for (3,0) value = 3 + 256(0) = 3.