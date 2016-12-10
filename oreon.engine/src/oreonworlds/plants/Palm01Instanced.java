package oreonworlds.plants;

import java.nio.FloatBuffer;
import java.util.List;

import engine.buffers.MeshVAO;
import engine.buffers.UBO;
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
import oreonworlds.shaders.Palm01InstancedShader;
import oreonworlds.shaders.Palm01InstancedShadowShader;

public class Palm01Instanced extends Node{

public Palm01Instanced(){

		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/oreonworlds/plants/Palm_01","Palma 001.obj","Palma 001.mtl");
		
		List<Matrix4f> instancedWorldMatrices = ResourceLoader.loadObjectTransforms("./res/oreonworlds/plants/Palm_01/Palm_01_instancedtransforms.txt");
		List<Matrix4f> instancedModelMatrices = ResourceLoader.loadObjectTransformsModelMatrix("./res/oreonworlds/plants/Palm_01/Palm_01_instancedtransforms.txt");

		
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
		ubo.setBinding_point_index(Constants.Palm01InstancedMatrices);
		ubo.bindBufferBase();
		ubo.allocate(buffersize);
		ubo.updateData(floatBuffer, buffersize);

		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), Palm01InstancedShader.getInstance(), Palm01InstancedShadowShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
	}
}
