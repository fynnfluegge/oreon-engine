# Build Manual

## eclipse IDE
* add lib/lwjgwl.jar to Java Build Path
	* set the **lib** folder as the native library location of lwjgl.jar
* add lib/slick-util.jar to Java Build Path
* run simulations/TerrainEditor/Start.java to start Terrain Editor Engine

* executable TerrainEditor.jar and FFTOceanMap.jar prepared

## creating executable jar-file
* add the executable main-class to manifest.txt
* set the name of the application in build.xml
	* line 2, value
* run the build.xml with Apache Ant Build Tool

### Control Manual
* w,a,s,d or arrow keys to move camera
* hold mouse middle button while rotate camera by moving the mouse
* GUI grid button to render 3D-meshes

#### Notes
* since there is a bug with refreshing framebuffer texture, Motion Blur is disabled
	* set engine.specialEffects.MotionBlur enabled = true to enable Motion Blur

