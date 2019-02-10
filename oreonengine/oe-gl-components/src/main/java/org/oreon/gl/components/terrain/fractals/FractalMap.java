package org.oreon.gl.components.terrain.fractals;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.fft.FFT;
import org.oreon.gl.components.util.NormalRenderer;

import lombok.Getter;

@Getter
public class FractalMap {
	
	private GLTexture heightmap;
	private GLTexture normalmap;
	private int scaling;
	private float heightStrength;
	private float normalStrength;
	private boolean choppy;
	
	private int N;
	private int L;
	private float amplitude;
	private Vec2f direction;
	private float intensity;
	private float capillar;
	
	private FFT fft;
	
	
	public FractalMap(int N, int L, float amplitude, Vec2f direction, float intensity,
			float capillar, int scaling, float heightstrength, float normalStrength, int random){
		
		this.N = N;
		this.scaling = scaling;
		this.heightStrength = heightstrength;
		this.normalStrength = normalStrength;
		this.scaling = scaling;
		fft = new FractalFFT(N,L,amplitude,direction,intensity,capillar);
		fft.setT(random);
	}
	
	public void render(){
		
		fft.init();
		fft.render();
		heightmap = fft.getDy();
		heightmap.bind();
		heightmap.bilinearFilter();
		NormalRenderer normalRenderer = new NormalRenderer(N);
		normalRenderer.setStrength(normalStrength);
		normalRenderer.render(heightmap);
		normalmap = normalRenderer.getNormalmap();
		normalmap.bind();
		normalmap.bilinearFilter();
	}

}
