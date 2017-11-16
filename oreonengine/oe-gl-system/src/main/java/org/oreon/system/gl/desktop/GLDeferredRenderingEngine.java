package org.oreon.system.gl.desktop;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.gl.antialiasing.MSAA;
import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.deferred.DeferredLightingRenderer;
import org.oreon.core.gl.deferred.TransparencyLayer;
import org.oreon.core.gl.deferred.TransparencyBlendRenderer;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.picking.TerrainPicking;
import org.oreon.core.gl.scene.FullScreenMultisampleQuad;
import org.oreon.core.gl.scene.FullScreenQuad;
import org.oreon.core.gl.shadow.ParallelSplitShadowMaps;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.instancing.InstancingObjectHandler;
import org.oreon.core.light.LightHandler;
import org.oreon.core.math.Quaternion;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderingEngine;
import org.oreon.core.system.Window;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.gui.GUI;
import org.oreon.modules.gl.gui.GUIs.VoidGUI;
import org.oreon.modules.gl.postprocessfilter.bloom.Bloom;
import org.oreon.modules.gl.postprocessfilter.dofblur.DepthOfFieldBlur;
import org.oreon.modules.gl.postprocessfilter.lensflare.LensFlare;
import org.oreon.modules.gl.postprocessfilter.lightscattering.SunLightScattering;
import org.oreon.modules.gl.postprocessfilter.motionblur.MotionBlur;
import org.oreon.modules.gl.postprocessfilter.ssao.SSAO;
import org.oreon.modules.gl.terrain.Terrain;
import org.oreon.modules.gl.water.UnderWater;

public class GLDeferredRenderingEngine implements RenderingEngine{

	private Window window;
	private FullScreenQuad fullScreenQuad;
	private FullScreenMultisampleQuad fullScreenQuadMultisample;
	private MSAA msaa;
	
	private InstancingObjectHandler instancingObjectHandler;
	
	private GLFramebuffer finalSceneFbo;
	private Texture2D finalSceneTexture;
	private Texture2D sceneDepthmap;
	private Texture2D lightScatteringSceneTexture;
	private Texture2D postProcessingTexture;
	
	private DeferredLightingRenderer deferredRenderer;
	private TransparencyBlendRenderer transparencyBlendRenderer;
	private TransparencyLayer transparencyLayer;
	private GUI gui;
	
	private boolean grid;
	
	private Quaternion clipplane;
	private ParallelSplitShadowMaps shadowMaps;
	
	// post processing effects
	private MotionBlur motionBlur;
	private DepthOfFieldBlur dofBlur;
	private Bloom bloom;
	private SunLightScattering sunlightScattering;
	private LensFlare lensFlare;
	private SSAO ssao;
	private UnderWater underWater;
	
	private float sightRangeFactor = 2f;
	private boolean waterReflection = false;
	private boolean waterRefraction = false;
	private boolean cameraUnderWater = false;
	
	@Override
	public void init() {
		
		Default.init();
		window = CoreSystem.getInstance().getWindow();
		instancingObjectHandler = InstancingObjectHandler.getInstance();
		
		if (gui != null){
			gui.init();
		}
		else {
			gui = new VoidGUI();
		}
		
		fullScreenQuad = new FullScreenQuad();
		fullScreenQuadMultisample = new FullScreenMultisampleQuad();
		shadowMaps = new ParallelSplitShadowMaps();
		msaa = new MSAA();
		
		deferredRenderer = new DeferredLightingRenderer(window.getWidth(), window.getHeight());
		transparencyLayer = new TransparencyLayer(window.getWidth(), window.getHeight());
		transparencyBlendRenderer = new TransparencyBlendRenderer();
		
		motionBlur = new MotionBlur();
		dofBlur = new DepthOfFieldBlur();
		bloom = new Bloom();
		sunlightScattering = new SunLightScattering();
		lensFlare = new LensFlare();
		ssao = new SSAO(window.getWidth(),window.getHeight());
		underWater = UnderWater.getInstance();
		
		finalSceneTexture = new Texture2D();
		finalSceneTexture.generate();
		finalSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		finalSceneTexture.noFilter();
		
		lightScatteringSceneTexture = new Texture2D();
		lightScatteringSceneTexture.generate();
		lightScatteringSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		lightScatteringSceneTexture.noFilter();
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(2);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.flip();
		
		finalSceneFbo = new GLFramebuffer();
		finalSceneFbo.bind();
		finalSceneFbo.createColorTextureAttachment(finalSceneTexture.getId(),0);
		finalSceneFbo.createColorTextureAttachment(lightScatteringSceneTexture.getId(),1);
		finalSceneFbo.setDrawBuffers(drawBuffers);
		finalSceneFbo.checkStatus();
		finalSceneFbo.unbind();
	}
	
	@Override
	public void render() {

		GLDirectionalLight.getInstance().update();
		
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
		
		// deferred scene lighting - opaque objects
		deferredRenderer.getFbo().bind();
		Default.clearScreen();
		CoreSystem.getInstance().getScenegraph().render();
		deferredRenderer.getFbo().unbind();
		
		ssao.render(deferredRenderer.getGbuffer().getWorldPositionTexture(),
					deferredRenderer.getGbuffer().getNormalTexture());
		
		msaa.renderSampleCoverageMask(deferredRenderer.getGbuffer().getAlbedoTexture(),
				    deferredRenderer.getGbuffer().getWorldPositionTexture(),
				    deferredRenderer.getGbuffer().getNormalTexture(),
				    deferredRenderer.getGbuffer().getDepthTexture());
		
		deferredRenderer.render(msaa.getSampleCoverageMask(),
							    ssao.getSsaoBlurSceneTexture(),
							    shadowMaps.getDepthMaps());
		
		// forward scene lighting - transparent objects
		transparencyLayer.getFbo().bind();
		Default.clearScreen();
		CoreSystem.getInstance().getScenegraph().renderTransparentObejcts();
		transparencyLayer.getFbo().unbind();
		
		// blend scene/transparent layers
		finalSceneFbo.bind();
		transparencyBlendRenderer.render(deferredRenderer.getDeferredLightingSceneTexture(), 
										 deferredRenderer.getDepthmap(),
										 deferredRenderer.getGbuffer().getLightScatteringTexture(),
										 transparencyLayer.getGbuffer().getAlbedoTexture(),
										 transparencyLayer.getGbuffer().getDepthTexture(),
										 transparencyLayer.getGbuffer().getAlphaTexture(),
										 transparencyLayer.getGbuffer().getLightScatteringTexture());
		finalSceneFbo.unbind();
		
		// start Threads to update instancing objects
		instancingObjectHandler.signalAll();

		postProcessingTexture = new Texture2D(finalSceneTexture);
		sceneDepthmap = deferredRenderer.getDepthmap();
		
		// post processing effects
		
		if (isCameraUnderWater()){
			underWater.render(postProcessingTexture, sceneDepthmap);
			postProcessingTexture = underWater.getUnderwaterSceneTexture();
		}
		
		// Bloom
		bloom.render(postProcessingTexture);
		postProcessingTexture = bloom.getBloomBlurSceneTexture();
		
		// Depth of Field Blur			
//		dofBlur.render(deferredRenderer.getDepthmap(), postProcessingTexture, window.getWidth(), window.getHeight());
//		postProcessingTexture = dofBlur.getVerticalBlurSceneTexture();
				
		// Motion Blur
		if (CoreSystem.getInstance().getScenegraph().getCamera().getPreviousPosition().sub(
						CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() > 0.04f ||
				CoreSystem.getInstance().getScenegraph().getCamera().getForward().sub(
						CoreSystem.getInstance().getScenegraph().getCamera().getPreviousForward()).length() > 0.01f){
			motionBlur.render(deferredRenderer.getDepthmap(), postProcessingTexture);
			postProcessingTexture = motionBlur.getMotionBlurSceneTexture();
		}
		
		sunlightScattering.render(postProcessingTexture,lightScatteringSceneTexture);
		postProcessingTexture = sunlightScattering.getSunLightScatteringSceneTexture();
		
		fullScreenQuad.setTexture(deferredRenderer.getDeferredLightingSceneTexture());
		fullScreenQuad.render();
		
//		fullScreenQuadMultisample.setTexture(deferredRenderer.getGbuffer().getAlbedoTexture());
//		fullScreenQuadMultisample.render();
		
		deferredRenderer.getFbo().bind();
		LightHandler.doOcclusionQueries();
		deferredRenderer.getFbo().unbind();
		
//		lensFlare.render();
		
		gui.render();
		
		// draw into OpenGL window
		window.draw();
	}
	@Override
	public void update() {
		
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_G)){
			if (isGrid())
				setGrid(false);
			else
				setGrid(true);
		}
		
		CoreSystem.getInstance().getScenegraph().update();	
		
		if (CoreSystem.getInstance().getScenegraph().terrainExists()){
			TerrainPicking.getInstance().getTerrainPosition();
		}
	}
	@Override
	public void shutdown() {
		CoreSystem.getInstance().getScenegraph().shutdown();
		instancingObjectHandler.signalAll();
	}
	@Override
	public boolean isGrid() {
		return grid;
	}
	@Override
	public boolean isCameraUnderWater() {
		return cameraUnderWater;
	}
	@Override
	public boolean isWaterReflection() {
		return waterReflection;
	}
	@Override
	public boolean isWaterRefraction() {
		return waterRefraction;
	}
	@Override
	public boolean isBloomEnabled() {
		return false;
	}
	@Override
	public Framebuffer getMultisampledFbo() {
		return null;
	}
	@Override
	public Texture getSceneDepthmap() {
		return sceneDepthmap;
	}
	@Override
	public float getSightRangeFactor() {
		return sightRangeFactor;
	}

	@Override
	public void setGrid(boolean flag) {
		grid = flag;
	}
	@Override
	public void setWaterRefraction(boolean flag) {
		waterRefraction = flag;
	}
	@Override
	public void setWaterReflection(boolean flag) {
		waterReflection = flag;
	}
	@Override
	public void setCameraUnderWater(boolean flag) {
		cameraUnderWater = flag;
	}
	@Override
	public void setSightRangeFactor(float range) {
		
	}
	public ParallelSplitShadowMaps getShadowMaps() {
		return shadowMaps;
	}
	public void setShadowMaps(ParallelSplitShadowMaps shadowMaps) {
		this.shadowMaps = shadowMaps;
	}
	public Quaternion getClipplane() {
		return clipplane;
	}
	public void setClipplane(Quaternion clipplane) {
		this.clipplane = clipplane;
	}
	public GUI getGui() {
		return gui;
	}
	public void setGui(GUI gui) {
		this.gui = gui;
	}
	@Override
	public Framebuffer getDeferredFbo() {
		return deferredRenderer.getFbo();
	}

	public UnderWater getUnderWater() {
		return underWater;
	}

	public void setUnderWater(UnderWater underWater) {
		this.underWater = underWater;
	} 
}
