package audio.visualizer;

/**
 *
 * @author Landon
 */
public class Library implements Observer {
    
    // Necessary implementation for the Observer interface.
    public void alert() {}
    
    // These will probably have return types later on.
    public void getWAV() {}
    public void getFFT() {}
    
    // These are private because Library will be an observer.
    private void selectFile() {}
    private void importWAV() {}
    private void exportMP4() {}
    
}
