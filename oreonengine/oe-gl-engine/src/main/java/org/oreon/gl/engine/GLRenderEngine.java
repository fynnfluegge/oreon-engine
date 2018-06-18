package org.oreon.gl.engine;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.picking.TerrainPicking;
import org.oreon.core.gl.shadow.ParallelSplitShadowMapsFbo;
import org.oreon.core.gl.surface.FullScreenMultisampleQuad;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.util.GLUtil;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA16F;
import org.oreon.core.instanced.InstancedHandler;
import org.oreon.core.light.LightHandler;
import org.oreon.core.scenegraph.Scenegraph;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.target.FrameBufferObject.Attachment;
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
import org.oreon.gl.components.ui.GLGUI;
import org.oreon.gl.components.water.UnderWaterRenderer;
import org.oreon.gl.engine.antialiasing.FXAA;
import org.oreon.gl.engine.antialiasing.SampleCoverage;
import org.oreon.gl.engine.deferred.DeferredLighting;
import org.oreon.gl.engine.transparency.OpaqueTransparencyBlending;

import lombok.Setter;

public class GLRenderEngine extends RenderEngine{
	
	private GLFrameBufferObject offScreenFbo;
	private GLFrameBufferObject transparencyFbo;
	
	private FullScreenQuad fullScreenQuad;
	private FullScreenMultisampleQuad fullScreenQuadMultisample;
	private SampleCoverage sampleCoverage;
	private FXAA fxaa;
	
	private InstancedHandler instancingObjectHandler;
	
	private GLFramebuffer finalSceneFbo;
	private GLTexture finalSceneTexture;
	private GLTexture sceneDepthmap;
	private GLTexture lightScatteringSampleCoverageTexture;
	private GLTexture finalLightScatteringMask;
	private GLTexture postProcessingTexture;
	
	private DeferredLighting deferredLighting;
	private OpaqueTransparencyBlending opaqueTransparencyBlending;
	
	@Setter
	private GLGUI gui;

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
	private boolean renderPostProcessingEffects = true;
	
	@Override
	public void init() {
		
		super.init();
		
		camera.init();
		
		instancingObjectHandler = InstancedHandler.getInstance();
		
		// frameBuffers
		offScreenFbo = new OffScreenFbo(config.getX_ScreenResolution(),
				config.getY_ScreenResolution(), config.getMultisamples());
		transparencyFbo = new TransparencyFbo(config.getX_ScreenResolution(),
				config.getY_ScreenResolution());
		GLContext.getResources().setOffScreenFbo(offScreenFbo);
		
		if (gui != null){
			gui.init();
		}
		
		fullScreenQuad = new FullScreenQuad();
		fullScreenQuadMultisample = new FullScreenMultisampleQuad();
		pssmFbo = new ParallelSplitShadowMapsFbo();
		sampleCoverage = new SampleCoverage();
		fxaa = new FXAA();
		
		deferredLighting = new DeferredLighting(config.getX_ScreenResolution(),
				config.getY_ScreenResolution());
		opaqueTransparencyBlending = new OpaqueTransparencyBlending();
		
		motionBlur = new MotionBlur();
		dofBlur = new DepthOfFieldBlur();
		bloom = new Bloom();
		sunlightScattering = new SunLightScattering();
		lensFlare = new LensFlare();
		ssao = new SSAO(config.getX_ScreenResolution(), config.getY_ScreenResolution());
		underWaterRenderer = new UnderWaterRenderer();
		contrastController = new ContrastController();
		
		lightScatteringSampleCoverageTexture = new Texture2DBilinearFilterRGBA16F(
				config.getX_ScreenResolution(), config.getY_ScreenResolution());
		finalSceneTexture = new Texture2DBilinearFilterRGBA16F(
				config.getX_ScreenResolution(), config.getY_ScreenResolution());
		finalLightScatteringMask = new Texture2DNoFilterRGBA16F(
				config.getX_ScreenResolution(), config.getY_ScreenResolution());
		
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

		GLContext.getResources().setSceneDepthMap(sceneDepthmap);
		GLUtil.clearScreen();
		
		// render shadow maps into pssm framebuffer
		pssmFbo.getFBO().bind();
		pssmFbo.getConfig().enable();
		glClear(GL_DEPTH_BUFFER_BIT);
		glViewport(0,0,Constants.PSSM_SHADOWMAP_RESOLUTION,Constants.PSSM_SHADOWMAP_RESOLUTION);
		sceneGraph.renderShadows();
		glViewport(0,0,config.getX_ScreenResolution(),config.getY_ScreenResolution());
		pssmFbo.getConfig().disable();
		pssmFbo.getFBO().unbind();
		
		// deferred scene lighting - opaque objects
		// render into GBuffer of deffered lighting framebuffer
		offScreenFbo.bind();
		GLUtil.clearScreen();
		sceneGraph.render();
		offScreenFbo.unbind();
		
		// render screen space ambient occlusion
		if (EngineContext.getConfig().isSsaoEnabled()){
			ssao.render(offScreenFbo.getAttachmentTexture(Attachment.POSITION),
					offScreenFbo.getAttachmentTexture(Attachment.NORMAL));
		}
		
		// render post processing sample coverage mask
		sampleCoverage.render(offScreenFbo.getAttachmentTexture(Attachment.POSITION),
				offScreenFbo.getAttachmentTexture(Attachment.LIGHT_SCATTERING),
				lightScatteringSampleCoverageTexture);
		
		// render deferred lighting scene with multisampling
		deferredLighting.render(sampleCoverage.getSampleCoverageMask(), ssao.getSsaoBlurSceneTexture(),
				pssmFbo.getDepthMaps(),
				offScreenFbo.getAttachmentTexture(Attachment.ALBEDO),
				offScreenFbo.getAttachmentTexture(Attachment.POSITION),
				offScreenFbo.getAttachmentTexture(Attachment.NORMAL),
				offScreenFbo.getAttachmentTexture(Attachment.SPECULAR_EMISSION),
				EngineContext.getConfig().isSsaoEnabled());
		
		// forward scene lighting - transparent objects
		// render transparent objects into GBuffer of transparency framebuffer
		transparencyFbo.bind();
		GLUtil.clearScreen();
		sceneGraph.renderTransparentObejcts();
		transparencyFbo.unbind();
		
		// blend scene/transparent layers
		// render opaque + transparent (final scene) objects into main offscreen framebuffer
		finalSceneFbo.bind();
		opaqueTransparencyBlending.render(deferredLighting.getDeferredLightingSceneTexture(),
				offScreenFbo.getAttachmentTexture(Attachment.DEPTH),
				lightScatteringSampleCoverageTexture,
				transparencyFbo.getAttachmentTexture(Attachment.ALBEDO),
				transparencyFbo.getAttachmentTexture(Attachment.DEPTH),
				transparencyFbo.getAttachmentTexture(Attachment.ALPHA),
				transparencyFbo.getAttachmentTexture(Attachment.LIGHT_SCATTERING));
		finalSceneFbo.unbind();
		
		// start Threads to update instancing objects
		instancingObjectHandler.signalAll();
		
		// update Terrain Quadtree
		if (camera.isCameraMoved()){
			if (sceneGraph.hasTerrain()){
				((GLTerrain) sceneGraph.getTerrain()).getQuadtree().signal();
			}
		}

		
		// render post processing filters
		postProcessingTexture = finalSceneTexture;
		
		boolean doMotionBlur = camera.getPreviousPosition().sub(camera.getPosition()).length() > 0.04f
				|| camera.getForward().sub(camera.getPreviousForward()).length() > 0.01f;
				
		// perform FXAA
		if (!doMotionBlur && EngineContext.getConfig().isFxaaEnabled()){
			fxaa.render(postProcessingTexture);
			postProcessingTexture = fxaa.getFxaaSceneTexture();
		}
			
		if (renderPostProcessingEffects){
			
			// Depth of Field Blur
			if (EngineContext.getConfig().isDepthOfFieldBlurEnabled()){
				dofBlur.render(offScreenFbo.getAttachmentTexture(Attachment.DEPTH),
						finalLightScatteringMask, postProcessingTexture,
						config.getX_ScreenResolution(), config.getY_ScreenResolution());
				postProcessingTexture = dofBlur.getVerticalBlurSceneTexture();
			}
			
			// Bloom
			if (EngineContext.getConfig().isBloomEnabled()){
				bloom.render(postProcessingTexture);
				postProcessingTexture = bloom.getBloomSceneTexture();
			}
			
			// underwater
			if (EngineContext.getConfig().isRenderUnderwater()){
				underWaterRenderer.render(postProcessingTexture,
						offScreenFbo.getAttachmentTexture(Attachment.DEPTH));
				postProcessingTexture = underWaterRenderer.getUnderwaterSceneTexture();
			}
			
			// Motion Blur
			if (doMotionBlur && EngineContext.getConfig().isMotionBlurEnabled()){
				motionBlur.render(postProcessingTexture,
						offScreenFbo.getAttachmentTexture(Attachment.DEPTH));
				postProcessingTexture = motionBlur.getMotionBlurSceneTexture();
			}
			
			if (EngineContext.getConfig().isLightScatteringEnabled()){
				sunlightScattering.render(postProcessingTexture, finalLightScatteringMask);
				postProcessingTexture = sunlightScattering.getSunLightScatteringSceneTexture();
			}
		}
		
		if (EngineContext.getConfig().isRenderWireframe()
				|| renderAlbedoBuffer){
			fullScreenQuadMultisample.setTexture(offScreenFbo.getAttachmentTexture(Attachment.ALBEDO));
			fullScreenQuadMultisample.render();
		}
		if (renderNormalBuffer){
			fullScreenQuadMultisample.setTexture(offScreenFbo.getAttachmentTexture(Attachment.NORMAL));
			fullScreenQuadMultisample.render();
		}
		if (renderPositionBuffer){
			fullScreenQuadMultisample.setTexture(offScreenFbo.getAttachmentTexture(Attachment.POSITION));
			fullScreenQuadMultisample.render();
		}
		if (renderSampleCoverageMask){
			fullScreenQuad.setTexture(sampleCoverage.getSampleCoverageMask());
			fullScreenQuad.render();
		}
		if (renderSSAOBuffer){
			fullScreenQuad.setTexture(ssao.getSsaoBlurSceneTexture());
			fullScreenQuad.render();
		}
		if (renderDeferredLightingScene){
			fullScreenQuad.setTexture(deferredLighting.getDeferredLightingSceneTexture());
			fullScreenQuad.render();
		}
		
//		contrastController.render(postProcessingTexture);
		
		fullScreenQuad.setTexture(postProcessingTexture);
		fullScreenQuad.render();
		
		if (EngineContext.getConfig().isLensFlareEnabled()
			&& !renderDeferredLightingScene && !renderSSAOBuffer
			&& !renderSampleCoverageMask && !renderPositionBuffer
			&& !renderNormalBuffer && !renderAlbedoBuffer){
			
			offScreenFbo.bind();
			LightHandler.doOcclusionQueries();
			offScreenFbo.unbind();
			
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
			if (EngineContext.getConfig().isRenderWireframe())
				EngineContext.getConfig().setRenderWireframe(false);
			else
				EngineContext.getConfig().setRenderWireframe(true);
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
			if (EngineContext.getConfig().isFxaaEnabled()){
				EngineContext.getConfig().setFxaaEnabled(false);
			}
			else{
				EngineContext.getConfig().setFxaaEnabled(true);
			}
		}
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_8)){
			if (EngineContext.getConfig().isSsaoEnabled()){
				EngineContext.getConfig().setSsaoEnabled(false);
			}
			else {
				EngineContext.getConfig().setSsaoEnabled(true);
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
		
		if (sceneGraph.hasTerrain()){
			TerrainPicking.getInstance().getTerrainPosition();
		}
	}
	@Override
	public void shutdown() {
		
		super.shutdown();
		
		instancingObjectHandler.signalAll();
		if (sceneGraph.hasTerrain()){
			((GLTerrain) sceneGraph.getTerrain()).getQuadtree().signal();
		}
	}

	public Scenegraph getScenegraph() {
		return sceneGraph;
	}
}
