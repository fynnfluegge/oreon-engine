package org.oreon.modules.gl.water;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.oreon.core.gl.buffers.GLPatchVBO;
import org.oreon.core.gl.config.WaterConfig;
import org.oreon.core.gl.shaders.GLShader;
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
import org.oreon.modules.gl.terrain.GLTerrain;
import org.oreon.modules.gl.water.fft.OceanFFT;
import org.oreon.modules.gl.water.shader.OceanGridShader;

import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glDisable;
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
	
	private RefracReflecRenderer refractionRenderer;
	private RefracReflecRenderer reflectionRenderer;
	
	private OceanFFT fft;
	private NormalMapRenderer normalmapRenderer;
	private boolean cameraUnderwater;
	
	private WaterConfig config;
	private GLShader shader;

	public Water(int patches, int fftResolution, GLShader shader)
	{		
		this.shader = shader;
		
		GLPatchVBO meshBuffer = new GLPatchVBO();
		meshBuffer.addData(generatePatch2D4x4(patches),16);
		
		config = new WaterConfig();
		
		Renderer renderer = new Renderer(meshBuffer);
		renderer.setRenderInfo(new RenderInfo(config, shader));
		
		dudv = new Texture2D("textures/water/dudv/dudv1.jpg");
		dudv.bind();
		dudv.trilinearFilter();
		
		caustics = new Texture2D("textures/water/caustics/caustics.jpg");
		caustics.bind();
		caustics.trilinearFilter();
		
		addComponent("Renderer", renderer);

		fft = new OceanFFT(fftResolution); 
		fft.init();
		normalmapRenderer = new NormalMapRenderer(fftResolution);
		
		refractionRenderer = new RefracReflecRenderer(CoreSystem.getInstance().getWindow().getWidth()/2,
													  CoreSystem.getInstance().getWindow().getHeight()/2);
		
		reflectionRenderer = new RefracReflecRenderer(CoreSystem.getInstance().getWindow().getWidth()/2,
												 	  CoreSystem.getInstance().getWindow().getHeight()/2);
	}	
	
	public void update()
	{
		setCameraUnderwater(CoreSystem.getInstance().getScenegraph().getCamera().getPosition().getY() < (getWorldTransform().getTranslation().getY())); 
		if (CoreSystem.getInstance().getRenderEngine().isGrid())
		{
			((Renderer) getComponent("Renderer")).getRenderInfo().setShader(OceanGridShader.getInstance());
		}
		else
		{
			((Renderer) getComponent("Renderer")).getRenderInfo().setShader(shader);
		}
	}
	
	public void render()
	{
		if (!isCameraUnderwater()){
			glEnable(GL_CLIP_DISTANCE6);
			CoreSystem.getInstance().getRenderEngine().setCameraUnderWater(false);
		}
		else {
			CoreSystem.getInstance().getRenderEngine().setCameraUnderWater(true);
		}
			
		distortion += getDistortionOffset();
		motion += getMotionOffset();
		
		Scenegraph scenegraph = ((Scenegraph) getParent());
		
		CoreSystem.getInstance().getRenderEngine().setClipplane(getClipplane());
			
		//mirror scene to clipplane
			
		scenegraph.getWorldTransform().setScaling(1,-1,1);
		
		// TODO
//		scenegraph.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
//				(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));
			
		if (scenegraph.terrainExists()){
				
				GLTerrain terrain = (GLTerrain) scenegraph.getTerrain();
				terrain.getLowPolyConfiguration().setScaleY(terrain.getLowPolyConfiguration().getScaleY() * -1f);
				terrain.getLowPolyConfiguration().setWaterReflectionShift((int) (getClipplane().getW() * 2f));
		}
		
		scenegraph.update();
		
		//render reflection to texture

		glViewport(0,0,CoreSystem.getInstance().getWindow().getWidth()/2, CoreSystem.getInstance().getWindow().getHeight()/2);
		
		CoreSystem.getInstance().getRenderEngine().setWaterReflection(true);
		
		reflectionRenderer.getFbo().bind();
		config.clearScreenDeepOcean();
		glFrontFace(GL_CCW);
		
		if (!isCameraUnderwater()){
			scenegraph.getRoot().render();
			if (scenegraph.terrainExists()){
				((GLTerrain) scenegraph.getTerrain()).renderLowPoly();
			}
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish(); 
		glFrontFace(GL_CW);
		reflectionRenderer.getFbo().unbind();
		reflectionRenderer.render();
		
		CoreSystem.getInstance().getRenderEngine().setWaterReflection(false);
		
		// antimirror scene to clipplane
	
		scenegraph.getWorldTransform().setScaling(1,1,1);

		// TODO
//		scenegraph.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
//				(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));

		if (scenegraph.terrainExists()){
				GLTerrain terrain = (GLTerrain) scenegraph.getTerrain();
				terrain.getLowPolyConfiguration().setScaleY(terrain.getLowPolyConfiguration().getScaleY() / -1f);
				terrain.getLowPolyConfiguration().setWaterReflectionShift(0);
		}

		scenegraph.update();
		
		// render to refraction texture
		CoreSystem.getInstance().getRenderEngine().setWaterRefraction(true);
		
		refractionRenderer.getFbo().bind();
		config.clearScreenDeepOcean();
	
		scenegraph.getRoot().render();
		if (scenegraph.terrainExists()){
			((GLTerrain) scenegraph.getTerrain()).renderLowPoly();
		}
		
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
		refractionRenderer.getFbo().unbind();
		refractionRenderer.render();
		
		CoreSystem.getInstance().getRenderEngine().setWaterRefraction(false);
		
		glDisable(GL_CLIP_DISTANCE6);
		CoreSystem.getInstance().getRenderEngine().setClipplane(Constants.PLANE0);	
	
		glViewport(0,0,CoreSystem.getInstance().getWindow().getWidth(), CoreSystem.getInstance().getWindow().getHeight());
		
		fft.render();
		normalmapRenderer.render(fft.getDy());
		
		CoreSystem.getInstance().getRenderEngine().getDeferredFbo().bind();
		
		getComponents().get("Renderer").render();
		// glFinish() important, to prevent conflicts with following compute shaders
		glFinish();
		CoreSystem.getInstance().getRenderEngine().getDeferredFbo().unbind();
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

	public GLShader getShader() {
		return shader;
	}

	public void setShader(GLShader shader) {
		this.shader = shader;
	}
	
	public Texture2D getRefractionTexture(){
		return refractionRenderer.getDeferredLightingSceneTexture();
	}
	
	public Texture2D getReflectionTexture(){
		return reflectionRenderer.getDeferredLightingSceneTexture();
	}
}
