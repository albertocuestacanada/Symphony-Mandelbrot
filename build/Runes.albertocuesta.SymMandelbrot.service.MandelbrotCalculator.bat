@echo off
rem Runes.albertocuesta.SymMandelbrot.service.MandelbrotCalculator.bat
rem ###############################################################################
rem #
rem # This file contains a template that can be used to execute the 
rem # "com.platform.symphony.samples.SampleApp.service.MyService" class.
rem #
rem # This file was generated by the Symphony Code Generation Tool 
rem # version 5.1 at Wed Sep 19 16:16:04 2007
rem #
rem # Copyright (c) 2001-2011 Platform Computing Corporation.
rem # Accelerating Intelligence(TM). All rights reserved.
rem #
rem # This file will be re-generated every time the packaging wizard is run from
rem # within the Symphony Eclipse Pluggin.
rem #
rem # NOTE: You should read the additional comments embedded in this file to 
rem # get further details on the variables used in this script. This file
rem # can be used to execute your class on Windows hosts.
rem #
rem ###############################################################################

rem ###################
rem # 1. You do not need to modify JAVA_SDK_JARFILE.
rem # It has already been set by the Symphony Eclipse IDE pluggin. 
rem ###################
set SOAM_SDK_JARFILE=%SOAM_HOME%\5.1\%EGO_MACHINE_TYPE%\lib\JavaSoamApi.jar

rem ###################
rem # 2. This is where we put your dependent JARs containing all the implementation 
rem # required for your Application to function.
rem ###################
set SOAM_SERVICE_JARFILES=%SOAM_DEPLOY_DIR%\MandelbrotCalculatorCommon.jar
set SOAM_SERVICE_JARDEPS=%SOAM_DEPLOY_DIR%\lib\log4j-1.2.16.jar
set SOAM_SERVICE_CONFDEPS=%SOAM_DEPLOY_DIR%\conf

rem ###################
rem # 3. This is the main class used to start the Application code.
rem ###################
set SOAM_SERVICE_MAINCLASS=es.albertocuesta.SymMandelbrot.service.MandelbrotCalculator

set JAVA_HOME="C:\Program Files\Java\jdk1.6.0_23"

set > C:\EGO\soam\work\SMCVariables.log

rem ###################
rem # Attention
rem # You do not need to modify the java start command.
rem # You can use the appropriate variable instead.
rem ###################
%JAVA_HOME%\bin\java -cp %SOAM_SDK_JARFILE%;%SOAM_SERVICE_CONFDEPS%;%SOAM_SERVICE_JARDEPS%;%SOAM_SERVICE_JARFILES% %SOAM_SERVICE_MAINCLASS% 1> C:\EGO\soam\work\SMCJava.log 2>&1