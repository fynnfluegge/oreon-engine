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
import engine.core.Constants;
import engine.core.Input;
import engine.core.Util;
import engine.main.OpenGLDisplay;
import engine.main.RenderingEngine;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Scenegraph;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.shaders.water.OceanBRDF;
import engine.shaders.water.OceanGrid;
import engine.textures.Texture;
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
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.glViewport;

public class Water extends GameObject{
	
	private Quaternion clipplane;
	private float clip_offset = 10;
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
	private Texture dudv;
	private Texture reflectionTexture;
	private Texture refractionTexture;
	private Framebuffer reflectionFBO;
	private Framebuffer refractionFBO;
	
	private OceanFFT fft;
	private NormalMapRenderer normalmapRenderer;
	
	private Scenegraph scenegraph;
	
	public Water(int patches, int fftResolution)
	{
		PatchVAO meshBuffer = new PatchVAO();
		meshBuffer.addData(generatePatch2D4x4(patches),16);
		setRenderInfo(new RenderInfo( new Default(), OceanBRDF.getInstance()));
		Renderer renderer = new Renderer(OceanBRDF.getInstance(), meshBuffer);
		
		dudv = new Texture("./res/textures/water/dudv/dudv1.jpg");
		dudv.bind();
		dudv.mipmap();
		
		addComponent("Renderer", renderer);

		fft = new OceanFFT(fftResolution); 
		fft.init();
		normalmapRenderer = new NormalMapRenderer(fftResolution);
		
		reflectionTexture = new Texture();
		reflectionTexture.generate();
		reflectionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, OpenGLDisplay.getInstance().getLwjglWindow().getWidth()/2, OpenGLDisplay.getInstance().getLwjglWindow().getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		reflectionFBO = new Framebuffer();
		reflectionFBO.bind();
		reflectionFBO.setDrawBuffer(0);
		reflectionFBO.colorTextureAttachment(reflectionTexture.getId(), 0);
		reflectionFBO.checkStatus();
		reflectionFBO.unbind();
		
		refractionTexture = new Texture();
		refractionTexture.generate();
		refractionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, OpenGLDisplay.getInstance().getLwjglWindow().getWidth()/2, OpenGLDisplay.getInstance().getLwjglWindow().getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	
		refractionFBO = new Framebuffer();
		refractionFBO.bind();
		refractionFBO.setDrawBuffer(0);
		refractionFBO.colorTextureAttachment(refractionTexture.getId(), 0);
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
		if (RenderingEngine.isGrid())
		{
			((Renderer) getComponents().get("Renderer")).setShader(OceanGrid.getInstance());
		}
		
		else if (!RenderingEngine.isGrid())
		{
			((Renderer) getComponents().get("Renderer")).setShader(OceanBRDF.getInstance());
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

	
	public void render()
	{
	
		if (!Input.isPause()){
			distortion += getDistortionOffset();
			motion += getMotionOffset();
		}
		clipplane.setW(getTransform().getTranslation().getY() + clip_offset);
		
		Scenegraph scenegraph = ((Scenegraph) getParent());
		
		RenderingEngine.setClipplane(getClipplane());
			
		//mirror scene to clipplane
			
		// TODO mirror transformation
		
		scenegraph.getRoot().getTransform().setScaling(1,-1,1);
		scenegraph.getRoot().getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
				(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));
		
		// prevent reflesction distortion overlap
//		skySphere.getTransform().setScaling(1.1f,-1,1.1f);
//		skySphere.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
//			(skySphere.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));
			
		synchronized(Terrain.getLock()){
//			if (terrain != null){
//				terrain.getTerrainConfiguration().setScaleY(terrain.getTerrainConfiguration().getScaleY() * -1f);
//				terrain.getTransform().getLocalTranslation().setY(RenderingEngine.getClipplane().getW() - 
//					(terrain.getTransform().getLocalTranslation().getY() - RenderingEngine.getClipplane().getW()));
//			}
			
			// TODO prevent update terrain Quadtree in this update call;
			
			scenegraph.update();
			
			//render reflection to texture

			glViewport(0,0,OpenGLDisplay.getInstance().getLwjglWindow().getWidth()/2, OpenGLDisplay.getInstance().getLwjglWindow().getHeight()/2);
			
			this.getReflectionFBO().bind();
			RenderConfig.clearScreenDeepOceanReflection();
			glFrontFace(GL_CCW);
			scenegraph.getRoot().render();
			scenegraph.getTerrain().render();
			glFinish(); //important, prevent conflicts with following compute shaders
			glFrontFace(GL_CW);
			this.getReflectionFBO().unbind();
			
			// antimirror scene to clipplane
		
			scenegraph.getRoot().getTransform().setScaling(1,1,1);
			scenegraph.getRoot().getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - 
					(scenegraph.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));
//			if (terrain != null){
//				terrain.getTerrainConfiguration().setScaleY(terrain.getTerrainConfiguration().getScaleY()/ -1f);
//				terrain.getTransform().getLocalTranslation().setY(RenderingEngine.getClipplane().getW() + 
//					(RenderingEngine.getClipplane().getW() - terrain.getTransform().getLocalTranslation().getY()));
//			}
			scenegraph.update();
			
			// render to refraction texture
			
			this.getRefractionFBO().bind();
			RenderConfig.clearScreenDeepOceanRefraction();
			scenegraph.getRoot().render();
//			if (terrain != null){
//				terrain.render();
//			}
			glFinish(); //important, prevent conflicts with following compute shaders
			this.getRefractionFBO().unbind();
		}
			
		RenderingEngine.setClipplane(Constants.PLANE0);	
	
		glViewport(0,0,OpenGLDisplay.getInstance().getLwjglWindow().getWidth(), OpenGLDisplay.getInstance().getLwjglWindow().getHeight());
		
		OpenGLDisplay.getInstance().getFBO().bind();
		RenderConfig.clearScreen();
		fft.render();
		normalmapRenderer.render(fft.getDy());
		getComponents().get("Renderer").render();
		
		scenegraph.getRoot().render();
		
		glFinish(); //important, prevent conflicts with following compute shaders
		OpenGLDisplay.getInstance().getFBO().unbind();
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

	public Texture getReflectionTexture() {
		return reflectionTexture;
	}

	public void setReflectionTexture(Texture reflectionTexture) {
		this.reflectionTexture = reflectionTexture;
	}

	public Texture getRefractionTexture() {
		return refractionTexture;
	}

	public void setRefractionTexture(Texture refractionTexture) {
		this.refractionTexture = refractionTexture;
	}

	public float getTessellationShift() {
		return tessellationShift;
	}

	public void setTessellationShift(float tessellationShift) {
		this.tessellationShift = tessellationShift;
	}

	public Texture getDudv() {
		return dudv;
	}

	public void setDudv(Texture dudv) {
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
}
