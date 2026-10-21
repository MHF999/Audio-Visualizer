package audio.visualizer;

/**
 *
 * @author Landon
 */
public class Library implements Observer
{    
    // Necessary implementation for the Observer interface.
    public void update() {}
    
    // These will probably have return types later on.
    public void getWAV() {} // return file itself
    public void getFFT() {} // return FFT
    
    // These are private because Library will be an observer.
    private void selectFile() {}
    private void importWAV() {} // add wav implementation
    private void exportMP4() {}
    
    // TODO: Need duplicate() and delete() methods. Put in library class instead???

    
}

// Duplicate file into library as its own thing; delete/manipulate from there.