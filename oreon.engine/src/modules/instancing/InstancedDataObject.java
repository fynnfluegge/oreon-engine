package modules.instancing;

import engine.buffers.VBO;
import engine.components.model.Material;
import engine.components.renderer.RenderInfo;

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
