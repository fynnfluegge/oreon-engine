package modules.water;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;

import modules.gpgpu.NormalMapRenderer;
import modules.terrain.Terrain;
import modules.water.fft.OceanFFT;
import engine.buffers.Framebuffer;
import engine.buffers.PatchVAO;
import engine.configs.Default;
import engine.configs.RenderConfig;
import engine.core.Input;
import engine.core.Window;
import engine.core.RenderingEngine;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Scenegraph;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.shader.water.OceanBRDFShader;
import engine.textures.Texture2D;
import engine.utils.Constants;
import engine.utils.Util;

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
	private Texture2D reflectionTexture;
	private Texture2D refractionTexture;
	private Texture2D refractionDepthTexture;
	private Framebuffer reflectionFBO;
	private Framebuffer refractionFBO;
	
	private OceanFFT fft;
	private NormalMapRenderer normalmapRenderer;
	
	private Scenegraph scenegraph;
	
	public Water(int patches, int fftResolution)
	{		
		PatchVAO meshBuffer = new PatchVAO();
		meshBuffer.addData(generatePatch2D4x4(patches),16);
		setRenderInfo(new RenderInfo(new Default(), OceanBRDFShader.getInstance()));
		Renderer renderer = new Renderer(OceanBRDFShader.getInstance(), meshBuffer);
		
		dudv = new Texture2D("./res/textures/water/dudv/dudv1.jpg");
		dudv.bind();
		dudv.trilinearFilter();
		
		addComponent("Renderer", renderer);

		fft = new OceanFFT(fftResolution); 
		fft.init();
		normalmapRenderer = new NormalMapRenderer(fftResolution);
		
		reflectionTexture = new Texture2D();
		reflectionTexture.generate();
		reflectionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getInstance().getWidth()/2, Window.getInstance().getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		reflectionFBO = new Framebuffer();
		reflectionFBO.bind();
		reflectionFBO.setDrawBuffer(0);
		reflectionFBO.createColorTextureAttachment(reflectionTexture.getId(), 0);
		reflectionFBO.createDepthbufferAttachment(Window.getInstance().getWidth()/2, Window.getInstance().getHeight()/2);
		reflectionFBO.checkStatus();
		reflectionFBO.unbind();
		
		refractionTexture = new Texture2D();
		refractionTexture.generate();
		refractionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getInstance().getWidth()/2, Window.getInstance().getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	
		refractionFBO = new Framebuffer();
		refractionFBO.bind();
		refractionFBO.setDrawBuffer(0);
		refractionFBO.createColorTextureAttachment(refractionTexture.getId(), 0);
		refractionFBO.createDepthbufferAttachment(Window.getInstance().getWidth()/2, Window.getInstance().getHeight()/2);
		refractionFBO.checkStatus();
		refractionFBO.unbind();		
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
	
	public void update()
	{
//		if (RenderingEngine.isGrid())
//		{
//			getRenderInfo().setShader(OceanGridShader.getInstance());
//		}
//		else if (!RenderingEngine.isGrid())
//		{
//			getRenderInfo().setShader(OceanBRDFShader.getInstance());
//		}
//		getComponents().get("Renderer").setShader(getRenderInfo().getShader());
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

	
	public void render()
	{
		glEnable(GL_CLIP_DISTANCE6);
		
		if (!Input.isPause()){
			distortion += getDistortionOffset();
			motion += getMotionOffset();
		}
		
		Scenegraph scenegraph = ((Scenegraph) getParent());
		
		RenderingEngine.setClipplane(getClipplane());
			
		//mirror scene to clipplane
			
		// TODO mirror transformation
		
		scenegraph.getTransform().setScaling(1,-1,1);
		scenegraph.getTransform().getTranslation().setY(50);
//		scenegraph.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
//				(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));
			
		synchronized(Terrain.getLock()){
			
			Terrain terrain = (Terrain) scenegraph.getTerrain();
			terrain.getTerrainConfiguration().setScaleY(terrain.getTerrainConfiguration().getScaleY() * -1f);
			terrain.getTerrainConfiguration().setWaterReflectionShift((int) (getClipplane().getW() * 2f));
			
			// TODO prevent update terrain Quadtree in this update call;
			
			scenegraph.update();
			
			//render reflection to texture

			glViewport(0,0,Window.getInstance().getWidth()/2, Window.getInstance().getHeight()/2);
			RenderingEngine.setReflection(true);
			
			this.getReflectionFBO().bind();
			RenderConfig.clearScreenDeepOceanReflection();
			glFrontFace(GL_CCW);
			scenegraph.getRoot().render();
			scenegraph.getTerrain().render();
			glFinish(); //important, prevent conflicts with following compute shaders
			glFrontFace(GL_CW);
			this.getReflectionFBO().unbind();
			
			// antimirror scene to clipplane
		
			scenegraph.getTransform().setScaling(1,1,1);
			scenegraph.getTransform().getTranslation().setY(0);
//			scenegraph.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
//					(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));

			terrain.getTerrainConfiguration().setScaleY(terrain.getTerrainConfiguration().getScaleY() / -1f);
			terrain.getTerrainConfiguration().setWaterReflectionShift(0);

			scenegraph.update();
			
			// render to refraction texture
			RenderingEngine.setReflection(false);
			
			this.getRefractionFBO().bind();
			RenderConfig.clearScreenDeepOceanRefraction();
			scenegraph.getRoot().render();
			scenegraph.getTerrain().render();
			
			glFinish(); //important, prevent conflicts with following compute shaders
			this.getRefractionFBO().unbind();
		}
		
		glDisable(GL_CLIP_DISTANCE6);
		RenderingEngine.setClipplane(Constants.PLANE0);	
	
		glViewport(0,0,Window.getInstance().getWidth(), Window.getInstance().getHeight());
		
		fft.render();
		normalmapRenderer.render(fft.getDy());
		
		Window.getInstance().getMultisampledFbo().bind();
		getComponents().get("Renderer").render();
		glFinish(); //important, prevent conflicts with following compute shaders
		Window.getInstance().getMultisampledFbo().unbind();
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
	
	public float getDistorion() {
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

	public Framebuffer getReflectionFBO() {
		return reflectionFBO;
	}

	public void setReflectionFBO(Framebuffer reflectionFBO) {
		this.reflectionFBO = reflectionFBO;
	}

	public Framebuffer getRefractionFBO() {
		return refractionFBO;
	}

	public void setRefractionFBO(Framebuffer refractionFBO) {
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

	public Scenegraph getScenegraph() {
		return scenegraph;
	}

	public void setScenegraph(Scenegraph scenegraph) {
		this.scenegraph = scenegraph;
	}

	public Texture2D getRefractionDepthTexture() {
		return refractionDepthTexture;
	}

	public void setRefractionDepthTexture(Texture2D refractionDepthTexture) {
		this.refractionDepthTexture = refractionDepthTexture;
	}
}
