# Oreon Engine (Java - OpenGL/Vulkan)

![Banner](docs/_images/Banner.png)

Welcome to the Cross-Platform Java 3D Engine.
OpenGL and Vulkan Binding for Java is done  [LWJGL 3](https://www.lwjgl.org/).

<img src="docs/_images/gl_vs_vk.png" width="1000px">

## Build Manual
* Oreon Engine project uses [lombok](https://projectlombok.org/).
  Please download the latest [release](https://projectlombok.org/download) of lombok and run the .jar to install lombok plugin into Eclipse.

* Getting started guide for Eclipse available [here](http://fynnfluegge.github.io/oreon-engine/_navigation/Getting_Started.html).

## Example OpenGL Demo (terrain, water, amtosphere, several postprocessing effects)
* [examples-opengl](https://github.com/fynnfluegge/oreon-engine/tree/master/oreonengine/examples-opengl)
  [GLOreonWorlds](https://github.com/fynnfluegge/oreon-engine/blob/master/oreonengine/examples-opengl/src/main/java/org/oreon/examples/gl/oreonworlds/GLOreonworlds.java)

## Example Vulkan Demo (water, amtosphere, post processing effects)
* [examples-vulkan](https://github.com/fynnfluegge/oreon-engine/tree/master/oreonengine/examples-vulkan)
  [VkOrenworlds](https://github.com/fynnfluegge/oreon-engine/blob/master/oreonengine/examples-vulkan/src/main/java/org/oreon/examples/vk/oreonworlds/VkOreonworlds.java)

## User Input Manual
* Move: W, A, S, D
* Rotate: Hold the middle mouse button while moving the mouse; Alternatively up, down, left, right keys
* Accelerate Movespeed: Scroll mouse
* Move sun: I, J, K, L
* Enable/Disable Wireframe: G

## Gallery of Rendered Images
* TODO

## Features
### Deferred rendering pipeline with 2x to 8x MSAA and FXAA
* hier neue bilder

### LOD Quadtree 
#### Terrain
<img src="docs/_images/Terrain.png" width="400px" align="left">
<img src="docs/_images/Terrain2.png" width="400px">

#### Planet (Work In Progress)
<img src="docs/_images/WIP_Planet.png" width="500px">

### FFT Water
<img src="docs/_images/Water.png" width="500px">

### Atmosphere Scattering
* hier neue bilder

### Dynamic Sun
* hier neue bilder

### Parallel Split Shadow Mapping
#### Parallel Split Shadow Mapping + Variance Shadows
<img src="docs/_images/Shadow_Mapping.png" width="500px">

### Tessellation & Normal-/Displacement-Mapping
<img src="docs/_images/Normalmapping.png" width="500px">

### Post-Processing Effects
#### Motion Blur, Depth of Field Blur, Bloom
<img src="docs/_images/Blur.png" width="500px">

#### Light Scattering, Lens Flare
<img src="docs/_images/LightScattering_LensFlare.png" width="400px" align="left">
<img src="docs/_images/LightScattering_LensFlare2.png" width="400px">

#### SSAO
<img src="docs/_images/ssao.png" width="820px">

### GUI
## Credits
* [Nvidia Corporation](https://developer.nvidia.com/)
* [World Creator](https://www.world-creator.com/)