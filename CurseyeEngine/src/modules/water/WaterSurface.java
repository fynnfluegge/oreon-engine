package modules.water;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;

import engine.buffers.Framebuffer;
import engine.buffers.PatchVAO;
import engine.configs.Default;
import engine.core.Input;
import engine.core.Texture;
import engine.core.Util;
import engine.core.OpenGLWindow;
import engine.gameObject.GameObject;
import engine.gameObject.components.PatchRenderer;
import engine.gameObject.components.Renderer;
import engine.main.RenderingEngine;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.shaders.water.OceanBRDF;
import engine.shaders.water.OceanGrid;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

public class WaterSurface extends GameObject{
	
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
	private WaterMaps waterMaps;
	
	
	public WaterSurface(int patches)
	{
		PatchVAO meshBuffer = new PatchVAO();
		PatchRenderer renderer = new PatchRenderer(meshBuffer, OceanBRDF.getInstance(), new Default());
		meshBuffer.addData(generatePatch2D4x4(patches),16);
		
		dudv = new Texture("./res/textures/water/dudv/water1.jpg");
		dudv.bind();
		dudv.mipmap();
		
		addComponent("Renderer", renderer);

		waterMaps = new WaterMaps(256);
		
		reflectionTexture = new Texture();
		reflectionTexture.generate();
		reflectionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, OpenGLWindow.getWidth()/2, OpenGLWindow.getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		reflectionFBO = new Framebuffer();
		reflectionFBO.bind();
		reflectionFBO.setDrawBuffer(0);
		reflectionFBO.colorTextureAttachment(reflectionTexture.getId(), 0);
		reflectionFBO.depthbufferAttachment(OpenGLWindow.getWidth()/2, OpenGLWindow.getHeight()/2);
		reflectionFBO.checkStatus();
		reflectionFBO.unbind();
		
		refractionTexture = new Texture();
		refractionTexture.generate();
		refractionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, OpenGLWindow.getWidth()/2, OpenGLWindow.getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	
		refractionFBO = new Framebuffer();
		refractionFBO.bind();
		refractionFBO.setDrawBuffer(0);
		refractionFBO.colorTextureAttachment(refractionTexture.getId(), 0);
		refractionFBO.depthbufferAttachment(OpenGLWindow.getWidth()/2, OpenGLWindow.getHeight()/2);
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
						waterMaps.getNormalmapRenderer().setStrength(Float.valueOf(tokens[1]));
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
		if (!Input.isPause()){
			distortion += getDistortionOffset();
			motion += getMotionOffset();
		}
		clipplane.setW(getTransform().getTranslation().getY() + clip_offset);
		
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
		waterMaps.render();
		getComponents().get("Renderer").render();
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
	
	public WaterMaps getWaterMaps(){
		return this.waterMaps;
	}

	public int getLargeDetailRange() {
		return largeDetailRange;
	}

	public void setLargeDetailRange(int largeDetailRange) {
		this.largeDetailRange = largeDetailRange;
	}
}
