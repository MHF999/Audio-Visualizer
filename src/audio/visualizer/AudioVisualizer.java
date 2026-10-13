package audio.visualizer;

import java.util.Scanner;

/**
 *
 * @author Landon
 */
public class AudioVisualizer 
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Program start!");
        
        Scanner scan = new Scanner(System.in);
        AudioFile audioFile;
        
        do
        {
            // Like the following filepath, C:\Users\Landon\Downloads\sample.wav
            System.out.print("Enter the absolute path to your .wav file: ");
            audioFile = new AudioFile(scan.nextLine());
        }
        while (!audioFile.isWAV());

        System.out.println("Valid format, playing audio.");
        
        try
        {
            Player p = new Player(audioFile);
            p.play();
        }
        
        catch (Exception e)
        {
            System.out.println("Something broke...");
            System.out.println(e.getMessage());
        }
        
        
        while (true) {} // Need to manually shut off program.
    }
    
    // NOTE: I did not put in stubs for all of the classes yet...
    //       I might have time to do so later though. This should be
    //       a nice start though!
    
}