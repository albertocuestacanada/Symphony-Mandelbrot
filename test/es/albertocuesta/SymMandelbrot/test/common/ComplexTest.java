package es.albertocuesta.SymMandelbrot.test.common;

import static org.junit.Assert.*;

import org.junit.Test;
import es.albertocuesta.SymMandelbrot.common.Complex;

public class ComplexTest {

	@Test
	public void testGetReal() {
		Complex c = new Complex(0.0,1.0);
		assertEquals("Real part tested", 0.0, c.getReal(), 0.0);
	}

	@Test
	public void testGetImaginary() {
		Complex c = new Complex(0.0,1.0);
		assertEquals("Imaginary part tested", 1.0, c.getImaginary(), 0.0);
	}

	@Test
	public void testAdd() {
		Complex c1 = new Complex(1.0,1.5);
		Complex c2 = new Complex(1.0,1.5);
		c1.add(c2);
		assertEquals("Adding real parts tested", 2.0, c1.getReal(), 0.0);
		assertEquals("Adding imaginary parts tested", 3.0, c1.getImaginary(), 0.0);
	}

	@Test
	public void testMultiply() {
		Complex c1 = new Complex(1.0,1.5);
		Complex c2 = new Complex(1.0,1.5);
		c1.multiply(c2);
		assertEquals("Adding real parts tested", -1.25, c1.getReal(), 0.0);
		assertEquals("Adding imaginary parts tested", 3.0, c1.getImaginary(), 0.0);
	}

	@Test
	public void testIsInfinite() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsNaN() {
		fail("Not yet implemented");
	}

}
