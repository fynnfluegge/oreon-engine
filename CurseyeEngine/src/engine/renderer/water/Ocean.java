package engine.renderer.water;

import java.nio.ByteBuffer;

import engine.core.Constants;
import engine.core.Input;
import engine.core.Texture;
import engine.core.Vertex;
import engine.core.Window;
import engine.gameObject.GameObject;
import engine.gameObject.components.Model;
import engine.gameObject.components.PatchRenderer;
import engine.gameObject.components.Renderer;
import engine.main.RenderingEngine;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.models.data.Material;
import engine.models.data.Patch;
import engine.renderpipeline.configs.Default;
import engine.renderpipeline.data.Framebuffer;
import engine.renderpipeline.data.PatchVAO;
import engine.renderpipeline.shaderPrograms.ocean.OceanGrid;
import engine.renderpipeline.shaderPrograms.ocean.OceanBRDF;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

public class Ocean extends GameObject{
	
	private Quaternion clipplane;
	private float clip_offset;
	private float distortionOffset;
	private float motionOffset;
	private float displacementScale;
	private float choppiness;
	private int tessellationFactor;
	private float tessellationShift;
	private float tessellationSlope;
	private int texDetail;
	private float sightRangeFactor;
	private float shininess = 80;
	private float emission = 1;
	private float kReflection;
	private float kRefraction;
	private int patchAmount = 128;	
	private float distortion;
	private float motion;
	private Texture reflectionTexture;
	private Texture refractionTexture;
	private Framebuffer reflectionFBO;
	private Framebuffer refractionFBO;
	private FastFourierTransform fft;
	
	
	public Ocean()
	{
		PatchVAO meshBuffer = new PatchVAO();
		Model model = new Model(new Patch(generatePatchs4x4()));
		PatchRenderer renderer = new PatchRenderer(meshBuffer, OceanBRDF.getInstance(), new Default());
		meshBuffer.addData(model.getPatch(),16);
		
		Material material = new Material();
		material.setDiffusemap(new Texture("./res/textures/water/dudv/water1.jpg"));
		material.getDiffusemap().bind();
		material.getDiffusemap().mipmap();
		material.setColor(new Vec3f(0.1f,0.2f,0.6f));
		model.setMaterial(material);
		
		addComponent("Model", model);
		addComponent("Renderer", renderer);
		
		getTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getTransform().setTranslation(-Constants.ZFAR/2,200,-Constants.ZFAR/2);
		
		this.clipplane = new Quaternion(0,-1,0,getTransform().getTranslation().getY());
		
		fft = new FastFourierTransform();
		fft.init();
		
		reflectionTexture = new Texture();
		reflectionTexture.generate();
		reflectionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth()/2, Window.getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		reflectionFBO = new Framebuffer();
		reflectionFBO.bind();
		reflectionFBO.setDrawBuffer(0);
		reflectionFBO.colorTextureAttachment(reflectionTexture.getId(), 0);
		reflectionFBO.depthbufferAttachment(Window.getWidth()/2, Window.getHeight()/2);
		reflectionFBO.checkStatus();
		reflectionFBO.unbind();
		
		refractionTexture = new Texture();
		refractionTexture.generate();
		refractionTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth()/2, Window.getHeight()/2, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	
		refractionFBO = new Framebuffer();
		refractionFBO.bind();
		refractionFBO.setDrawBuffer(0);
		refractionFBO.colorTextureAttachment(refractionTexture.getId(), 0);
		refractionFBO.depthbufferAttachment(Window.getWidth()/2, Window.getHeight()/2);
		refractionFBO.checkStatus();
		refractionFBO.unbind();
	}
	
	public Vertex[] generatePatchs4x4()
	{
		
		int amountx = patchAmount; 
		int amounty = patchAmount;
		
		// 16 vertices for each patch
		Vertex[] vertices = new Vertex[amountx * amounty * 16];
		
		int index = 0;
		float dx = 1f/amountx;
		float dy = 1f/amounty;
		
		for (float i=0; i<1; i+=dx)
		{
			for (float j=0; j<1; j+=dy)
			{	
				vertices[index++] = new Vertex(new Vec3f(i,0,j), new Vec2f(i,1-j));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j), new Vec2f(i+dx,1-j));
				
				vertices[index++] = new Vertex(new Vec3f(i,0,j+dy*0.33f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j+dy*0.33f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j+dy*0.33f));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j+dy*0.33f));
				
				vertices[index++] = new Vertex(new Vec3f(i,0,j+dy*0.66f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j+dy*0.66f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j+dy*0.66f));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j+dy*0.66f));
				
				vertices[index++] = new Vertex(new Vec3f(i,0,j+dy),  new Vec2f(i,1-j-dy));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j+dy));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j+dy));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j+dy), new Vec2f(i+dx,1-j-dy));

			}
		}
		
		return vertices;
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
			OceanBRDF.getInstance().setDistortion(distortion);
		}
	}
	
	public void render()
	{
		fft.render();
		fft.renderNormalmap();
		getComponents().get("Renderer").render();
	}

	public Quaternion getClipplane() {
		return clipplane;
	}

	public void setClipplane(Quaternion clipplane) {
		this.clipplane = clipplane;
	}

	public FastFourierTransform getFFT() {
		return fft;
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

	public int getPatchAmount() {
		return patchAmount;
	}

	public void setPatchAmount(int patchAmount) {
		this.patchAmount = patchAmount;
	}

	public float getClip_offset() {
		return clip_offset;
	}

	public void setClip_offset(float clip_offset) {
		this.clip_offset = clip_offset;
	}

	public float getSightRangeFactor() {
		return sightRangeFactor;
	}

	public void setSightRangeFactor(float sightRangeFactor) {
		this.sightRangeFactor = sightRangeFactor;
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
}
