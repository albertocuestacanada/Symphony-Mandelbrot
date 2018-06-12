package es.albertocuesta.SymMandelbrot.test.client;

import static org.junit.Assert.*;

import java.awt.event.MouseEvent;

import org.junit.Test;

import es.albertocuesta.SymMandelbrot.client.MandelbrotDisplay;

public class MandelbrotDisplayTest {

	@Test
	public void testFromPixelToRGB() {
		Double[] mathCenter = new Double[2];
		mathCenter[0] = 0.0;
		mathCenter[1] = 0.0;
		
		Double zoom = 1.0;
		
		Integer[] size = new Integer[2];
		size[0] = 10;
		size[1] = 10;
		
		Integer[] pixelPosition = new Integer[2];
		pixelPosition[0] = 0;
		pixelPosition[1] = 0;
		
		Double[] result = MandelbrotDisplay.fromPixelToMath(mathCenter, zoom, size, pixelPosition);
		assertEquals("X position test 1", -1.0, (double)result[0], 0.0);
		assertEquals("Y position test 1", 1.0, (double)result[1], 0.0);
		
		pixelPosition[0] = 1;
		pixelPosition[1] = 1;
		result = MandelbrotDisplay.fromPixelToMath(mathCenter, zoom, size, pixelPosition);
		assertEquals("X position test 2", -0.8, (double)result[0], 0.0);
		assertEquals("Y position test 2", 0.8, (double)result[1], 0.0);
		
		pixelPosition[0] = 0;
		pixelPosition[1] = 0;
		zoom = 2.0;
		result = MandelbrotDisplay.fromPixelToMath(mathCenter, zoom, size, pixelPosition);
		assertEquals("X position test 3", -0.5, (double)result[0], 0.0);
		assertEquals("Y position test 3", 0.5, (double)result[1], 0.0);
		
		zoom = 1.0;
		mathCenter[1] = -0.5;
		result = MandelbrotDisplay.fromPixelToMath(mathCenter, zoom, size, pixelPosition);
		assertEquals("X position test 4", -1.0, (double)result[0], 0.0);
		assertEquals("Y position test 4", 0.5, (double)result[1], 0.0);
	}
	
	@Test
	public void testIterationsToRGB(){
		int maxIterations = 10000;
		int colour;
		for (int iterations = 0; iterations <= maxIterations; iterations++){
			colour = MandelbrotDisplay.iterationsToRGB(iterations, maxIterations);
			System.out.println(maxIterations + " " + iterations + " " + Integer.toHexString(colour));
		}
	}
	
	@Test
	public void testUpdate(){
		fail("Not yet implemented");
	}
	
	@Test
	public void mouseClickedTest(){
		fail("Not yet fully implemented");
		
		MandelbrotDisplay display = new MandelbrotDisplay(); // This should be refactored so it doesn't open a session to the grid
		// Click with button 1 in the 10,10 pixel
		MouseEvent event = new MouseEvent(null,0,0L,MouseEvent.BUTTON1,10,10,1,false);
		
		display.mouseClicked(event);
	}
}
