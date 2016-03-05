package engine.renderer.terrain.fractals;

import engine.core.Texture;
import engine.gpgpu.normalmap.NormalMapRenderer;
import engine.math.Vec2f;

public class FractalMaps {
	
	private Texture heightmap;
	private Texture normalmap;
	
	public FractalMaps(int N, float amplitude, float l){
		
		int L = 1000;
		int v = 100;
		Vec2f w = new Vec2f(1,3).normalize();
		FractalFFT fft = new FractalFFT(N,L,amplitude,v,w,l);
		fft.init();
		fft.render();
		NormalMapRenderer normalmapRenderer = new NormalMapRenderer(N);
		normalmapRenderer.setStrength(32);
		normalmapRenderer.render(fft.getHeightmap());
		heightmap = fft.getHeightmap();
		normalmap = normalmapRenderer.getNormalmap();
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
}
