package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class OutputFrame extends JFrame{

	private BufferedImage graph;
	private Graphics graphGraphics;
	private final int leftLabelSize = 100;
	private final int bottomLabelSize = 100;
	private int windowSizeX = 900;
	private int windowSizeY = 600;
	private int graphSizeX = 800;
	private int graphSizeY = 500;
	private final int pointSize = 10;
	private final int maximaOvalSize = 50;
	private final int markerLength = 20;
	
	private BufferedImage gradient;
	
	private int previousFrequencyLocation;
	private int previousDecibelLocation;
	
	private final double maxDecibelsLog = Math.log10(100);
	private final double minDecibelsLog = Math.log10(0.001);
	private final double maxFrequencyLog = Math.log10(2000);
	private final double minFrequencyLog = Math.log10(150);
	
	private final double minFrequency = 150;
	
	
	public OutputFrame(){
		this.setUndecorated(true);
		getScreenSize();
		initGraph();
		previousFrequencyLocation = getFrequencyPixels(150);
		previousDecibelLocation = getDecibelPixels(0.001);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(windowSizeX, windowSizeY);
		this.setVisible(true);
	}
	
	private void getScreenSize(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		windowSizeX = (int)screenSize.getWidth();
		windowSizeY = (int)screenSize.getHeight();
		graphSizeX = windowSizeX - leftLabelSize;
		graphSizeY = windowSizeY - bottomLabelSize;
	}
	private void initGraph(){
		graph = new BufferedImage(windowSizeX,windowSizeY,BufferedImage.TYPE_INT_ARGB);
		graphGraphics = graph.createGraphics();
		drawBackground();
		this.pack();
	}
	
	private void drawBackground(){
		
		graphGraphics.setColor(Color.WHITE);
		graphGraphics.fillRect(0, 0, windowSizeX, windowSizeY+50);
		graphGraphics.setColor(Color.BLACK);
		((Graphics2D) graphGraphics).setStroke(new BasicStroke(5));
        ((Graphics2D) graphGraphics).draw(new Line2D.Float(leftLabelSize-3, windowSizeY - bottomLabelSize+3, windowSizeX, windowSizeY - bottomLabelSize+3));
        ((Graphics2D) graphGraphics).draw(new Line2D.Float(leftLabelSize-3, windowSizeY - bottomLabelSize+3, leftLabelSize-3, 0));
		graphGraphics.drawLine(0+leftLabelSize, windowSizeY - bottomLabelSize, windowSizeX, windowSizeY - bottomLabelSize);
		graphGraphics.drawLine(0+leftLabelSize, windowSizeY - bottomLabelSize, 0+leftLabelSize, 0);
		graphGraphics.fillRect(0, 0, 10, 10);
		gradient= null;
		try {
		    gradient = ImageIO.read(new File("res/gradient.jpg"));
		} catch (IOException e) {
			System.out.println("image loading error");
		}
		drawGraphMarkers();
	}
	
	private void drawGraphMarkers(){
		drawFrequencyMarker(1046);
		drawDecibelMarker(1);
        graphGraphics.setFont(new Font("Arial", Font.BOLD, 40));
        graphGraphics.drawString("FREQUENCY", 500, graphSizeY+90);
		
		
	}
	public void enterData(double freq, double decibel){
		graphGraphics.setColor(Color.BLACK);
		int freqLoc = getFrequencyPixels(freq);
		int decibelLoc = getDecibelPixels(decibel);	
//		((Graphics2D) graphGraphics).setStroke(new BasicStroke(3));
//        ((Graphics2D) graphGraphics).draw(new Line2D.Float(previousFrequencyLocation, previousDecibelLocation, freqLoc, decibelLoc));

//		Polygon graphSegment = new Polygon();		
//		graphSegment.addPoint(previousFrequencyLocation,previousDecibelLocation);
//		graphSegment.addPoint(previousFrequencyLocation, graphSizeY);
//		graphSegment.addPoint(freqLoc, graphSizeY);
//		graphSegment.addPoint(freqLoc, decibelLoc);
//		graphGraphics.setClip(graphSegment);
//		graphGraphics.drawImage(gradient,0,0,null);
//		graphGraphics.fillPolygon(graphSegment);
		
		graphGraphics.fillRect(getFrequencyPixels(freq),getDecibelPixels(decibel),2, 2);
		previousFrequencyLocation = freqLoc;
		previousDecibelLocation = decibelLoc;
	}
	

	private int getFrequencyPixels(double freq){
		return scaleFrequency(freq)+leftLabelSize;
	}
	private int getDecibelPixels(double decibel){
		return  graphSizeY - scaleDecibels(decibel);
	}
	private int scaleFrequency(double f){
//		System.out.println((int)(Math.log10(f-minFrequency)* graphSizeX/(maxFrequencyLog - minFrequencyLog) ));
//		return (int)(Math.log10(f-minFrequency)* graphSizeX/(maxFrequencyLog - minFrequencyLog) );
		return(int)((Math.log10(f)-minFrequencyLog)*graphSizeX/(maxFrequencyLog - minFrequencyLog));
	}
	private int scaleDecibels(double f){
//		return (int)(Math.log10(f)/maxDecibelsLog * graphSizeY);
//		System.out.println((int)((Math.log10(f)-minDecibelsLog)*graphSizeY/(maxDecibelsLog - minDecibelsLog)));
		return (int)((Math.log10(f)-minDecibelsLog)*graphSizeY/(maxDecibelsLog - minDecibelsLog));
	}
	
	private void drawFrequencyMarker(double frequency){
//		System.out.println(getFrequencyPixels(frequency) + " ");
		((Graphics2D) graphGraphics).setStroke(new BasicStroke(5));
        ((Graphics2D) graphGraphics).draw(new Line2D.Float(getFrequencyPixels(frequency), graphSizeY, getFrequencyPixels(frequency), graphSizeY + markerLength));
        graphGraphics.setFont(new Font("Arial", Font.BOLD, 20));
        graphGraphics.drawString((int)frequency+ " hz", getFrequencyPixels(frequency), graphSizeY+40);
//		graphGraphics.drawLine(getFrequencyPixels(frequency), graphSizeY, getFrequencyPixels(frequency), graphSizeY + markerLength);
	}
	private void drawDecibelMarker(double decibel){
		((Graphics2D) graphGraphics).setStroke(new BasicStroke(5));
        ((Graphics2D) graphGraphics).draw(new Line2D.Float(leftLabelSize, getDecibelPixels(decibel), leftLabelSize - markerLength, getDecibelPixels(decibel)));
        graphGraphics.setFont(new Font("Arial", Font.BOLD, 20));
        graphGraphics.drawString(decibel+ " dB", leftLabelSize-90, getDecibelPixels(decibel));
		
	}
	
	public void drawMaxima(double freq, double decibel){
	graphGraphics.drawOval(getFrequencyPixels(freq)-maximaOvalSize/2, getDecibelPixels(decibel)-maximaOvalSize/2, maximaOvalSize, maximaOvalSize);	
	}
	
	@Override
	public void paint(Graphics g){
		g.drawImage(graph, 0, 0, null);
	}
	
}
