///////////////////////////////////////////////////////////////////////////////
// $Id: SyncClient.java,v 1.7 2008/06/17 03:57:26 yhzhan Exp $
//
// This file is a part of the "SampleApp" project which implements all the
// code to demonstate the use of the Symphony API within in a client
// application in a synchronous manner.
//
//
// Copyright (c) 2001-2011 Platform Computing Corporation. 
// Accelerating Intelligence(TM). All rights reserved. 
//
// This exposed source code is the confidential and proprietary property of
// Platform Computing Corporation. Your right to use is strictly limited by
// the terms of the license agreement entered into with Platform Computing
// Corporation. 
//
///////////////////////////////////////////////////////////////////////////////

package es.albertocuesta.SymMandelbrot.client;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.platform.symphony.soam.Connection;
import com.platform.symphony.soam.DefaultSecurityCallback;
import com.platform.symphony.soam.Session;
import com.platform.symphony.soam.SessionCreationAttributes;
import com.platform.symphony.soam.SoamException;
import com.platform.symphony.soam.SoamFactory;
import com.platform.symphony.soam.TaskSubmissionAttributes;

import es.albertocuesta.SymMandelbrot.common.TaskInput;
//import es.albertocuesta.SymMandelbrot.common.TaskOutput;

/**
 * The GridProxy manages the communication between the display and the grid. From a few parameters describing
 * the mandelbrot set to calculate, it will create a suitable number of tasks and send them to the grid. If in
 * synchronous mode it will wait for the results and collate them for the display. If in asynchronous mode it 
 * will return a TaskCallback object to the caller so it can retrieve the results itself.
 * 
 * @author Alberto Cuesta-Canada
 *
 */
public class GridProxy {
	
	private Logger logger;
	
	private Session session;
	private Connection connection;
	private TaskCallback taskCallback;
	private String lastSessionId;
	
	public TaskCallback getCallback(){
		return taskCallback;
	}
	
	public String getLastSessionId() {
		return lastSessionId;
	}
	
    public GridProxy(Integer[] size) {
		logger = Logger.getLogger("MandelbrotClient");
		
        try {
            // Initialize the API
            logger.debug("Initializing SOAM API");
        	SoamFactory.initialize();

            // Set up application specific information to be supplied to Symphony
            String appName = "SymMandelbrotCommon";

            // Set up application authentication information using the default security provider
            DefaultSecurityCallback securityCB = new DefaultSecurityCallback("Guest", "Guest");

            connection = null;
            
            lastSessionId = "None";
            try {
                // Connect to the specified application
            	logger.debug("Connecting to SOAM");
            	connection = SoamFactory.connect(appName, securityCB);
            	logger.debug("connection ID=" + connection.getId());
            }
            catch (Exception ex) {
                // Report exception
                System.out.println("Exception caught:");
                System.out.println(ex.toString());
            }
        }
        catch (Exception ex) {
            // Report exception
            System.out.println("Exception caught:");
            System.out.println(ex.toString());
        }
    }
    
    public void openSession(Integer[] size, Double[][] coordinateSet){
    	try {
	    	// Set up session attributes
	        SessionCreationAttributes attributes = new SessionCreationAttributes();
	        attributes.setSessionName("mySession");
	        attributes.setSessionType("ShortRunningTasks");
	        
	        taskCallback = new TaskCallback(size);
	        attributes.setSessionCallback(taskCallback);
	    	attributes.setSessionFlags(Session.PARTIAL_ASYNC);
	    	attributes.setCommonData(coordinateSet);
	
	    	logger.debug("Creating SOAM Session");
            session = connection.createSession(attributes);
            logger.debug("Session ID:" + session.getId());
            lastSessionId = session.getId();
        }
        catch (Exception ex) {
            // Report exception
            System.out.println("Exception caught:");
            System.out.println(ex.toString());
        }
    	
    }
    
    public void calculateMandelbrot(Double[] mathCenter, Double zoom, Integer[] size, Integer iterations){
    	logger.debug("Sending " + size[0] + " tasks");
    	for (int xPixel = 0; xPixel < size[0]; xPixel++){
    		
			TaskInput taskInput = new TaskInput(xPixel,iterations);

            // Set task submission attributes
            TaskSubmissionAttributes taskAttr = new TaskSubmissionAttributes();
            try{
            	taskAttr.setTaskInput(taskInput);
	            session.sendTaskInput(taskAttr); 
            }
            catch (SoamException ex) {
                // Report exception
                System.out.println("Exception caught:");
                System.out.println(ex.toString());
            }
    	}
    	logger.debug("Submitted " + size[0] + " tasks.");
    }
    
    public void closeSession(){
        try{
        	if (session != null) {
        		session.close();
        		logger.debug("Session closed");
        	}
        }
        catch (SoamException ex) {
            // Report exception
            System.out.println("Exception caught:");
            System.out.println(ex.toString());
        }
    }

    public void closeConnection(){
        try{
            if (connection != null) {
                connection.close();
                logger.debug("Connection closed");
            }
        }
        catch (SoamException ex) {
            // Report exception
            System.out.println("Exception caught:");
            System.out.println(ex.toString());
        }
    }
    
    public void close(){
    	closeSession();
    	closeConnection();
    	
        // Uninitialize the API
        // This is the only means to ensure proper shutdown
        // of the interaction between the client and the system.
        SoamFactory.uninitialize();
        logger.info("All Done !!");
    }
}
