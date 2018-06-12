set JAR_EXE="C:\Program Files\Java\jdk1.6.0_23\bin\jar.exe"
set PACKAGE_NAME=SymMandelbrotCommonPackage.jar
set LIB_DEPS="lib"
set CONF_DEPS="conf"
set GRID_SERVICE=MandelbrotCalculatorCommon.jar
set STARTUP_SCRIPT=Runes.albertocuesta.SymMandelbrot.service.MandelbrotCalculator.bat

%JAR_EXE% cf %PACKAGE_NAME% %LIB_DEPS% %CONF_DEPS% %STARTUP_SCRIPT% %GRID_SERVICE%