package modules.terrain.fractals;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_BLUE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import java.nio.FloatBuffer;

import engine.math.Vec2f;
import engine.textures.Texture2D;
import engine.utils.BufferAllocation;
import engine.utils.Constants;
import modules.gpgpu.NormalMapRenderer;

public class FractalMaps {
	
	private int N;
	private float amplitude; 
	private float l;
	private Texture2D heightmap;
	private Texture2D normalmap;
	private FloatBuffer heightDataBuffer;
	private FloatBuffer slopeDataBuffer;
	private int scaling;
	private float strength;
	private int random;
	private int normalStrength;
	
	public FractalMaps(int N, float amplitude, float l, int scaling, float strength, int normalStrength, int random){
		
		this.scaling = scaling;
		this.strength = strength;
		this.setAmplitude(amplitude);
		this.random = random;
		this.l = l;
		this.N = N;
		this.normalStrength = normalStrength;
		int L = 1000;
		int v = 100;
		this.scaling = scaling;
		this.strength = strength;
		Vec2f w = new Vec2f(1,3).normalize();
		FractalFFT fft = new FractalFFT(N,L,amplitude,v,w,l);
		fft.setT(random);
		fft.init();
		fft.render();
		NormalMapRenderer normalmapRenderer = new NormalMapRenderer(N);
		normalmapRenderer.setStrength(normalStrength);
		normalmapRenderer.render(fft.getHeightmap());
		heightmap = fft.getHeightmap();
		normalmap = normalmapRenderer.getNormalmap();
		heightDataBuffer = BufferAllocation.createFloatBuffer(Constants.TERRAIN_FRACTALS_RESOLUTION * Constants.TERRAIN_FRACTALS_RESOLUTION);
		heightmap.bind();
		glGetTexImage(GL_TEXTURE_2D,0,GL_RED,GL_FLOAT,heightDataBuffer);
		slopeDataBuffer = BufferAllocation.createFloatBuffer(Constants.TERRAIN_FRACTALS_RESOLUTION * Constants.TERRAIN_FRACTALS_RESOLUTION);
		normalmap.bind();
		glGetTexImage(GL_TEXTURE_2D,0,GL_BLUE,GL_FLOAT,slopeDataBuffer);
	}

	public Texture2D getHeightmap() {
		return heightmap;
	}

	public void setHeightmap(Texture2D heightmap) {
		this.heightmap = heightmap;
	}

	public Texture2D getNormalmap() {
		return normalmap;
	}

	public void setNormalmap(Texture2D normalmap) {
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

	public int getRandom() {
		return random;
	}

	public void setRandom(int random) {
		this.random = random;
	}

	public float getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(float amplitude) {
		this.amplitude = amplitude;
	}

	public float getL() {
		return l;
	}

	public int getN() {
		return N;
	}

	public int getNormalStrength() {
		return normalStrength;
	}

	public void setNormalStrength(int normalStrength) {
		this.normalStrength = normalStrength;
	}

	public FloatBuffer getSlopeDataBuffer() {
		return slopeDataBuffer;
	}

	public void setSlopeDataBuffer(FloatBuffer slopeDataBuffer) {
		this.slopeDataBuffer = slopeDataBuffer;
	}
}
