package audio.visualizer;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Landon
 */
public class AudioFile
{
    private Path file;
    private double[] monoDataCopy;
    
    public AudioFile(String filepath)
    {
        file = Paths.get(filepath);
    }
    
    public AudioFile(Path path)
    {
        file = path;
    }
    
    public boolean isWav()
    {
        boolean isWAV = false;
        String filepath = getFile().getAbsolutePath();
        
        int periodIndex = filepath.lastIndexOf(".");
        
        if (filepath.substring(periodIndex+1).equals("wav") ||
                filepath.substring(periodIndex+1).equals("WAV"))
        {
            isWAV = true;
        }
        
        return isWAV;
    }
    
    public String getName()
    {
        return file.getFileName().toString();
    }
    
    public Path getPath()
    {
        return file;
    }
    
    public File getFile()
    {
        return file.toFile();
    }
    
    // Method that opens the audio file and creates an input stream for reading 
    // in audio data. Returns audio duration in seconds; throws Illegal State
    // Exception if wrong audio format, file can't be read, or duration can't
    // be determined.
    public long getFrameLength() {
        
        // Try should automatically close input stream after execution.
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(getFile()))
        {
            
            // Variables for frame rate and frame length; AudioInputStream provides these.
            // Frame rate = number of audio frames processed per second.
            // Frame length = total number of frames in audio file.
            long frameLength = audio.getFrameLength(); 

            // These values must be positive. Otherwise, duration cannot be
            // accurately calculated.
            if (frameLength < 0) {
                throw new IllegalStateException("Failed to retrieve audio duration: " + file);
            }
            
            // Retern total number of frames in .wav file:
            return frameLength;
        }
        
        // Handle cases with wrong audio format and general file read issues:
        catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException("Could not read WAV file: " + file, e);
        }
    }
    
    // Method to get sample rate from the audio in samples per second.
    // Return sample rate as an integer.
    // Throws Illegal State Exception if file is not or cannot be read.
    public int getSampleRate() {
        
     // Open audio file and extract its audio format information.
     // Stream should close automatically after execution.
     try (AudioInputStream audio = AudioSystem.getAudioInputStream(getFile())) {
         // Read/return sample rate and convert this to an integer.
            return (int) audio.getFormat().getSampleRate();
        }
        
        // Handle incorrect audio format, file read errors.
     catch (UnsupportedAudioFileException | IOException e) { 
        throw new IllegalStateException("Could not read file: " + file, e);
        }
    }
    
    // 
    private short[] getData() throws UnsupportedAudioFileException, IOException
    {
        short[] data = new short[(int) getFrameLength() / 2];
        
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(getFile())) {
            AudioFormat format = audio.getFormat();

            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || format.getSampleSizeInBits() != 16)
            {
                throw new UnsupportedAudioFileException("Only 16-bit signed PCM .wav files are supported.");
            }

            byte[] audioBytes = new byte[audio.available()];
            audio.read(audioBytes);

            if (audioBytes.length % 2 != 0) {
                throw new IOException("Incomplete 16-bit audio sample.");
            }

            ByteBuffer.wrap(audioBytes)
                    .order(format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN)
                    .asShortBuffer()
                    .get(data);

            return data;
        }

    }
    
    public double[] getMonoDataCopy() throws UnsupportedAudioFileException, IOException {
        if (monoDataCopy == null) {
            monoDataCopy = decodeMonoData();
        }
        
        return monoDataCopy.clone();
    }
    
    // Method that decodes audio data read by getData and prepares it for transformation by the FFT.
    public double[] decodeMonoData() throws UnsupportedAudioFileException, IOException {
        
        // Open audio input stream and read .WAV, get its format:
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(getFile())) {
            
            AudioFormat format = audio.getFormat();
            
            int channels = format.getChannels();
            int bytesPerSample = format.getSampleSizeInBits() / 8;
            int frameSize = format.getFrameSize();
            
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || format.getSampleSizeInBits() != 16) {
                throw new UnsupportedAudioFileException("Only 16-bit WAV files are currently supported.");
            }
            
            if (channels < 1 || bytesPerSample != 2 || frameSize != channels * bytesPerSample) {
                throw new UnsupportedAudioFileException("Unsupported channel or frame layout.");
            }
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            byte[] bytes = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = audio.read(bytes)) != -1) {
                if (bytesRead > 0) {
                    output.write(bytes, 0, bytesRead);
                }
            }
            
            byte[] audioBytes = output.toByteArray();

            if (audioBytes.length % frameSize != 0) {
                throw new IOException(
                        "Audio data ends with an incomplete frame.");
            }

            int frameCount = audioBytes.length / frameSize;
            short[] rawSamples = new short[audioBytes.length / 2];

            ByteOrder byteOrder;

            // Handle both big/little endian:
            if (format.isBigEndian()) {
                byteOrder = ByteOrder.BIG_ENDIAN;
            } else {
                byteOrder = ByteOrder.LITTLE_ENDIAN;
            }

            ByteBuffer buffer = ByteBuffer.wrap(audioBytes);
            buffer.order(byteOrder);
            buffer.asShortBuffer().get(rawSamples);
            
            double[] mono = new double[frameCount];
            
            for (int frame = 0; frame < frameCount; frame++) {
                double sum = 0.0;
                                                                  // finds the array 
                                                                  // index for each frame and channel.
                                                                  // Say we have 2 channels; then for frame 0, we have right(1) and left(0). for left,
                                                                  // 0 * 2 + 0 = 0. For right, 0 * 2 + 1 = 1. Next for frame 1: left = 1 * 2 + 0 = 2;
                                                                  // right = 1 * 2 + 1 = 3, etc etc. Dividing each index by 32768.0 yields a floating pt
                                                                  // value in the range -1 to 1 (must end in .0 to ensure floating point division!!).
                
                for (int channel = 0; channel < channels; channel++) {
                    int index = frame * channels + channel;
                    sum += rawSamples[index] / 32768.0;
                }
                
                mono[frame] = sum / channels;
            } 
            
            return mono;
        }
        
       /*
        // ERROR HANDLING:
        // Handle weird results like no channel, or an amount we can't divide 1,024 samples between neatly:
        if (channels < 1 || raw.length % channels != 0) {
            throw new IOException("Invalid audio channel layout.");
        }

        // Find number of frames and create an array to store our output:
        int frameCount = raw.length / channels;
        double[] mono = new double[frameCount];
        
        // Loop through each frame:
        for (int frame = 0; frame < frameCount; frame++) {
            double sum = 0.0; // Sum will store the values from all channels within the current frame.
            
            // Normalize and store each channel's samples:
            for (int channel = 0; channel < channels; channel++) {
                sum += raw[frame * channels + channel] / 32768.0; 
            mono[frame] = sum / channels; // Convert to mono by dividing total # of channels in a frame by total channel count; store the average in a mono
        }                                 // array.

        return mono;*/
       }
    }
// NOTES FOR AUDIO DATA DECODING: integer value = lower byte + 256(higher byte); 
// eg for (3,0) value = 3 + 256(0) = 3.