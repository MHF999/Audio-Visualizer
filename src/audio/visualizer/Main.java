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
        
        System.out.println(library.getLibraryLocation());
        
//        library.importWav("C:\\Users\\Landon\\Downloads\\default2.wav");
                
        while (!nextLine.equals("play"))
        {
            // Like the following (Windows) filepath -- C:\Users\Landon\Downloads\sample.wav
            System.out.print("Enter \"disp\", the absolute path to your .wav file, or \"end\": ");
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
        
        while (true) // Need to manually exit program.
        {
            System.out.print("Enter the index of the .wav file to play (1, 2, etc.): ");
            nextLine = scan.nextLine();
            int index = Integer.parseInt(nextLine);

            try
            {
                Player p = new Player(library.getAudioFiles().get(index-1));
                p.play();
            }

            catch (Exception e)
            {
                System.out.println("Something broke...");
                System.out.println(e.getMessage());
            }
        }
    } 
        
        
// Random Tests:
        
//        try
//        {
//            System.out.println(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath());
//            System.out.println(new File(".").getCanonicalPath());
//            data = audioFile.getData();
//            
//            for (int i = (int) audioFile.getFrameLength() / 4; i < audioFile.getFrameLength()/2; i++)
//            {
//                System.out.println(data[i]);
//                Thread.sleep(1);
//            }
//        }
//        
//        catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//        }
//                
    
    
    public void popUp(String warning)
    {
        // This will generate a JSwing pop-up window for whenever
        // we want to display warnings, errors, or notifications
        // to the user.
    }
}


