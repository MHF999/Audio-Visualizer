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
    }
    
    
    // TODO: Need duplicate() and delete() methods.
    
    
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

            // These values must be positive natural numbers. Otherwise, duration
            // cannot be accurately calculated.
            if (frameRate <= 0 || frameLength < 0) {
                throw new IllegalStateException("Failed to retrieve audio duration: " + file);
            }
            
            // Divide total number of frames by number of frames per second.
            // Doesn't have to be cast as a double, but preserves fractional values.
            return frameLength / (double) frameRate;
        }
        
        // Handle cases with wrong audio format and general file read issues:
        catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException("Could not read WAV file: " + file);
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
    
    public void getChunk() {} // Return type of some sort.
    
}
