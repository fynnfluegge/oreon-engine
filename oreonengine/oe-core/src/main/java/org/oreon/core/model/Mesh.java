package org.oreon.core.model;

import org.oreon.core.model.Vertex.VertexLayout;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Mesh{

	private Vertex[] vertices;
	private int[] indices;
	private int instances;
	private VertexLayout vertexLayout;
	private boolean tangentSpace = false;
	private boolean instanced = false;
	
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

	public VertexLayout getVertexLayout() {
		return vertexLayout;
	}

	public void setVertexLayout(VertexLayout vertexLayout) {
		this.vertexLayout = vertexLayout;
	}
}
