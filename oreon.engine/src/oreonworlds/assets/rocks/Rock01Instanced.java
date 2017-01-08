package oreonworlds.assets.rocks;

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
import engine.utils.Util;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import modules.terrain.Terrain;
import oreonworlds.shaders.rocks.Rock01InstancedShader;
import oreonworlds.shaders.rocks.Rock01InstancedShadowShader;

public class Rock01Instanced extends Node{
	
	public Rock01Instanced() {
		
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/oreonworlds/assets/rocks/Rock_01","rock01.obj","rock01.mtl");
		
		List<Matrix4f> instancedWorldMatrices = ResourceLoader.loadObjectTransforms("./res/oreonworlds/assets/rocks/Rock_01/Rock_01_instancedtransforms.txt");
		List<Matrix4f> instancedModelMatrices = ResourceLoader.loadObjectTransformsModelMatrix("./res/oreonworlds/assets/rocks/Rock_01/Rock_01_instancedtransforms.txt");
		
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
		ubo.setBinding_point_index(Constants.Rock01InstancedMatricesBinding);
		ubo.bindBufferBase();
		ubo.allocate(buffersize);
		ubo.updateData(floatBuffer, buffersize);
		
		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			Util.generateTangentsBitangents(model.getMesh());
			model.getMesh().setTangentSpace(true);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), Rock01InstancedShader.getInstance(), Rock01InstancedShadowShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
	}

}
