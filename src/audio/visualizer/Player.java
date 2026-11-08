package audio.visualizer;

import java.io.*;
import javax.sound.sampled.*;

/**
 *
 * @author Landon
 */
public class Player {

    private AudioFile audioFile;
    private AudioInputStream inputStream;
    private Clip clip;
    private String playerState;
    private long microPosition;

    Player(AudioFile audioFile) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        this.audioFile = audioFile;
        playerState = "paused";
        microPosition = 0L;
    }

    public void play() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        if (!playerState.equals("playing")) {
            inputStream = AudioSystem.getAudioInputStream(audioFile.getFile());
            clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.setMicrosecondPosition(microPosition);
            clip.start();

            playerState = "playing";
        }
    }

    public void pause() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        if (!playerState.equals("paused")) {
            microPosition = clip.getMicrosecondPosition();
            clip.stop();
            clip.close();

            playerState = "paused";
        }
    }

    public void stop() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        pause();
        microPosition = 0L;
    }

    public void skipBack() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        String initialPlayerState = playerState;

        pause();

        if (microPosition - 10000000L >= 0) {
            microPosition -= 10000000L;
        }

        if (initialPlayerState.equals("playing")) {
            play();
        }
    }

    public void skipForward() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        String initialPlayerState = playerState;

        pause();

        if (microPosition + 10000000L < (audioFile.getFrameLength() / audioFile.getSampleRate()) * 1000000) {
            microPosition += 10000000L;
        }

        if (initialPlayerState.equals("playing")) {
            play();
        }
    }

    public double getCurrentTime() {
        double currentTime;
        currentTime = clip.getMicrosecondPosition() / 1000000.0d;

        return currentTime;
    }

    public double getTotalTime() {
        double totalTime;
        totalTime = audioFile.getFrameLength() / (double) audioFile.getSampleRate();

        return totalTime;
    }

    // Public accessor method to expose audio file to FFT:
    public AudioFile getAudioFile() {
        return audioFile;
    }

    // Public accessor method to expose current frame to FFT:
    public long getCurrentFrame() {
        long currentPosition;

        if (playerState.equals("playing")) {
            currentPosition = clip.getLongFramePosition(); // While playing, get current position.
        } else {
            currentPosition = Math.round(microPosition * audioFile.getSampleRate() / 1000000.0); // Convert microPosition to frames using sample rate.
        }

        return currentPosition;
    }

    //Added by sdnapoles: seek-on-click supportr for GUI seek bar.
    public void seekTo(long millis) {
        microPosition = millis * 1000L;
        if (playerState.equals("playing")) {
            clip.setMicrosecondPosition(microPosition);
        }
    }
}
