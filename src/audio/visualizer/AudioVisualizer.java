package audio.visualizer;

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
        // Just a bunch of Git/GitHub tests:
        System.out.println("Landon was here.");
        System.out.println("I am error");
        System.out.println("another message");
        System.out.println("Dacota can push stuff!");
        
        try
        {
            Player p = new Player();
            p.play();
        }
        
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.out.println("It did something!");
        }
        
        
        while (true)
        {}
    }
    
    // NOTE: I did not put in stubs for all of the classes yet...
    //       I might have time to do so later though. This should be
    //       a nice start though!
    
}