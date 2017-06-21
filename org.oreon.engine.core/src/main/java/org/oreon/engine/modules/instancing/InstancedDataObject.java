package org.oreon.engine.modules.instancing;

import org.oreon.engine.engine.buffers.VAO;
import org.oreon.engine.engine.scenegraph.components.Material;
import org.oreon.engine.engine.scenegraph.components.RenderInfo;

public class InstancedDataObject {

	private VAO vao;
	private Material material;
	private RenderInfo renderInfo;

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

	public VAO getVao() {
		return vao;
	}

	public void setVao(VAO vao) {
		this.vao = vao;
	}
}
