package org.oreon.gl.components.terrain;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.fft.FFT;

import lombok.Getter;

@Getter
public class FractalMap {
	
	private GLTexture heightmap;
	private int scaling;
	private float strength;
	private boolean choppy;
	
	private int N;
	private int L;
	private float amplitude;
	private Vec2f direction;
	private float intensity;
	private float capillarSuppression;
	
	
	public FractalMap(int N, int L, float amplitude, Vec2f direction, float intensity,
			float capillarSuppression, int scaling, float strength, int random){
		
		this.scaling = scaling;
		this.strength = strength;
		this.scaling = scaling;
		FFT fft = new FFT(N,L,amplitude,direction,intensity,capillarSuppression);
		fft.setT(random);
		fft.init();
		fft.render();
		heightmap = fft.getDy();
	}

}
