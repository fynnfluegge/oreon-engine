package engine.scenegraph.components;

import engine.configs.RenderingConfig;
import engine.shaders.Shader;

public abstract class Renderer extends Component{
	
	private RenderingConfig config;
	private Shader shader;
	
	public Renderer(RenderingConfig config, Shader shader)
	{
		this.config = config;
		this.shader = shader;
	}
	
	public void render(){};
	public void update(){};
	public void input(){};
	
	public RenderingConfig getConfig() {
		return config;
	}

	public void setConfig(RenderingConfig config) {
		this.config = config;
	}

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}
}
