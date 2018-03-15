package org.oreon.core.model;

import org.oreon.core.scenegraph.Component;

public class Model extends Component{

	private Mesh mesh;
	private Material material;
	
	public Model() {}

	public Mesh getMesh() {
		return mesh;
	}
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
}
