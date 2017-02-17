package oreonworlds.assets.plants;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.configs.AlphaTestCullFaceDisable;
import engine.math.Matrix4f;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.utils.BufferAllocation;
import engine.utils.Constants;
import engine.utils.Util;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import modules.terrain.Terrain;
import oreonworlds.shaders.plants.Grass01InstancedShader;
import oreonworlds.shaders.plants.Grass01InstancedShadowShader;

public class Grass01Instanced extends Node{
	
	
	public Grass01Instanced(){
		
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/oreonworlds/assets/plants/Grass_01","grassmodel.obj","grassmodel.mtl");
		
		List<Matrix4f> instancedWorldMatrices = new ArrayList<Matrix4f>();
		List<Matrix4f> instancedModelMatrices = new ArrayList<Matrix4f>();
		
		for (int i=0; i<512; i++){
			Vec3f translation = new Vec3f((float)(Math.random()*800)-400 + 1196, 0, (float)(Math.random()*800)-400 - 450);
			float s = (float)(Math.random()*6 + 10);
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,0,0);
			
			Matrix4f translationMatrix = new Matrix4f().Translation(translation);
			Matrix4f rotationMatrix = new Matrix4f().Rotation(rotation);
			Matrix4f scalingMatrix = new Matrix4f().Scaling(scaling);
			
			Matrix4f worldMatrix = translationMatrix.mul(scalingMatrix.mul(rotationMatrix));
			Matrix4f modelMatrix = rotationMatrix;
			
			instancedWorldMatrices.add(worldMatrix);
			instancedModelMatrices.add(modelMatrix);
		}
		
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
		ubo.setBinding_point_index(Constants.Grass01InstancedMatricesBinding);
		ubo.bindBufferBase();
		ubo.allocate(buffersize);
		ubo.updateData(floatBuffer, buffersize);
		
		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			//Util.generateNormalsCCW(model.getMesh().getVertices(), model.getMesh().getIndices());
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			model.getMesh().setInstances(512);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.3f), Grass01InstancedShader.getInstance(), Grass01InstancedShadowShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
	}
}
