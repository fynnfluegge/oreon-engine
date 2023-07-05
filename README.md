![Banner](docs/_images/welcome_image.png)

<h1 align="center">Oreon Engine (Java - OpenGL/Vulkan)</h1>

**UPDATE: Checkout Kotlin implementation of Oreon Engine [oreon-engine-kotlin](https://github.com/fynnfluegge/oreon-engine-vk-kt)**

## Build Manual
* Install the official Vulkan SDK from [LunarG](https://vulkan.lunarg.com/sdk/home).
* This project uses [lombok](https://projectlombok.org/).
  Please use this [guide](https://projectlombok.org/setup/intellij) for adding lombok support to IntelliJ Idea
* Visit the [wiki](https://github.com/fynnfluegge/oreon-engine/wiki) for further information.

## Keymappings
* Move: W, A, S, D
* Rotate: Hold the middle mouse button while moving the mouse; Alternatively up, down, left, right keys
* Accelerate Movespeed: Scroll mouse
* Move sun: I, J, K, L
* Enable/Disable Wireframe: G

## Demo
https://github.com/fynnfluegge/oreon-engine-creative/assets/16321871/bf06e085-fb40-4d3f-ac14-9eb8c1fd196c

## Features
### Deferred rendering pipeline with 2x to 8x MSAA and FXAA
<p float="left">
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0010.png" width="270" />
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0011.png" width="270" /> 
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0012.png" width="270" />
</p>
<p float="left">
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0013.png" width="270" />
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0014.png" width="270" /> 
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0015.png" width="270" />
</p>

### Diamond Square Terrain Quadtree 
<img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/1005.png" width="500px">

### FFT Water
<p float="left">
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0060.png" width="270" />
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0061.png" width="270" /> 
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0062.png" width="270" />
</p>

### Atmospheric Scattering
<p float="left">
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0005.png" width="270" />
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0002.png" width="270" /> 
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0003.png" width="270" />
</p>

### Dynamic Sun
<p float="left">
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0018.png" width="270" />
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0019.png" width="270" /> 
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0020.png" width="270" />
</p>

### Parallel Split Shadow Mapping & Variance Shadows
<p float="left">
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0001.png" width="270" />
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0030.png" width="270" /> 
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0031.png" width="270" />
</p>

### Tessellation & Normal-/Displacement-Mapping
<img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/1003.png" width="500px">

### Post-Processing Effects
#### Motion Blur, Depth of Field Blur, Bloom
<img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/1001.png" width="500px">

#### Light Scattering, Lens Flare
<p float="left">
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/0070.png" width="270" />
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/1002.png" width="270" />
  <img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/1008.png" width="270" />
</p>

#### SSAO
<img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/9001_ssao.png" width="820px">

#### Planet (Work In Progress)
<img src="https://github.com/fynnfluegge/oreon-engine-creative/blob/master/images/9001_planet.png" width="500px">

## Credits
* [Nvidia Corporation](https://developer.nvidia.com/)
* [World Creator](https://www.world-creator.com/)
