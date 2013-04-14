package code;
import java.io.File;

import java.io.IOException;





import javax.sound.sampled.AudioFormat;

import javax.sound.sampled.AudioInputStream;

import javax.sound.sampled.AudioSystem;

import javax.sound.sampled.DataLine;

import javax.sound.sampled.LineUnavailableException;

import javax.sound.sampled.SourceDataLine;

//Thanks to painkiller on StackOverflow



public class DelayedPlay implements Runnable {



	private final int BUFFER = 131072;

	private File soundFile;

	private AudioInputStream audioStream;

	private SourceDataLine sourceLine;

	private AudioFormat format;



	public DelayedPlay(String filename){

     	try {

         	soundFile = new File(filename);

         	audioStream = AudioSystem.getAudioInputStream(soundFile);

         	format = audioStream.getFormat();

         	DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

         	sourceLine = (SourceDataLine) AudioSystem.getLine(info);

         	sourceLine.open(format);

     	} catch (Exception e) {

    	 	e.printStackTrace();

     	}	 

 	}

 

 	public void run(){

	     int read = 0; //past tense of read "red"

	     byte[] buff = new byte[BUFFER]; 

	     

	     sourceLine.start();

	     while (read != -1) {

	         try {

	             read = audioStream.read(buff, 0, buff.length);

	         } catch (Exception e) {

	             e.printStackTrace();

	         }

	         if (read >= 0) {

	             sourceLine.write(buff, 0, read);

	         }

	     }

	

	     sourceLine.drain();

	     sourceLine.close();	 

 	}

}