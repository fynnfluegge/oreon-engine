package org.oreon.system.gl.desktop;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.picking.TerrainPicking;
import org.oreon.core.gl.scene.FullScreenQuad;
import org.oreon.core.gl.shadow.ParallelSplitShadowMaps;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.instancing.InstancingObjectHandler;
import org.oreon.core.light.LightHandler;
import org.oreon.core.math.Quaternion;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderingEngine;
import org.oreon.core.system.Window;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.gpgpu.ContrastController;
import org.oreon.modules.gl.gui.GUI;
import org.oreon.modules.gl.gui.GUIs.VoidGUI;
import org.oreon.modules.gl.postprocessfilter.bloom.Bloom;
import org.oreon.modules.gl.postprocessfilter.dofblur.DepthOfFieldBlur;
import org.oreon.modules.gl.postprocessfilter.lensflare.LensFlare;
import org.oreon.modules.gl.postprocessfilter.lightscattering.SunLightScattering;
import org.oreon.modules.gl.postprocessfilter.motionblur.MotionBlur;
import org.oreon.modules.gl.terrain.GLTerrain;
import org.oreon.modules.gl.water.UnderWater;

public class GLForwardRenderingEngine implements RenderingEngine{

	private Window window;
	private FullScreenQuad fullScreenQuad;
	private Texture2D postProcessingTexture;

	private GLFramebuffer fbo;
	private GLFramebuffer multisampledFbo;
	private Texture2D sceneTexture;
	private Texture2D lightScatteringTexture;
	private Texture2D sceneDepthmap;
	
	private Quaternion clipplane;
	private boolean grid;
	
	private InstancingObjectHandler instancingObjectHandler;
	private static ParallelSplitShadowMaps shadowMaps;
	private GUI gui;
	
	private MotionBlur motionBlur;
	private DepthOfFieldBlur dofBlur;
	private Bloom bloom;
	private SunLightScattering sunlightScattering;
	private LensFlare lensFlare;
	private UnderWater underWater;
	private ContrastController contrastController;
	
	private static boolean motionBlurEnabled = true;
	private static boolean depthOfFieldBlurEnabled = true;
	private static boolean bloomEnabled = true;
	private static boolean lightScatteringEnabled = true;
	private static boolean waterReflection = false;
	private static boolean waterRefraction = false;
	private static boolean cameraUnderWater = false;
	private static float t_causticsDistortion = 0;
	private static float sightRangeFactor = 2f;
	
	public GLForwardRenderingEngine()
	{
		instancingObjectHandler = InstancingObjectHandler.getInstance();
	}
	
	public void init()
	{
		Default.init();
		window = CoreSystem.getInstance().getWindow();
		
		if (gui != null){
			gui.init();
		}
		else {
			gui = new VoidGUI();
		}
		
		shadowMaps = new ParallelSplitShadowMaps();
		fullScreenQuad = new FullScreenQuad();
		motionBlur = new MotionBlur();
		dofBlur = new DepthOfFieldBlur();
		bloom = new Bloom();
		sunlightScattering = new SunLightScattering();
		lensFlare = new LensFlare();
		contrastController = new ContrastController();
		underWater = UnderWater.getInstance();
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(5);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.put(GL_COLOR_ATTACHMENT3);
		drawBuffers.put(GL_COLOR_ATTACHMENT4);
		drawBuffers.flip();
		
		multisampledFbo = new GLFramebuffer();
		multisampledFbo.bind();
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 0, window.getWidth(), window.getHeight(), GL_RGBA8);
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 4, window.getWidth(), window.getHeight(), GL_RGBA16F);
		multisampledFbo.createDepthBufferMultisampleAttachment(Constants.MULTISAMPLES, window.getWidth(), window.getHeight());
		multisampledFbo.setDrawBuffers(drawBuffers);
		multisampledFbo.checkStatus();
		multisampledFbo.unbind();
		
		sceneTexture = new Texture2D();
		sceneTexture.generate();
		sceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		sceneTexture.bilinearFilter();
		sceneTexture.clampToEdge();
		
		lightScatteringTexture = new Texture2D();
		lightScatteringTexture.generate();
		lightScatteringTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		lightScatteringTexture.bilinearFilter();
		lightScatteringTexture.clampToEdge();
		
		sceneDepthmap = new Texture2D();
		sceneDepthmap.generate();
		sceneDepthmap.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, window.getWidth(), window.getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		sceneDepthmap.bilinearFilter();
		sceneDepthmap.clampToEdge();
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureAttachment(sceneTexture.getId(),0);
		fbo.createColorTextureAttachment(lightScatteringTexture.getId(),1);
		fbo.createDepthTextureAttachment(sceneDepthmap.getId());
		fbo.checkStatus();
		fbo.unbind();
	}
	
	public void render()
	{	
		GLDirectionalLight.getInstance().update();
		if (CoreSystem.getInstance().getScenegraph().getCamera().isCameraMoved()){
			if (CoreSystem.getInstance().getScenegraph().terrainExists()){
				((GLTerrain) CoreSystem.getInstance().getScenegraph().getTerrain()).updateQuadtree();
			}
		}
		
		setClipplane(Constants.PLANE0);
		Default.clearScreen();
		
		//render shadow maps
		shadowMaps.getFBO().bind();
		shadowMaps.getConfig().enable();
		glClear(GL_DEPTH_BUFFER_BIT);
		CoreSystem.getInstance().getScenegraph().renderShadows();
		shadowMaps.getConfig().disable();
		shadowMaps.getFBO().unbind();
		
		// render scene/deferred maps
		multisampledFbo.bind();
		Default.clearScreen();
		CoreSystem.getInstance().getScenegraph().render();
		multisampledFbo.unbind();
		
		fbo.bind();
		Default.clearScreen();
		fbo.unbind();
		
		// blit SceneTexture
		multisampledFbo.blitFrameBuffer(0,0,fbo.getId(), window.getWidth(), window.getHeight());
		
		// blit light Scattering SceneTexture
		multisampledFbo.blitFrameBuffer(4,1,fbo.getId(), window.getWidth(), window.getHeight());
		
		// start Threads to update instancing objects
		instancingObjectHandler.signalAll();
		
		// start Thread to update Terrain Quadtree
		//TODO Context Sharing
		/*if (Camera.getInstance().isCameraMoved()){
			if (scenegraph.terrainExists()){
				((Terrain) scenegraph.getTerrain()).getLock().lock();;
				try{
					((Terrain) scenegraph.getTerrain()).getCondition().signalAll();
				}
				finally{
					((Terrain) scenegraph.getTerrain()).getLock().unlock();
				}
			}
		}*/
		
		// post processing effects
		
		postProcessingTexture = new Texture2D(sceneTexture);
		
		if (isCameraUnderWater()){
			underWater.render(postProcessingTexture, sceneDepthmap);
			postProcessingTexture = underWater.getUnderwaterSceneTexture();
		}
		
		// HDR Bloom
		if (isBloomEnabled()) {
			bloom.render(postProcessingTexture);
			postProcessingTexture = bloom.getBloomBlurSceneTexture();
		}
		
		// Depth of Field Blur
		if (isDepthOfFieldBlurEnabled()){
			
			dofBlur.render(sceneDepthmap, postProcessingTexture,window.getWidth(),window.getHeight());
			postProcessingTexture = dofBlur.getVerticalBlurSceneTexture();
		}
		
		// Motion Blur
		if (isMotionBlurEnabled()){
			if (CoreSystem.getInstance().getScenegraph().getCamera().getPreviousPosition().sub(
							CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() > 0.04f ||
					CoreSystem.getInstance().getScenegraph().getCamera().getForward().sub(
							CoreSystem.getInstance().getScenegraph().getCamera().getPreviousForward()).length() > 0.01f){
				motionBlur.render(sceneDepthmap, postProcessingTexture);
				postProcessingTexture = motionBlur.getMotionBlurSceneTexture();
			}
		}
		
		if (isLightScatteringEnabled() && !isGrid()){
			sunlightScattering.render(postProcessingTexture,lightScatteringTexture);
			postProcessingTexture = sunlightScattering.getSunLightScatteringSceneTexture();
		}
		
//		contrastController.render(postProcessingTexture);

		// final scene texture
		fullScreenQuad.setTexture(postProcessingTexture);	
		
		fullScreenQuad.render();
		
		fbo.bind();
		LightHandler.doOcclusionQueries();
		fbo.unbind();
		
		lensFlare.render();
		
		gui.render();
		
		// draw into OpenGL window
		window.draw();
	}
	
	public void update()
	{
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_G)){
			if (isGrid())
				setGrid(false);
			else
				setGrid(true);
		}
		
		CoreSystem.getInstance().getScenegraph().update();
		gui.update();
		contrastController.update();
		
		if (CoreSystem.getInstance().getScenegraph().terrainExists()){
			TerrainPicking.getInstance().getTerrainPosition();
		}
	}
	
	public void shutdown()
	{
		CoreSystem.getInstance().getScenegraph().shutdown();
		instancingObjectHandler.signalAll();
	}

	public Quaternion getClipplane() {
		return clipplane;
	}

	public void setClipplane(Quaternion clipplane) {
		this.clipplane = clipplane;
	} 

	public boolean isGrid() {
		return grid;
	}

	public void setGrid(boolean grid) {
		this.grid = grid;
	}
	
	public static ParallelSplitShadowMaps getShadowMaps() {
		return shadowMaps;
	}

	public void setShadowMaps(ParallelSplitShadowMaps shadowMaps) {
		GLForwardRenderingEngine.shadowMaps = shadowMaps;
	}

	public static boolean isMotionBlurEnabled() {
		return motionBlurEnabled;
	}

	public static void setMotionBlurEnabled(boolean motionBlurEnabled) {
		GLForwardRenderingEngine.motionBlurEnabled = motionBlurEnabled;
	}

	public static boolean isDepthOfFieldBlurEnabled() {
		return depthOfFieldBlurEnabled;
	}

	public static void setDepthOfFieldBlurEnabled(boolean depthOfFieldBlurEnabled) {
		GLForwardRenderingEngine.depthOfFieldBlurEnabled = depthOfFieldBlurEnabled;
	}

	public boolean isBloomEnabled() {
		return bloomEnabled;
	}

	public void setBloomEnabled(boolean enabled) {
		GLForwardRenderingEngine.bloomEnabled = enabled;
	}

	public boolean isWaterReflection() {
		return waterReflection;
	}

	public void setWaterReflection(boolean isReflection) {
		GLForwardRenderingEngine.waterReflection = isReflection;
	}

	public boolean isWaterRefraction() {
		return waterRefraction;
	}

	public void setWaterRefraction(boolean waterRefraction) {
		GLForwardRenderingEngine.waterRefraction = waterRefraction;
	}

	public static boolean isLightScatteringEnabled() {
		return lightScatteringEnabled;
	}

	public void setLightScatteringEnabled(boolean lightScatteringEnabled) {
		GLForwardRenderingEngine.lightScatteringEnabled = lightScatteringEnabled;
	}
	
	public boolean isCameraUnderWater() {
		return cameraUnderWater;
	}

	public void setCameraUnderWater(boolean underWater) {
		GLForwardRenderingEngine.cameraUnderWater = underWater;
	}

	public float getT_causticsDistortion() {
		return t_causticsDistortion;
	}

	public float getSightRangeFactor() {
		return sightRangeFactor;
	}

	public void setSightRangeFactor(float sightRangeFactor) {
		GLForwardRenderingEngine.sightRangeFactor = sightRangeFactor;
	}
	
	public FullScreenQuad getFullScreenTexture() {
		return fullScreenQuad;
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}

	public Texture2D getSceneDepthmap() {
		return sceneDepthmap;
	}

	public void setSceneDepthmap(Texture2D sceneDepthmap) {
		this.sceneDepthmap = sceneDepthmap;
	}

	public GLFramebuffer getMultisampledFbo() {
		return multisampledFbo;
	}

	public void setMultisampledFbo(GLFramebuffer multisampledFbo) {
		this.multisampledFbo = multisampledFbo;
	}

	@Override
	public Framebuffer getDeferredFbo() {
		// TODO Auto-generated method stub
		return null;
	}

}
