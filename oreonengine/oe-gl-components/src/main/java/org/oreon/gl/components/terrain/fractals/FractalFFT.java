package org.oreon.gl.components.terrain.fractals;

import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.fft.FFT;

public class FractalFFT extends FFT{

	public FractalFFT(int N, int L, float amplitude, Vec2f direction, float alignment, 
			float intensity, float capillarSupressFactor)
	{
		super(N, L, amplitude, direction, alignment, intensity, capillarSupressFactor);
		
		inversionShader = FractalFFTInversionShader.getInstance();
		h0k = new FractalH0k(N, L, amplitude, direction, alignment, intensity, capillarSupressFactor);
		hkt = new FractalHkt(N, L);
	}
	
}
