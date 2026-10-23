package audio.visualizer;

import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Team 7
 */
public class Library implements Observer
{    
    private Path libraryPath;
    private ArrayList<AudioFile> audioFiles;
    private AudioFile selectedAudioFile;
    
    public Library()
    {
        String filepath;
        AudioFile audioFile;
        audioFiles = new ArrayList<AudioFile>();
        
  //      audioFiles.add(new AudioFile("C:\\Users\\Landon\\Downloads\\default2.wav"));
        
        try
        {
            filepath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile().getAbsolutePath();
            libraryPath = Paths.get(filepath + File.separator + "Library");
        }
        
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        
        for (File file : libraryPath.toFile().listFiles())
        {
            audioFile = new AudioFile(Paths.get(file.getAbsolutePath()));
            
            if (audioFile.isWav())
            {
                audioFiles.add(audioFile);
            }
        }
    }
    
    public String getLibraryLocation()
    {
        return libraryPath.toAbsolutePath().toString();
    }
    
    private void select(String name)
    {
        for (AudioFile audioFile : audioFiles)
        {
            if (audioFile.getName().equals(name))
            {
                selectedAudioFile = audioFile;
            }
        }
    }
    
    public AudioFile getSelected()
    {
        return selectedAudioFile;
    }
    
    public void importWav(String filepath)
    {
        AudioFile sourceAudioFile = new AudioFile(filepath);
        String name = sourceAudioFile.getName();
        boolean uniqueName = true;
        
        if (sourceAudioFile.isWav())
        {
            for (AudioFile audioFile : audioFiles)
            {
                if (audioFile.getName().equals(name))
                {
                    uniqueName = false;
                }
            }
        
            if (uniqueName)
            {
                try
                {
                    AudioFile targetAudioFile = new AudioFile(Paths.get(libraryPath.toAbsolutePath().toString() + File.separator + sourceAudioFile.getName()));
                    Files.copy(sourceAudioFile.getPath(), targetAudioFile.getPath());
                    audioFiles.add(targetAudioFile);
                }
                
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    
    public void rename(AudioFile oldAudioFile, String newName)
    {
        boolean inLibrary = false;
        boolean uniqueName = true;
        
        AudioFile newAudioFile = new AudioFile(Paths.get(libraryPath.toAbsolutePath().toString() + File.separator + newName));
        
        
        if (newAudioFile.isWav())
        {
            for (AudioFile audioFile : audioFiles)
            {
                if (audioFile == oldAudioFile)
                {
                    inLibrary = true;
                }

                if (audioFile.getName() == newName)
                {
                    uniqueName = false;
                }
            }

            if (inLibrary && uniqueName)
            {
                try
                {
                    Files.move(oldAudioFile.getPath(), newAudioFile.getPath());
                }
                
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }    
        }       
    }
    
    public void delete(AudioFile oldAudioFile)
    {
        if (audioFiles.remove(oldAudioFile))
        {
            try
            {
                Files.delete(oldAudioFile.getPath());
            }
            
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
    
    // This is kind of bad coding practice...
    // I might need to rewrite rename() and delete()
    // to take String inputs instead of AudioFile
    // inputs. That way, this method could return an
    // array of String variables instead of an
    // ArrayList of AudioFile objects.
    public ArrayList<AudioFile> getAudioFiles()
    {
        return audioFiles;
    }
    
    // These will probably have return types later on.
    public void getWav() {} // return file itself
    public void getFFT() {} // return FFT
    
   
    private void exportMP4() {}
    
    
    // Necessary implementation for the Observer interface.
    public void update() {}
    // TODO: Need duplicate() and delete() methods. Put in library class instead???

    
}