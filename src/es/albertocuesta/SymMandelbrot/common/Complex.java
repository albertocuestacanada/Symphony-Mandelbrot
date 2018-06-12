package es.albertocuesta.SymMandelbrot.common;

public class Complex {
	private Double real;
	private Double imaginary;
	
	public Double getReal(){
		return real;
	}
	
	public Double getImaginary(){
		return imaginary;
	}
	
	public Complex(Double real, Double imaginary){
		this.real = new Double(real);
		this.imaginary = new Double(imaginary);
	}
	
	public Complex add(Complex c){
		real += c.real;
		imaginary += c.imaginary;
		return this;
	}
	
	public Complex multiply(Complex c){
		Double newReal;
		newReal = (real*c.real)-(imaginary*c.imaginary);
		imaginary = (real*c.imaginary)+(imaginary*c.real);
		real = newReal;
		return this;
	}
	
	public boolean isInfinite(){
		return (real.isInfinite() || imaginary.isInfinite());
	}
	
	public boolean isNaN(){
		return (real.isNaN() || imaginary.isNaN());
	}
}
