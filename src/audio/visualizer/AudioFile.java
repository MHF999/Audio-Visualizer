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
    
    public int getLength() {
    return 0;   
    }
    
    public int getSampleRate() {
     try (AudioInputStream audio = AudioSystem.getAudioInputStream(getFile())) {
            return (int) audio.getFormat().getSampleRate();
        }
        
        catch (UnsupportedAudioFileException | IOException e) { 
            throw new IllegalStateException("Could not read file: " + file, e);
        }
    }
    public void getChunk() {} // Return type of some sort.
    
}
