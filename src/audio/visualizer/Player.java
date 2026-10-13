package audio.visualizer;

import java.io.*;
import javax.sound.sampled.*;

/**
 *
 * @author Landon
 */
public class Player
{
    private AudioFile audioFile;
    private AudioInputStream inputStream;
    private Clip clip;
    
    Player(AudioFile audioFile) throws UnsupportedAudioFileException, LineUnavailableException, IOException
    {
        this.audioFile = audioFile;
        inputStream = AudioSystem.getAudioInputStream(audioFile.getFile());
        clip = AudioSystem.getClip();
    }
    
    public void play() throws LineUnavailableException, IOException
    {
        clip.open(inputStream);
        clip.start();
    }
}
