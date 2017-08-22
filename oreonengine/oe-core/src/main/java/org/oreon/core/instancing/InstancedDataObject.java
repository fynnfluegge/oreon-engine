package org.oreon.core.instancing;

import org.oreon.core.buffers.VBO;
import org.oreon.core.model.Material;
import org.oreon.core.renderer.RenderInfo;

public class InstancedDataObject {

	private VBO vao;
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

	public VBO getVao() {
		return vao;
	}

	public void setVao(VBO vao) {
		this.vao = vao;
	}

	public RenderInfo getShadowRenderInfo() {
		return shadowRenderInfo;
	}

	public void setShadowRenderInfo(RenderInfo shadowRenderInfo) {
		this.shadowRenderInfo = shadowRenderInfo;
	}
}
