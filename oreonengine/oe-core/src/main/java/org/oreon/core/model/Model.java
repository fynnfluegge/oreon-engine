package org.oreon.core.model;

import org.oreon.core.scenegraph.NodeComponent;

public class Model<T> extends NodeComponent{

	private Mesh mesh;
	private Material<T> material;
	
	public Model() {}

	public Mesh getMesh() {
		return mesh;
	}
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Material<T> getMaterial() {
		return material;
	}

	public void setMaterial(Material<T> material) {
		this.material = material;
	}

}
