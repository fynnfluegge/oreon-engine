package samples.objLoader;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import oreonworlds.shaders.plants.Grass01InstancedShader;
import oreonworlds.shaders.plants.PalmBillboardShadowShader;
import oreonworlds.shaders.plants.PalmShader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.configs.CullFaceDisable;
import engine.core.Input;
import engine.math.Matrix4f;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.utils.BufferAllocation;
import engine.utils.Constants;

public class OBJ extends Node{

	public OBJ(){
		
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/oreonworlds/assets/plants/Tree_01","tree01.obj","tree01.mtl");
		List<Matrix4f> instancedWorldMatrices = new ArrayList<Matrix4f>();
		List<Matrix4f> instancedModelMatrices = new ArrayList<Matrix4f>();
		
		for (int i=0; i<1; i++){
			Vec3f translation = new Vec3f(0, 0, 0);
			float s = 100;
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,-90,0);
			
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
			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix));
		}
		
		for(Matrix4f matrix : instancedModelMatrices){
			floatBuffer.put(BufferAllocation.createFlippedBuffer(matrix));
		}
		
		UBO ubo = new UBO();
		ubo.setBinding_point_index(Constants.Palm01BillboardInstancedWorldMatricesBinding);
		ubo.bindBufferBase();
		ubo.allocate(buffersize);
		ubo.updateData(floatBuffer, buffersize);
		
		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			model.getMesh().setInstances(1);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), PalmBillboardShadowShader.getInstance()));
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