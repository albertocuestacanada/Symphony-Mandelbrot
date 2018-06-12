///////////////////////////////////////////////////////////////////////////////
// $Id: MyService.java,v 1.3 2007/10/17 17:04:04 rclayton Exp $
//
// This file contains code which demonstrates the use of the Symphony API
// within a service. 
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

package es.albertocuesta.SymMandelbrot.service;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.platform.symphony.soam.ServiceContainer;
import com.platform.symphony.soam.ServiceContext;
import com.platform.symphony.soam.SessionContext;
import com.platform.symphony.soam.SoamException;
import com.platform.symphony.soam.TaskContext;

import es.albertocuesta.SymMandelbrot.common.Complex;
import es.albertocuesta.SymMandelbrot.common.TaskInput;
import es.albertocuesta.SymMandelbrot.common.TaskOutput;
import es.albertocuesta.SymMandelbrot.common.Utils;

/**
 * This class runs on Platform Symphony engines to calculate Mandelbrot sets. With the current implementation
 * each task or invocation calculates one column of points of such a set.
 * 
 * @author Alberto Cuesta-Canada
 *
 */

public class MandelbrotCalculator extends ServiceContainer
{
    private Double[][] coordinateSet;
	private Logger logger;
	
	MandelbrotCalculator()
    {
        super();
    }


    public void onCreateService(ServiceContext serviceContext) throws SoamException
    {
        /********************************************************************
         * Do your service initialization here. 
         ********************************************************************/
		//BasicConfigurator.configure();
		logger = Logger.getLogger("MandelbrotService");
    	logger.debug("SymMandelbrot service created");
    }

    public void onSessionEnter(SessionContext sessionContext) throws SoamException
    {
        /********************************************************************
         * Do your session-specific initialization here, when common data is
         * provided. 
         ********************************************************************/
    	logger.info("Retrieving common data");
    	coordinateSet = (Double[][])sessionContext.getCommonData();
    	sessionContext.discardCommonData();
   	 	// This might be memory intensive, consider disabling it
    	logger.info("Retrieved " + Utils.getSerializableSize(coordinateSet) + " bytes");
    }

    public void onInvoke (TaskContext taskContext) throws SoamException
    {
        // Get the input that was sent from the client 
        TaskInput taskInput = (TaskInput)taskContext.getTaskInput();
        TaskOutput taskOutput = new TaskOutput(taskInput.xPixel, new int[coordinateSet[1].length]);
        logger.debug("Computing column " + taskInput.xPixel);
        
        for (int yPosition = 0; yPosition < coordinateSet[1].length; yPosition++){
    		Complex c = new Complex(coordinateSet[0][taskInput.xPixel], coordinateSet[1][yPosition]);
    		Complex zn = new Complex(0.0,0.0);
    		Complex zn1 = new Complex(0.0,0.0);
    		Boolean goesToInfinite = false;
    		for(int i = 0; i < taskInput.iterations ; i++){
    			zn1 = zn.multiply(zn);
    			zn1 = zn1.add(c);
    			zn = new Complex(zn1.getReal(),zn1.getImaginary());
    			// If the value goes to infinite the value returned is the number of iterations needed to arrive to that result.
    			if (zn.isInfinite() || zn.isNaN()){
    				goesToInfinite = true;
    				taskOutput.iterations[yPosition] = i;
    				break;
    			}
    		}
    		if (!goesToInfinite) taskOutput.iterations[yPosition] = taskInput.iterations;
    	}

        // Set our output message 
        taskContext.setTaskOutput(taskOutput);
        logger.debug("Column " + taskInput.xPixel + " done");
    }

    public void onSessionLeave() throws SoamException
    {
        /********************************************************************
         * Do your session-specific uninitialization here, when common data
         * is provided. 
         ********************************************************************/
    }

    public void onDestroyService() throws SoamException
    {
        /********************************************************************
         * Do your service uninitialization here. 
         ********************************************************************/
    	logger.debug("Destroying SymMandelbrot service");
    }

    // Entry point to the service 
    public static void main(String args[])
    {
        // Return value of our service program 
        int retVal = 0;
        try
        {
            /****************************************************************
             * Do not implement any service initialization before calling the
             * ServiceContainer.run() method. If any service initialization
             * needs to be done, implement the onCreateService() handler for
             * your service container. 
             ****************************************************************/

            // Create the container and run it 
            MandelbrotCalculator myContainer = new MandelbrotCalculator();
            myContainer.run();

            /****************************************************************
             * Do not implement any service uninitialization after calling
             * the ServiceContainer.run() method. If any service
             * uninitialization needs to be done, implement the
             * onDestroyService() handler for your service container since
             * there is no guarantee that the remaining code in main() will
             * be executed after calling ServiceContainer::run(). Also, in
             * some cases, the remaining code can even cause an orphan
             * service instance if the code cannot be finished. 
             ****************************************************************/
        }
        catch (Exception ex)
        {
            // Report the exception to stdout 
            System.out.println("Exception caught:");
            System.out.println(ex.toString());
            retVal = -1;
        }


        /********************************************************************
         * NOTE: Although our service program will return an overall failure
         * or success code it will always be ignored in the current revision
         * of the middleware. The value being returned here is for
         * consistency. 
         ********************************************************************/
        System.exit(retVal);
    }
}
