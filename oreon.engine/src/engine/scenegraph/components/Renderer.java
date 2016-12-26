package engine.scenegraph.components;

import engine.buffers.VAO;
import engine.shader.Shader;

public class Renderer extends Component{
	
	private Shader shader;
	private VAO vao;
	
	public Renderer(Shader shader, VAO vao)
	{
		this.shader = shader;
		this.vao = vao;
	}
	
	public void render(){
		getShader().bind();			
		getShader().updateUniforms(getParent());
		vao.draw();
	};

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public VAO getVao() {
		return vao;
	}

	public void setVao(VAO vao) {
		this.vao = vao;
	}
}
