![Banner](docs/_images/Banner.png)
# Oreon Engine (Java - OpenGL/Vulkan)
Welcome to the Cross-Platform Java 3D Engine.
As OpenGL and Vulkan Binding for Java the [LWJGL 3](https://www.lwjgl.org/) API is used.  
Both OpenGL and the next generation graphics API Vulkan is supported!

<img src="docs/_images/gl_vs_vk.png" width="1000px">

## Build Manual
* Oreon Engine project uses [lombok](https://projectlombok.org/).
  Please download the latest [release](https://projectlombok.org/download) of lombok and run the .jar to install lombok plugin into Eclipse.

* Getting started guide for Eclipse available [here](http://oreonengine.github.io/oreon-engine/_navigation/Getting_Started.html).

## Example Open World Ocean OpenGL Demo
* [examples-opengl](https://github.com/oreonengine/oreon-engine/tree/master/oreonengine/examples-opengl)
  [GLOreonWorlds](https://github.com/oreonengine/oreon-engine/blob/master/oreonengine/examples-opengl/src/main/java/org/oreon/examples/gl/oreonworlds/GLOreonworlds.java)

<img src="docs/_images/thumbnail4.png" width="500px">

## Example Vulkan Ocean Demo
* [examples-vulkan](https://github.com/oreonengine/oreon-engine/tree/master/oreonengine/examples-vulkan)
  [VkOrenworlds](https://github.com/oreonengine/oreon-engine/blob/master/oreonengine/examples-vulkan/src/main/java/org/oreon/examples/vk/oreonworlds/VkOreonworlds.java)

## Camera Control
* Move: W, A, S, D
* Rotate: Hold the middle mouse button while moving the mouse
* Accelerate Movespeed: Scroll mouse

## Features
### Deferred rendering pipeline with up to 8x MSAA and FXAA
### LOD Quadtree 
#### Terrain
<img src="docs/_images/Terrain.png" width="400px" align="left">
<img src="docs/_images/Terrain2.png" width="400px">

#### WIP Planet
<img src="docs/_images/WIP_Planet.png" width="500px">

### FFT Water
<img src="docs/_images/Water.png" width="500px">

### Skydome/Atmosphere and Dynamic Sunlight
<img src="docs/_images/sun.png" width="500px">

### Shadow Mapping
#### Parallel Split Shadow Mapping + Variance Shadows
<img src="docs/_images/Shadow_Mapping.png" width="500px">

### Tessellation with Normal-/Displacement-Mapping
<img src="docs/_images/Normalmapping.png" width="500px">

### Post-Processing Effects
#### Motion Blur, Depth of Field Blur, Bloom
<img src="docs/_images/Blur.png" width="500px">

#### Light Scattering, Lens Flare
<img src="docs/_images/LightScattering_LensFlare.png" width="400px" align="left">
<img src="docs/_images/LightScattering_LensFlare2.png" width="400px">

#### SSAO
<img src="docs/_images/ssao.png" width="800px" align="left">  

### GUI
## Credits
* [Nvidia Corporation](https://developer.nvidia.com/)
* [World Creator](https://www.world-creator.com/)