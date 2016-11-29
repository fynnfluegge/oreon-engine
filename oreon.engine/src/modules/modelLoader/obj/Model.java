package modules.modelLoader.obj;

import engine.geometry.Mesh;
import engine.scenegraph.components.Material;

public class Model {

	private Mesh mesh;
	private Material material;
	
	public Model(Mesh mesh)
	{
		this.mesh = mesh;
	}
	
	public Model() {
	}

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
