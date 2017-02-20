package oreonworlds.assets.plants;

import java.nio.FloatBuffer;
import java.util.List;
import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.configs.AlphaBlending;
import engine.configs.AlphaTestCullFaceDisable;
import engine.configs.AlphaTest;
import engine.configs.CullFaceDisable;
import engine.math.Matrix4f;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.utils.BufferAllocation;
import engine.utils.Constants;
import engine.utils.ResourceLoader;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import modules.terrain.Terrain;
import oreonworlds.shaders.plants.Bush01InstancedShader;
import oreonworlds.shaders.plants.Bush01InstancedShadowShader;

public class Bush01Instanced extends Node{
	
	
	public Bush01Instanced(){
		
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/oreonworlds/assets/plants/Bush_01","Bush_01.obj","Bush_01.mtl");
		
		List<Matrix4f> instancedWorldMatrices = ResourceLoader.loadObjectTransforms("./res/oreonworlds/assets/plants/Bush_01/Bush_01_instancedtransforms.txt");
		List<Matrix4f> instancedModelMatrices = ResourceLoader.loadObjectTransformsModelMatrix("./res/oreonworlds/assets/plants/Bush_01/Bush_01_instancedtransforms.txt");
		
		// 2 matrices for each transform
		int buffersize = Float.BYTES * 16 * 2 * instancedWorldMatrices.size();
		FloatBuffer floatBuffer = BufferAllocation.createFloatBuffer(buffersize);
		
		for(Matrix4f matrix : instancedWorldMatrices){
			Matrix4f verticalTranslation = new Matrix4f().Translation(
					new Vec3f(0,Terrain.getInstance().getTerrainHeight(matrix.get(0,3),matrix.get(2, 3)),0));
			matrix = verticalTranslation.mul(matrix);
			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix));
		}
		
		for(Matrix4f matrix : instancedModelMatrices){
			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix));
		}
		
		UBO ubo = new UBO();
		ubo.setBinding_point_index(Constants.Bush01InstancedMatricesBinding);
		ubo.bindBufferBase();
		ubo.allocate(buffersize);
		ubo.updateData(floatBuffer, buffersize);
		
		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new AlphaTest(0.2f), Bush01InstancedShader.getInstance(), Bush01InstancedShadowShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
	}
}
