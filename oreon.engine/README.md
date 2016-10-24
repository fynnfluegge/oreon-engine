# Build Manual

## eclipse IDE
* add lib/lwjgwl.jar to Java Build Path
	* set the **lib** folder as the native library location of lwjgl.jar
* add lib/slick-util.jar to Java Build Path

## creating executable jar-file
* add the executable main-class to manifest.txt
* set the name of the application in build.xml
	* line 2, value
* run the build.xml with Apache Ant Build Tool

### Control Manual
* w,a,s,d or arrow keys to move camera
* hold mouse middle button while rotate camera by moving the mouse
* mouse wheel to accelerate move speed
* GUI grid button to switch to 3D-mesh rendering

#### Notes
* set modules.vfx.MotionBlur enabled = true to enable Motion Blur Effect

