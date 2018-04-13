package org.oreon.core.gl.surface;

import org.oreon.core.gl.buffer.GLMeshVBO;
import org.oreon.core.gl.parameter.Default;
import org.oreon.core.gl.parameter.RenderParameter;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.shaders.FullScreenMSQuadShader;
import org.oreon.core.math.Vec2f;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.MeshGenerator;

public class FullScreenMultisampleQuad {

	private Texture texture;
	private GLShaderProgram shader;
	private GLMeshVBO vao;
	private RenderParameter config;
	protected Vec2f[] texCoords;
	
	public FullScreenMultisampleQuad(){
		
		texture = new Texture();
		
		shader = FullScreenMSQuadShader.getInstance();
		config = new Default();
		vao = new GLMeshVBO();
		vao.addData(MeshGenerator.NDCQuad2D());
	}
	
	
	public void render()
	{
		getConfig().enable();
		getShader().bind();
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


	public RenderParameter getConfig() {
		return config;
	}


	public void setConfig(RenderParameter config) {
		this.config = config;
	}


	public GLShaderProgram getShader() {
		return shader;
	}


	public void setShader(GLShaderProgram shader) {
		this.shader = shader;
	}


	public GLMeshVBO getVao() {
		return vao;
	}


	public void setVao(GLMeshVBO vao) {
		this.vao = vao;
	}
}
