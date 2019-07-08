package org.oreon.core.gl.instanced;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.memory.GLUniformBuffer;
import org.oreon.core.instanced.InstancedObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLInstancedObject extends InstancedObject{

	private GLUniformBuffer modelMatricesBuffer;
	private GLUniformBuffer worldMatricesBuffer;
	
	public void renderShadows(){
		
		// only low-poly objects are rendered into shadow maps
		getWorldMatricesBuffer().bindBufferBase(0);
		if (getHighPolyInstanceCount().getValue() == 0){
			renderLowPolyShadows();
		}
		else{
			super.renderShadows();
		}
	}
	
	public void render() {
		
		getWorldMatricesBuffer().bindBufferBase(0);
		getModelMatricesBuffer().bindBufferBase(1);
		
		// only low-poly objects rendered when reflection or refraction rendering
		if (BaseContext.getConfig().isRenderRefraction()){
			renderLowPoly();
		}
		else if (BaseContext.getConfig().isRenderReflection()){
			renderLowPoly();
		}
		else{
			if (getHighPolyInstanceCount().getValue() == 0){
				renderLowPoly();
			}
			else{
				super.render();
			}
		}
	}
}
