package engine.components.renderer;

import engine.buffers.VBO;
import engine.components.Component;

public class Renderer extends Component{
	
	private VBO vbo;
	private RenderInfo renderInfo;
	
	public Renderer(){}
	
	public Renderer(VBO vao)
	{
		this.vbo = vao;
	}
	
	public void render(){
		renderInfo.getConfig().enable();
		renderInfo.getShader().bind();			
		renderInfo.getShader().updateUniforms(getParent());
		getVbo().draw();
		renderInfo.getConfig().disable();
	};

	public VBO getVbo() {
		return vbo;
	}

	public void setVbo(VBO vbo) {
		this.vbo = vbo;
	}

	public RenderInfo getRenderInfo() {
		return renderInfo;
	}

	public void setRenderInfo(RenderInfo renderinfo) {
		this.renderInfo = renderinfo;
	}
}
