package es.albertocuesta.SymMandelbrot.common;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Utils {
	public static int getSerializableSize(Serializable ser){
    	try{
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	ObjectOutputStream oos = new ObjectOutputStream(baos);
	    	oos.writeObject(ser);
	    	oos.close();
	    	return baos.size();
    	}catch (Exception e){
    		e.printStackTrace();
    		return -1;
    	}
    }
}
