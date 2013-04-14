package code;
//credit to http://ganeshtiwaridotcomdotnp.blogspot.com
import java.io.ByteArrayInputStream;  
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;  
import javax.sound.sampled.AudioInputStream;  
import javax.sound.sampled.AudioSystem;  
import javax.sound.sampled.DataLine;  
import javax.sound.sampled.TargetDataLine;  

 public class MicrophoneInput implements Runnable {  
   private AudioInputStream audioInputStream;  
   private AudioFormat format;
   private AnalysisTools at;
   public TargetDataLine line;  
   public Thread thread;  
   private double duration;  
   public MicrophoneInput(AudioFormat format, AnalysisTools at) {  
     super();  
     this.at = at;
     this.format = format;  
   }  
   @Override  
   public void run() {  
     duration = 0;  
     line = getTargetDataLineForRecord();  
     final ByteArrayOutputStream out = new ByteArrayOutputStream();  
     final int frameSizeInBytes = format.getFrameSize();  
     final int bufferLengthInFrames = line.getBufferSize() / 8;  
     final int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;  
     final byte[] data = new byte[bufferLengthInBytes];  
     int numBytesRead;  
     line.start();  
     while (true) {
       if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {  
         break;  
       }
       at.addByte(System.currentTimeMillis(),data);
//       System.out.println(volume);
//       System.out.println(data[0] + " : " + data[1]);
     }  
     // we reached the end of the stream. stop and close the line.  
     line.stop();  
     line.close();  
     line = null;  
     // stop and close the output stream  
     try {  
       out.flush();  
       out.close();  
     } catch (final IOException ex) {  
       ex.printStackTrace();  
     }  

   }  
   private TargetDataLine getTargetDataLineForRecord() {  
     TargetDataLine line;
     final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);  
     if (!AudioSystem.isLineSupported(info)) {  
       return null;  
     }  
     // get and open the target data line for capture.  
     try {  
    	 
       line = (TargetDataLine) AudioSystem.getLine(info);  
       line.open(format, line.getBufferSize());  
       
     } catch (final Exception ex) {  
       return null;  
     }  
     return line;  
   }  

 }  