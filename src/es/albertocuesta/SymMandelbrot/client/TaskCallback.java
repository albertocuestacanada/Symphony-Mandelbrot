///////////////////////////////////////////////////////////////////////////////
// $Id: MySessionCallback.java,v 1.8 2008/06/17 03:57:26 yhzhan Exp $
//
// This file is a part of the "SampleApp" project which implements
// all the code to demonstrate the use of the Symphony API within a client
// application in an asynchronous manner.
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

import com.platform.symphony.soam.*;

import es.albertocuesta.SymMandelbrot.common.TaskOutput;

/**
 * This class can be used for asynchronous interaction with the grid. It provides a method
 * to collate the results from the grid in a single matrix of values and is able to tell
 * when has it received all the data from the grid.
 * 
 * @author Alberto Cuesta-Canada
 *
 */
public class TaskCallback extends SessionCallback
{
    //=========================================================================
    //  Constructor
    //=========================================================================

    public TaskCallback(Integer[] size) 
    {
        this.tasksToReceive = size[0];

        mandelbrotSet = new Integer[size[0]][size[1]];
    	tasksReceived = 0;
        exception = false;
        
    }

    //=========================================================================
    //  SessionCallback Interface Methods
    //=========================================================================

    /**
     * Invoked when an exception occurs within the scope of the given session.
     * Must be implemented by the application developer. 
     */
    public void onException(SoamException exception) throws SoamException
    {
        /********************************************************************
         * Although the Symphony API currently invokes this method with a
         * single callback thread, the number of threads used by the API to
         * invoke this method may increase in future revisions of the API.
         * Therefore, the developer should never assume that the invocation
         * of this method will be done serially and should always implement
         * this method in a thread-safe manner. 
         ********************************************************************/
        
        System.out.println("An exception occured on the callback :");
        System.out.println(exception.getMessage());
        setException(true);
    }

    /**
     * Invoked when a task response is ready.
     * Must be implemented by the application developer. 
     */
    public void onResponse(TaskOutputHandle output) throws SoamException
    {
        /********************************************************************
         * Although the Symphony API currently invokes this method with a
         * single callback thread, the number of threads used by the API to
         * invoke this method may increase in future revisions of the API.
         * Therefore, the developer should never assume that the invocation
         * of this method will be done serially and should always implement
         * this method in a thread-safe manner. 
         ********************************************************************/
        
        try
        {
            // check for success of task
            if (output.isSuccessful())
            {
                TaskOutput taskOutput;
                int xPixel;
                
                // Get the message returned from the service
                taskOutput = (TaskOutput)output.getTaskOutput();
                xPixel = taskOutput.xPixel;
                for (int yPixel = 0; yPixel < taskOutput.iterations.length; yPixel++){
                	mandelbrotSet[xPixel][yPixel] = taskOutput.iterations[yPixel]; // I'll need to add this to a synchronized block to make it thread safe, if I can also copy the array atomically so much better.
                }
            }
            else
            {
                // get the exception associated with this task
                SoamException ex = output.getException();
                System.out.println("Task Not Successful :");
                System.out.println(ex.getMessage());
            }
        }
        catch (Exception exception)
        {
            System.out.println("Exception occured in onResponse() : ");
            System.out.println(exception.getMessage());
        }

        // Update counter used to synchronize the controlling thread 
        // with this callback object
        incrementTaskCount();
    }
    

    //=========================================================================
    //  Additional Public Methods
    //=========================================================================

    public boolean isDone()
    {
        if (exception)
        {
            return true;
        }
        return tasksReceived == tasksToReceive;
    }

    public Integer[][] getMandelbrotSet(){
    	return mandelbrotSet;
    }
    
    // This needs to be called if the same callback object is used for several calculations
    public void reset(){
    	tasksReceived = 0;
        exception = false;
    }
    
    //=========================================================================
    //  Private Synchronized Helper Methods
    //=========================================================================

    private synchronized void incrementTaskCount()
    {
        tasksReceived++;
        if (tasksReceived == tasksToReceive)
        {
            this.notifyAll();
        }
    }

    private synchronized void setException(boolean exception)
    {
        this.exception = exception;
        this.notifyAll();
    }

    //=========================================================================
    //  Private Member Variables
    //=========================================================================

    private int tasksReceived;
    private boolean exception;
    private int tasksToReceive;
    private Integer[][] mandelbrotSet;
}
