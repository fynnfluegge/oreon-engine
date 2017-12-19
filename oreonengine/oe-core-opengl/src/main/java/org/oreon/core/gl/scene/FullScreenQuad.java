package org.oreon.core.gl.scene;

import org.oreon.core.configs.RenderConfig;
import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.shaders.FullScreenQuadShader;
import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Vec2f;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.MeshGenerator;

public class FullScreenQuad {
	
	private Texture texture;
	private GLShader shader;
	private GLMeshVBO vao;
	private RenderConfig config;
	protected Vec2f[] texCoords;
	
	public FullScreenQuad(){
		
		texture = new Texture();
		
		shader = FullScreenQuadShader.getInstance();
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


	public GLMeshVBO getVao() {
		return vao;
	}


	public void setVao(GLMeshVBO vao) {
		this.vao = vao;
	}

}
