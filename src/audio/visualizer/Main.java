package audio.visualizer;

import java.util.Scanner;
import java.util.ArrayList;
import java.nio.*;
import java.io.*;

/**
 *
 * @author Landon
 */
public class Main 
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        
        Library library = new Library();
        Scanner scan = new Scanner(System.in);
        String nextLine = "";
       
        System.out.println("Program start!");
        
        System.out.println("Current Library Directory: " + library.getLibraryLocation() + "\n");
                        
        while (!nextLine.equals("play"))
        {
            // Like the following (Windows) filepath -- C:\Users\Landon\Downloads\sample.wav
            System.out.print("Enter \"disp\" to show all .wav files in Library, the absolute path to your .wav file, or \"play\": ");
            nextLine = scan.nextLine();
            library.importWav(nextLine);
            
            if (nextLine.equals("disp"))
            {
                for (AudioFile audioFile : library.getAudioFiles())
                {
                    System.out.println(audioFile.getName());
                }
            }    
        }
        
        System.out.print("Enter the index of the .wav file to play (1, 2, etc.): ");
        nextLine = scan.nextLine();
        int index = Integer.parseInt(nextLine);

        try
        {
            Player player = new Player(library.getAudioFiles().get(index-1));
            player.play();

            while (!nextLine.equals("stop"))
            {
                System.out.print("Enter a player control (play, pause, stop, skipBack, skipForward, or getTime): ");
                nextLine = scan.nextLine();
                
                if (nextLine.equals("play"))
                    player.play();
                else if (nextLine.equals("pause"))
                    player.pause();
                else if (nextLine.equals("stop"))
                    player.stop();
                else if (nextLine.equals("skipBack"))
                    player.skipBack();
                else if (nextLine.equals("skipForward"))
                    player.skipForward();
                else if (nextLine.equals("getTime"))
                    System.out.println("Current Audio Time: "
                                       + (int) player.getCurrentTime() + "/"
                                       + (int) player.getTotalTime());
            }
        }

        catch (Exception e)
        {
            System.out.println("Something broke...");
            System.out.println(e.getMessage());
        }
    } 
        
    public void popUp(String warning)
    {
        // This will generate a JSwing pop-up window for whenever
        // we want to display warnings, errors, or notifications
        // to the user.
    }
}


