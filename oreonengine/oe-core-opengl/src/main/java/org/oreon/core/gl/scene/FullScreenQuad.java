package org.oreon.core.gl.scene;

import org.oreon.core.configs.RenderConfig;
import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.shaders.FullScreenQuadShader;
import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Transform;
import org.oreon.core.math.Vec2f;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.MeshGenerator;

public class FullScreenQuad {
	
	private Texture texture;
	private Transform orthoTransform;
	private Matrix4f orthographicMatrix;
	private GLShader shader;
	private GLMeshVBO vao;
	private RenderConfig config;
	protected Vec2f[] texCoords;
	
	public FullScreenQuad(){
		
		texture = new Texture();
		
		orthographicMatrix = new Matrix4f().Orthographic2D();
		orthoTransform = new Transform();
		orthoTransform.setTranslation(0, 0, 0);
		orthoTransform.setScaling(CoreSystem.getInstance().getWindow().getWidth(), CoreSystem.getInstance().getWindow().getHeight(), 0);
		orthographicMatrix = orthographicMatrix.mul(orthoTransform.getWorldMatrix());
		
		shader = FullScreenQuadShader.getInstance();
		config = new Default();
		vao = new GLMeshVBO();
		vao.addData(MeshGenerator.Quad2D());
	}
	
	
	public void render()
	{
		getConfig().enable();
		getShader().bind();
		getShader().updateUniforms(getOrthographicMatrix());
		getShader().updateUniforms(texture);
		getVao().draw();
		getConfig().disable();
	}	

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}


	public RenderConfig getConfig() {
		return config;
	}


	public void setConfig(RenderConfig config) {
		this.config = config;
	}


	public GLShader getShader() {
		return shader;
	}


	public void setShader(GLShader shader) {
		this.shader = shader;
	}


	public Matrix4f getOrthographicMatrix() {
		return orthographicMatrix;
	}


	public void setOrthographicMatrix(Matrix4f orthographicMatrix) {
		this.orthographicMatrix = orthographicMatrix;
	}


	public GLMeshVBO getVao() {
		return vao;
	}


	public void setVao(GLMeshVBO vao) {
		this.vao = vao;
	}


	public Transform getOrthoTransform() {
		return orthoTransform;
	}


	public void setOrthoTransform(Transform orthoTransform) {
		this.orthoTransform = orthoTransform;
	}
}
