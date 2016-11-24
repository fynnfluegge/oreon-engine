package modules.terrain.fractals;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;


import java.nio.FloatBuffer;

import engine.buffers.BufferAllocation;
import engine.math.Vec2f;
import engine.textures.Texture;
import modules.gpgpu.NormalMapRenderer;

public class FractalMaps {
	
	private Texture heightmap;
	private Texture normalmap;
	private FloatBuffer heightDataBuffer;
	private int scaling;
	private float strength;
	
	public FractalMaps(int N, float amplitude, float l, int scaling, float strength){
		
		int L = 1000;
		int v = 100;
		this.scaling = scaling;
		this.strength = strength;
		Vec2f w = new Vec2f(1,3).normalize();
		FractalFFT fft = new FractalFFT(N,L,amplitude,v,w,l);
		fft.init();
		fft.render();
		NormalMapRenderer normalmapRenderer = new NormalMapRenderer(N);
		normalmapRenderer.setStrength(32);
		normalmapRenderer.render(fft.getHeightmap());
		heightmap = fft.getHeightmap();
		normalmap = normalmapRenderer.getNormalmap();
		heightDataBuffer = BufferAllocation.createFloatBuffer(512*512);
		glGetTexImage(GL_TEXTURE_2D,1,GL_RGBA32F,0,heightDataBuffer);
	}

	public Texture getHeightmap() {
		return heightmap;
	}

	public void setHeightmap(Texture heightmap) {
		this.heightmap = heightmap;
	}

	public Texture getNormalmap() {
		return normalmap;
	}

	public void setNormalmap(Texture normalmap) {
		this.normalmap = normalmap;
	}

	public int getScaling() {
		return scaling;
	}

	public float getStrength() {
		return strength;
	}
	
	public void setStrength(float s) {
		strength = s;
	}

	public FloatBuffer getHeightDataBuffer() {
		return heightDataBuffer;
	}

	public void setHeightDataBuffer(FloatBuffer heightDataBuffer) {
		this.heightDataBuffer = heightDataBuffer;
	}
}
