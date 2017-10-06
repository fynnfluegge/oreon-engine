package org.oreon.modules.gl.water;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;

import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.buffers.GLPatchVBO;
import org.oreon.core.gl.config.WaterConfig;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Quaternion;
import org.oreon.core.math.Vec2f;
import org.oreon.core.renderer.RenderInfo;
import org.oreon.core.renderer.Renderer;
import org.oreon.core.scene.GameObject;
import org.oreon.core.scene.Scenegraph;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.Util;
import org.oreon.modules.gl.gpgpu.NormalMapRenderer;
import org.oreon.modules.gl.terrain.Terrain;
import org.oreon.modules.gl.water.fft.OceanFFT;
import org.oreon.modules.gl.water.shader.OceanBRDFShader;
import org.oreon.modules.gl.water.shader.OceanGridShader;

import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE6;

public class Water extends GameObject{
	
	private Quaternion clipplane;
	private float clip_offset;
	private float distortionOffset;
	private float motionOffset;
	private float motion;
	private float distortion;
	private float displacementScale;
	private float choppiness;
	private int tessellationFactor;
	private float tessellationShift;
	private float tessellationSlope;
	private int largeDetailRange;
	private int texDetail;
	private float shininess;
	private float emission;
	private float kReflection;
	private float kRefraction;	
	private Texture2D dudv;
	private Texture2D caustics;
	private Texture2D reflectionTexture;
	private Texture2D refractionTexture;
	private Texture2D refractionDepthTexture;
	private GLFramebuffer reflectionFBO;
	private GLFramebuffer refractionFBO;
	
	private OceanFFT fft;
	private NormalMapRenderer normalmapRenderer;
	private boolean cameraUnderwater;
	
	private WaterConfig config;

	public Water(int patches, int fftResolution)
	{		
		GLPatchVBO meshBuffer = new GLPatchVBO();
		meshBuffer.addData(generatePatch2D4x4(patches),16);
		
		config = new WaterConfig();
		
		Renderer renderer = new Renderer(meshBuffer);
		renderer.setRenderInfo(new RenderInfo(config, OceanBRDFShader.getInstance()));
		
		dudv = new Texture2D("textures/water/dudv/dudv1.jpg");
		dudv.bind();
		dudv.trilinearFilter();
		
		addComponent("Renderer", renderer);

		fft = new OceanFFT(fftResolution); 
		fft.init();
		normalmapRenderer = new NormalMapRenderer(fftResolution);
		
		reflectionTexture = new Texture2D();
		reflectionTexture.generate();
		reflectionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, CoreSystem.getInstance().getWindow().getWidth()/2, CoreSystem.getInstance().getWindow().getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		reflectionFBO = new GLFramebuffer();
		reflectionFBO.bind();
		reflectionFBO.setDrawBuffer(0);
		reflectionFBO.createColorTextureAttachment(reflectionTexture.getId(), 0);
		reflectionFBO.createDepthbufferAttachment(CoreSystem.getInstance().getWindow().getWidth()/2, CoreSystem.getInstance().getWindow().getHeight()/2);
		reflectionFBO.checkStatus();
		reflectionFBO.unbind();
		
		refractionTexture = new Texture2D();
		refractionTexture.generate();
		refractionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, CoreSystem.getInstance().getWindow().getWidth()/2, CoreSystem.getInstance().getWindow().getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	
		refractionFBO = new GLFramebuffer();
		refractionFBO.bind();
		refractionFBO.setDrawBuffer(0);
		refractionFBO.createColorTextureAttachment(refractionTexture.getId(), 0);
		refractionFBO.createDepthbufferAttachment(CoreSystem.getInstance().getWindow().getWidth()/2, CoreSystem.getInstance().getWindow().getHeight()/2);
		refractionFBO.checkStatus();
		refractionFBO.unbind();	
	}
	
	
	
	public void update()
	{
		setCameraUnderwater(CoreSystem.getInstance().getScenegraph().getCamera().getPosition().getY() < (getWorldTransform().getTranslation().getY())); 
		if (CoreSystem.getInstance().getRenderingEngine().isGrid())
		{
			((Renderer) getComponent("Renderer")).getRenderInfo().setShader(OceanGridShader.getInstance());
		}
		else
		{
			((Renderer) getComponent("Renderer")).getRenderInfo().setShader(OceanBRDFShader.getInstance());
		}
	}
	
	public void render()
	{
		if (!isCameraUnderwater()){
			glEnable(GL_CLIP_DISTANCE6);
			CoreSystem.getInstance().getRenderingEngine().setCameraUnderWater(false);
		}
		else {
			CoreSystem.getInstance().getRenderingEngine().setCameraUnderWater(true);
		}
			
		distortion += getDistortionOffset();
		motion += getMotionOffset();
		
		Scenegraph scenegraph = ((Scenegraph) getParent());
		
		CoreSystem.getInstance().getRenderingEngine().setClipplane(getClipplane());
			
		//mirror scene to clipplane
			
		scenegraph.getWorldTransform().setScaling(1,-1,1);
		
		// TODO
//		scenegraph.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
//				(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));
			
		if (scenegraph.terrainExists()){
				
				Terrain terrain = (Terrain) scenegraph.getTerrain();
				terrain.getLowPolyConfiguration().setScaleY(terrain.getLowPolyConfiguration().getScaleY() * -1f);
				terrain.getLowPolyConfiguration().setWaterReflectionShift((int) (getClipplane().getW() * 2f));
		}
		
		scenegraph.update();
		
		//render reflection to texture

		glViewport(0,0,CoreSystem.getInstance().getWindow().getWidth()/2, CoreSystem.getInstance().getWindow().getHeight()/2);
		
		CoreSystem.getInstance().getRenderingEngine().setWaterReflection(true);
		
		this.getReflectionFBO().bind();
		config.clearScreenDeepOcean();
		glFrontFace(GL_CCW);
		
		if (!isCameraUnderwater()){
			scenegraph.getRoot().render();
			if (scenegraph.terrainExists()){
				((Terrain) scenegraph.getTerrain()).renderLowPoly();
			}
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish(); 
		glFrontFace(GL_CW);
		this.getReflectionFBO().unbind();
		CoreSystem.getInstance().getRenderingEngine().setWaterReflection(false);
		
		// antimirror scene to clipplane
	
		scenegraph.getWorldTransform().setScaling(1,1,1);

		// TODO
//		scenegraph.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
//				(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));

		if (scenegraph.terrainExists()){
				Terrain terrain = (Terrain) scenegraph.getTerrain();
				terrain.getLowPolyConfiguration().setScaleY(terrain.getLowPolyConfiguration().getScaleY() / -1f);
				terrain.getLowPolyConfiguration().setWaterReflectionShift(0);
		}

		scenegraph.update();
		
		// render to refraction texture
		CoreSystem.getInstance().getRenderingEngine().setWaterRefraction(true);
		this.getRefractionFBO().bind();
		
		config.clearScreenDeepOcean();
	
		scenegraph.getRoot().render();
		if (scenegraph.terrainExists()){
			((Terrain) scenegraph.getTerrain()).renderLowPoly();
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
		this.getRefractionFBO().unbind();
		CoreSystem.getInstance().getRenderingEngine().setWaterRefraction(false);
		
		glDisable(GL_CLIP_DISTANCE6);
		CoreSystem.getInstance().getRenderingEngine().setClipplane(Constants.PLANE0);	
	
		glViewport(0,0,CoreSystem.getInstance().getWindow().getWidth(), CoreSystem.getInstance().getWindow().getHeight());
		
		fft.render();
		normalmapRenderer.render(fft.getDy());
		
		CoreSystem.getInstance().getRenderingEngine().getMultisampledFbo().bind();
		
		getComponents().get("Renderer").render();
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
		CoreSystem.getInstance().getRenderingEngine().getMultisampledFbo().unbind();
	}
		
	public void loadSettingsFile(String file)
	{
		BufferedReader reader = null;
		
		try{
			if(new File(file).exists()){
				reader = new BufferedReader(new FileReader(file));
				String line;
				
				while((line = reader.readLine()) != null){
					
					String[] tokens = line.split(" ");
					tokens = Util.removeEmptyStrings(tokens);
					
					if(tokens.length == 0)
						continue;
					if(tokens[0].equals("displacementScale")){
						displacementScale = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("choppiness")){
						choppiness = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("distortion")){
						distortionOffset = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("wavemotion")){
						motionOffset = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("texDetail")){
						texDetail = Integer.valueOf(tokens[1]);
					}
					if(tokens[0].equals("tessellationFactor")){
						tessellationFactor = Integer.valueOf(tokens[1]);
					}
					if(tokens[0].equals("tessellationSlope")){
						tessellationSlope = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("tessellationShift")){
						tessellationShift = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("shininess")){
						shininess = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("emission")){
						emission = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("kReflection")){
						kReflection = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("kRefraction")){
						kRefraction = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("normalStrength")){
						getNormalmapRenderer().setStrength(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("detailRange")){
						largeDetailRange = Integer.valueOf(tokens[1]);
					}
					if(tokens[0].equals("delta_T")){
						getFft().setT_delta(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("choppy")){
						getFft().setChoppy(Integer.valueOf(tokens[1]) == 1 ? true : false);
					}
					
				}
				reader.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static Vec2f[] generatePatch2D4x4(int patches)
	{
		
		int amountx = patches; 
		int amounty = patches;
		
		// 16 vertices for each patch
		Vec2f[] vertices = new Vec2f[amountx * amounty * 16];
		
		int index = 0;
		float dx = 1f/amountx;
		float dy = 1f/amounty;
		
		for (float i=0; i<1; i+=dx)
		{
			for (float j=0; j<1; j+=dy)
			{	
				vertices[index++] = new Vec2f(i,j);
				vertices[index++] = new Vec2f(i+dx*0.33f,j);
				vertices[index++] = new Vec2f(i+dx*0.66f,j);
				vertices[index++] = new Vec2f(i+dx,j);
				
				vertices[index++] = new Vec2f(i,j+dy*0.33f);
				vertices[index++] = new Vec2f(i+dx*0.33f,j+dy*0.33f);
				vertices[index++] = new Vec2f(i+dx*0.66f,j+dy*0.33f);
				vertices[index++] = new Vec2f(i+dx,j+dy*0.33f);
				
				vertices[index++] = new Vec2f(i,j+dy*0.66f);
				vertices[index++] = new Vec2f(i+dx*0.33f,j+dy*0.66f);
				vertices[index++] = new Vec2f(i+dx*0.66f,j+dy*0.66f);
				vertices[index++] = new Vec2f(i+dx,j+dy*0.66f);
				
				vertices[index++] = new Vec2f(i,j+dy);
				vertices[index++] = new Vec2f(i+dx*0.33f,j+dy);
				vertices[index++] = new Vec2f(i+dx*0.66f,j+dy);
				vertices[index++] = new Vec2f(i+dx,j+dy);
			}
		}
		
		return vertices;
	}


	public Quaternion getClipplane() {
		return clipplane;
	}

	public void setClipplane(Quaternion clipplane) {
		this.clipplane = clipplane;
	}

	public int getTessellationFactor() {
		return tessellationFactor;
	}

	public void setTessellationFactor(int tessellationFactor) {
		this.tessellationFactor = tessellationFactor;
	}

	public float getTessellationSlope() {
		return tessellationSlope;
	}

	public void setTessellationSlope(float tessellationSlope) {
		this.tessellationSlope = tessellationSlope;
	}

	public float getShininess() {
		return shininess;
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public float getEmission() {
		return emission;
	}

	public void setEmission(float emission) {
		this.emission = emission;
	}

	public float getkReflection() {
		return kReflection;
	}

	public void setkReflection(float kReflection) {
		this.kReflection = kReflection;
	}

	public float getkRefraction() {
		return kRefraction;
	}

	public void setkRefraction(float kRefraction) {
		this.kRefraction = kRefraction;
	}

	public float getMotion() {
		return motion;
	}

	public void setMotion(float motion) {
		this.motion = motion;
	}

	public float getDistortionOffset() {
		return distortionOffset;
	}
	
	public float getDistortion() {
		return distortion;
	}

	public void setDistortionOffset(float distortionOffset) {
		this.distortionOffset = distortionOffset;
	}

	public float getMotionOffset() {
		return motionOffset;
	}

	public void setMotionOffset(float motionOffset) {
		this.motionOffset = motionOffset;
	}

	public float getDisplacementScale() {
		return displacementScale;
	}

	public void setDisplacementScale(float displacementScale) {
		this.displacementScale = displacementScale;
	}

	public int getTexDetail() {
		return texDetail;
	}

	public void setTexDetail(int texDetail) {
		this.texDetail = texDetail;
	}

	public float getClip_offset() {
		return clip_offset;
	}

	public void setClip_offset(float clip_offset) {
		this.clip_offset = clip_offset;
	}

	public float getChoppiness() {
		return choppiness;
	}

	public void setChoppiness(float choppiness) {
		this.choppiness = choppiness;
	}

	public GLFramebuffer getReflectionFBO() {
		return reflectionFBO;
	}

	public void setReflectionFBO(GLFramebuffer reflectionFBO) {
		this.reflectionFBO = reflectionFBO;
	}

	public GLFramebuffer getRefractionFBO() {
		return refractionFBO;
	}

	public void setRefractionFBO(GLFramebuffer refractionFBO) {
		this.refractionFBO = refractionFBO;
	}

	public Texture2D getReflectionTexture() {
		return reflectionTexture;
	}

	public void setReflectionTexture(Texture2D reflectionTexture) {
		this.reflectionTexture = reflectionTexture;
	}

	public Texture2D getRefractionTexture() {
		return refractionTexture;
	}

	public void setRefractionTexture(Texture2D refractionTexture) {
		this.refractionTexture = refractionTexture;
	}

	public float getTessellationShift() {
		return tessellationShift;
	}

	public void setTessellationShift(float tessellationShift) {
		this.tessellationShift = tessellationShift;
	}

	public Texture2D getDudv() {
		return dudv;
	}

	public void setDudv(Texture2D dudv) {
		this.dudv = dudv;
	}

	public int getLargeDetailRange() {
		return largeDetailRange;
	}

	public void setLargeDetailRange(int largeDetailRange) {
		this.largeDetailRange = largeDetailRange;
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

	public Texture2D getRefractionDepthTexture() {
		return refractionDepthTexture;
	}

	public void setRefractionDepthTexture(Texture2D refractionDepthTexture) {
		this.refractionDepthTexture = refractionDepthTexture;
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
}
