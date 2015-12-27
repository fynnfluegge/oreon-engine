package engine.gui;

import engine.configs.RenderingConfig;
import engine.core.Transform;
import engine.math.Matrix4f;
import engine.math.Vec2f;
import engine.shaderprograms.Shader;

public abstract class GUIElement {

	private Transform orthoTransform;
	private Matrix4f orthographicMatrix;
	private Shader shader;
	private GUIVAO vao;
	private RenderingConfig config;
	protected Vec2f[] texCoords;
	
	public abstract void init();
	
	public abstract void render();
	
	public void update(){}

	public Transform getOrthoTransform() {
		return orthoTransform;
	}

	public void setOrthoTransform(Transform transform) {
		this.orthoTransform = transform;
	}

	public Matrix4f getOrthographicMatrix() {
		return orthographicMatrix;
	}

	public void setOrthographicMatrix(Matrix4f orthographicMatrix) {
		this.orthographicMatrix = orthographicMatrix;
	}

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public GUIVAO getVao() {
		return vao;
	}

	public void setVao(GUIVAO vao) {
		this.vao = vao;
	}

	public Vec2f[] getTexCoords() {
		return texCoords;
	}

	public void setTexCoords(Vec2f[] texCoords) {
		this.texCoords = texCoords;
	}

	public RenderingConfig getConfig() {
		return config;
	}

	public void setConfig(RenderingConfig config) {
		this.config = config;
	}
}
