package org.oreon.vk.components.atmosphere;

import org.oreon.core.model.Mesh;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ProceduralTexturing;
import org.oreon.core.vk.core.buffer.VkBuffer;
import org.oreon.core.vk.core.pipeline.VkPipeline;
import org.oreon.core.vk.core.util.VkAssimpModelLoader;

public class Skydome extends Renderable{
	
	private VkBuffer vertexBufferObject;
	private VkBuffer indexBufferObject;
	private VkPipeline graphicsPipeline;
	
	public Skydome() {
		
		getWorldTransform().setLocalScaling(Constants.ZFAR*0.5f, Constants.ZFAR*0.5f, Constants.ZFAR*0.5f);
		
		Mesh mesh = VkAssimpModelLoader.loadModel("models/obj/dome", "dome.obj").get(0).getMesh();
		ProceduralTexturing.dome(mesh);
	}

}
