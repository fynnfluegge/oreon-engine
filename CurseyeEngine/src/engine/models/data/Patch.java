package engine.models.data;

import engine.core.Vertex;

public class Patch {

	private Vertex[] vertices;

	public Patch(Vertex[] vertices)
	{
		this.vertices = vertices;
	}
	
	public Vertex[] getVertices() {
		return vertices;
	}

	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
	}
	
	
}
