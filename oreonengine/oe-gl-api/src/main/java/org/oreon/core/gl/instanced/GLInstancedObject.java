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
		getHighPolyInstanceCount().setValue(0);
		super.renderShadows();
		getHighPolyInstanceCount().setValue(getHighPolyIndices().size());
	}
	
	public void render() {
		
		getWorldMatricesBuffer().bindBufferBase(0);
		getModelMatricesBuffer().bindBufferBase(1);
		
		// only low-poly objects rendered when reflection or refraction rendering
		if (BaseContext.getConfig().isRenderRefraction()){
			getHighPolyInstanceCount().setValue(0);
			super.render();
			getHighPolyInstanceCount().setValue(getHighPolyIndices().size());
		}
		else if (BaseContext.getConfig().isRenderReflection()){
			getHighPolyInstanceCount().setValue(0);
			super.render();
			getHighPolyInstanceCount().setValue(getHighPolyIndices().size());
		}
		else{
			super.render();
		}
	}
}
