package audio.visualizer;

import java.io.File;
import java.io.*;
import javax.sound.sampled.*;

/**
 *
 * @author Landon
 */
public class Player
{
    File audioFile;
    AudioInputStream inputStream;
    Clip clip;
    
    Player() throws UnsupportedAudioFileException, LineUnavailableException, IOException
    {
        audioFile = new File("C:\\Users\\Landon\\Downloads\\ezyZip.wav");
        inputStream = AudioSystem.getAudioInputStream(audioFile);
        clip = AudioSystem.getClip();
    }
    
    public void play() throws LineUnavailableException, IOException
    {
        clip.open(inputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}
