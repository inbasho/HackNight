package code;

import gui.OutputFrame;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;

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
	long initialTime, previousTime;
	Timer countdown;
	ArrayList<Double> baselineX;

	ArrayList<Double> baselineY;

	int currentBaselineIndex;
	
	
	final int RESOLUTION = 100;
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
		frame.enterData(x,y);
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

	public void addByte(long time, byte[] y){
		if(!acceptingData) return;
		if(firstByte){
			previousTime = time;
			initialTime = time;
			firstByte = false;
			countdown.start();
		}
//		System.out.println((time-previousTime)/y.length);
//		System.out.println((time-previousTime));
//		System.out.println(y.length);
//		System.out.println((double)125/(double)11024);
		double timeSlope = (double)(time-previousTime)/(double)y.length;
		
		for(int i=1; i<y.length; i=i+2*RESOLUTION){
			double actualTime = previousTime+timeSlope*i;
//			System.out.println(timeSlope);
			double frequency = Math.pow(10, m*(actualTime-initialTime) + b);
			while(frequency > baselineX.get(currentBaselineIndex)){

				currentBaselineIndex++;

				} 
				currentBaselineIndex--;


				double volume = Math.abs((int)y[i]) / interpolate(baselineY.get(currentBaselineIndex+1), 

				baselineY.get(currentBaselineIndex), baselineX.get(currentBaselineIndex+1), 

				baselineX.get(currentBaselineIndex), frequency);
			if(frequency>MAX_FREQUENCY){
				endData();
			} else {
				newDatum(frequency, volume);
				//newDatum(time-initialTime,volume);
			}
		}
		previousTime = time;
		
	}
	
	public double interpolate(double y1, double y2, double x1, double x2, double xc){

		int counter = 0;

		while(y1==0){

		y1=baselineY.get(currentBaselineIndex)-(++counter);

		}


		int counter2 = 0;


		while(y2==0){

		y2=baselineY.get(currentBaselineIndex)+(++counter);

		}

		double result = ((y2-y1)/(x2-x1))*(xc-x1)+y1; 

		if (result<0){

		System.out.println(y1+":"+y2+":"+x1+":"+x2+":"+xc);

		}

		return result;
		}

	private void setup(){
		acceptingData = true;
		baselineX = new ArrayList<Double>();
		baselineY = new ArrayList<Double>();
		X = new ArrayList<Double>();
		Y = new ArrayList<Double>();
		maxX = Double.MIN_VALUE;
		maxY = Double.MIN_VALUE;
		firstByte = true;
		m = (Math.log10(MAX_FREQUENCY) - Math.log10(MIN_FREQUENCY)) / DURATION;
		b = Math.log10(MIN_FREQUENCY);
		try {

			String content = "";

			File file = new File("res/data.txt");

			FileReader fr = new FileReader(file.getAbsoluteFile());

			BufferedReader br = new BufferedReader(fr);

			String next = "";

			int space = 0;

			double nextFreq = 0.0;

			double nextVolume = 0.0;

			int counter = 0;

			while(!((next=br.readLine())==null)){

			space = next.indexOf(" ");

			nextFreq = Double.valueOf(next.substring(0, space-1));

			baselineX.add(counter,nextFreq);

			nextVolume = Double.valueOf(next.substring(space+1, next.length()-1));

			baselineY.add(counter, nextVolume);
			counter++;
			}

			br.close();

			} catch (Exception e) {

			e.printStackTrace();

			}
		countdown = new Timer((int)(DURATION*1.3), new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				endData();
				writeDataTo("res/baseline.txt");
				countdown.stop();
			}
		});
	}
}
