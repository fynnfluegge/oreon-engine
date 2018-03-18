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
import org.oreon.core.gl.system.GLConfiguration;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.gl.texture.Texture2DMultisample;
import org.oreon.core.instanced.InstancedHandler;
import org.oreon.core.light.LightHandler;
import org.oreon.core.platform.Window;
import org.oreon.core.system.CommonConfig;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderEngine;
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
import org.oreon.modules.gl.water.UnderWaterRenderer;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public class GLRenderEngine implements RenderEngine{

	private Window window;
	private FullScreenQuad fullScreenQuad;
	private FullScreenMultisampleQuad fullScreenQuadMultisample;
	private MSAA msaa;
	private FXAA fxaa;
	
	private InstancedHandler instancingObjectHandler;
	
	private GLFramebuffer finalSceneFbo;
	private Texture2D finalSceneTexture;
	private Texture2DMultisample sceneDepthmap;
	private Texture2D deferredLightScatteringMask;
	private Texture2D finalLightScatteringMask;
	private Texture2D postProcessingTexture;
	
	private DeferredLightingRenderer deferredLightingRenderer;
	private TransparencyBlendRenderer transparencyBlendRenderer;
	private TransparencyLayer transparencyLayer;
	
	@Setter
	private GUI gui;

	private ParallelSplitShadowMaps shadowMaps;
	
	// post processing effects
	private MotionBlur motionBlur;
	private DepthOfFieldBlur dofBlur;
	private Bloom bloom;
	private SunLightScattering sunlightScattering;
	private LensFlare lensFlare;
	private SSAO ssao;
	private UnderWaterRenderer underWaterRenderer;
	private ContrastController contrastController;

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
		
		CommonConfig.getInstance().setWireframe(false);
		CommonConfig.getInstance().setClipplane(Constants.PLANE0);
		CommonConfig.getInstance().setSightRange(2f);
		CommonConfig.getInstance().setReflection(false);
		CommonConfig.getInstance().setRefraction(false);
		CommonConfig.getInstance().setUnderwater(false);
		
		Default.init();
		window = CoreSystem.getInstance().getWindow();
		instancingObjectHandler = InstancedHandler.getInstance();
		
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
		
		deferredLightingRenderer = new DeferredLightingRenderer(window.getWidth(), window.getHeight());
		transparencyLayer = new TransparencyLayer(window.getWidth(), window.getHeight());
		transparencyBlendRenderer = new TransparencyBlendRenderer();
		
		motionBlur = new MotionBlur();
		dofBlur = new DepthOfFieldBlur();
		bloom = new Bloom();
		sunlightScattering = new SunLightScattering();
		lensFlare = new LensFlare();
		ssao = new SSAO(window.getWidth(),window.getHeight());
		underWaterRenderer = new UnderWaterRenderer();
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

		CommonConfig.getInstance().setClipplane(Constants.PLANE0);
		GLConfiguration.getInstance().setSceneDepthMap(sceneDepthmap);
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
		deferredLightingRenderer.getFbo().bind();
		Default.clearScreen();
		CoreSystem.getInstance().getScenegraph().render();
		deferredLightingRenderer.getFbo().unbind();
		
		ssao.render(deferredLightingRenderer.getGbuffer().getWorldPositionTexture(),
					deferredLightingRenderer.getGbuffer().getNormalTexture());
		
		msaa.renderSampleCoverageMask(deferredLightingRenderer.getGbuffer().getWorldPositionTexture(),
									  deferredLightingRenderer.getGbuffer().getLightScatteringMask(),
									  deferredLightScatteringMask);
		
		deferredLightingRenderer.render(msaa.getSampleCoverageMask(),
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
		transparencyBlendRenderer.render(deferredLightingRenderer.getDeferredLightingSceneTexture(), 
										 deferredLightingRenderer.getGbuffer().getDepthTexture(),
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
		sceneDepthmap = deferredLightingRenderer.getGbuffer().getDepthTexture();
		
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
			dofBlur.render(deferredLightingRenderer.getGbuffer().getDepthTexture(), finalLightScatteringMask, postProcessingTexture, window.getWidth(), window.getHeight());
			postProcessingTexture = dofBlur.getVerticalBlurSceneTexture();
			
			// post processing effects
			if (CommonConfig.getInstance().isUnderwater()){
				underWaterRenderer.render(postProcessingTexture, deferredLightingRenderer.getGbuffer().getDepthTexture());
				postProcessingTexture = underWaterRenderer.getUnderwaterSceneTexture();
			}
			
			// Bloom
			bloom.render(postProcessingTexture);
			postProcessingTexture = bloom.getBloomBlurSceneTexture();
			
			// Motion Blur
			if (doMotionBlur){
				motionBlur.render(deferredLightingRenderer.getGbuffer().getDepthTexture(), postProcessingTexture);
				postProcessingTexture = motionBlur.getMotionBlurSceneTexture();
			}
			
			sunlightScattering.render(postProcessingTexture,finalLightScatteringMask);
			postProcessingTexture = sunlightScattering.getSunLightScatteringSceneTexture();
		}
		
		if (CommonConfig.getInstance().isWireframe()){
			fullScreenQuadMultisample.setTexture(deferredLightingRenderer.getGbuffer().getAlbedoTexture());
			fullScreenQuadMultisample.render();
		}
		if (renderAlbedoBuffer){
			fullScreenQuadMultisample.setTexture(deferredLightingRenderer.getGbuffer().getAlbedoTexture());
			fullScreenQuadMultisample.render();
		}
		if (renderNormalBuffer){
			fullScreenQuadMultisample.setTexture(deferredLightingRenderer.getGbuffer().getNormalTexture());
			fullScreenQuadMultisample.render();
		}
		if (renderPositionBuffer){
			fullScreenQuadMultisample.setTexture(deferredLightingRenderer.getGbuffer().getWorldPositionTexture());
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
			fullScreenQuad.setTexture(deferredLightingRenderer.getDeferredLightingSceneTexture());
			fullScreenQuad.render();
		}
		
		contrastController.render(postProcessingTexture);
		
		fullScreenQuad.setTexture(contrastController.getContrastTexture());
		fullScreenQuad.render();
		
		deferredLightingRenderer.getFbo().bind();
		LightHandler.doOcclusionQueries();
		deferredLightingRenderer.getFbo().unbind();
		
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
			if (CommonConfig.getInstance().isWireframe())
				CommonConfig.getInstance().setWireframe(false);
			else
				CommonConfig.getInstance().setWireframe(true);
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
	
	private void getDeviceProperties(){
		
		log.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
		log.info("Max Geometry Uniform Blocks: " + GL11.glGetInteger(GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS));
		log.info("Max Geometry Shader Invocations: " + GL11.glGetInteger(GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS));
		log.info("Max Uniform Buffer Bindings: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS));
		log.info("Max Uniform Block Size: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BLOCK_SIZE) + " bytes");
		log.info("Max SSBO Block Size: " + GL11.glGetInteger(GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE) + " bytes");	
		log.info("Max Image Bindings: " + GL11.glGetInteger(GL42.GL_MAX_IMAGE_UNITS));
	}
}
