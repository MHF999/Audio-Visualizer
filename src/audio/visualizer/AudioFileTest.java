/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package audio.visualizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 *
 * @author dacota
 */

// CONVERT DATA INTO AMPLITUDES TO GIVE FFT: 
public class AudioFileTest {
    
    public static void main(String[] args) {
        
         AudioFile audio = new AudioFile("/home/dacota/NetBeansProjects/WAVFILES/sample.wav"); // Create new AudioFile object.
         
         try (AudioInputStream source = AudioSystem.getAudioInputStream(audio.getFile())) {
            AudioFormat sourceFormat = source.getFormat();
             
            AudioFormat newFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                     sourceFormat.getSampleRate(), 16,
                                                     sourceFormat.getChannels(),
                                                     sourceFormat.getChannels() * 2,
                                                     sourceFormat.getSampleRate(),
                                                     false);
             
            if (!AudioSystem.isConversionSupported(newFormat, sourceFormat)) {
                 throw new IllegalArgumentException("Cannot convert this WAV to the target format.");
            }
             
            try (AudioInputStream converted = AudioSystem.getAudioInputStream(newFormat, source)) {
                 System.out.println("Converted format: " + converted.getFormat());
                 
            byte[] data = converted.readAllBytes();

            int framesRead = data.length / 4;

            double minAmplitude = Double.POSITIVE_INFINITY;
            double maxAmplitude = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < framesRead; i++) {
                int offset = i * 4;

                // Decode samples:
                int left = (data[offset] & 0xFF)
                        | ((data[offset + 1] & 0xFF) << 8);

                int right = (data[offset + 2] & 0xFF)
                        | ((data[offset + 3] & 0xFF) << 8);

                // Convert samples to signed integers:
                if (left >= 32768) {
                    left -= 65536;
                }

                if (right >= 32768) {
                    right -= 65536;
                }

                // Normalize both channels to [-1, 1]:
                double leftAmplitude = left / 32768.0;
                double rightAmplitude = right / 32768.0;

                // Combine stereo L-R into mono sample:
                double mono = (leftAmplitude + rightAmplitude) / 2.0;

                // Track smallest/largest amplitudes:
                minAmplitude = Math.min(minAmplitude, mono);
                maxAmplitude = Math.max(maxAmplitude, mono);
            }

            System.out.println("Frames read: " + framesRead);
            System.out.println("Minimum amplitude: " + minAmplitude);
            System.out.println("Maximum amplitude: " + maxAmplitude);
        
           } 
         } catch (Exception e) {
               e.printStackTrace();
             }
             
    
         /*try (AudioInputStream stream = AudioSystem.getAudioInputStream(audio.getFile())) {
             System.out.println(stream.getFormat());
         } catch (Exception e) {
             e.printStackTrace();
         }*/
         
         /*try (AudioInputStream stream = AudioSystem.getAudioInputStream(audio.getFile())) {
             byte[] buffer = new byte[4];
             int bytesRead = stream.read(buffer);
             
             int left = (buffer[0] & 0xFF) | ((buffer[1] & 0xFF) << 8);
             int right = (buffer[2] & 0xFF) | ((buffer[3] & 0xFF) << 8);

             System.out.println("Left sample: " + left);
             System.out.println("Right sample: " + right);
             
             double leftAmplitude = left / 32768.0;
             double rightAmplitude = right / 32768.0;

             System.out.println("Left amplitude: " + leftAmplitude);
             System.out.println("Right amplitude: " + rightAmplitude);

             System.out.println("Bytes read: " + bytesRead);
             
             for (int i = 0; i < bytesRead; i++) {
                 System.out.println("Byte " + i + ": " + buffer[i]);
             }
             
         } catch (Exception e) {
                     e.printStackTrace();
                    }
     */
        // Output to verify that everything worked:
        System.out.println("Sample Rate: " + audio.getSampleRate()); // Call getSampleRate method and print result.
        System.out.println("Duration: " + audio.getLength() + " seconds."); // Call getLength and print result.
    }
}

