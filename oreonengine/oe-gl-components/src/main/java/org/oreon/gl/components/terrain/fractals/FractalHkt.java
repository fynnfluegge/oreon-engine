package org.oreon.gl.components.terrain.fractals;

import org.oreon.gl.components.fft.Hkt;

public class FractalHkt extends Hkt{

	public FractalHkt(int N, int L) {
		super(N, L);
		
		shader = FractalHktShader.getInstance();
	}

}
