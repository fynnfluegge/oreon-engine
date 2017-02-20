package engine.scenegraph.components;

import engine.math.Matrix4f;

public class TransformsInstanced extends Transform{
	
	private Matrix4f worldMatrix;
	private Matrix4f modelMatrix;
	
	public TransformsInstanced(){
		worldMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
	}
	
	public void initMatrices() {
		worldMatrix = super.getWorldMatrix();
		modelMatrix = super.getModelMatrix();
	}
	
	public Matrix4f getWorldMatrix() {
		return worldMatrix;
	}

	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}
}
