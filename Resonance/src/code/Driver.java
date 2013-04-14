package code;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.swing.Timer;

import javax.swing.JFrame;

import gui.OutputFrame;

public class Driver {
	OutputFrame frame;
	Timer timer;
	
	public Driver(){
//		MakeSound makeSound = new MakeSound();
//		makeSound.playSound("res/Sine150to2000.wav");
		new Thread(new DelayedPlay("res/Sine150to2000.wav")).start();
		AudioFormat format = new AudioFormat(44100.0f,16,1,true,false);
		frame = new OutputFrame();
		AnalysisTools at = new AnalysisTools(frame);
	     MicrophoneInput mr = new MicrophoneInput(format,at); 
	     new Thread(mr).start();  
	     
//	     try {
//			Thread.sleep(100000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	     System.out.println();
//	     mr.stop();

//		frame.enterData(180,3);
//		frame.enterData(300, 4);
//		frame.enterData(400, 7);
//		frame.enterData(800, 20);
//		frame.enterData(1000,40);
//		
		timer = new Timer(1000, new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
//					
//					frame.enterData(100, 10);
//					frame.enterData(200, 10);
//					frame.enterData(300, 10);
//					frame.enterData(400, 10);
//					frame.enterData(500, 10);
//					frame.enterData(600, 10);
//					frame.enterData(700, 10);
//					frame.enterData(800, 10);
//					frame.enterData(900, 10);
//					frame.enterData(1000, 10);
//					frame.enterData(1100, 10);
//					frame.enterData(1200, 10);
//					frame.enterData(1300, 10);
//					frame.enterData(1400, 10);
//					frame.enterData(1500, 10);
//					frame.enterData(1600, 10);
//					frame.enterData(1700, 10);
//					frame.enterData(1800, 10);
//					frame.enterData(1900, 10);
//					frame.enterData(2000, 10);
//					frame.enterData(850,0.001);
//					frame.enterData(850,0.01);
//					frame.enterData(850,0.1);
//					for(int i = 1; i < 80;i++){
//						frame.enterData(850,i);
//					}
					

					frame.repaint();
				}
			});
		timer.start();
		frame.drawMaxima(800, 20);
	}
	public static void main(String[] args){
		new Driver();
	}
}
