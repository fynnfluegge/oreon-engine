package oreonworlds.assets.plants;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.configs.AlphaTestCullFaceDisable;
import engine.configs.CullFaceDisable;
import engine.core.Camera;
import engine.math.Matrix4f;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.scenegraph.components.TransformsInstanced;
import engine.utils.BufferAllocation;
import engine.utils.Constants;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import modules.terrain.Terrain;
import oreonworlds.shaders.plants.Palm01InstancedShader;
import oreonworlds.shaders.plants.Palm01InstancedShadowShader;
import oreonworlds.shaders.plants.Palm01BillboardInstancedShader;

public class Palm01Instanced extends Node{
	
	private List<TransformsInstanced> transforms = new ArrayList<TransformsInstanced>();
	private List<TransformsInstanced> modelTransforms = new ArrayList<TransformsInstanced>();
	private List<TransformsInstanced> billboardTransforms = new ArrayList<TransformsInstanced>();
	
	private UBO modelMatricesBuffer;
	private UBO billboardMatricesBuffer;
	
	// 16 float per matrix, 2 matrices, 100 objects
	private final int buffersize = Float.BYTES * 16 * 2 * 100;

public Palm01Instanced(){

		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/oreonworlds/assets/plants/Palm_01","Palma 001.obj","Palma 001.mtl");
		Model[] billboard = loader.load("./res/oreonworlds/assets/plants/Palm_01","billboardmodel.obj","billboardmodel.mtl");
		
//		List<Matrix4f> instancedWorldMatrices = new ArrayList<Matrix4f>();
//		List<Matrix4f> instancedModelMatrices = new ArrayList<Matrix4f>();
		
		for (int i=0; i<100; i++){
			Vec3f translation = new Vec3f((float)(Math.random()*200)-100 + 1196, 0, (float)(Math.random()*200)-100 - 450);
			float terrainHeight = Terrain.getInstance().getTerrainHeight(translation.getX(),translation.getZ());
			translation.setY(terrainHeight);
			float s = (float)(Math.random()*0.1 + 0.15);
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float)(Math.random()*360),0);
			
			
//			Matrix4f translationMatrix = new Matrix4f().Translation(translation);
//			Matrix4f rotationMatrix = new Matrix4f().Rotation(rotation);
//			Matrix4f scalingMatrix = new Matrix4f().Scaling(scaling);
			
//			Matrix4f worldMatrix = translationMatrix.mul(scalingMatrix.mul(rotationMatrix));
//			Matrix4f modelMatrix = rotationMatrix;
			
			TransformsInstanced transform = new TransformsInstanced();
			transform.setTranslation(translation);
			transform.setScaling(scaling);
			transform.setRotation(rotation);
			transform.setLocalRotation(rotation);
			transform.initMatrices();
			transforms.add(transform);
			
//			instancedWorldMatrices.add(worldMatrix);
//			instancedModelMatrices.add(modelMatrix);
		}
		
		modelMatricesBuffer = new UBO();
		modelMatricesBuffer.setBinding_point_index(Constants.Palm01InstancedMatricesBinding);
		modelMatricesBuffer.bindBufferBase();
		modelMatricesBuffer.allocate(buffersize);
		
		billboardMatricesBuffer = new UBO();
		billboardMatricesBuffer.setBinding_point_index(Constants.Palm01BillboardInstancedMatricesBinding);
		billboardMatricesBuffer.bindBufferBase();
		billboardMatricesBuffer.allocate(buffersize);

		

//		FloatBuffer floatBuffer = BufferAllocation.createFloatBuffer(buffersize);
//		
//		for(Matrix4f matrix : instancedWorldMatrices){
//			Matrix4f verticalTranslation = new Matrix4f().Translation(
//					new Vec3f(0,Terrain.getInstance().getTerrainHeight(matrix.get(0,3),matrix.get(2, 3)),0));
//			matrix = verticalTranslation.mul(matrix);
//			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix));
//		}
//		
//		for(Matrix4f matrix : instancedModelMatrices){
//			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix));
//		}
		
//		modelMatricesBuffer = new UBO();
//		modelMatricesBuffer.setBinding_point_index(Constants.Palm01InstancedMatricesBinding);
//		modelMatricesBuffer.bindBufferBase();
//		modelMatricesBuffer.allocate(buffersize);
//		modelMatricesBuffer.updateData(floatBuffer, buffersize);

		update();
		
		
		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			model.getMesh().setInstances(modelTransforms.size());
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), Palm01InstancedShader.getInstance(), Palm01InstancedShadowShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
		
			
		GameObject object = new GameObject();
		MeshVAO meshBuffer = new MeshVAO();
		billboard[0].getMesh().setTangentSpace(false);
		billboard[0].getMesh().setInstanced(true);
		billboard[0].getMesh().setInstances(billboardTransforms.size());
		meshBuffer.addData(billboard[0].getMesh());

		object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.3f), Palm01BillboardInstancedShader.getInstance()));
		Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

		object.addComponent("Material", billboard[0].getMaterial());
		object.addComponent("Renderer", renderer);
		addChild(object);
	}

	public void update()
	{
		modelTransforms.clear();
		billboardTransforms.clear();
		
		for (TransformsInstanced transform : transforms){
			if (transform.getTranslation().sub(Camera.getInstance().getPosition()).length() > 200){
				billboardTransforms.add(transform);
			}
			else{
				modelTransforms.add(transform);
			}
		}
		
		FloatBuffer floatBuffer = BufferAllocation.createFloatBuffer(buffersize);
		
		for(TransformsInstanced matrix : modelTransforms){
			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getWorldMatrix()));
		}
		
		for(TransformsInstanced matrix : modelTransforms){
			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getModelMatrix()));
		}
		
		modelMatricesBuffer.updateData(floatBuffer, Float.BYTES * 32 * modelTransforms.size());
		floatBuffer.clear();
		floatBuffer = BufferAllocation.createFloatBuffer(buffersize);
		
		for(TransformsInstanced matrix : billboardTransforms){
			
			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getWorldMatrix()));
		}
		
		for(TransformsInstanced matrix : billboardTransforms){
			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getModelMatrix()));
		}
		
		billboardMatricesBuffer.updateData(floatBuffer, Float.BYTES * 32 * billboardTransforms.size());
		floatBuffer.clear();
		floatBuffer = BufferAllocation.createFloatBuffer(buffersize);
	}
}
