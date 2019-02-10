package org.oreon.gl.components.terrain.fractals;

import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.fft.H0k;

public class FractalH0k extends H0k{

	public FractalH0k(int N, int L, float amplitude, Vec2f direction, float intensity, float capillarSupressFactor) {
		super(N, L, amplitude, direction, intensity, capillarSupressFactor);
		
		shader = FractalH0kShader.getInstance();
	}

}
