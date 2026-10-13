package audio.visualizer;

import java.nio.file.*;
import java.io.*;

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
    
    
    
    public int getLength() {return 0;}
    public int getSampleRate() {return 0;}
    public void getChunk() {} // Return type of some sort.
    
}
