package org.oreon.gl.engine;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.gl.antialiasing.FXAA;
import org.oreon.core.gl.antialiasing.MSAA;
import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.deferred.DeferredLightingRenderer;
import org.oreon.core.gl.deferred.TransparencyLayer;
import org.oreon.core.gl.deferred.TransparencyBlendRenderer;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.picking.TerrainPicking;
import org.oreon.core.gl.shadow.ParallelSplitShadowMaps;
import org.oreon.core.gl.surface.FullScreenMultisampleQuad;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.gl.texture.Texture2DMultisample;
import org.oreon.core.instancing.InstancingObjectHandler;
import org.oreon.core.light.LightHandler;
import org.oreon.core.math.Quaternion;
import org.oreon.core.platform.Window;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.texture.Texture;
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
import org.oreon.modules.gl.postprocessfilter.ssao.SSAO;
import org.oreon.modules.gl.terrain.GLTerrain;
import org.oreon.modules.gl.water.UnderWater;

public class GLRenderEngine implements RenderEngine{

	private Window window;
	private FullScreenQuad fullScreenQuad;
	private FullScreenMultisampleQuad fullScreenQuadMultisample;
	private MSAA msaa;
	private FXAA fxaa;
	
	private InstancingObjectHandler instancingObjectHandler;
	
	private GLFramebuffer finalSceneFbo;
	private Texture2D finalSceneTexture;
	private Texture2DMultisample sceneDepthmap;
	private Texture2D deferredLightScatteringMask;
	private Texture2D finalLightScatteringMask;
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
	private ContrastController contrastController;
	
	private float sightRangeFactor = 2f;
	private boolean waterReflection = false;
	private boolean waterRefraction = false;
	private boolean cameraUnderWater = false;
	
	private boolean renderAlbedoBuffer = false;
	private boolean renderNormalBuffer = false;
	private boolean renderPositionBuffer = false;
	private boolean renderSampleCoverageMask = false;
	private boolean renderDeferredLightingScene = false;
	private boolean renderSSAOBuffer = false;
	private boolean renderFXAA = true;
	private boolean renderPostProcessingEffects = true;
	private boolean renderSSAO = true;
	
	@Override
	public void init() {
		
		getDeviceProperties();
		
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
		fxaa = new FXAA();
		
		deferredRenderer = new DeferredLightingRenderer(window.getWidth(), window.getHeight());
		transparencyLayer = new TransparencyLayer(window.getWidth(), window.getHeight());
		transparencyBlendRenderer = new TransparencyBlendRenderer();
		
		motionBlur = new MotionBlur();
		dofBlur = new DepthOfFieldBlur();
		bloom = new Bloom();
		sunlightScattering = new SunLightScattering();
		lensFlare = new LensFlare();
		ssao = new SSAO(window.getWidth(),window.getHeight());
		underWater = new UnderWater();
		contrastController = new ContrastController();
		
		deferredLightScatteringMask = new Texture2D();
		deferredLightScatteringMask.generate();
		deferredLightScatteringMask.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		deferredLightScatteringMask.bilinearFilter();
		
		finalSceneTexture = new Texture2D();
		finalSceneTexture.generate();
		finalSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		finalSceneTexture.bilinearFilter();
		
		finalLightScatteringMask = new Texture2D();
		finalLightScatteringMask.generate();
		finalLightScatteringMask.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		finalLightScatteringMask.noFilter();
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(2);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.flip();
		
		finalSceneFbo = new GLFramebuffer();
		finalSceneFbo.bind();
		finalSceneFbo.createColorTextureAttachment(finalSceneTexture.getId(),0);
		finalSceneFbo.createColorTextureAttachment(finalLightScatteringMask.getId(),1);
		finalSceneFbo.setDrawBuffers(drawBuffers);
		finalSceneFbo.checkStatus();
		finalSceneFbo.unbind();
	}
	
	@Override
	public void render() {

		GLDirectionalLight.getInstance().update();
		
		setClipplane(Constants.PLANE0);
		Default.clearScreen();
		
		//render shadow maps
		shadowMaps.getFBO().bind();
		shadowMaps.getConfig().enable();
		glClear(GL_DEPTH_BUFFER_BIT);
		glViewport(0,0,Constants.PSSM_SHADOWMAP_RESOLUTION,Constants.PSSM_SHADOWMAP_RESOLUTION);
		CoreSystem.getInstance().getScenegraph().renderShadows();
		glViewport(0,0,CoreSystem.getInstance().getWindow().getWidth(), CoreSystem.getInstance().getWindow().getHeight());
		shadowMaps.getConfig().disable();
		shadowMaps.getFBO().unbind();
		
		// deferred scene lighting - opaque objects
		deferredRenderer.getFbo().bind();
		Default.clearScreen();
		CoreSystem.getInstance().getScenegraph().render();
		deferredRenderer.getFbo().unbind();
		
		ssao.render(deferredRenderer.getGbuffer().getWorldPositionTexture(),
					deferredRenderer.getGbuffer().getNormalTexture());
		
		msaa.renderSampleCoverageMask(deferredRenderer.getGbuffer().getWorldPositionTexture(),
									  deferredRenderer.getGbuffer().getLightScatteringMask(),
									  deferredLightScatteringMask);
		
		deferredRenderer.render(msaa.getSampleCoverageMask(),
							    ssao.getSsaoBlurSceneTexture(),
							    shadowMaps.getDepthMaps(),
							    renderSSAO);
		
		// forward scene lighting - transparent objects
		transparencyLayer.getFbo().bind();
		Default.clearScreen();
		CoreSystem.getInstance().getScenegraph().renderTransparentObejcts();
		transparencyLayer.getFbo().unbind();
		
		// blend scene/transparent layers
		finalSceneFbo.bind();
		transparencyBlendRenderer.render(deferredRenderer.getDeferredLightingSceneTexture(), 
										 deferredRenderer.getGbuffer().getDepthTexture(),
										 deferredLightScatteringMask,
										 transparencyLayer.getGbuffer().getAlbedoTexture(),
										 transparencyLayer.getGbuffer().getDepthTexture(),
										 transparencyLayer.getGbuffer().getAlphaTexture(),
										 transparencyLayer.getGbuffer().getLightScatteringMask());
		finalSceneFbo.unbind();
		
		// start Threads to update instancing objects
		instancingObjectHandler.signalAll();
		
		// update Terrain Quadtree
		if (CoreSystem.getInstance().getScenegraph().getCamera().isCameraMoved()){
			if (CoreSystem.getInstance().getScenegraph().terrainExists()){
				((GLTerrain) CoreSystem.getInstance().getScenegraph().getTerrain()).signal();
			}
		}

		postProcessingTexture = new Texture2D(finalSceneTexture);
		sceneDepthmap = deferredRenderer.getGbuffer().getDepthTexture();
		
		boolean doMotionBlur = CoreSystem.getInstance().getScenegraph().getCamera().getPreviousPosition().sub(
							   CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() > 0.04f ||
							   CoreSystem.getInstance().getScenegraph().getCamera().getForward().sub(
							   CoreSystem.getInstance().getScenegraph().getCamera().getPreviousForward()).length() > 0.01f;
				
		// perform FXAA
		if (!doMotionBlur && renderFXAA){
			fxaa.render(postProcessingTexture);
			postProcessingTexture = fxaa.getFxaaSceneTexture();
		}
			
		if (renderPostProcessingEffects){
			// Depth of Field Blur			
			dofBlur.render(deferredRenderer.getGbuffer().getDepthTexture(), finalLightScatteringMask, postProcessingTexture, window.getWidth(), window.getHeight());
			postProcessingTexture = dofBlur.getVerticalBlurSceneTexture();
			
			// post processing effects
			if (isCameraUnderWater()){
				underWater.render(postProcessingTexture, deferredRenderer.getGbuffer().getDepthTexture());
				postProcessingTexture = underWater.getUnderwaterSceneTexture();
			}
			
			// Bloom
			bloom.render(postProcessingTexture);
			postProcessingTexture = bloom.getBloomBlurSceneTexture();
			
			// Motion Blur
			if (doMotionBlur){
				motionBlur.render(deferredRenderer.getGbuffer().getDepthTexture(), postProcessingTexture);
				postProcessingTexture = motionBlur.getMotionBlurSceneTexture();
			}
			
			sunlightScattering.render(postProcessingTexture,finalLightScatteringMask);
			postProcessingTexture = sunlightScattering.getSunLightScatteringSceneTexture();
		}
		
		if (isWireframe()){
			fullScreenQuadMultisample.setTexture(deferredRenderer.getGbuffer().getAlbedoTexture());
			fullScreenQuadMultisample.render();
		}
		if (renderAlbedoBuffer){
			fullScreenQuadMultisample.setTexture(deferredRenderer.getGbuffer().getAlbedoTexture());
			fullScreenQuadMultisample.render();
		}
		if (renderNormalBuffer){
			fullScreenQuadMultisample.setTexture(deferredRenderer.getGbuffer().getNormalTexture());
			fullScreenQuadMultisample.render();
		}
		if (renderPositionBuffer){
			fullScreenQuadMultisample.setTexture(deferredRenderer.getGbuffer().getWorldPositionTexture());
			fullScreenQuadMultisample.render();
		}
		if (renderSampleCoverageMask){
			fullScreenQuad.setTexture(msaa.getSampleCoverageMask());
			fullScreenQuad.render();
		}
		if (renderSSAOBuffer){
			fullScreenQuad.setTexture(ssao.getSsaoBlurSceneTexture());
			fullScreenQuad.render();
		}
		if (renderDeferredLightingScene){
			fullScreenQuad.setTexture(deferredRenderer.getDeferredLightingSceneTexture());
			fullScreenQuad.render();
		}
		
		contrastController.render(postProcessingTexture);
		
		fullScreenQuad.setTexture(contrastController.getContrastTexture());
		fullScreenQuad.render();
		
		deferredRenderer.getFbo().bind();
		LightHandler.doOcclusionQueries();
		deferredRenderer.getFbo().unbind();
		
		if (!renderDeferredLightingScene && !renderSSAOBuffer
			&& !renderSampleCoverageMask && !renderPositionBuffer
			&& !renderNormalBuffer && !renderAlbedoBuffer){
			lensFlare.render();
		}
		
		gui.render();
	}
	
	@Override
	public void update() {
		
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_G)){
			if (isWireframe())
				setGrid(false);
			else
				setGrid(true);
		}
		
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_KP_1)){
			if (renderAlbedoBuffer){
				renderAlbedoBuffer = false;
			}
			else{
				renderAlbedoBuffer  = true;
				renderNormalBuffer = false;
				renderPositionBuffer = false;
				renderSampleCoverageMask = false;
				renderSSAOBuffer = false;
				renderDeferredLightingScene = false;
			}
		}
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_KP_2)){
			if (renderNormalBuffer){
				renderNormalBuffer = false;
			}
			else{
				renderNormalBuffer  = true;
				renderAlbedoBuffer = false;
				renderPositionBuffer = false;
				renderSampleCoverageMask = false;
				renderSSAOBuffer = false;
				renderDeferredLightingScene = false;
			}
		}
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_KP_3)){
			if (renderPositionBuffer){
				renderPositionBuffer = false;
			}
			else{
				renderPositionBuffer  = true;
				renderAlbedoBuffer = false;
				renderNormalBuffer = false;
				renderSampleCoverageMask = false;
				renderSSAOBuffer = false;
				renderDeferredLightingScene = false;
			}
		}
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_KP_4)){
			if (renderSampleCoverageMask){
				renderSampleCoverageMask = false;
			}
			else{
				renderSampleCoverageMask = true;
				renderAlbedoBuffer = false;
				renderNormalBuffer = false;
				renderPositionBuffer = false;
				renderSSAOBuffer = false;
				renderDeferredLightingScene = false;
			}
		}
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_KP_5)){
			if (renderSSAOBuffer){
				renderSSAOBuffer = false;
			}
			else{
				renderSSAOBuffer = true;
				renderAlbedoBuffer = false;
				renderNormalBuffer = false;
				renderPositionBuffer = false;
				renderSampleCoverageMask = false;
				renderDeferredLightingScene = false;
			}
		}
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_KP_6)){
			if (renderDeferredLightingScene){
				renderDeferredLightingScene = false;
			}
			else{
				renderDeferredLightingScene = true;
				renderAlbedoBuffer = false;
				renderNormalBuffer = false;
				renderPositionBuffer = false;
				renderSampleCoverageMask = false;
				renderSSAOBuffer = false;
			}
		}
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_KP_7)){
			if (renderFXAA){
				renderFXAA = false;
			}
			else{
				renderFXAA = true;
			}
		}
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_KP_8)){
			if (renderSSAO){
				renderSSAO = false;
			}
			else {
				renderSSAO = true;
			}
		}
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_KP_9)){
			if (renderPostProcessingEffects){
				renderPostProcessingEffects = false;
			}
			else {
				renderPostProcessingEffects = true;
			}
		}
		
		gui.update();
		contrastController.update();
		
		if (CoreSystem.getInstance().getScenegraph().terrainExists()){
			TerrainPicking.getInstance().getTerrainPosition();
		}
	}
	@Override
	public void shutdown() {
		
		instancingObjectHandler.signalAll();
		if (CoreSystem.getInstance().getScenegraph().terrainExists()){
			((GLTerrain) CoreSystem.getInstance().getScenegraph().getTerrain()).signal();
		}
	}
	@Override
	public boolean isWireframe() {
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

	@Override
	public Object getUnderwater() {
		return underWater;
	} 
	
	private void getDeviceProperties(){
		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
		System.out.println("Max Geometry Uniform Blocks: " + GL11.glGetInteger(GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS));
		System.out.println("Max Geometry Shader Invocations: " + GL11.glGetInteger(GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS));
		System.out.println("Max Uniform Buffer Bindings: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS));
		System.out.println("Max Uniform Block Size: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BLOCK_SIZE) + " bytes");
		System.out.println("Max SSBO Block Size: " + GL11.glGetInteger(GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE) + " bytes");	
		System.out.println("Max Image Bindings: " + GL11.glGetInteger(GL42.GL_MAX_IMAGE_UNITS));
	}
}
