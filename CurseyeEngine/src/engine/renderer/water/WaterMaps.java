package engine.renderer.water;

import engine.gpgpu.normalmap.NormalMapRenderer;
import engine.renderer.water.fft.OceanFFT;

public class WaterMaps {
	
	private OceanFFT fft;
	private NormalMapRenderer normalmapRenderer;
	
	public WaterMaps(int N){
		fft = new OceanFFT(N);
		fft.init();
		normalmapRenderer = new NormalMapRenderer(N);
	}
	
	public void render(){
		fft.render();
		normalmapRenderer.render(fft.getDy());
	}
	
	public OceanFFT getFFT(){
		return this.fft;
	}

	public NormalMapRenderer getNormalmapRenderer() {
		return normalmapRenderer;
	}
}
