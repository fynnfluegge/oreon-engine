package engine.geometry;


public class Mesh{

	private Vertex[] vertices;
	private int[] indices;
	private int instances;
	private boolean tangentSpace;
	private boolean instanced;
	
	public Mesh(Vertex[] vertices, int[] indices)
	{
		this.vertices = vertices;
		this.indices = indices;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public void setIndices(int[] indices) {
		this.indices = indices;
	}

	public boolean isTangentSpace() {
		return tangentSpace;
	}

	public void setTangentSpace(boolean tangentSpace) {
		this.tangentSpace = tangentSpace;
	}

	public boolean isInstanced() {
		return instanced;
	}

	public void setInstanced(boolean instanced) {
		this.instanced = instanced;
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}
}
