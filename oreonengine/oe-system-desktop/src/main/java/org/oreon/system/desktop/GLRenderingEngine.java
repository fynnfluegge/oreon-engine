package org.oreon.system.desktop;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.configs.Default;
import org.oreon.core.texture.Texture2D;
import org.oreon.core.instancing.InstancingObjectHandler;
import org.oreon.core.light.DirectionalLight;
import org.oreon.core.light.LightHandler;
import org.oreon.core.math.Quaternion;
import org.oreon.core.shadow.ShadowMaps;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderingEngine;
import org.oreon.core.system.Window;
import org.oreon.core.utils.BufferUtil;
import org.oreon.core.utils.Constants;
import org.oreon.modules.gpgpu.ContrastController;
import org.oreon.modules.gui.GUI;
import org.oreon.modules.gui.GUIs.VoidGUI;
import org.oreon.modules.gui.elements.TexturePanel;
import org.oreon.modules.mousePicking.TerrainPicking;
import org.oreon.modules.postProcessingEffects.Bloom;
import org.oreon.modules.postProcessingEffects.DepthOfFieldBlur;
import org.oreon.modules.postProcessingEffects.MotionBlur;
import org.oreon.modules.postProcessingEffects.SunLightScattering;
import org.oreon.modules.postProcessingEffects.lensFlare.LensFlare;
import org.oreon.modules.terrain.Terrain;
import org.oreon.modules.water.UnderWater;

public class GLRenderingEngine implements RenderingEngine{

	private Window window;
	private TexturePanel fullScreenTexture;
	private Texture2D postProcessingTexture;

	private Framebuffer fbo;
	private Framebuffer multisampledFbo;
	private Texture2D sceneTexture;
	private Texture2D blackScene4LightScatteringTexture;
	private Texture2D sceneDepthmap;
	
	private Quaternion clipplane;
	private boolean grid;
	
	private InstancingObjectHandler instancingObjectHandler;
	private static ShadowMaps shadowMaps;
	private GUI gui;
	
	private MotionBlur motionBlur;
	private DepthOfFieldBlur dofBlur;
	private Bloom bloom;
	private SunLightScattering sunlightScattering;
	private LensFlare lensFlare;
	private UnderWater underWater;
	private ContrastController contrastController;
	
	private static boolean motionBlurEnabled = true;
	private static boolean depthOfFieldBlurEnabled = false;
	private static boolean bloomEnabled = true;
	private static boolean lightScatteringEnabled = true;
	private static boolean waterReflection = false;
	private static boolean waterRefraction = false;
	private static boolean cameraUnderWater = false;
	private static float t_causticsDistortion = 0;
	private static float sightRangeFactor = 1.4f;
	
	public GLRenderingEngine()
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
		
		shadowMaps = new ShadowMaps();
		fullScreenTexture = new TexturePanel();
		motionBlur = new MotionBlur();
		dofBlur = new DepthOfFieldBlur();
		bloom = new Bloom();
		sunlightScattering = new SunLightScattering();
		lensFlare = new LensFlare();
		contrastController = new ContrastController();
		underWater = UnderWater.getInstance();
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(2);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.flip();
		
		multisampledFbo = new Framebuffer();
		multisampledFbo.bind();
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 0);
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 1);
		multisampledFbo.createDepthBufferMultisampleAttachment(Constants.MULTISAMPLES);
		multisampledFbo.setDrawBuffers(drawBuffers);
		multisampledFbo.checkStatus();
		multisampledFbo.unbind();
		
		sceneTexture = new Texture2D();
		sceneTexture.generate();
		sceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		sceneTexture.bilinearFilter();
		sceneTexture.clampToEdge();
		
		blackScene4LightScatteringTexture = new Texture2D();
		blackScene4LightScatteringTexture.generate();
		blackScene4LightScatteringTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		blackScene4LightScatteringTexture.bilinearFilter();
		blackScene4LightScatteringTexture.clampToEdge();
		
		sceneDepthmap = new Texture2D();
		sceneDepthmap.generate();
		sceneDepthmap.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, window.getWidth(), window.getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		sceneDepthmap.bilinearFilter();
		sceneDepthmap.clampToEdge();
		
		fbo = new Framebuffer();
		fbo.bind();
		fbo.createColorTextureAttachment(sceneTexture.getId(),0);
		fbo.createColorTextureAttachment(blackScene4LightScatteringTexture.getId(),1);
		fbo.createDepthTextureAttachment(sceneDepthmap.getId());
		fbo.checkStatus();
		fbo.unbind();
	}
	
	public void render()
	{	
		DirectionalLight.getInstance().update();
		if (CoreSystem.getInstance().getScenegraph().getCamera().isCameraMoved()){
			if (CoreSystem.getInstance().getScenegraph().terrainExists()){
				((Terrain) CoreSystem.getInstance().getScenegraph().getTerrain()).updateQuadtree();
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
		multisampledFbo.blitFrameBuffer(0,0,fbo.getId());
		// blit light Scattering SceneTexture
		multisampledFbo.blitFrameBuffer(1,1,fbo.getId());
		
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
			
			// copy scene texture into low-resolution texture
			dofBlur.getLowResFbo().bind();
			fullScreenTexture.setTexture(postProcessingTexture);
			glViewport(0,0,(int)(window.getWidth()/1.4f),(int)(window.getHeight()/1.4f));
			fullScreenTexture.render();
			dofBlur.getLowResFbo().unbind();
			glViewport(0,0, window.getWidth(), window.getHeight());
			
			dofBlur.render(sceneDepthmap, postProcessingTexture);
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
			sunlightScattering.render(postProcessingTexture,blackScene4LightScatteringTexture);
			postProcessingTexture = sunlightScattering.getSunLightScatteringSceneTexture();
		}
		
		contrastController.render(postProcessingTexture);

		// final scene texture
		fullScreenTexture.setTexture(contrastController.getContrastTexture());	
		
		fullScreenTexture.render();
		
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
		
		if (isGrid()){
			setDepthOfFieldBlurEnabled(false);
			setBloomEnabled(false);
		}
		else{
			setDepthOfFieldBlurEnabled(true);
			setBloomEnabled(true);
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
	
	public static ShadowMaps getShadowMaps() {
		return shadowMaps;
	}

	public void setShadowMaps(ShadowMaps shadowMaps) {
		GLRenderingEngine.shadowMaps = shadowMaps;
	}

	public static boolean isMotionBlurEnabled() {
		return motionBlurEnabled;
	}

	public static void setMotionBlurEnabled(boolean motionBlurEnabled) {
		GLRenderingEngine.motionBlurEnabled = motionBlurEnabled;
	}

	public static boolean isDepthOfFieldBlurEnabled() {
		return depthOfFieldBlurEnabled;
	}

	public static void setDepthOfFieldBlurEnabled(boolean depthOfFieldBlurEnabled) {
		GLRenderingEngine.depthOfFieldBlurEnabled = depthOfFieldBlurEnabled;
	}

	public boolean isBloomEnabled() {
		return bloomEnabled;
	}

	public void setBloomEnabled(boolean enabled) {
		GLRenderingEngine.bloomEnabled = enabled;
	}

	public boolean isWaterReflection() {
		return waterReflection;
	}

	public void setWaterReflection(boolean isReflection) {
		GLRenderingEngine.waterReflection = isReflection;
	}

	public boolean isWaterRefraction() {
		return waterRefraction;
	}

	public void setWaterRefraction(boolean waterRefraction) {
		GLRenderingEngine.waterRefraction = waterRefraction;
	}

	public static boolean isLightScatteringEnabled() {
		return lightScatteringEnabled;
	}

	public void setLightScatteringEnabled(boolean lightScatteringEnabled) {
		GLRenderingEngine.lightScatteringEnabled = lightScatteringEnabled;
	}
	
	public boolean isCameraUnderWater() {
		return cameraUnderWater;
	}

	public void setCameraUnderWater(boolean underWater) {
		GLRenderingEngine.cameraUnderWater = underWater;
	}

	public float getT_causticsDistortion() {
		return t_causticsDistortion;
	}

	public float getSightRangeFactor() {
		return sightRangeFactor;
	}

	public void setSightRangeFactor(float sightRangeFactor) {
		GLRenderingEngine.sightRangeFactor = sightRangeFactor;
	}
	
	public TexturePanel getFullScreenTexture() {
		return fullScreenTexture;
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

	public Framebuffer getMultisampledFbo() {
		return multisampledFbo;
	}

	public void setMultisampledFbo(Framebuffer multisampledFbo) {
		this.multisampledFbo = multisampledFbo;
	}

}
