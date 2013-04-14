package code;

 

import gui.OutputFrame;

import java.awt.Point;

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

final int RESOLUTION = 5;

final double MAX_FREQUENCY = 2000;

final double MIN_FREQUENCY = 150;

final double DURATION = 30*1000;

int timesCalled;

final int averageThreshold = 2000;

ArrayList<Double> movingAverageX;

ArrayList<Double> movingAverageY;

 

public AnalysisTools(OutputFrame F){

frame = F;

setup();

}

 

private void endData(){
System.out.println("STAOP");
acceptingData = false;
ArrayList<Point> res = findResonance();
for(Point p : res){
	frame.drawMaxima(p.getX(), p.getY());
	System.out.println(p.getX() + " " + p.getY());
	
}
}

 



 
//
//private void newDatum(double x, double y){
//
//if(!acceptingData) return;
//
//timesCalled++;
//
//X.add(x);
//
//Y.add(y);
//
//if(x>maxX) maxX = x;
//
//if(y>maxY) maxY = y;
//
//frame.enterData(x,y);
//
// 
//
//if(timesCalled%averageThreshold==0){
//
////calculate moving average
//
//double averageX = 0;
//
//int size = X.size();
//
//for(int i=0; i<averageThreshold; i++){
//	
//	if(X.get(size-i-1)<.01) X.set(size-i-1, .01);
//
//
//	averageX +=(X.get(size-i-1)/averageThreshold);
//
//}
//
// 
//
//double averageY = 0;
//
//size = Y.size();
//
//for(int i=0; i<averageThreshold; i++){
//	
//	if(Y.get(size-i-1)<.01) Y.set(size-i-1, .01);
//
//	averageY +=(Y.get(size-i-1)/averageThreshold);
//
//}
//
// 
//
//movingAverageX.add(averageX);
//
//movingAverageY.add(averageY);
//
// 
//
//frame.enterAverage(averageX, averageY); 
//
// 
//
//}//end if 
//
//}
//
// 


private void newDatum(double x, double y){
	if(!acceptingData) return;
	timesCalled++;
	X.add(x);
	Y.add(y);
	if(x>maxX) maxX = x;
	if(y>maxY) maxY = y;
	frame.enterData(x,y);
	
	if(x<.07*MAX_FREQUENCY) return;
	if(averageThreshold>X.size()) return;
	if(!(timesCalled%5==0)) return;

	double averageX = 0;
	int size = X.size();
	for(int i=1; i<averageThreshold; i++){
		if(X.get(size-i-1)<.01) X.set(size-i-1, .1);
		averageX +=(X.get(size-i-1)/averageThreshold);
	}
		
	double averageY = 0;
	size = Y.size();
	for(int i=1; i<averageThreshold; i++){
		if(Y.get(size-i-1)<.01) Y.set(size-i-1, .1);
		averageY +=(Y.get(size-i-1)/averageThreshold);
	}
		
	movingAverageX.add(averageX);
	movingAverageY.add(averageY);
		
	frame.enterAverage(averageX, averageY);	
		
}



public void writeDataTo(String filename){

 

//Thanks to mkyong at mkyong.com/java

 

try {

String content = "";

for(int index = 0; index<X.size()-1; index++){

content += X.get(index) + " " + Y.get(index) + "\r\n";

}

content += X.get(X.size()-1) + " " + Y.get(Y.size() - 1);

//System.out.println(content);

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

 

double timeSlope = (double)(time-previousTime)/(double)y.length;

 

for(int i=1; i<y.length; i=i+2*RESOLUTION){

double actualTime = previousTime+timeSlope*i;

double frequency = Math.pow(10, m*(actualTime-initialTime) + b);

while(frequency > baselineX.get(currentBaselineIndex) ){

currentBaselineIndex++;
if(currentBaselineIndex>=baselineX.size()) {
	acceptingData = false; return;
}
}

currentBaselineIndex--;

 

double volume = Math.abs((int)y[i]) / interpolate(baselineY.get(currentBaselineIndex+1),

baselineY.get(currentBaselineIndex), baselineX.get(currentBaselineIndex+1),

baselineX.get(currentBaselineIndex), frequency);

 

if(frequency>=MAX_FREQUENCY){

endData();

} else {

newDatum(frequency, volume);

}

}

previousTime = time;

 

}


public ArrayList<Point> findResonance(){

if(acceptingData) return null;

ArrayList<Point> result = new ArrayList<Point>();

for(int i=1; i<movingAverageX.size()-1; i++){

double preceedingSlope = (movingAverageY.get(i) + movingAverageY.get(i-1))/

movingAverageX.get(i) - movingAverageX.get(i-1);

double succeedingSlope = (movingAverageY.get(i+1) + movingAverageY.get(i))/

movingAverageX.get(i+1) - movingAverageX.get(i);

double preceedingAngle = (Math.abs(preceedingSlope)/preceedingSlope)*Math.atan(preceedingSlope);

double succeedingAngle = (Math.abs(succeedingSlope)/succeedingSlope)*Math.atan(succeedingSlope);

if(preceedingAngle>0){

if(preceedingAngle-succeedingAngle > (80/180)*Math.PI){

Point p = new Point();

p.setLocation(movingAverageX.get(i), movingAverageY.get(i));

result.add(p);

}

}

}

return result;

}

public double interpolate(double y1, double y2, double x1, double x2, double xc){

 

//Assume there's a non-zero value in the neighborhood

int counter = 0;

while(y1==0){

y1=baselineY.get(currentBaselineIndex)-(++counter);

}

 

int counter2 = 0;

while(y2==0){

y2=baselineY.get(currentBaselineIndex)+(++counter2);

}

 

double result = ((y2-y1)/(x2-x1))*(xc-x1)+y1;

return result;

}

 

private void setup(){

acceptingData = true;

baselineX = new ArrayList<Double>();

baselineY = new ArrayList<Double>();

X = new ArrayList<Double>();

Y = new ArrayList<Double>();

movingAverageX = new ArrayList<Double>();

movingAverageY = new ArrayList<Double>();

maxX = Double.MIN_VALUE;

maxY = Double.MIN_VALUE;

firstByte = true;

timesCalled=0;

m = (Math.log10(MAX_FREQUENCY) - Math.log10(MIN_FREQUENCY)) / DURATION;

b = Math.log10(MIN_FREQUENCY);

 

try {

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

}//end while

br.close();

} catch (Exception e) {

e.printStackTrace();

}//end try/catch

 

countdown = new Timer((int)(DURATION*1.1), new ActionListener () {

@Override

public void actionPerformed(ActionEvent e) {

endData();

writeDataTo("res/baseline.txt");

countdown.stop();

}//end method

});//end countdown initializiation

}//end setup

}//end classs