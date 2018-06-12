package es.albertocuesta.SymMandelbrot.client;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

/**
 * This class shows a simple Java AWT interface for Mandelbrot Sets, calculated through a proxy to a Platform Symphony grid
 * @author Alberto Cuesta-Canada
 *
 */
public class MandelbrotDisplay extends JFrame implements MouseListener, WindowListener{
	
	private static final long serialVersionUID = 1L;

	private Logger logger;
	
	// Connections to other objects, could be adapted to use Spring
	private BufferedImage image;
	private GraphicsConfiguration graphicsConfiguration;
	private GridProxy gridProxy;
	
	// Coordinates will be 2-array of Doubles, 0 for x and 1 for y.
	public Double[] center;  // Center of the display
	public Double zoom;        // 2 divided by zoom is the size of the shown portion of each axis
	public Integer[] size;     // Number of pixels of the display on each axis
	public Integer iterations; // More iterations give more detail to the image, needs to increase with zoom

	/***********************************************************************
	 *                         CONSTRUCTOR & INITS                         *
	 ***********************************************************************/
	
	public MandelbrotDisplay(){
		//BasicConfigurator.configure();
		logger = Logger.getLogger("MandelbrotClient");
	}
	
	public void setImageParameters(Double[] center, Double zoom, Integer[] size, Integer iterations){
    	this.center = center;
    	this.zoom = zoom;
    	this.size = size; // size[0] = X-axis, width   /   size[1] = Y-axis, height
    	this.iterations = iterations;
	}
	
	/**
	 * Initializes the display
	 */
	public void initDisplay(){ // This is the stuff that should be done with Spring
		logger.debug("Initiating image storage and graphic display");
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	GraphicsDevice gs = ge.getDefaultScreenDevice();
    	graphicsConfiguration = gs.getDefaultConfiguration();
    	addWindowListener(this);
    	this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	image = graphicsConfiguration.createCompatibleImage(size[0], size[1], Transparency.OPAQUE);
	}
	
	/**
	 * Initializes just the image for calculation without a display
	 */
	public void initImage(){
		logger.debug("Initiating image storage");
		image = new BufferedImage(size[0], size[1], BufferedImage.OPAQUE);
	}
	
	/**
	 * Initializes the grid connections 
	 */
	
	public void initGridProxy(){
    	gridProxy = new es.albertocuesta.SymMandelbrot.client.GridProxy(size);		
	}
	
	/***********************************************************************
	 *                             MOUSE EVENTS                            *
	 ***********************************************************************/
	
	// TODO Move the application logic out of the event management (make mouseClicked much simpler)
    public void mouseClicked(MouseEvent e) {
    	if(!e.isConsumed()){
    		e.consume();
    		
    		Integer[] pixelClicked = new Integer[2];
    		pixelClicked[0] = e.getX();
    		pixelClicked[1] = e.getY();

    		center = fromPixelToMath(center, zoom, size, pixelClicked);
			
			if(e.getButton() == MouseEvent.BUTTON1){ // Left button zooms in
				zoom = zoom * 1.5;
				iterations = (int)Math.floor(iterations * 1.1); // Ugly
			}else if (e.getButton() == MouseEvent.BUTTON3){ // Right button zooms out
				zoom = zoom / 1.5;
				iterations = (int)Math.floor(iterations / 1.1);
			}else if (e.getButton() == MouseEvent.BUTTON2){
				logger.debug("Pixel " + pixelClicked[0].toString() + "," + pixelClicked[1].toString());
				Double[] newCenter = fromPixelToMath(center, zoom, size, pixelClicked);
				logger.debug("Math " + newCenter[0].toString() + "," + newCenter[1].toString());
				return;
			}

			updateImage(calculateMandelbrot());
	    	refresh();
    	}
    }
    
    public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
 
	/***********************************************************************
	 *                            WINDOW EVENTS                            *
	 ***********************************************************************/
	
	public void windowActivated(WindowEvent e){}
    
    public void windowClosed(WindowEvent e){}
    
    public void windowClosing(WindowEvent e){
    	logger.debug("Window closing");
    	gridProxy.close();
    	this.dispose();
    }
    
    public void windowDeactivated(WindowEvent e){}
    
    public void windowDeiconified(WindowEvent e){}
    
    public void windowIconified(WindowEvent e){}
    
    public void windowOpened(WindowEvent e){}
    
	/***********************************************************************
	 *                           IMAGE MANAGEMENT                          *
	 ***********************************************************************/
	
    public Integer[][] calculateMandelbrot(){
    	logger.info("Calculating set:" +
    			"\n    center: [" +center[0]+","+center[1]+ "]" +
    			"\n    zoom: " + zoom + "" +
    			"\n    iterations: " + iterations);
    	Integer[][] mandelbrotSet;
		Double[][] coordinateSet;
		try{
			coordinateSet = fromPixelToMathSet(center, zoom, size);
			gridProxy.openSession(size, coordinateSet);
			gridProxy.calculateMandelbrot(center, zoom, size, iterations);
			TaskCallback taskCallback = gridProxy.getCallback();
	        synchronized(taskCallback){ // I might try to synchronize just parts of the callback
	        	logger.info("Waiting for the results from the grid...");
	        	while (!taskCallback.isDone()){
	            	taskCallback.wait();
	            }
	        }
	        logger.info("Received results for all tasks");
			mandelbrotSet = taskCallback.getMandelbrotSet();
			gridProxy.closeSession();
			return mandelbrotSet;
		}
		catch(Exception ex){ex.printStackTrace();}
		return null;
	}
    
    public void refresh(){
    	this.getContentPane().removeAll();
    	this.getContentPane().add(new javax.swing.JLabel(new javax.swing.ImageIcon(image)));
    	addMouseListener(this);
    	this.setSize(image.getWidth(), image.getHeight());
    	this.setVisible(true);
    }
    
    // We receive a matrix of integers showing the iterations that the mandelbrot equation
    // took to converge on each pixel. Note mandelbrotSet[x][y], so each row in the matrix
    // is a column on the image.
    public void updateImage(Integer[][] mandelbrotSet){
    	if (mandelbrotSet.length == image.getWidth()){
        	for(int x = 0; x < mandelbrotSet[x].length - 1; x++){ // Not sure why I'm blowing the array size without the -1
        		if (mandelbrotSet[x].length == image.getHeight()){
            		for(int y = 0; y < mandelbrotSet[y].length - 1; y++){ // Idem
            			image.setRGB(x, y, iterationsToRGB(mandelbrotSet[x][y],iterations));
            		}
        		} else {
        			// Raise exception because at least this column is too short
        		}
        	}
    	} else {
    		// Raise exception because we didn't get enough columns
    	}
    }
    
    public void updateColumn(Integer x, Integer[] column){
		if (column.length == image.getHeight()){
    		for(int y = 0; y < column.length - 1; y++){ // Idem
    			image.setRGB(x, y, iterationsToRGB(column[y],iterations));
    		}
		} else {
			// Raise exception because at least this column is too short
		}
    }
	
    // Translate a pixel clicked on the window to the point in the mathematical realm it shows
	public static Double[] fromPixelToMath(Double[] mathCenter, Double zoom, Integer[] size, Integer[] pixelPosition){
		// TODO Ensure size, mathCenter and pixelPosition are 2-arrays
		
		// Size in the mathematical realm of a pixel from the display
		Double[] pixelSize = new Double[2];
		pixelSize[0] = 2/(zoom*size[0]);
		pixelSize[1] = 2/(zoom*size[1]);
		
		// Top and left margins of the display in the mathematical realm
		Double[] margins = new Double[2];
		margins[0] = mathCenter[0] - (size[0]/2)*pixelSize[0];
		margins[1] = mathCenter[1] + (size[1]/2)*pixelSize[1];
    	
		// Position on the mathematical realm of the coordinates given
		Double[] mathPosition = new Double[2];
		mathPosition[0] = margins[0] + pixelPosition[0]*pixelSize[0];
		mathPosition[1] = margins[1] - pixelPosition[1]*pixelSize[1]; // In a AWT image the y axis is inverted
		
		return mathPosition;
	}
	
	// We just need two arrays with the math equivalences to pixels
	public static Double[][] fromPixelToMathSet(Double[] mathCenter, Double zoom, Integer[] size){
		Double[][] coordinateSet = new Double[2][Math.max(size[0],size[1])];
		Integer[] pixelPosition = new Integer[2];
		Double[] coordinate;
		for (int x = 0; x < size[1]; x++){
			pixelPosition[0] = x;
			for (int y = 0; y < size[1]; y++){
				pixelPosition[1] = y;
				coordinate = fromPixelToMath(mathCenter, zoom, size, pixelPosition);
				coordinateSet[0][x] = coordinate[0]; // Not needed more than once per x, but added for clarity.
				coordinateSet[1][y] = coordinate[1];
			}
		}
		return coordinateSet;
	}
	
    public static int iterationsToRGB(Integer iterations, Integer maxIterations){
    	if (iterations.equals(maxIterations)) return 0; // Ensure the points in the set are black
    	
    	int red = 256*256;
    	int green = 256;
    	int blue = 1;
    	
    	int start = 192*red + 192*green + 64*blue;
    	int end = 192*red + 192*green + 256*blue;
    	
    	double iterationDelta = maxIterations / (end - start);
    	
    	return (int) Math.round(start + iterations * iterationDelta);
    }
    
	/***********************************************************************
	 *                             ENTRY METHOD                            *
	 ***********************************************************************/
    
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		MandelbrotDisplay d = new MandelbrotDisplay();
		
		String[] argv = { "--centerX", "-0.5", "--centerY", "0.0", "--zoom", "1.0", "--sizeX", "700", "--sizeY", "700", "--iterations", "300"};
		JSAP jsap = new JSAP();
		
        FlaggedOption centerXOpt = new FlaggedOption("centerX")
        .setStringParser(JSAP.DOUBLE_PARSER)
        .setDefault("0.0") 
        .setRequired(true) 
        .setShortFlag('x') 
        .setLongFlag("centerX");
        jsap.registerParameter(centerXOpt);

        FlaggedOption centerYOpt = new FlaggedOption("centerY")
        .setStringParser(JSAP.DOUBLE_PARSER)
        .setDefault("0.0") 
        .setRequired(true) 
        .setShortFlag('y') 
        .setLongFlag("centerY");
        jsap.registerParameter(centerYOpt);
        
        FlaggedOption zoomOpt = new FlaggedOption("zoom")
        .setStringParser(JSAP.DOUBLE_PARSER)
        .setDefault("1.0") 
        .setRequired(true) 
        .setShortFlag('z') 
        .setLongFlag("zoom");
        jsap.registerParameter(zoomOpt);
        
        FlaggedOption sizeXOpt = new FlaggedOption("sizeX")
        .setStringParser(JSAP.INTEGER_PARSER)
        .setDefault("500") 
        .setRequired(true) 
        .setShortFlag('X') 
        .setLongFlag("sizeX");
        jsap.registerParameter(sizeXOpt);

        FlaggedOption sizeYOpt = new FlaggedOption("sizeY")
        .setStringParser(JSAP.INTEGER_PARSER)
        .setDefault("500") 
        .setRequired(true) 
        .setShortFlag('Y') 
        .setLongFlag("sizeY");
        jsap.registerParameter(sizeYOpt);
        
        FlaggedOption iterationsOpt = new FlaggedOption("iterations")
        .setStringParser(JSAP.INTEGER_PARSER)
        .setDefault("100") 
        .setRequired(true) 
        .setShortFlag('i') 
        .setLongFlag("iterations");
        jsap.registerParameter(iterationsOpt);
        
        // If no output option is set then main will recognize the default and display the image in a JFrame instead
        FlaggedOption outputOpt = new FlaggedOption("output")
        .setStringParser(JSAP.STRING_PARSER)
        .setDefault("display")
        .setRequired(false)
        .setShortFlag('o')
        .setLongFlag("output");
        jsap.registerParameter(outputOpt);
        
        // Parse the command line options
        JSAPResult config = jsap.parse(argv);
        
        // I cannot parse arrays, so I use separate options for x and y
        Double[] center = new Double[2];
		center[0] = config.getDouble("centerX");
		center[1] = config.getDouble("centerY");
		Double zoom = config.getDouble("zoom");
		Integer[] size = new Integer[2];
		size[0] = config.getInt("sizeX");
		size[1] = config.getInt("sizeY");
		Integer iterations = config.getInt("iterations");
		
		// Start working!		
		d.setImageParameters(center, zoom, size, iterations);
		
		if(config.getString("output").equals("display")){
			d.initDisplay();
		}else{
			d.initImage();
		}
		
		d.initGridProxy();
		
		d.updateImage(d.calculateMandelbrot());
		
		if(config.getString("output").equals("display")){
			d.refresh();
		}else{
			File outputfile = new File(config.getString("output") + d.gridProxy.getLastSessionId() + ".png");
			ImageIO.write(d.image, "png", outputfile);
			d.gridProxy.closeConnection();
		}
	}
}
