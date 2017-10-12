package org.oreon.core.gl.deferred;

public class DeferredRenderer {

	private GBuffer gbuffer;
	private DeferredShader shader;
	
	public DeferredRenderer(GBuffer gbuffer) {
		this.gbuffer = gbuffer;
		this.shader = DeferredShader.getInstance();
	}
	
	public void render(){
		
	}

	public GBuffer getGbuffer() {
		return gbuffer;
	}

	public void setGbuffer(GBuffer gbuffer) {
		this.gbuffer = gbuffer;
	}
}
