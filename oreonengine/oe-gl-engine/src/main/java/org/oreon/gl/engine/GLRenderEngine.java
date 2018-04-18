package org.oreon.gl.engine;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.buffer.GLFramebuffer;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.picking.TerrainPicking;
import org.oreon.core.gl.shadow.ParallelSplitShadowMapsFbo;
import org.oreon.core.gl.surface.FullScreenMultisampleQuad;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.parameter.Default;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA16F;
import org.oreon.core.instanced.InstancedHandler;
import org.oreon.core.light.LightHandler;
import org.oreon.core.scenegraph.Scenegraph;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.gl.components.filter.bloom.Bloom;
import org.oreon.gl.components.filter.contrast.ContrastController;
import org.oreon.gl.components.filter.dofblur.DepthOfFieldBlur;
import org.oreon.gl.components.filter.lensflare.LensFlare;
import org.oreon.gl.components.filter.lightscattering.SunLightScattering;
import org.oreon.gl.components.filter.motionblur.MotionBlur;
import org.oreon.gl.components.filter.ssao.SSAO;
import org.oreon.gl.components.terrain.GLTerrain;
import org.oreon.gl.components.ui.GUI;
import org.oreon.gl.components.water.UnderWaterRenderer;
import org.oreon.gl.engine.antialiasing.FXAA;
import org.oreon.gl.engine.antialiasing.MSAA;
import org.oreon.gl.engine.deferred.DeferredLightingRenderer;
import org.oreon.gl.engine.transparency.TransparencyBlendRenderer;
import org.oreon.gl.engine.transparency.TransparencyLayer;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public class GLRenderEngine extends RenderEngine{
	
	private FullScreenQuad fullScreenQuad;
	private FullScreenMultisampleQuad fullScreenQuadMultisample;
	private MSAA msaa;
	private FXAA fxaa;
	
	private InstancedHandler instancingObjectHandler;
	
	private GLFramebuffer finalSceneFbo;
	private GLTexture finalSceneTexture;
	private GLTexture sceneDepthmap;
	private GLTexture deferredLightScatteringMask;
	private GLTexture finalLightScatteringMask;
	private GLTexture postProcessingTexture;
	
	private DeferredLightingRenderer deferredLightingRenderer;
	private TransparencyBlendRenderer transparencyBlendRenderer;
	private TransparencyLayer transparencyLayer;
	
	@Setter
	private GUI gui;

	private ParallelSplitShadowMapsFbo pssmFbo;
	
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
		
		super.init();
		
		getDeviceProperties();
		
		EngineContext.getConfig().setWireframe(false);
		EngineContext.getConfig().setClipplane(Constants.PLANE0);
		EngineContext.getConfig().setSightRange(1f);
		EngineContext.getConfig().setReflection(false);
		EngineContext.getConfig().setRefraction(false);
		EngineContext.getConfig().setUnderwater(false);
		
		Default.init();
		window = EngineContext.getWindow();
		camera = EngineContext.getCamera();
		camera.init();
		instancingObjectHandler = InstancedHandler.getInstance();
		
		if (gui != null){
			gui.init();
		}
		
		fullScreenQuad = new FullScreenQuad();
		fullScreenQuadMultisample = new FullScreenMultisampleQuad();
		pssmFbo = new ParallelSplitShadowMapsFbo();
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
		
		deferredLightScatteringMask = new Texture2DBilinearFilterRGBA16F(window.getWidth(), window.getHeight());
		finalSceneTexture = new Texture2DBilinearFilterRGBA16F(window.getWidth(), window.getHeight());
		finalLightScatteringMask = new Texture2DNoFilterRGBA16F(window.getWidth(), window.getHeight());
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(2);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.flip();
		
		finalSceneFbo = new GLFramebuffer();
		finalSceneFbo.bind();
		finalSceneFbo.createColorTextureAttachment(finalSceneTexture.getHandle(),0);
		finalSceneFbo.createColorTextureAttachment(finalLightScatteringMask.getHandle(),1);
		finalSceneFbo.setDrawBuffers(drawBuffers);
		finalSceneFbo.checkStatus();
		finalSceneFbo.unbind();
	}
	
	@Override
	public void render() {

		GLDirectionalLight.getInstance().update();

		EngineContext.getConfig().setClipplane(Constants.PLANE0);
		GLContext.getRenderContext().setSceneDepthMap(sceneDepthmap);
		Default.clearScreen();
		
		// render shadow maps into pssm framebuffer
		pssmFbo.getFBO().bind();
		pssmFbo.getConfig().enable();
		glClear(GL_DEPTH_BUFFER_BIT);
		glViewport(0,0,Constants.PSSM_SHADOWMAP_RESOLUTION,Constants.PSSM_SHADOWMAP_RESOLUTION);
		sceneGraph.renderShadows();
		glViewport(0,0,window.getWidth(), window.getHeight());
		pssmFbo.getConfig().disable();
		pssmFbo.getFBO().unbind();
		
		// deferred scene lighting - opaque objects
		// render into GBuffer of deffered lighting framebuffer
		deferredLightingRenderer.getFbo().bind();
		Default.clearScreen();
		sceneGraph.render();
		deferredLightingRenderer.getFbo().unbind();
		
		// render post processing screen space ambient occlusion
		ssao.render(deferredLightingRenderer.getGbuffer().getWorldPositionTexture(),
					deferredLightingRenderer.getGbuffer().getNormalTexture());
		
		// render post processing sample coverage mask
		msaa.renderSampleCoverageMask(deferredLightingRenderer.getGbuffer().getWorldPositionTexture(),
									  deferredLightingRenderer.getGbuffer().getLightScatteringMask(),
									  deferredLightScatteringMask);
		
		// render deferred lighting scene with multisampling
		deferredLightingRenderer.render(msaa.getSampleCoverageMask(),
							    ssao.getSsaoBlurSceneTexture(),
							    pssmFbo.getDepthMaps(),
							    renderSSAO);
		
		// forward scene lighting - transparent objects
		// render transparent objects into GBuffer of transparency framebuffer
		transparencyLayer.getFbo().bind();
		Default.clearScreen();
		sceneGraph.renderTransparentObejcts();
		transparencyLayer.getFbo().unbind();
		
		// blend scene/transparent layers
		// render opaque + transparent (final scene) objects into main offscreen framebuffer
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
		if (camera.isCameraMoved()){
			if (sceneGraph.isRenderTerrain()){
				((GLTerrain) sceneGraph.getTerrain()).getQuadtree().signal();
			}
		}

		
		// render post processing filters
		postProcessingTexture = finalSceneTexture;
		sceneDepthmap = deferredLightingRenderer.getGbuffer().getDepthTexture();
		
		boolean doMotionBlur = camera.getPreviousPosition().sub(
							   camera.getPosition()).length() > 0.04f ||
							   camera.getForward().sub(camera.getPreviousForward()).length() > 0.01f;
				
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
			if (EngineContext.getConfig().isUnderwater()){
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
		
		if (EngineContext.getConfig().isWireframe()){
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
		
		if (gui != null){
			gui.render();
		}
	}
	
	@Override
	public void update() {
		
		super.update();
		
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_G)){
			if (EngineContext.getConfig().isWireframe())
				EngineContext.getConfig().setWireframe(false);
			else
				EngineContext.getConfig().setWireframe(true);
		}
		
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_1)){
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
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_2)){
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
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_3)){
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
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_4)){
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
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_5)){
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
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_6)){
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
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_7)){
			if (renderFXAA){
				renderFXAA = false;
			}
			else{
				renderFXAA = true;
			}
		}
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_8)){
			if (renderSSAO){
				renderSSAO = false;
			}
			else {
				renderSSAO = true;
			}
		}
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_9)){
			if (renderPostProcessingEffects){
				renderPostProcessingEffects = false;
			}
			else {
				renderPostProcessingEffects = true;
			}
		}
		
		if (gui != null){
			gui.update();
		}
		
		contrastController.update();
		
		if (sceneGraph.isRenderTerrain()){
			TerrainPicking.getInstance().getTerrainPosition();
		}
	}
	@Override
	public void shutdown() {
		
		super.shutdown();
		
		instancingObjectHandler.signalAll();
		if (sceneGraph.isRenderTerrain()){
			((GLTerrain) sceneGraph.getTerrain()).getQuadtree().signal();
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

	public Scenegraph getScenegraph() {
		return sceneGraph;
	}
}
