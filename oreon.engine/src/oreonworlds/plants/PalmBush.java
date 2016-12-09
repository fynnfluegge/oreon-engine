package oreonworlds.plants;

import java.nio.FloatBuffer;
import java.util.List;

import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.configs.CullFaceDisable;
import engine.core.Constants;
import engine.math.Matrix4f;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.utils.BufferAllocation;
import engine.utils.ResourceLoader;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import oreonworlds.shaders.PalmBushInstancedShader;
import oreonworlds.shaders.PalmBushInstancedShadwoShader;

public class PalmBush extends Node{
	
	
	public PalmBush(Vec3f translation){
		
		getTransform().setLocalRotation(0, 0, 0);
		getTransform().setLocalScaling(1f,1f,1f);
		getTransform().setLocalTranslation(translation);
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/oreonworlds/plants/PalmBush","Palm_01.obj","Palm_01.mtl");
		
		List<Matrix4f> instancedWorldMatrices = ResourceLoader.loadObjectTransforms("./res/oreonworlds/plants/PalmBush/instancedtransforms.txt");
		int buffersize = Float.BYTES * 16 * instancedWorldMatrices.size();
		FloatBuffer floatBuffer = BufferAllocation.createFloatBuffer(buffersize);
		UBO ubo = new UBO();
		ubo.setBinding_point_index(Constants.PalmBushInstancedMatrices);
		ubo.bindBufferBase();
		ubo.allocate(buffersize);
		
		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), PalmBushInstancedShader.getInstance(), PalmBushInstancedShadwoShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
	}
}
