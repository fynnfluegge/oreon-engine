package org.oreon.core.instancing;

import org.oreon.core.buffers.VBO;
import org.oreon.core.model.Material;
import org.oreon.core.renderer.RenderInfo;

public class InstancedDataObject {

	private VBO vbo;
	private Material material;
	private RenderInfo renderInfo;
	private RenderInfo shadowRenderInfo;

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public RenderInfo getRenderInfo() {
		return renderInfo;
	}

	public void setRenderInfo(RenderInfo renderInfo) {
		this.renderInfo = renderInfo;
	}

	public VBO getVbo() {
		return vbo;
	}

	public void setVbo(VBO vao) {
		this.vbo = vao;
	}

	public RenderInfo getShadowRenderInfo() {
		return shadowRenderInfo;
	}

	public void setShadowRenderInfo(RenderInfo shadowRenderInfo) {
		this.shadowRenderInfo = shadowRenderInfo;
	}
}
