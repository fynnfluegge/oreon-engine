package apps.samples.objLoader;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import apps.oreonworlds.shaders.plants.TreeLeavesShader;
import apps.oreonworlds.shaders.plants.TreeTrunkShader;
import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.configs.AlphaTest;
import engine.configs.Default;
import engine.core.Input;
import engine.geometry.Vertex;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.scenegraph.components.TransformsInstanced;
import engine.utils.BufferAllocation;

public class OBJ extends Node{

	public OBJ(){
		
		int buffersize = Float.BYTES * 16 * 1;
		
		List<TransformsInstanced> transforms = new ArrayList<TransformsInstanced>();
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Tree_02","tree02.obj","tree02.mtl");
		
		for (int i=0; i<1; i++){
			Vec3f translation = new Vec3f(0,0,0);
			float terrainHeight = 0;
			terrainHeight -= 1;
			translation.setY(terrainHeight);
			float s = (float)(Math.random()*4 + 8);
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float) Math.random()*0f,0);
			
			TransformsInstanced transform = new TransformsInstanced();
			transform.setTranslation(translation);
			transform.setScaling(scaling);
			transform.setRotation(rotation);
			transform.setLocalRotation(rotation);
			transform.initMatrices();
			transforms.add(transform);
		}
		
		UBO modelMatricesBuffer;
		UBO worldMatricesBuffer;
		
		modelMatricesBuffer = new UBO();
		modelMatricesBuffer.setBinding_point_index(12);
		modelMatricesBuffer.bindBufferBase();
		modelMatricesBuffer.allocate(buffersize);
		
		worldMatricesBuffer = new UBO();
		worldMatricesBuffer.setBinding_point_index(13);
		worldMatricesBuffer.bindBufferBase();
		worldMatricesBuffer.allocate(buffersize);	
		
		/**
		 * init matrices UBO's
		 */
		int size = Float.BYTES * 16 * 1;
		
		FloatBuffer worldMatricesFloatBuffer = BufferAllocation.createFloatBuffer(size);
		FloatBuffer modelMatricesFloatBuffer = BufferAllocation.createFloatBuffer(size);
		
		for(TransformsInstanced matrix : transforms){
			worldMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getWorldMatrix()));
			modelMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getModelMatrix()));
		}
		
		worldMatricesBuffer.updateData(worldMatricesFloatBuffer, size);
		modelMatricesBuffer.updateData(modelMatricesFloatBuffer, size);
		
		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			model.getMesh().setInstances(1);
			
			for (Vertex vertex : model.getMesh().getVertices()){
				vertex.getPos().setX(vertex.getPos().getX()*1.2f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1.2f);
			}
			
			meshBuffer.addData(model.getMesh());

			if (model.equals(models[0]))
				object.setRenderInfo(new RenderInfo(new Default(), TreeTrunkShader.getInstance()));
			else
				object.setRenderInfo(new RenderInfo(new AlphaTest(0.6f), TreeLeavesShader.getInstance()));
				
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
	}
	
	public void update(){
		
		super.update();
		
		if (Input.getHoldingKeys().contains(Keyboard.KEY_G))
		{
			for(Node gameobject : this.getChildren()){
				((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shader.basic.BasicGridShader.getInstance());
			}
		}
	}
	
}