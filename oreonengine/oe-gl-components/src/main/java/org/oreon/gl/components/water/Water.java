package org.oreon.gl.components.water;

import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE6;

import org.oreon.common.water.WaterConfiguration;
import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.memory.GLPatchVBO;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.parameter.WaterRenderParameter;
import org.oreon.core.gl.wrapper.texture.Texture2DTrilinearFilter;
import org.oreon.core.math.Vec4f;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.scenegraph.Scenegraph;
import org.oreon.core.util.Constants;
import org.oreon.core.util.MeshGenerator;
import org.oreon.gl.components.fft.FFT;
import org.oreon.gl.components.terrain.GLTerrain;
import org.oreon.gl.components.terrain.TerrainConfiguration;
import org.oreon.gl.components.util.NormalRenderer;

import lombok.Getter;

public class Water extends Renderable{
	
	private Vec4f clipplane;
	private float clip_offset;
	private float motion;
	private float distortion;
	private GLTexture dudv;
	private GLTexture caustics;
	
	private RefracReflecRenderer refractionRenderer;
	private RefracReflecRenderer reflectionRenderer;
	
	@Getter
	private FFT fft;
	private NormalRenderer normalmapRenderer;
	private boolean cameraUnderwater;
	
	private WaterRenderParameter renderConfig;
	
	@Getter
	private WaterConfiguration waterConfiguration;

	public Water(int patches, GLShaderProgram shader, GLShaderProgram wireframeShader)
	{		
		waterConfiguration = new WaterConfiguration();
		waterConfiguration.loadFile("water-config.properties");
		
		GLPatchVBO meshBuffer = new GLPatchVBO();
		meshBuffer.addData(MeshGenerator.generatePatch2D4x4(patches),16);
		
		renderConfig = new WaterRenderParameter();
		
		GLRenderInfo renderInfo = new GLRenderInfo(shader,renderConfig,meshBuffer);
		GLRenderInfo wireframeRenderInfo = new GLRenderInfo(wireframeShader,renderConfig,meshBuffer);
		
		dudv = new Texture2DTrilinearFilter("textures/water/dudv/dudv1.jpg");
		caustics = new Texture2DTrilinearFilter("textures/water/caustics/caustics.jpg");
		
		addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
		addComponent(NodeComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);

		fft = new FFT(waterConfiguration.getN(), waterConfiguration.getL(),
				waterConfiguration.getAmplitude(), waterConfiguration.getWindDirection(),
				waterConfiguration.getWindSpeed(), waterConfiguration.getCapillarWavesSupression());
		fft.setT_delta(waterConfiguration.getDelta_T());
		fft.setChoppy(waterConfiguration.isChoppy());
		fft.init();
		
		normalmapRenderer = new NormalRenderer(waterConfiguration.getN());
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
			EngineContext.getConfig().setRenderUnderwater(false);
		}
		else {
			EngineContext.getConfig().setRenderUnderwater(true);
		}
			
		distortion += waterConfiguration.getDistortion();
		motion += waterConfiguration.getWaveMotion();
		
		Scenegraph scenegraph = ((Scenegraph) getParentNode());
		
		EngineContext.getConfig().setClipplane(getClipplane());
			
		//mirror scene to clipplane
		scenegraph.getWorldTransform().setScaling(1,-1,1);
			
		if (scenegraph.hasTerrain()){
				
			GLContext.getObject(TerrainConfiguration.class).setScaleY(
					GLContext.getObject(TerrainConfiguration.class).getScaleY() * -1f);
			GLContext.getObject(TerrainConfiguration.class).setWaterReflectionShift(
					(int) (getClipplane().getW() * 2f));
		}
		
		scenegraph.update();
		
		//render reflection to texture

		glViewport(0,0,EngineContext.getWindow().getWidth()/2, EngineContext.getWindow().getHeight()/2);
		
		EngineContext.getConfig().setRenderReflection(true);
		
		reflectionRenderer.getFbo().bind();
		renderConfig.clearScreenDeepOcean();
		glFrontFace(GL_CCW);
		
		if (!isCameraUnderwater()){
			scenegraph.getRoot().render();
			if (scenegraph.hasTerrain()){
				((GLTerrain) scenegraph.getTerrain()).render();
			}
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish(); 
		glFrontFace(GL_CW);
		reflectionRenderer.getFbo().unbind();
		reflectionRenderer.render();
		
		EngineContext.getConfig().setRenderReflection(false);
		
		// antimirror scene to clipplane
	
		scenegraph.getWorldTransform().setScaling(1,1,1);

		if (scenegraph.hasTerrain()){
			GLContext.getObject(TerrainConfiguration.class).setScaleY(
					GLContext.getObject(TerrainConfiguration.class).getScaleY() / -1f);
			GLContext.getObject(TerrainConfiguration.class).setWaterReflectionShift(0);
		}

		scenegraph.update();
		
		// render to refraction texture
		EngineContext.getConfig().setRenderRefraction(true);
		
		refractionRenderer.getFbo().bind();
		renderConfig.clearScreenDeepOcean();
	
		scenegraph.getRoot().render();
		if (scenegraph.hasTerrain()){
			((GLTerrain) scenegraph.getTerrain()).render();
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
		refractionRenderer.getFbo().unbind();
		refractionRenderer.render();
		
		EngineContext.getConfig().setRenderRefraction(false);
		
		glDisable(GL_CLIP_DISTANCE6);
		EngineContext.getConfig().setClipplane(Constants.ZEROPLANE);	
	
		glViewport(0,0,EngineContext.getWindow().getWidth(), EngineContext.getWindow().getHeight());
		
		fft.render();
		normalmapRenderer.render(fft.getDy());
		
		GLContext.getResources().getDeferredFbo().bind();
		
		if (EngineContext.getConfig().isRenderWireframe())
		{
			getComponents().get(NodeComponentType.WIREFRAME_RENDERINFO).render();
		}
		else
		{
			getComponents().get(NodeComponentType.MAIN_RENDERINFO).render();
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
		GLContext.getResources().getDeferredFbo().unbind();
	}
		
	public Vec4f getClipplane() {
		return clipplane;
	}

	public void setClipplane(Vec4f clipplane) {
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

	public NormalRenderer getNormalmapRenderer() {
		return normalmapRenderer;
	}

	public void setNormalmapRenderer(NormalRenderer normalmapRenderer) {
		this.normalmapRenderer = normalmapRenderer;
	}
	
	public void setCameraUnderwater(boolean cameraUnderwater) {
		this.cameraUnderwater = cameraUnderwater;
	}

	public boolean isCameraUnderwater() {
		return cameraUnderwater;
	}

	
	public GLTexture getRefractionTexture(){
		return refractionRenderer.getDeferredLightingSceneTexture();
	}
	
	public GLTexture getReflectionTexture(){
		return reflectionRenderer.getDeferredLightingSceneTexture();
	}

	public GLTexture getDudv() {
		return dudv;
	}

	public GLTexture getCaustics() {
		return caustics;
	}
}
