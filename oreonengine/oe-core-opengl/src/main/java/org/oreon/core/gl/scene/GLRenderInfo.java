package org.oreon.core.gl.scene;

import org.oreon.core.gl.buffers.VBO;
import org.oreon.core.gl.config.RenderConfig;
import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.scene.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GLRenderInfo extends Component{
	
	private GLShader shader;
	private RenderConfig config;
	@Getter
	private VBO vbo;
	
	public void render(){
		config.enable();
		shader.bind();			
		shader.updateUniforms(getParent());
		vbo.draw();
		config.disable();
	}

}
