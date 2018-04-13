package org.oreon.modules.gl.water;

import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE6;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.buffer.GLPatchVBO;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.parameter.WaterRenderConfig;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Quaternion;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.scenegraph.Scenegraph;
import org.oreon.core.util.Constants;
import org.oreon.core.util.MeshGenerator;
import org.oreon.modules.gl.gpgpu.NormalMapRenderer;
import org.oreon.modules.gl.terrain.GLTerrain;
import org.oreon.modules.gl.terrain.GLTerrainContext;
import org.oreon.modules.gl.water.fft.OceanFFT;

import lombok.Getter;

public class Water extends Renderable{
	
	private Quaternion clipplane;
	private float clip_offset;
	private float motion;
	private float distortion;
	private Texture2D dudv;
	private Texture2D caustics;
	
	private RefracReflecRenderer refractionRenderer;
	private RefracReflecRenderer reflectionRenderer;
	
	private OceanFFT fft;
	private NormalMapRenderer normalmapRenderer;
	private boolean cameraUnderwater;
	
	private WaterRenderConfig renderConfig;
	
	@Getter
	private WaterConfiguration waterConfiguration;

	public Water(int patches, int fftResolution, GLShaderProgram shader, GLShaderProgram wireframeShader)
	{		
		waterConfiguration = new WaterConfiguration();
		waterConfiguration.loadFile("water-config.properties");
		
		GLPatchVBO meshBuffer = new GLPatchVBO();
		meshBuffer.addData(MeshGenerator.generatePatch2D4x4(patches),16);
		
		renderConfig = new WaterRenderConfig();
		
		GLRenderInfo renderInfo = new GLRenderInfo(shader,renderConfig,meshBuffer);
		GLRenderInfo wireframeRenderInfo = new GLRenderInfo(wireframeShader,renderConfig,meshBuffer);
		
		dudv = new Texture2D("textures/water/dudv/dudv1.jpg");
		dudv.bind();
		dudv.trilinearFilter();
		
		caustics = new Texture2D("textures/water/caustics/caustics.jpg");
		caustics.bind();
		caustics.trilinearFilter();
		
		addComponent(ComponentType.MAIN_RENDERINFO, renderInfo);
		addComponent(ComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);

		fft = new OceanFFT(fftResolution); 
		fft.init();
		getFft().setT_delta(waterConfiguration.getDelta_T());
		getFft().setChoppy(waterConfiguration.isChoppy());
		
		normalmapRenderer = new NormalMapRenderer(fftResolution);
		getNormalmapRenderer().setStrength(waterConfiguration.getNormalStrength());
		
		refractionRenderer = new RefracReflecRenderer(EngineContext.getWindow().getWidth()/2,
													  EngineContext.getWindow().getHeight()/2);
		
		reflectionRenderer = new RefracReflecRenderer(EngineContext.getWindow().getWidth()/2,
												 	  EngineContext.getWindow().getHeight()/2);
	}	
	
	public void update()
	{
		setCameraUnderwater(EngineContext.getCamera().getPosition().getY() < (getWorldTransform().getTranslation().getY())); 
	}
	
	public void render()
	{
		if (!isCameraUnderwater()){
			glEnable(GL_CLIP_DISTANCE6);
			EngineContext.getConfig().setUnderwater(false);
		}
		else {
			EngineContext.getConfig().setUnderwater(true);
		}
			
		distortion += waterConfiguration.getDistortion();
		motion += waterConfiguration.getWaveMotion();
		
		Scenegraph scenegraph = ((Scenegraph) getParent());
		
		EngineContext.getConfig().setClipplane(getClipplane());
			
		//mirror scene to clipplane
		scenegraph.getWorldTransform().setScaling(1,-1,1);
		
		// TODO
//		scenegraph.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
//				(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));
			
		if (scenegraph.isRenderTerrain()){
				
				GLTerrainContext.getConfiguration().setScaleY(GLTerrainContext.getConfiguration().getScaleY() * -1f);
				GLTerrainContext.getConfiguration().setWaterReflectionShift((int) (getClipplane().getW() * 2f));
		}
		
		scenegraph.update();
		
		//render reflection to texture

		glViewport(0,0,EngineContext.getWindow().getWidth()/2, EngineContext.getWindow().getHeight()/2);
		
		EngineContext.getConfig().setReflection(true);
		
		reflectionRenderer.getFbo().bind();
		renderConfig.clearScreenDeepOcean();
		glFrontFace(GL_CCW);
		
		if (!isCameraUnderwater()){
			scenegraph.getRoot().render();
			if (scenegraph.isRenderTerrain()){
				((GLTerrain) scenegraph.getTerrain()).render();
			}
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish(); 
		glFrontFace(GL_CW);
		reflectionRenderer.getFbo().unbind();
		reflectionRenderer.render();
		
		EngineContext.getConfig().setReflection(false);
		
		// antimirror scene to clipplane
	
		scenegraph.getWorldTransform().setScaling(1,1,1);

		// TODO
//		scenegraph.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
//				(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));

		if (scenegraph.isRenderTerrain()){
				GLTerrainContext.getConfiguration().setScaleY(GLTerrainContext.getConfiguration().getScaleY() / -1f);
				GLTerrainContext.getConfiguration().setWaterReflectionShift(0);
		}

		scenegraph.update();
		
		// render to refraction texture
		EngineContext.getConfig().setRefraction(true);
		
		refractionRenderer.getFbo().bind();
		renderConfig.clearScreenDeepOcean();
	
		scenegraph.getRoot().render();
		if (scenegraph.isRenderTerrain()){
			((GLTerrain) scenegraph.getTerrain()).render();
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
		refractionRenderer.getFbo().unbind();
		refractionRenderer.render();
		
		EngineContext.getConfig().setRefraction(false);
		
		glDisable(GL_CLIP_DISTANCE6);
		EngineContext.getConfig().setClipplane(Constants.PLANE0);	
	
		glViewport(0,0,EngineContext.getWindow().getWidth(), EngineContext.getWindow().getHeight());
		
		fft.render();
		normalmapRenderer.render(fft.getDy());
		
		GLContext.getRenderContext().getDeferredFbo().bind();
		
		if (EngineContext.getConfig().isWireframe())
		{
			getComponents().get(ComponentType.WIREFRAME_RENDERINFO).render();
		}
		else
		{
			getComponents().get(ComponentType.MAIN_RENDERINFO).render();
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
		GLContext.getRenderContext().getDeferredFbo().unbind();
	}
		
	public Quaternion getClipplane() {
		return clipplane;
	}

	public void setClipplane(Quaternion clipplane) {
		this.clipplane = clipplane;
	}

	public float getMotion() {
		return motion;
	}

	public void setMotion(float motion) {
		this.motion = motion;
	}

	public float getDistortion() {
		return distortion;
	}

	public float getClip_offset() {
		return clip_offset;
	}

	public void setClip_offset(float clip_offset) {
		this.clip_offset = clip_offset;
	}

	public Texture2D getDudv() {
		return dudv;
	}

	public void setDudv(Texture2D dudv) {
		this.dudv = dudv;
	}

	public OceanFFT getFft() {
		return fft;
	}

	public void setFft(OceanFFT fft) {
		this.fft = fft;
	}

	public NormalMapRenderer getNormalmapRenderer() {
		return normalmapRenderer;
	}

	public void setNormalmapRenderer(NormalMapRenderer normalmapRenderer) {
		this.normalmapRenderer = normalmapRenderer;
	}
	
	public void setCameraUnderwater(boolean cameraUnderwater) {
		this.cameraUnderwater = cameraUnderwater;
	}

	public boolean isCameraUnderwater() {
		return cameraUnderwater;
	}
	
	public Texture2D getCaustics() {
		return caustics;
	}

	public void setCaustics(Texture2D caustics) {
		this.caustics = caustics;
	}
	
	public Texture2D getRefractionTexture(){
		return refractionRenderer.getDeferredLightingSceneTexture();
	}
	
	public Texture2D getReflectionTexture(){
		return reflectionRenderer.getDeferredLightingSceneTexture();
	}
}
