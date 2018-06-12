///////////////////////////////////////////////////////////////////////////////
// $Id: MyOutput.java,v 1.7 2008/02/02 03:47:06 yuzhang Exp $
//
// This file is a part of the "SampleApp" project which implements all the
// common classes shared by the client and service samples.
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

package es.albertocuesta.SymMandelbrot.common;
import java.io.Serializable;


/////////////////////////////////
// Output Data Object
/////////////////////////////////
public class TaskOutput implements Serializable
{
    //=========================================================================
    //  Private Member Variables
    //=========================================================================

    public int xPixel;
    public int[] iterations;
    private static final long serialVersionUID = 2L;

    //=========================================================================
    //  Constructor
    //=========================================================================

    public TaskOutput(int xPixel, int[] iterations)
    {
        super();
        this.xPixel = xPixel;
        this.iterations = iterations;
    }



}
