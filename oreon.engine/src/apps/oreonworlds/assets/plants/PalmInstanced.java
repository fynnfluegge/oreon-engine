package apps.oreonworlds.assets.plants;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import apps.oreonworlds.shaders.plants.PalmBillboardShader;
import apps.oreonworlds.shaders.plants.PalmBillboardShadowShader;
import apps.oreonworlds.shaders.plants.PalmShader;
import apps.oreonworlds.shaders.plants.PalmShadowShader;
import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.configs.AlphaTestCullFaceDisable;
import engine.configs.CullFaceDisable;
import engine.core.Camera;
import engine.geometry.Vertex;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.scenegraph.components.TransformsInstanced;
import engine.utils.BufferAllocation;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import modules.terrain.Terrain;

public class PalmInstanced extends Node{
	
	private List<TransformsInstanced> transforms = new ArrayList<TransformsInstanced>();
	private List<Integer> highPolyIndices = new ArrayList<Integer>();
	private List<Integer> billboardIndices = new ArrayList<Integer>();
	
	private UBO modelMatricesBuffer;
	private UBO worldMatricesBuffer;

	private final int instances = 80;
	private final int buffersize = Float.BYTES * 16 * instances;
	private Vec3f center;
	
	private int modelMatBinding;
	private int worldMatBinding;

	public PalmInstanced(Vec3f pos, int modelMatBinding, int worldMatBinding){
		
		center = pos;
		this.setModelMatBinding(modelMatBinding);
		this.setWorldMatBinding(worldMatBinding);
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Palm_01","Palma 001.obj","Palma 001.mtl");
		Model[] billboards = new OBJLoader().load("./res/oreonworlds/assets/plants/Palm_01","billboardmodel.obj","billboardmodel.mtl");
		
		for (int i=0; i<instances; i++){
			Vec3f translation = new Vec3f((float)(Math.random()*500)-250 + center.getX(), 0, (float)(Math.random()*500)-250 + center.getZ());
			float terrainHeight = Terrain.getInstance().getTerrainHeight(translation.getX(),translation.getZ());
			terrainHeight -= 3;
			translation.setY(terrainHeight);
			float s = (float)(Math.random()*0.15 + 0.2);
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float) Math.random()*360f,0);
			
			TransformsInstanced transform = new TransformsInstanced();
			transform.setTranslation(translation);
			transform.setScaling(scaling);
			transform.setRotation(rotation);
			transform.setLocalRotation(rotation);
			transform.initMatrices();
			transforms.add(transform);
		}
		
		modelMatricesBuffer = new UBO();
		modelMatricesBuffer.setBinding_point_index(modelMatBinding);
		modelMatricesBuffer.bindBufferBase();
		modelMatricesBuffer.allocate(buffersize);
		
		worldMatricesBuffer = new UBO();
		worldMatricesBuffer.setBinding_point_index(worldMatBinding);
		worldMatricesBuffer.bindBufferBase();
		worldMatricesBuffer.allocate(buffersize);	
		
		/**
		 * init matrices UBO's
		 */
		int size = Float.BYTES * 16 * instances;
		
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
			model.getMesh().setInstances(instances);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), PalmShader.getInstance(), PalmShadowShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
		for (Model billboard : billboards){	
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			billboard.getMesh().setInstances(0);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPos(vertex.getPos().mul(135));
				vertex.getPos().setX(vertex.getPos().getX()*1.1f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1.1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.4f), PalmBillboardShader.getInstance(), PalmBillboardShadowShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			
			object.addComponent("Material", billboard.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
		
		updateUBOs();
	}

	public void update()
	{	
		if (center.sub(Camera.getInstance().getPosition()).length() < 600){
			
			updateUBOs();
			
//			System.out.println(center.sub(Camera.getInstance().getPosition()).length());
//			System.out.println(highPolyIndices.size());
		}
	}
	
	public void updateUBOs(){
		
		highPolyIndices.clear();
		billboardIndices.clear();
		
		int index = 0;
		
		for (TransformsInstanced transform : transforms){
			if (transform.getTranslation().sub(Camera.getInstance().getPosition()).length() > 200){
				billboardIndices.add(index);
			}
			else{
				highPolyIndices.add(index);
			}
			index++;
		}
		
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(0)).getComponent("Renderer")).getVao()).setInstances(highPolyIndices.size());
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getVao()).setInstances(highPolyIndices.size());
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(2)).getComponent("Renderer")).getVao()).setInstances(highPolyIndices.size());
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(3)).getComponent("Renderer")).getVao()).setInstances(highPolyIndices.size());
		
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(4)).getComponent("Renderer")).getVao()).setInstances(billboardIndices.size());
	}

	public int getModelMatBinding() {
		return modelMatBinding;
	}

	public void setModelMatBinding(int modelMatBinding) {
		this.modelMatBinding = modelMatBinding;
	}

	public int getWorldMatBinding() {
		return worldMatBinding;
	}

	public void setWorldMatBinding(int worldMatBinding) {
		this.worldMatBinding = worldMatBinding;
	}

	public List<Integer> getHighPolyIndices() {
		return highPolyIndices;
	}

	public void setHighPolyIndices(List<Integer> highPolyIndices) {
		this.highPolyIndices = highPolyIndices;
	}

	public List <Integer> getBillboardIndices() {
		return billboardIndices;
	}

	public void setBillboardIndices(List <Integer> billboardIndices) {
		this.billboardIndices = billboardIndices;
	}
}
