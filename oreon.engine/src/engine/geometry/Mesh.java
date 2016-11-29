package engine.geometry;


public class Mesh{

	private Vertex[] vertices;
	private int[] indices;
	private boolean tangentSpace;
	private int GL_PRIMITIVE;
	
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

	public int getGL_PRIMITIVE() {
		return GL_PRIMITIVE;
	}

	public void setGL_PRIMITIVE(int gL_PRIMITIVE) {
		GL_PRIMITIVE = gL_PRIMITIVE;
	}

	public boolean isTangentSpace() {
		return tangentSpace;
	}

	public void setTangentSpace(boolean tangentSpace) {
		this.tangentSpace = tangentSpace;
	}
}
