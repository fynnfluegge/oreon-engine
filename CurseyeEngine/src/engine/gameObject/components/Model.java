package engine.gameObject.components;

import engine.models.data.Material;
import engine.models.data.Mesh;
import engine.models.data.Patch;

public class Model extends Component{

	private Mesh mesh;
	private Patch patch;
	private Material material;
	
	public Model(Mesh mesh)
	{
		this.mesh = mesh;
	}
	
	public Model(Patch patch)
	{
		this.patch = patch;
	}
		
	public Model() {
	}

	public Mesh getMesh() {
		return mesh;
	}
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Patch getPatch() {
		return patch;
	}

	public void setPatch(Patch patch) {
		this.patch = patch;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
}
