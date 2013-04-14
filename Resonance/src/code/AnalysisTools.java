package code;

import gui.OutputFrame;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.io.BufferedWriter;

import java.io.File;

import java.io.FileWriter;

import java.util.ArrayList;



import javax.swing.Timer;



public class AnalysisTools {



	boolean acceptingData, firstByte;

	ArrayList<Double> X;

	ArrayList<Double> Y;

	OutputFrame frame;

	double maxX;

	double maxY;

	double b,m;

	long initialTime;

	Timer countdown;



	final double MAX_FREQUENCY = 2000;

	final double MIN_FREQUENCY = 150;

	final double DURATION = 30*1000;



	public AnalysisTools(){

		setup();

	}



	public AnalysisTools(OutputFrame F){

		frame = F;

		setup();

	}



	private void endData(){

		acceptingData = false;

	}



	private void newDatum(double x, double y){

		if(!acceptingData) return;

		X.add(x);

		Y.add(y);

		if(x>maxX) maxX = x;

		if(y>maxY) maxY = y;

//		frame.enterData(x,y*(100/128));
	}



	public void writeDataTo(String filename){

		//Thanks to mkyong at mkyong.com/java

		try {

			String content = "";

			for(int index = 0; index<X.size()-1; index++){

				content += X.get(index) + " " + Y.get(index) + "\r\n";

			}

			content += X.get(X.size()-1) + " " + Y.get(Y.size() - 1);

			System.out.println(content);

			File file = new File(filename);

			if (!file.exists()) {

				file.createNewFile();

			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());

			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(content);

			bw.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}



	public void addByte(long time, byte y){

		if(!acceptingData) return;

		if(firstByte){

			initialTime = time;

			firstByte = false;

			countdown.start();

		}

		double frequency = Math.pow(10, m*(time-initialTime) + b);

		double volume = Math.abs((int)y);

		if(frequency>MAX_FREQUENCY){

			endData();

		} else {

			//newDatum(frequency, volume);
			newDatum(time-initialTime,volume);
			
		}

	}



	private void setup(){

		acceptingData = true;

		X = new ArrayList<Double>();

		Y = new ArrayList<Double>();

		maxX = Double.MIN_VALUE;

		maxY = Double.MIN_VALUE;

		firstByte = true;

		m = (Math.log10(MAX_FREQUENCY) - Math.log10(MIN_FREQUENCY)) / DURATION;

		b = Math.log10(MIN_FREQUENCY);

		countdown = new Timer((int)(DURATION*1.3), new ActionListener () {

			@Override

			public void actionPerformed(ActionEvent e) {

				endData();
				writeDataTo("res/baseline.txt");
			}

		});

	}



}