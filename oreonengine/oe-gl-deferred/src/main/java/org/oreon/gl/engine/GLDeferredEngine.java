package org.oreon.gl.engine;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.RenderEngine;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.picking.TerrainPicking;
import org.oreon.core.gl.shadow.ParallelSplitShadowMapsFbo;
import org.oreon.core.gl.surface.FullScreenMultisampleQuad;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.util.GLUtil;
import org.oreon.core.instanced.InstancedHandler;
import org.oreon.core.light.LightHandler;
import org.oreon.core.scenegraph.RenderList;
import org.oreon.core.target.FrameBufferObject.Attachment;
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

public class GLDeferredEngine extends RenderEngine{
	
	private RenderList opaqueSceneRenderList;
	private RenderList transparencySceneRenderList;
	
	private GLFrameBufferObject primarySceneFbo;
	private GLFrameBufferObject secondarySceneFbo;
	
	private FullScreenQuad fullScreenQuad;
	private FullScreenMultisampleQuad fullScreenQuadMultisample;
	private SampleCoverage sampleCoverage;
	private FXAA fxaa;
	
	private InstancedHandler instancingObjectHandler;
	
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
		
		opaqueSceneRenderList = new RenderList();
		transparencySceneRenderList = new RenderList();
		
		instancingObjectHandler = InstancedHandler.getInstance();
		
		// frameBuffers
		primarySceneFbo = new OffScreenFbo(config.getX_ScreenResolution(),
				config.getY_ScreenResolution(), config.getMultisamples());
		secondarySceneFbo = new TransparencyFbo(config.getX_ScreenResolution(),
				config.getY_ScreenResolution());
		GLContext.getResources().setOpaqueSceneFbo(primarySceneFbo);
		
		fullScreenQuad = new FullScreenQuad();
		fullScreenQuadMultisample = new FullScreenMultisampleQuad();
		pssmFbo = new ParallelSplitShadowMapsFbo();
		sampleCoverage = new SampleCoverage(config.getX_ScreenResolution(), config.getY_ScreenResolution());
		fxaa = new FXAA();
		
		deferredLighting = new DeferredLighting(config.getX_ScreenResolution(),
				config.getY_ScreenResolution());
		opaqueTransparencyBlending = new OpaqueTransparencyBlending(config.getX_ScreenResolution(),
				config.getY_ScreenResolution());
		
		motionBlur = new MotionBlur();
		dofBlur = new DepthOfFieldBlur();
		bloom = new Bloom();
		sunlightScattering = new SunLightScattering();
		lensFlare = new LensFlare();
		ssao = new SSAO(config.getX_ScreenResolution(), config.getY_ScreenResolution());
		underWaterRenderer = new UnderWaterRenderer();
		contrastController = new ContrastController();
		
		GLContext.getResources().setSceneDepthMap(primarySceneFbo.getAttachmentTexture(Attachment.DEPTH));
		
		if (gui != null){
			gui.init();
		}
		
		glFinish();
	}
	
	@Override
	public void render() {

		GLUtil.clearScreen();
		primarySceneFbo.bind();
		GLUtil.clearScreen();
		secondarySceneFbo.bind();
		GLUtil.clearScreen();
		pssmFbo.getFBO().bind();
		glClear(GL_DEPTH_BUFFER_BIT);
		pssmFbo.getFBO().unbind();
		glFinish();
		
		// render shadow maps into pssm framebuffer
		pssmFbo.getFBO().bind();
		pssmFbo.getConfig().enable();
		glViewport(0,0,Constants.PSSM_SHADOWMAP_RESOLUTION,Constants.PSSM_SHADOWMAP_RESOLUTION);
		sceneGraph.renderShadows();
		glViewport(0,0,config.getX_ScreenResolution(),config.getY_ScreenResolution());
		pssmFbo.getConfig().disable();
		pssmFbo.getFBO().unbind();
		
		// deferred scene lighting - opaque objects
		// render into GBuffer of deffered lighting framebuffer
		
		primarySceneFbo.bind();
		sceneGraph.record(opaqueSceneRenderList);
		
		opaqueSceneRenderList.getValues().forEach(object ->
			{
				if (BaseContext.getConfig().isRenderWireframe())
					object.renderWireframe();
				else
					object.render();
			});
		
		primarySceneFbo.unbind();
		
		// forward scene lighting - transparent objects
		// render transparent objects into GBuffer of transparency framebuffer
		secondarySceneFbo.bind();
		sceneGraph.recordTransparentObjects(transparencySceneRenderList);
		transparencySceneRenderList.getValues().forEach(object -> object.render());
		secondarySceneFbo.unbind();
		
		// render screen space ambient occlusion
		if (BaseContext.getConfig().isSsaoEnabled()){
			ssao.render(primarySceneFbo.getAttachmentTexture(Attachment.POSITION),
					primarySceneFbo.getAttachmentTexture(Attachment.NORMAL));
		}
		
		// render post processing sample coverage mask
		sampleCoverage.render(primarySceneFbo.getAttachmentTexture(Attachment.POSITION),
				primarySceneFbo.getAttachmentTexture(Attachment.LIGHT_SCATTERING));
		
		// render deferred lighting scene with multisampling
		deferredLighting.render(sampleCoverage.getSampleCoverageMask(),
				ssao.getSsaoBlurSceneTexture(),
				pssmFbo.getDepthMaps(),
				primarySceneFbo.getAttachmentTexture(Attachment.ALBEDO),
				primarySceneFbo.getAttachmentTexture(Attachment.POSITION),
				primarySceneFbo.getAttachmentTexture(Attachment.NORMAL),
				primarySceneFbo.getAttachmentTexture(Attachment.SPECULAR_EMISSION),
				BaseContext.getConfig().isSsaoEnabled());
		
		// blend scene/transparent layers
		// render opaque + transparent (final scene) objects into main offscreen framebuffer
		opaqueTransparencyBlending.render(deferredLighting.getDeferredLightingSceneTexture(),
				primarySceneFbo.getAttachmentTexture(Attachment.DEPTH),
				sampleCoverage.getLightScatteringMaskDownSampled(),
				secondarySceneFbo.getAttachmentTexture(Attachment.ALBEDO),
				secondarySceneFbo.getAttachmentTexture(Attachment.DEPTH),
				secondarySceneFbo.getAttachmentTexture(Attachment.ALPHA),
				secondarySceneFbo.getAttachmentTexture(Attachment.LIGHT_SCATTERING));
		
		// start Threads to update instancing objects
		instancingObjectHandler.signalAll();
		
		// update Terrain Quadtree
		if (camera.isCameraMoved()){
			if (sceneGraph.hasTerrain()){
				((GLTerrain) sceneGraph.getTerrain()).getQuadtree().signal();
			}
		}
		
		// render post processing filters
		GLTexture displayTexture = opaqueTransparencyBlending.getBlendedSceneTexture();
		GLTexture lightScatteringMaskTexture = sampleCoverage.getLightScatteringMaskDownSampled();
		
		boolean doMotionBlur = camera.getPreviousPosition().sub(camera.getPosition()).length() > 0.04f
				|| camera.getForward().sub(camera.getPreviousForward()).length() > 0.01f;
				
		// perform FXAA
		if (!doMotionBlur && BaseContext.getConfig().isFxaaEnabled()){
			fxaa.render(displayTexture);
			displayTexture = fxaa.getFxaaSceneTexture();
		}
			
		if (renderPostProcessingEffects){
			
			// Depth of Field Blur
			if (BaseContext.getConfig().isDepthOfFieldBlurEnabled()){
				dofBlur.render(primarySceneFbo.getAttachmentTexture(Attachment.DEPTH),
						lightScatteringMaskTexture, displayTexture,
						config.getX_ScreenResolution(), config.getY_ScreenResolution());
				displayTexture = dofBlur.getVerticalBlurSceneTexture();
			}
			
			// Bloom
			if (BaseContext.getConfig().isBloomEnabled()){
				bloom.render(displayTexture);
				displayTexture = bloom.getBloomSceneTexture();
			}
			
			// under water
			if (BaseContext.getConfig().isRenderUnderwater()){
				underWaterRenderer.render(displayTexture,
						primarySceneFbo.getAttachmentTexture(Attachment.DEPTH));
				displayTexture = underWaterRenderer.getUnderwaterSceneTexture();
			}
			
			// Motion Blur
			if (doMotionBlur && BaseContext.getConfig().isMotionBlurEnabled()){
				motionBlur.render(displayTexture,
						primarySceneFbo.getAttachmentTexture(Attachment.DEPTH));
				displayTexture = motionBlur.getMotionBlurSceneTexture();
			}
			
			if (BaseContext.getConfig().isLightScatteringEnabled()){
				sunlightScattering.render(displayTexture, lightScatteringMaskTexture);
				displayTexture = sunlightScattering.getSunLightScatteringSceneTexture();
			}
		}
		
		if (BaseContext.getConfig().isRenderWireframe()
				|| renderAlbedoBuffer){
			fullScreenQuadMultisample.setTexture(primarySceneFbo.getAttachmentTexture(Attachment.ALBEDO));
			fullScreenQuadMultisample.render();
		}
		if (renderNormalBuffer){
			fullScreenQuadMultisample.setTexture(primarySceneFbo.getAttachmentTexture(Attachment.NORMAL));
			fullScreenQuadMultisample.render();
		}
		if (renderPositionBuffer){
			fullScreenQuadMultisample.setTexture(primarySceneFbo.getAttachmentTexture(Attachment.POSITION));
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
		
//		contrastController.render(displayTexture);

//		fullScreenQuadMultisample.setTexture(primarySceneFbo.getAttachmentTexture(Attachment.LIGHT_SCATTERING));
//		fullScreenQuadMultisample.render();
		
		fullScreenQuad.setTexture(displayTexture);
		fullScreenQuad.render();
		
		if (BaseContext.getConfig().isLensFlareEnabled()
			&& !renderDeferredLightingScene && !renderSSAOBuffer
			&& !renderSampleCoverageMask && !renderPositionBuffer
			&& !renderNormalBuffer && !renderAlbedoBuffer){
			
			primarySceneFbo.bind();
			LightHandler.doOcclusionQueries();
			primarySceneFbo.unbind();
			
			lensFlare.render();
		}
		
		if (gui != null){
			gui.render();
		}
		
		glFinish();
	}
	
	@Override
	public void update() {
		
		super.update();
		
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_G)){
			if (BaseContext.getConfig().isRenderWireframe())
				BaseContext.getConfig().setRenderWireframe(false);
			else
				BaseContext.getConfig().setRenderWireframe(true);
		}
		
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_1)){
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
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_2)){
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
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_3)){
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
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_4)){
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
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_5)){
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
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_6)){
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
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_7)){
			if (BaseContext.getConfig().isFxaaEnabled()){
				BaseContext.getConfig().setFxaaEnabled(false);
			}
			else{
				BaseContext.getConfig().setFxaaEnabled(true);
			}
		}
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_8)){
			if (BaseContext.getConfig().isSsaoEnabled()){
				BaseContext.getConfig().setSsaoEnabled(false);
			}
			else {
				BaseContext.getConfig().setSsaoEnabled(true);
			}
		}
		if (BaseContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_9)){
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
	
}
