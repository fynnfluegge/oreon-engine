package org.oreon.gl.components.water;

import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE6;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

import java.nio.IntBuffer;

import org.oreon.common.water.WaterConfiguration;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.memory.GLPatchVBO;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.parameter.WaterRenderParameter;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.math.Vec4f;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.scenegraph.Scenegraph;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.core.util.MeshGenerator;
import org.oreon.gl.components.fft.FFT;
import org.oreon.gl.components.terrain.GLTerrain;
import org.oreon.gl.components.util.NormalRenderer;

import lombok.Getter;

public class Water extends Renderable{
	
	private Vec4f clipplane;
	private int clip_offset;
	private float motion;
	private float distortion;
	private GLTexture dudv;
	
	private GLFramebuffer reflection_fbo;
	private GLTexture reflection_texture;
	private GLFramebuffer refraction_fbo;
	private GLTexture refraction_texture;
	
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
		GLContext.getResources().setWaterConfig(waterConfiguration);
		
		GLPatchVBO meshBuffer = new GLPatchVBO();
		meshBuffer.addData(MeshGenerator.generatePatch2D4x4(patches),16);
		
		renderConfig = new WaterRenderParameter();
		
		GLRenderInfo renderInfo = new GLRenderInfo(shader, renderConfig, meshBuffer);
		GLRenderInfo wireframeRenderInfo = new GLRenderInfo(wireframeShader, renderConfig, meshBuffer);
		
		dudv = new TextureImage2D("textures/water/dudv/dudv1.jpg", SamplerFilter.Trilinear);
		
		addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
		addComponent(NodeComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);

		fft = new FFT(waterConfiguration.getN(), waterConfiguration.getL(),
				waterConfiguration.getAmplitude(), waterConfiguration.getWindDirection(),
				waterConfiguration.getAlignment(), waterConfiguration.getWindSpeed(),
				waterConfiguration.getCapillarWavesSupression());
		fft.setT_delta(waterConfiguration.getT_delta());
		fft.setChoppy(waterConfiguration.isChoppy());
		fft.init();
		
		normalmapRenderer = new NormalRenderer(waterConfiguration.getN());
		getNormalmapRenderer().setStrength(waterConfiguration.getNormalStrength());
		
		reflection_texture = new TextureImage2D(BaseContext.getWindow().getWidth()/2,
				BaseContext.getWindow().getHeight()/2,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest);
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(1);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.flip();
		
		reflection_fbo = new GLFramebuffer();
		reflection_fbo.bind();
		reflection_fbo.createColorTextureAttachment(reflection_texture.getHandle(),0);
		reflection_fbo.createDepthBufferAttachment(BaseContext.getWindow().getWidth()/2,
				BaseContext.getWindow().getHeight()/2);
		reflection_fbo.setDrawBuffers(drawBuffers);
		reflection_fbo.checkStatus();
		reflection_fbo.unbind();
		
		refraction_texture = new TextureImage2D(BaseContext.getWindow().getWidth()/2,
				BaseContext.getWindow().getHeight()/2,
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest);
		
		refraction_fbo = new GLFramebuffer();
		refraction_fbo.bind();
		refraction_fbo.createColorTextureAttachment(refraction_texture.getHandle(),0);
		refraction_fbo.createDepthBufferAttachment(BaseContext.getWindow().getWidth()/2,
				BaseContext.getWindow().getHeight()/2);
		refraction_fbo.setDrawBuffers(drawBuffers);
		refraction_fbo.checkStatus();
		refraction_fbo.unbind();
	}	
	
	public void update()
	{
		setCameraUnderwater(BaseContext.getCamera().getPosition().getY() < (getWorldTransform().getTranslation().getY())); 
	}
	
	public void renderWireframe(){
		
		fft.render();
		
		super.renderWireframe();
		
		glFinish();
	}
	
	public void render()
	{
		if (!isCameraUnderwater()){
			glEnable(GL_CLIP_DISTANCE6);
			BaseContext.getConfig().setRenderUnderwater(false);
		}
		else {
			BaseContext.getConfig().setRenderUnderwater(true);
		}
			
		distortion += waterConfiguration.getDistortion();
		motion += waterConfiguration.getWaveMotion();
		
		Scenegraph scenegraph = ((Scenegraph) getParentNode());
		
		BaseContext.getConfig().setClipplane(getClipplane());
			
		//-----------------------------------//
		//     mirror scene to clipplane     //
		//-----------------------------------//
		
		scenegraph.getWorldTransform().setScaling(1,-1,1);
			
		if (scenegraph.hasTerrain()){
				
			GLTerrain.getConfiguration().setScaleY(
					GLTerrain.getConfiguration().getScaleY() * -1f);
			GLTerrain.getConfiguration().setReflectionOffset(
					getClip_offset() * 2);
		}
		scenegraph.update();
		
		//-----------------------------------//
		//    render reflection to texture   //
		//-----------------------------------//

		int tempScreenResolutionX = BaseContext.getConfig().getX_ScreenResolution(); 
		int tempScreenResolutionY = BaseContext.getConfig().getY_ScreenResolution(); 
		BaseContext.getConfig().setX_ScreenResolution(tempScreenResolutionX/2);
		BaseContext.getConfig().setY_ScreenResolution(tempScreenResolutionY/2);
		glViewport(0,0,tempScreenResolutionX/2, tempScreenResolutionY/2);
		
		BaseContext.getConfig().setRenderReflection(true);
		
		reflection_fbo.bind();
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
		reflection_fbo.unbind();
		
		BaseContext.getConfig().setRenderReflection(false);
		
		//-----------------------------------//
		//   antimirror scene to clipplane   //
		//-----------------------------------//
	
		scenegraph.getWorldTransform().setScaling(1,1,1);

		if (scenegraph.hasTerrain()){
			GLTerrain.getConfiguration().setScaleY(
					GLTerrain.getConfiguration().getScaleY() / -1f);
			GLTerrain.getConfiguration().setReflectionOffset(0);
		}

		scenegraph.update();
		
		//-----------------------------------//
		//    render refraction to texture   //
		//-----------------------------------//
		
		BaseContext.getConfig().setRenderRefraction(true);
		
		refraction_fbo.bind();
		renderConfig.clearScreenDeepOcean();
	
		scenegraph.getRoot().render();
		if (scenegraph.hasTerrain()){
			((GLTerrain) scenegraph.getTerrain()).render();
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
		refraction_fbo.unbind();
		
		//-----------------------------------//
		//     reset rendering settings      //
		//-----------------------------------//
		
		BaseContext.getConfig().setRenderRefraction(false);
		
		glDisable(GL_CLIP_DISTANCE6);
		BaseContext.getConfig().setClipplane(Constants.ZEROPLANE);	
	
		glViewport(0,0,tempScreenResolutionX, tempScreenResolutionY);
		BaseContext.getConfig().setX_ScreenResolution(tempScreenResolutionX);
		BaseContext.getConfig().setY_ScreenResolution(tempScreenResolutionY);
		
		GLContext.getResources().getPrimaryFbo().bind();
		
		//-----------------------------------//
		//            render FFT'S           //
		//-----------------------------------//
		fft.render();
		normalmapRenderer.render(fft.getDy());

		super.render();
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
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

	public int getClip_offset() {
		return clip_offset;
	}

	public void setClip_offset(int clip_offset) {
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
		return refraction_texture;
	}
	
	public GLTexture getReflectionTexture(){
		return reflection_texture;
	}

	public GLTexture getDudv() {
		return dudv;
	}

}
